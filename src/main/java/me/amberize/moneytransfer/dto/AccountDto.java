package me.amberize.moneytransfer.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccountDto {

    private String holderName;

    private String accountNumber;

    private String amount;

}
