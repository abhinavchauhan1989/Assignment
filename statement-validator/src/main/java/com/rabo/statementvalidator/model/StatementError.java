package com.rabo.statementvalidator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StatementError {
    private Long reference;
    private String accountNumber;
}
