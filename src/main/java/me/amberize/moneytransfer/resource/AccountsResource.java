package me.amberize.moneytransfer.resource;

import me.amberize.moneytransfer.domain.Account;
import me.amberize.moneytransfer.dto.AccountDto;
import me.amberize.moneytransfer.exceptions.StorageException;
import me.amberize.moneytransfer.mappers.AccountMapper;
import me.amberize.moneytransfer.repository.AccountsRepository;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @Inject
    private Validator validator;

    @Inject
    private AccountMapper mapper;

    @Inject
    private AccountsRepository repository;

    @GET
    @Path("{accountNumber}")
    public Response getAccount(@PathParam("accountNumber") String accountNumber) {
        AccountDto dto;

        try {
            Account account = repository.get(accountNumber);

            if(account.isDeleted()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            dto = mapper.toDto(account);
        } catch (StorageException e) {
            return serverError(e.getMessage());
        }

        return Response.ok(dto).build();
    }

    @GET
    public Response getAllAccounts() {
        return Response.ok(mapper.toDto(repository.getAll())).build();
    }

    @POST
    public Response createAccount(AccountDto dto) {
        try {
            if(accountExists(dto.getAccountNumber())) {
                return Response.status(Response.Status.CONFLICT).entity("Account already exists").build();
            }
        } catch (StorageException e) {
            return serverError(e.getMessage());
        }

        Account account = mapper.toDomain(dto);

        List<String> violations = getViolations(account);
        if(!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
        }

        try {
            repository.create(account);
        } catch (StorageException e) {
            return serverError(e.getMessage());
        }

        return Response.ok().build();
    }

    @PUT
    @Path("{accountNumber}")
    public Response updateAccount(@PathParam("accountNumber") String accountNumber, AccountDto dto) {
        if(!accountNumber.equals(dto.getAccountNumber())) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Both path and body account numbers should be equal")
                           .build();
        }

        try {
            if(!accountExists(dto.getAccountNumber())) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Account account = mapper.toDomain(dto);

            List<String> violations = getViolations(account);
            if(!violations.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
            }

            repository.update(account);
        } catch (StorageException e) {
            return serverError(e.getMessage());
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("{accountNumber}")
    public Response deleteAccount(@PathParam("accountNumber") String accountNumber) {
        try {
            if(!accountExists(accountNumber)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Account account = repository.get(accountNumber);

            account.setDeleted(true);

            repository.update(account);
        } catch (StorageException e) {
            return serverError(e.getMessage());
        }

        return Response.ok().build();
    }

    private boolean accountExists(String accountNumber) throws StorageException {
        Account account = repository.get(accountNumber);

        return account != null;
    }

    private Response serverError(String message) {
        return Response.serverError().entity(message).build();
    }

    private List<String> getViolations(Account account) {
        Set<ConstraintViolation<Account>> violations = validator.validate(account);

        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

}
