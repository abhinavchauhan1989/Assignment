package com.rabo.statementvalidator.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabo.statementvalidator.StatementFactory;
import com.rabo.statementvalidator.model.CustomerStatementResponse;
import com.rabo.statementvalidator.model.PaymentStatement;
import com.rabo.statementvalidator.model.StatementStatus;

@SpringBootTest
@AutoConfigureMockMvc
class StatementValidatorIntegrationTests {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockStatementValidatorController;

	private ResultMatcher expectedOK = MockMvcResultMatchers.status().isOk();
	private ResultMatcher expectedBadRequest = MockMvcResultMatchers.status().isBadRequest();
	
	@Test
	public void validPaymentStatementsPasses() throws Exception {
		List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);

		String request = objectMapper.writeValueAsString(statements);
		MockHttpServletResponse response = performRequest(request,  expectedOK);

		CustomerStatementResponse customerStatementResponse = objectMapper.readValue(response.getContentAsString(),CustomerStatementResponse.class);
		assertEquals(StatementStatus.SUCCESSFUL.getStatus(), customerStatementResponse.getResult());
		assertTrue(customerStatementResponse.getErrorRecords().isEmpty());
	}

    @Test
    public void emptyStatementThrowsJsonParseException() throws Exception {

    	String request = "[]";

    	MockHttpServletResponse response  = performRequest(request,  expectedBadRequest);

    	CustomerStatementResponse customerStatementResponse = objectMapper.readValue(response.getContentAsString(),CustomerStatementResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST.name(), customerStatementResponse.getResult());
        assertEquals(customerStatementResponse.getErrorRecords().size(), 0);
    }

  @Test
  public void duplicateStatementsThrowsStatementValidationException() throws Exception {
	  List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
	  PaymentStatement duplicateStatement = StatementFactory.getValidPaymentStatement(1);
	  statements.add(duplicateStatement);
	  String request = objectMapper.writeValueAsString(statements);
	  MockHttpServletResponse response  =  performRequest(request,  expectedOK);

	  CustomerStatementResponse customerStatementResponse = objectMapper.readValue(response.getContentAsString(),CustomerStatementResponse.class);

	  assertEquals(StatementStatus.DUPLICATE_REFERENCE.getStatus(), customerStatementResponse.getResult());
	  assertEquals(1, customerStatementResponse.getErrorRecords().size());
	  assertThat(customerStatementResponse.getErrorRecords(), hasItem(hasProperty("reference", is(1L))));
  }

   @Test
   public void invalidBalanceStatementThrowsStatementValidationException() throws Exception{

	   List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
	   PaymentStatement invalidBalanceStatement = StatementFactory.getInvalidStatement(10);
	   statements.add(invalidBalanceStatement);
	   String request = objectMapper.writeValueAsString(statements);
	   MockHttpServletResponse response  = performRequest(request,  expectedOK);

	   CustomerStatementResponse customerStatementResponse = objectMapper.readValue(response.getContentAsString(),CustomerStatementResponse.class);

	   assertEquals(StatementStatus.INCORRECT_END_BALANCE.getStatus(), customerStatementResponse.getResult());
	   assertEquals(1, customerStatementResponse.getErrorRecords().size());
	   assertThat(customerStatementResponse.getErrorRecords(), hasItem(hasProperty("reference", is(10L))));
   }

   	@Test
   	public void invalidBalanceAndDuplicatedStatementThrowsStatementValidationException() throws Exception{

   		List<PaymentStatement> statements = StatementFactory.getValidPaymentStatements(5);
   		PaymentStatement invalidBalanceStatement = StatementFactory.getInvalidStatement(10);
   		statements.add(invalidBalanceStatement);
   		PaymentStatement duplicateStatement = StatementFactory.getValidPaymentStatement(1);
   		statements.add(duplicateStatement);
   		String request = objectMapper.writeValueAsString(statements);
   		MockHttpServletResponse response  =  performRequest(request,  expectedOK);

   		CustomerStatementResponse customerStatementResponse = objectMapper.readValue(response.getContentAsString(),CustomerStatementResponse.class);

   		assertEquals(StatementStatus.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE.getStatus(), customerStatementResponse.getResult());
   		assertEquals(2, customerStatementResponse.getErrorRecords().size());
   		assertThat(customerStatementResponse.getErrorRecords(), hasItem(hasProperty("reference", is(10L))));
   		assertThat(customerStatementResponse.getErrorRecords(), hasItem(hasProperty("reference", is(1L))));
   	}
   	private MockHttpServletResponse performRequest(String request, ResultMatcher expectedResult) throws Exception {
   		return mockStatementValidatorController
				.perform(MockMvcRequestBuilders.post("/customer/payments")
				.content(request).contentType(MediaType.APPLICATION_JSON))
				.andExpect(expectedResult)
				.andReturn().getResponse();
   	}
}
