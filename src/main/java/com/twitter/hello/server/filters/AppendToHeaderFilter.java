package com.twitter.hello.server.filters;

import scala.runtime.AbstractFunction0;

import com.twitter.finagle.Service;
import com.twitter.finagle.SimpleFilter;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.util.Future;

public class AppendToHeaderFilter extends SimpleFilter<Request, Response> {
  private final String headerName;
  private final String headerValue;

  public AppendToHeaderFilter(String header, String value) {
    this.headerName = header;
    this.headerValue = value;
  }

  @Override
  public Future<Response> apply(Request request, Service<Request, Response> service) {

    Object oldValue = request.headerMap().getOrElseUpdate(headerName, new AbstractFunction0<String>() {
        @Override
        public String apply() {
          return "";
        }
      });
    request.headerMap().update(headerName, oldValue + headerValue);
    return service.apply(request);
  }
}
