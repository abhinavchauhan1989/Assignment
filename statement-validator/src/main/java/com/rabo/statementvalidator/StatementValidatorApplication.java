package com.rabo.statementvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class StatementValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatementValidatorApplication.class, args);
	}

}
