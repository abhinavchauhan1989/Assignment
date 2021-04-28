package com.rabo.statementvalidator.model;

import java.util.Collections;
import java.util.List;

import lombok.Getter;


@Getter
public class CustomerStatementResponse {
    private final String result;
    private final List<StatementError> errorRecords;
    
    public CustomerStatementResponse(String result, List<StatementError> errorRecords) {
    	this.result = result;
        this.errorRecords = errorRecords;
    }
    
    public CustomerStatementResponse(String result) {
        this.result = result;
        this.errorRecords = Collections.emptyList();
    }
    public CustomerStatementResponse() {
        this.result = "";
        this.errorRecords = Collections.emptyList();
    }

    
}
