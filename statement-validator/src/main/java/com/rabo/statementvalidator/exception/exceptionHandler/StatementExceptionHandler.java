package com.rabo.statementvalidator.exception.exceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rabo.statementvalidator.exception.JsonParseException;
import com.rabo.statementvalidator.exception.StatementValidationException;
import com.rabo.statementvalidator.model.CustomerStatementResponse;
import com.rabo.statementvalidator.model.PaymentStatement;
import com.rabo.statementvalidator.model.StatementError;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StatementExceptionHandler {

    @ExceptionHandler(StatementValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    public CustomerStatementResponse handleBusinessValidationExceptions(StatementValidationException statementValidationException){
        log.error("Invalid statement record",statementValidationException);
        List<StatementError> statementErrors = statementValidationException.getStatements()
                                               .stream().map(statementError-> getStatementErrorObject(statementError))
                                               .collect(Collectors.toList());
        return new CustomerStatementResponse(statementValidationException.getStatementStatus().getStatus(), statementErrors);
    }

    private StatementError getStatementErrorObject(PaymentStatement statementError) {
        return new StatementError(statementError.getTransactionReference(),statementError.getAccountNumber());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomerStatementResponse handleGenericException(Exception exception){
        log.error("An exception occurred",exception);
        return new CustomerStatementResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(),new ArrayList<>());
    }

    @ExceptionHandler({ConstraintViolationException.class,HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomerStatementResponse handleValidationConstraintsException(Exception exception){
        log.error("Validation Constraint exception occurred",exception);
        return new CustomerStatementResponse(HttpStatus.BAD_REQUEST.name(),new ArrayList<>());
    }
    
    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomerStatementResponse handleJsonParseException(Exception exception){
        log.error("Json Parse Exception occurred",exception);
        return new CustomerStatementResponse(HttpStatus.BAD_REQUEST.name(),new ArrayList<>());
    }
    	
}
