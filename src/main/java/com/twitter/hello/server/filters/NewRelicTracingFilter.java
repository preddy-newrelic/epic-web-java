package com.twitter.hello.server.filters;

import java.util.Collections;
import java.util.Enumeration;

import com.newrelic.api.agent.ExtendedRequest;
import com.newrelic.api.agent.ExtendedResponse;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.twitter.finagle.Service;
import com.twitter.finagle.SimpleFilter;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.util.Future;
import com.twitter.util.Function;
import scala.Function1;
import scala.runtime.BoxedUnit;


public class NewRelicTracingFilter extends SimpleFilter<Request, Response> {

	@Override
	@Trace(dispatcher=true)
	public Future<Response> apply(Request request, Service<Request, Response> service) {
		ExtendedRequest nrRequest = new TracingWebRequest(request);
		Transaction txn = NewRelic.getAgent().getTransaction();
		String path = request.path();
		txn.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "finagle", path);
		txn.setWebRequest(nrRequest);
		
		Future<Response> ret = service.apply(request);	
		Function1<Response, BoxedUnit> onSuccess = new Function<Response, BoxedUnit>() {
			@Override
			public BoxedUnit apply(Response response) {
				ExtendedResponse nrResponse = new NewRelicTracingFilter.TracingWebResponse(response);
				txn.setWebResponse(nrResponse);
				return BoxedUnit.UNIT;
			}
		};
		ret.onSuccess(onSuccess);
		
		Function1<Throwable, BoxedUnit> onFailure = new Function<Throwable, BoxedUnit>() {
			@Override
			public BoxedUnit apply(Throwable response) {
				txn.markResponseSent();
				return null;
			}
		};
		ret.onFailure(onFailure);

		
		return ret;
	}
	
	
	class TracingWebRequest  extends ExtendedRequest {

		private Request request = null;

		public TracingWebRequest(Request request) {
			this.request  = request;
		}

		@Override
		public Object getAttribute(String name) {
			return null;
		}

		@Override
		public String getCookieValue(String name) {
			return null;
		}

		@Override
		public Enumeration getParameterNames() {
			return Collections.enumeration(request.getParamNames());
		}

		@Override
		public String[] getParameterValues(String arg0) {
			return request.getParamNames().toArray(new String[0]);
		}

		@Override
		public String getRemoteUser() {
			return null;
		}

		@Override
		public String getRequestURI() {
			return request.uri();
		}

		@Override
		public String getHeader(String name) {
			return request.headerMap().getOrNull(name);
		}

		@Override
		public HeaderType getHeaderType() {
			return HeaderType.HTTP;
		}

		@Override
		public String getMethod() {
			return request.method().name();
		}
		
	}
	
	public class TracingWebResponse  extends ExtendedResponse {

		private Response response = null;

		public TracingWebResponse(Response response) {
			this.response  = response;
		}

		@Override
		public String getContentType() {
			return response.headerMap().getOrNull("Content-Type");
		}

		@Override
		public int getStatus() throws Exception {
			return response.status().code();
		}

		@Override
		public String getStatusMessage() throws Exception {
			return response.status().reason();
		}

		@Override
		public HeaderType getHeaderType() {
			return HeaderType.HTTP;
		}

		@Override
		public void setHeader(String name, String val) {
			response.headerMap().add(name, val);
		}

		@Override
		public long getContentLength() {
			return response.contentLengthOrElse(0);
		}
		
	
	}

}
