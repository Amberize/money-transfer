package me.amberize.moneytransfer.dto;

import lombok.Data;


@Data
public class TransferDto {

    private String recipientAccount;

    private String senderAccount;

    private String sum;

}
