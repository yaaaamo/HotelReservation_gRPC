package com.example.agenceservice.auth;


import io.grpc.*;

public class AuthInterceptor implements ClientInterceptor {

  private final String agencyId;
  private final String password;

  public static final Metadata.Key<String> AGENCY_ID_KEY =
          Metadata.Key.of("agency-id", Metadata.ASCII_STRING_MARSHALLER);
  public static final Metadata.Key<String> PASSWORD_KEY =
          Metadata.Key.of("agency-password", Metadata.ASCII_STRING_MARSHALLER);

  public AuthInterceptor(String agencyId, String password) {
    this.agencyId = agencyId;
    this.password = password;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
          MethodDescriptor<ReqT, RespT> method,
          CallOptions callOptions,
          Channel next) {

    return new ForwardingClientCall.SimpleForwardingClientCall<>(
            next.newCall(method, callOptions)) {

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        headers.put(AGENCY_ID_KEY, agencyId);
        headers.put(PASSWORD_KEY, password);
        super.start(responseListener, headers);
      }
    };
  }
}
