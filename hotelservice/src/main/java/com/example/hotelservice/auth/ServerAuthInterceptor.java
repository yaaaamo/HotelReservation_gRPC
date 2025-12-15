package com.example.hotelservice.auth;

import com.example.hotelservice.repository.AgenceRepository;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class ServerAuthInterceptor implements ServerInterceptor {

  // Header keys (metadata)
  public static final Metadata.Key<String> AGENCY_ID_KEY =
          Metadata.Key.of("agency-id", Metadata.ASCII_STRING_MARSHALLER);

  public static final Metadata.Key<String> PASSWORD_KEY =
          Metadata.Key.of("agency-password", Metadata.ASCII_STRING_MARSHALLER);

  // Context key
  public static final Context.Key<String> AGENCY_ID_CTX = Context.key("agency-id");

  private final AgenceRepository agenceRepository;

  public ServerAuthInterceptor(AgenceRepository agenceRepository) {
    this.agenceRepository = agenceRepository;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, RespT> call,
          Metadata headers,
          ServerCallHandler<ReqT, RespT> next) {

    String agencyId = headers.get(AGENCY_ID_KEY);
    String password = headers.get(PASSWORD_KEY);

    if (!isValid(agencyId, password)) {
      call.close(Status.UNAUTHENTICATED.withDescription("Invalid credentials"), new Metadata());
      return new ServerCall.Listener<ReqT>() {};
    }

    Context ctx = Context.current().withValue(AGENCY_ID_CTX, agencyId);
    return Contexts.interceptCall(ctx, call, headers, next);
  }

  private boolean isValid(String agencyId, String password) {
    if (agencyId == null || agencyId.isBlank() || password == null || password.isBlank()) {
      return false;
    }
    return agenceRepository.findById(agencyId)
            .filter(a -> a.validateCredentials(agencyId, password))
            .isPresent();
  }
}
