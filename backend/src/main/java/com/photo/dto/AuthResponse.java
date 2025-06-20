package com.photo.dto;

public record AuthResponse(
    String token,
    String username
) {} 