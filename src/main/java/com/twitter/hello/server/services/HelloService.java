package com.twitter.hello.server.services;

import java.util.List;

import javax.inject.Inject;

import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.finatra.http.response.ResponseBuilder;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

public class HelloService {
	private final ResponseBuilder response;
	private StatefulRedisConnection<String, String> connection = null;

	@Inject
	public HelloService(ResponseBuilder response) {
		this.response = response;
	}

	/**
	 * returns a String
	 */
	public Response hi(Request request) {
		commitToDatabase();
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(599000);
		return response.ok("Hello " + request.getParam("name"));
	}

	/**
	 * returns a GoodbyeResponse
	 */
	public GoodbyeResponse goodbye(Request request) {
		commitToDatabase();
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(499000);
		return new GoodbyeResponse("guest", "cya", 123);
	}

	/**
	 * returns an http Response
	 */
	public Response echo(Request request) {
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(399000);
		commitToDatabase();
		return response.ok(request.getParam("q"));
	}

	/**
	 * put, trace, & head should not return response bodies
	 */
	public Response handleAnyMethod(Request request) {
		List<Integer> primes = PrimeCalc.sieveOfEratosthenes(999000);
		commitToDatabase();
		if (request.method().equals(Method.Get()) || request.method().equals(Method.Post())
				|| request.method().equals(Method.Patch()) || request.method().equals(Method.Delete())
				|| request.method().equals(Method.Connect()) || request.method().equals(Method.Options())) {
			String html = "Primes " + primes.toString();
			return response.ok().html(html );
		} else {
			return response.ok();
		}
	}

	private void initDatabase() {
		try {
		RedisURI redisUri = RedisURI.Builder
		  .redis("52.25.228.193", 6379).withPassword("Brespo585")
		  .withDatabase(1).build();
		
		RedisClient redisClient = RedisClient
				  .create(redisUri);
		connection  = redisClient.connect();
		} catch (Throwable e) {
			e.printStackTrace();
			connection = null;
		}
	}
	
	private void commitToDatabase() {
		if (connection == null) {
			initDatabase();
		} else {
			for (int i = 0 ; i < 100 ; i++) {
				RedisCommands<String, String> synCommand = connection.sync();
				synCommand.set("foo"+i, "bar"+i);
				
				RedisAsyncCommands<String, String> asyncCommands = connection.async();
				RedisFuture<String> future = asyncCommands.get("foo"+i);
				future.thenRun(new Runnable() {
				    @Override
				    public void run() {
				        try {
				            System.out.println("Got value: " + future.get());
				        } catch (Exception e) {
				            e.printStackTrace();
				        }

				    }
				});
			}
		}
		
	}
}
