package com.example.agenceservice.services;


public enum HotelStatus {
  ONLINE,
  OFFLINE,       // UNAVAILABLE / connection refused vs.
  TIMEOUT,       // DEADLINE_EXCEEDED
  AUTH_FAILED,   // UNAUTHENTICATED
  ERROR          // other gRPC errors
}

