package com.rabo.statementvalidator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatementStatus {
    SUCCESSFUL("SUCCESSFUL"),
    DUPLICATE_REFERENCE("DUPLICATE_REFERENCE"),
    INCORRECT_END_BALANCE("INCORRECT_END_BALANCE"),
    DUPLICATE_REFERENCE_INCORRECT_END_BALANCE(DUPLICATE_REFERENCE.status + "_" + INCORRECT_END_BALANCE.status);

    @Getter
    private String status;
}
