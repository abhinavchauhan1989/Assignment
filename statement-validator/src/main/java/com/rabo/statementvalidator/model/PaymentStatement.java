package com.rabo.statementvalidator.model;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentStatement {

	@NotNull(message = "Reference should not be null")
    private Long transactionReference;

    @NotBlank(message = "Account number should not be blank")
    private String accountNumber;

    @NotNull(message = "Start balance should not be null")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal startBalance;

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal mutation;

    private String description;

    @NotNull(message = "Enter the end_balance")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal endBalance;

    @Override
    public boolean equals(Object o) {
        boolean isEqual;
        if (this == o) {
            isEqual = true;
        }
        if (o == null || getClass() != o.getClass()) {
            isEqual = false;
        } else {
            PaymentStatement statement = (PaymentStatement) o;
            isEqual = transactionReference.equals(statement.transactionReference);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionReference);
    }
}
