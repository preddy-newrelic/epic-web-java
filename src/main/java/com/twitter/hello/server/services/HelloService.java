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
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(599000);
		return response.ok("Hello " + request.getParam("name"));
	}

	/**
	 * returns a GoodbyeResponse
	 */
	public GoodbyeResponse goodbye(Request request) {
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(499000);
		return new GoodbyeResponse("guest", "cya", 123);
	}

	/**
	 * returns an http Response
	 */
	public Response echo(Request request) {
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(399000);
		return response.ok(request.getParam("q"));
	}

	/**
	 * put, trace, & head should not return response bodies
	 */
	public Response handleAnyMethod(Request request) {
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(999000);
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
