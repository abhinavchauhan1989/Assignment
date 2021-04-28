package com.rabo.statementvalidator.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabo.statementvalidator.model.PaymentStatement;
import com.rabo.statementvalidator.service.StatementValidatorService;

@RestController
@RequestMapping("/customer")
@Validated
public class StatementValidatorController {

    private StatementValidatorService statementValidatorService;
    
    public StatementValidatorController(StatementValidatorService statementValidatorService) {
    	this.statementValidatorService = statementValidatorService;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> validateStatements(@RequestBody @Valid List<PaymentStatement> statements){
        return ResponseEntity.ok(statementValidatorService.validateStatements(statements));
    }

    @GetMapping("/")
    public List<PaymentStatement> getPayment(){
        List<PaymentStatement> statements = new ArrayList<>();
        statements.add(new PaymentStatement (12312312312L,"INGBNL12312",new BigDecimal(122311),new BigDecimal(122311),"any",
        								new BigDecimal(0)));
        
        return statements;
    }

}
