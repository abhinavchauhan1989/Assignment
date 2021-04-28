package com.rabo.statementvalidator.exception;

import java.util.List;

import com.rabo.statementvalidator.model.PaymentStatement;
import com.rabo.statementvalidator.model.StatementStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StatementValidationException extends RuntimeException{

	private static final long serialVersionUID = -7937250798101429624L;
	
	private StatementStatus statementStatus;
    private List<PaymentStatement> statements;
}
