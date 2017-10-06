package me.amberize.moneytransfer.domain;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class Account {

    @NotBlank(message = "Holder name must be provided")
    private String holderName;

    @NotBlank(message = "Account number must be provided")
    private String accountNumber;

    @Min(value = 0L, message = "Amount should be positive or zero")
    private BigDecimal amount;

    @QuerySqlField
    private boolean deleted;

    public Account() {
        amount = BigDecimal.ZERO;
        deleted = false;
    }

}
