package me.amberize.moneytransfer.mappers;

import ma.glasnost.orika.MapperFacade;
import me.amberize.moneytransfer.domain.Transfer;
import me.amberize.moneytransfer.dto.TransferDto;

import javax.inject.Inject;

public class TransferMapper {

    @Inject
    private MapperFacade mapperFacade;

    public Transfer toDomain(TransferDto dto) {
        return mapperFacade.map(dto, Transfer.class);
    }

    public TransferDto toDto(Transfer domain) {
        return mapperFacade.map(domain, TransferDto.class);
    }

}
