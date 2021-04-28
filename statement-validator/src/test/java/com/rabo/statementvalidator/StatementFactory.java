package com.rabo.statementvalidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabo.statementvalidator.model.PaymentStatement;

public class StatementFactory {

    public static List<PaymentStatement> getValidPaymentStatements(int numberOfValidStatements) {
        return IntStream.range(1, numberOfValidStatements + 1)
        		.mapToObj(StatementFactory::getValidPaymentStatement)
        		.collect(Collectors.toList());
    }

    public static PaymentStatement getValidPaymentStatement(int reference) {
        BigDecimal startBalance = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble() * 100).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal mutation = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble() * 100).setScale(2, RoundingMode.HALF_EVEN);
        boolean operation = ThreadLocalRandom.current().nextBoolean();
        if (!operation)
            mutation = mutation.multiply(BigDecimal.valueOf(-1)).setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal endBalance = startBalance.add(mutation);

        return new PaymentStatement((long) reference,"NLXXXXVALIDACCOUNT",mutation,startBalance,"description",endBalance);
    }
    
    public static PaymentStatement getInvalidStatement(int reference) {
    	return new PaymentStatement((long) reference, "NLXXXINVALIDBALANCEACCOUNT", new BigDecimal(10), new BigDecimal(10), "Invalid end balance", new BigDecimal(10));
    }
}
