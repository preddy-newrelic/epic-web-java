package com.twitter.hello.server.services;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.newrelic.api.agent.Trace;

public class PrimeCalc {

	@Trace
	public static List<Integer> sieveOfEratosthenes(int n) {
	    boolean prime[] = new boolean[n + 1];
	    Arrays.fill(prime, true);
	    for (int p = 2; p * p <= n; p++) {
	        if (prime[p]) {
	            for (int i = p * 2; i <= n; i += p) {
	                prime[i] = false;
	            }
	        }
	    }
	    List<Integer> primeNumbers = new LinkedList<>();
	    for (int i = 2; i <= n; i++) {
	        if (prime[i]) {
	            primeNumbers.add(i);
	        }
	    }
	    return primeNumbers;
	}
	
}
