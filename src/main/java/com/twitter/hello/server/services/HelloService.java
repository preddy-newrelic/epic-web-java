package com.twitter.hello.server.services;

import java.util.List;

import javax.inject.Inject;

import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.finatra.http.response.ResponseBuilder;

public class HelloService {
	private final ResponseBuilder response;

	@Inject
	public HelloService(ResponseBuilder response) {
		this.response = response;
	}

	/**
	 * returns a String
	 */
	public Response hi(Request request) {
		//System.out.println("calculating hi primes for 50000");
		List<Integer> p = PrimeCalc.sieveOfEratosthenes(50000);
		return response.ok("Hello " + request.getParam("name"));
	}

	/**
	 * returns a GoodbyeResponse
	 */
	public GoodbyeResponse goodbye(Request request) {
		//System.out.println("calculating goodbye primes for 95000");
		PrimeCalc.sieveOfEratosthenes(95000);
		return new GoodbyeResponse("guest", "cya", 123);
	}

	/**
	 * returns an http Response
	 */
	public Response echo(Request request) {
		//System.out.println("calculating echo primes for 999000");
		PrimeCalc.sieveOfEratosthenes(999000);
		return response.ok(request.getParam("q"));
	}

	/**
	 * put, trace, & head should not return response bodies
	 */
	public Response handleAnyMethod(Request request) {
		//System.out.println("calculating any method primes for 29900000");
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(29900000);
		if (request.method().equals(Method.Get()) || request.method().equals(Method.Post())
				|| request.method().equals(Method.Patch()) || request.method().equals(Method.Delete())
				|| request.method().equals(Method.Connect()) || request.method().equals(Method.Options())) {
			String html = "Primes " + primes.toString();
			return response.ok().html(html );
		} else {
			return response.ok();
		}
	}
}
