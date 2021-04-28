package com.rabo.statementvalidator.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rabo.statementvalidator.StatementFactory;
import com.rabo.statementvalidator.exception.JsonParseException;
import com.rabo.statementvalidator.exception.StatementValidationException;
import com.rabo.statementvalidator.model.CustomerStatementResponse;
import com.rabo.statementvalidator.model.PaymentStatement;
import com.rabo.statementvalidator.model.StatementStatus;

class ValidationStatementServiceTest {

    private StatementValidatorService statementValidatorService;

    @BeforeEach
    private void initTest(){
    	statementValidatorService = new StatementValidatorService();
    }

    @Test
    public void nullStatementThrowsJsonParseException() {
        Assertions.assertThrows(JsonParseException.class, () -> statementValidatorService.validateStatements(null), "Statement without data");
    }

    @Test
    public void emptyStatementThrowsJsonParseException() {
        List<PaymentStatement> statements = new ArrayList<>();
        Assertions.assertThrows(JsonParseException.class, () -> statementValidatorService.validateStatements(statements), "Statement without data");
    }

    @Test
    public void duplicateStatementsThrowsStatementValidationException() {
    	List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
    	PaymentStatement duplicateStatement = StatementFactory.getValidPaymentStatement(1);
        statements.add(duplicateStatement);

        StatementValidationException exception = assertThrows(StatementValidationException.class, () -> statementValidatorService.validateStatements(statements));
        assertEquals(StatementStatus.DUPLICATE_REFERENCE, exception.getStatementStatus());
        assertEquals(1, exception.getStatements().size());
        assertThat(exception.getStatements(), hasItem(hasProperty("transactionReference", is(1L))));
    }

    @Test
    public void invalidBalanceStatementThrowsStatementValidationException() {
    	List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
    	PaymentStatement invalidBalanceStatement = StatementFactory.getInvalidStatement(10);
        statements.add(invalidBalanceStatement);

        StatementValidationException exception = assertThrows(StatementValidationException.class, () -> statementValidatorService.validateStatements(statements));
        assertEquals(StatementStatus.INCORRECT_END_BALANCE, exception.getStatementStatus());
        assertEquals(1, exception.getStatements().size());
        assertThat(exception.getStatements(), hasItem(hasProperty("transactionReference", is(10L))));
    }

    @Test
    public void invalidBalanceAndDuplicatedStatementThrowsStatementValidationException() {
    	List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
    	PaymentStatement invalidBalanceStatement = StatementFactory.getInvalidStatement(10);
        statements.add(invalidBalanceStatement);
        PaymentStatement duplicateStatement = StatementFactory.getValidPaymentStatement(1);
        statements.add(duplicateStatement);

        StatementValidationException exception = assertThrows(StatementValidationException.class, () -> statementValidatorService.validateStatements(statements));
        assertEquals(StatementStatus.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE, exception.getStatementStatus());
        assertEquals(2, exception.getStatements().size());
        assertThat(exception.getStatements(), hasItem(hasProperty("transactionReference", is(1L))));
        assertThat(exception.getStatements(), hasItem(hasProperty("transactionReference", is(10L))));
    }

    @Test
    public void validStatementsPasses() {
    	List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
        CustomerStatementResponse response = statementValidatorService.validateStatements(statements);
        assertEquals(StatementStatus.SUCCESSFUL.getStatus(), response.getResult());
        assertTrue(response.getErrorRecords().isEmpty());
    }
}