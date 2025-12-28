package org.hotel.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * service definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.62.2)",
    comments = "Source: hotel.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class HotelServiceGrpc {

  private HotelServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "hotel.HotelService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.hotel.grpc.AvailabilityRequest,
      org.hotel.grpc.AvailabilityResponse> getCheckAvailabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckAvailability",
      requestType = org.hotel.grpc.AvailabilityRequest.class,
      responseType = org.hotel.grpc.AvailabilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hotel.grpc.AvailabilityRequest,
      org.hotel.grpc.AvailabilityResponse> getCheckAvailabilityMethod() {
    io.grpc.MethodDescriptor<org.hotel.grpc.AvailabilityRequest, org.hotel.grpc.AvailabilityResponse> getCheckAvailabilityMethod;
    if ((getCheckAvailabilityMethod = HotelServiceGrpc.getCheckAvailabilityMethod) == null) {
      synchronized (HotelServiceGrpc.class) {
        if ((getCheckAvailabilityMethod = HotelServiceGrpc.getCheckAvailabilityMethod) == null) {
          HotelServiceGrpc.getCheckAvailabilityMethod = getCheckAvailabilityMethod =
              io.grpc.MethodDescriptor.<org.hotel.grpc.AvailabilityRequest, org.hotel.grpc.AvailabilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckAvailability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hotel.grpc.AvailabilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hotel.grpc.AvailabilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HotelServiceMethodDescriptorSupplier("CheckAvailability"))
              .build();
        }
      }
    }
    return getCheckAvailabilityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hotel.grpc.ReservationRequest,
      org.hotel.grpc.ReservationResponse> getMakeReservationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "MakeReservation",
      requestType = org.hotel.grpc.ReservationRequest.class,
      responseType = org.hotel.grpc.ReservationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hotel.grpc.ReservationRequest,
      org.hotel.grpc.ReservationResponse> getMakeReservationMethod() {
    io.grpc.MethodDescriptor<org.hotel.grpc.ReservationRequest, org.hotel.grpc.ReservationResponse> getMakeReservationMethod;
    if ((getMakeReservationMethod = HotelServiceGrpc.getMakeReservationMethod) == null) {
      synchronized (HotelServiceGrpc.class) {
        if ((getMakeReservationMethod = HotelServiceGrpc.getMakeReservationMethod) == null) {
          HotelServiceGrpc.getMakeReservationMethod = getMakeReservationMethod =
              io.grpc.MethodDescriptor.<org.hotel.grpc.ReservationRequest, org.hotel.grpc.ReservationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "MakeReservation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hotel.grpc.ReservationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hotel.grpc.ReservationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new HotelServiceMethodDescriptorSupplier("MakeReservation"))
              .build();
        }
      }
    }
    return getMakeReservationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hotel.grpc.Empty,
      org.hotel.grpc.HotelInfo> getGetHotelInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetHotelInfo",
      requestType = org.hotel.grpc.Empty.class,
      responseType = org.hotel.grpc.HotelInfo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hotel.grpc.Empty,
      org.hotel.grpc.HotelInfo> getGetHotelInfoMethod() {
    io.grpc.MethodDescriptor<org.hotel.grpc.Empty, org.hotel.grpc.HotelInfo> getGetHotelInfoMethod;
    if ((getGetHotelInfoMethod = HotelServiceGrpc.getGetHotelInfoMethod) == null) {
      synchronized (HotelServiceGrpc.class) {
        if ((getGetHotelInfoMethod = HotelServiceGrpc.getGetHotelInfoMethod) == null) {
          HotelServiceGrpc.getGetHotelInfoMethod = getGetHotelInfoMethod =
              io.grpc.MethodDescriptor.<org.hotel.grpc.Empty, org.hotel.grpc.HotelInfo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetHotelInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hotel.grpc.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hotel.grpc.HotelInfo.getDefaultInstance()))
              .setSchemaDescriptor(new HotelServiceMethodDescriptorSupplier("GetHotelInfo"))
              .build();
        }
      }
    }
    return getGetHotelInfoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HotelServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HotelServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HotelServiceStub>() {
        @java.lang.Override
        public HotelServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HotelServiceStub(channel, callOptions);
        }
      };
    return HotelServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HotelServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HotelServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HotelServiceBlockingStub>() {
        @java.lang.Override
        public HotelServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HotelServiceBlockingStub(channel, callOptions);
        }
      };
    return HotelServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HotelServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HotelServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HotelServiceFutureStub>() {
        @java.lang.Override
        public HotelServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HotelServiceFutureStub(channel, callOptions);
        }
      };
    return HotelServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * service definition
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Service web 1: Consulter les disponibilités
     * </pre>
     */
    default void checkAvailability(org.hotel.grpc.AvailabilityRequest request,
        io.grpc.stub.StreamObserver<org.hotel.grpc.AvailabilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckAvailabilityMethod(), responseObserver);
    }

    /**
     * <pre>
     * Service web 2: Effectuer une réservation
     * </pre>
     */
    default void makeReservation(org.hotel.grpc.ReservationRequest request,
        io.grpc.stub.StreamObserver<org.hotel.grpc.ReservationResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getMakeReservationMethod(), responseObserver);
    }

    /**
     * <pre>
     * Additional: Get hotel information
     * </pre>
     */
    default void getHotelInfo(org.hotel.grpc.Empty request,
        io.grpc.stub.StreamObserver<org.hotel.grpc.HotelInfo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetHotelInfoMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service HotelService.
   * <pre>
   * service definition
   * </pre>
   */
  public static abstract class HotelServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return HotelServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service HotelService.
   * <pre>
   * service definition
   * </pre>
   */
  public static final class HotelServiceStub
      extends io.grpc.stub.AbstractAsyncStub<HotelServiceStub> {
    private HotelServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HotelServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HotelServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Service web 1: Consulter les disponibilités
     * </pre>
     */
    public void checkAvailability(org.hotel.grpc.AvailabilityRequest request,
        io.grpc.stub.StreamObserver<org.hotel.grpc.AvailabilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Service web 2: Effectuer une réservation
     * </pre>
     */
    public void makeReservation(org.hotel.grpc.ReservationRequest request,
        io.grpc.stub.StreamObserver<org.hotel.grpc.ReservationResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getMakeReservationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Additional: Get hotel information
     * </pre>
     */
    public void getHotelInfo(org.hotel.grpc.Empty request,
        io.grpc.stub.StreamObserver<org.hotel.grpc.HotelInfo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetHotelInfoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service HotelService.
   * <pre>
   * service definition
   * </pre>
   */
  public static final class HotelServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<HotelServiceBlockingStub> {
    private HotelServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HotelServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HotelServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Service web 1: Consulter les disponibilités
     * </pre>
     */
    public org.hotel.grpc.AvailabilityResponse checkAvailability(org.hotel.grpc.AvailabilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckAvailabilityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Service web 2: Effectuer une réservation
     * </pre>
     */
    public org.hotel.grpc.ReservationResponse makeReservation(org.hotel.grpc.ReservationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getMakeReservationMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Additional: Get hotel information
     * </pre>
     */
    public org.hotel.grpc.HotelInfo getHotelInfo(org.hotel.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetHotelInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service HotelService.
   * <pre>
   * service definition
   * </pre>
   */
  public static final class HotelServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<HotelServiceFutureStub> {
    private HotelServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HotelServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HotelServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Service web 1: Consulter les disponibilités
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hotel.grpc.AvailabilityResponse> checkAvailability(
        org.hotel.grpc.AvailabilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Service web 2: Effectuer une réservation
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hotel.grpc.ReservationResponse> makeReservation(
        org.hotel.grpc.ReservationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getMakeReservationMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Additional: Get hotel information
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hotel.grpc.HotelInfo> getHotelInfo(
        org.hotel.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetHotelInfoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_AVAILABILITY = 0;
  private static final int METHODID_MAKE_RESERVATION = 1;
  private static final int METHODID_GET_HOTEL_INFO = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK_AVAILABILITY:
          serviceImpl.checkAvailability((org.hotel.grpc.AvailabilityRequest) request,
              (io.grpc.stub.StreamObserver<org.hotel.grpc.AvailabilityResponse>) responseObserver);
          break;
        case METHODID_MAKE_RESERVATION:
          serviceImpl.makeReservation((org.hotel.grpc.ReservationRequest) request,
              (io.grpc.stub.StreamObserver<org.hotel.grpc.ReservationResponse>) responseObserver);
          break;
        case METHODID_GET_HOTEL_INFO:
          serviceImpl.getHotelInfo((org.hotel.grpc.Empty) request,
              (io.grpc.stub.StreamObserver<org.hotel.grpc.HotelInfo>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getCheckAvailabilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.hotel.grpc.AvailabilityRequest,
              org.hotel.grpc.AvailabilityResponse>(
                service, METHODID_CHECK_AVAILABILITY)))
        .addMethod(
          getMakeReservationMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.hotel.grpc.ReservationRequest,
              org.hotel.grpc.ReservationResponse>(
                service, METHODID_MAKE_RESERVATION)))
        .addMethod(
          getGetHotelInfoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.hotel.grpc.Empty,
              org.hotel.grpc.HotelInfo>(
                service, METHODID_GET_HOTEL_INFO)))
        .build();
  }

  private static abstract class HotelServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    HotelServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.hotel.grpc.Hotel.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("HotelService");
    }
  }

  private static final class HotelServiceFileDescriptorSupplier
      extends HotelServiceBaseDescriptorSupplier {
    HotelServiceFileDescriptorSupplier() {}
  }

  private static final class HotelServiceMethodDescriptorSupplier
      extends HotelServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    HotelServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (HotelServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HotelServiceFileDescriptorSupplier())
              .addMethod(getCheckAvailabilityMethod())
              .addMethod(getMakeReservationMethod())
              .addMethod(getGetHotelInfoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
