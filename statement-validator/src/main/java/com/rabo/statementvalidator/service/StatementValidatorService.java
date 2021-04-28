package com.rabo.statementvalidator.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.rabo.statementvalidator.exception.JsonParseException;
import com.rabo.statementvalidator.exception.StatementValidationException;
import com.rabo.statementvalidator.model.CustomerStatementResponse;
import com.rabo.statementvalidator.model.PaymentStatement;
import com.rabo.statementvalidator.model.StatementStatus;

import lombok.SneakyThrows;

@Service
public class StatementValidatorService {

	@SneakyThrows
	public CustomerStatementResponse validateStatements(@Valid List<PaymentStatement> statements) {
		validateNullOrEmptyStatements(statements);
		Set<PaymentStatement> duplicateStatements =getDuplicateTransactionStatements(statements);
		Set<PaymentStatement> invalidBalanceStatements = getInvalidBalanceStatements(statements);
		checkValidations(duplicateStatements,invalidBalanceStatements);
		return new CustomerStatementResponse(StatementStatus.SUCCESSFUL.getStatus(),new ArrayList<>());
	}


	private Set<PaymentStatement> getInvalidBalanceStatements(List<PaymentStatement> statements) {
		Set<PaymentStatement> invalidBalanceStatements = statements.stream()
													.filter(statement-> isBalanceInvalid(statement))
													.collect(Collectors.toSet());

		return invalidBalanceStatements;
	}


	private boolean isBalanceInvalid(PaymentStatement statement) {
		return !statement.getStartBalance()
				.add(statement.getMutation())
				.equals(statement.getEndBalance());
	}


	private Set<PaymentStatement> getDuplicateTransactionStatements(List<PaymentStatement> statements) {
		Set<PaymentStatement> duplicateStatements = new HashSet<>();
		Map<PaymentStatement,Long> statementCountMap = statements.stream()
				.collect(
						Collectors.groupingBy(
								Function.identity(),Collectors.counting())
						);
		duplicateStatements = statementCountMap.entrySet().stream()
															.filter(statement -> statement.getValue()>1)
															.map(statement -> statement.getKey())
															.collect(Collectors.toSet());
		return duplicateStatements;
	}

	private void checkValidations(Set<PaymentStatement> duplicateStatements, Set<PaymentStatement> invalidBalanceStatements) throws StatementValidationException {

		List<PaymentStatement> errorStatements = getUniqueErrorStatements(duplicateStatements,invalidBalanceStatements);
		if(!duplicateStatements.isEmpty() && !invalidBalanceStatements.isEmpty()){
			throw new StatementValidationException(StatementStatus.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE, errorStatements);
		}
		else if(!duplicateStatements.isEmpty()){
			throw new StatementValidationException(StatementStatus.DUPLICATE_REFERENCE, errorStatements);
		}
		else if(!invalidBalanceStatements.isEmpty()){
			throw new StatementValidationException(StatementStatus.INCORRECT_END_BALANCE, errorStatements);

		}

	}


	private List<PaymentStatement> getUniqueErrorStatements(Set<PaymentStatement> duplicateStatements,
			Set<PaymentStatement> invalidBalanceStatements) {
		Set<PaymentStatement> errorStatements = new HashSet<>();
		errorStatements.addAll(duplicateStatements);
		errorStatements.addAll(invalidBalanceStatements);

		return  new ArrayList<>(errorStatements);
	}

	private void validateNullOrEmptyStatements(List<PaymentStatement> statements) throws JsonParseException {
		if (statements == null || statements.isEmpty()) {
			throw new JsonParseException("Null or empty data sent");
		}
	}    


}
