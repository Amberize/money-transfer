package me.amberize.moneytransfer.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Transfer {

    private String id;

    @NotEmpty(message = "Recipient account must be provided")
    private String recipientAccount;

    @NotEmpty(message = "Sender account must be provided")
    private String senderAccount;

    private String sum;

}
