package me.amberize.moneytransfer.mappers;


import ma.glasnost.orika.MapperFacade;
import me.amberize.moneytransfer.domain.Account;
import me.amberize.moneytransfer.dto.AccountDto;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

    @Inject
    private MapperFacade mapperFacade;

    public Account toDomain(AccountDto dto) {
        return mapperFacade.map(dto, Account.class);
    }

    public AccountDto toDto(Account domain) {
        return mapperFacade.map(domain, AccountDto.class);
    }

    public List<AccountDto> toDto(List<Account> domainList) {
        return domainList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
