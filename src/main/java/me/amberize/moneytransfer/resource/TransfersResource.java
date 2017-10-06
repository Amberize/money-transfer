package me.amberize.moneytransfer.resource;

import me.amberize.moneytransfer.domain.Transfer;
import me.amberize.moneytransfer.dto.TransferDto;
import me.amberize.moneytransfer.exceptions.StorageException;
import me.amberize.moneytransfer.exceptions.TransferException;
import me.amberize.moneytransfer.mappers.TransferMapper;
import me.amberize.moneytransfer.repository.AccountsRepository;
import me.amberize.moneytransfer.repository.TransfersRepository;
import me.amberize.moneytransfer.services.TransferService;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Path("transfers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransfersResource {

    @Inject
    private Validator validator;

    @Inject
    private TransferMapper mapper;

    @Inject
    private TransfersRepository repository;

    @Inject
    private AccountsRepository accountsRepository;

    @Inject
    private TransferService transferService;

    @POST
    public Response transfer(TransferDto dto) {
        Transfer transfer = mapper.toDomain(dto);

        List<String> violations = getViolations(transfer);
        if(!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
        }

        try {
            transfer = repository.create(transfer);

            transferService.executeTransfer(transfer.getId());
        } catch (StorageException | TransferException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        return Response.ok().build();
    }

    private List<String> getViolations(Transfer transfer) {
        Set<ConstraintViolation<Transfer>> violations = validator.validate(transfer);

        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

}
