package com.be.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDtos {

    public record SignupReq(
            @Schema(example = "kimjh")
            String username,

            @Schema(example = "password123!")
            String password
    ) {}

    public record LoginReq(
            @Schema(example = "kimjh")
            String username,

            @Schema(example = "password123!")
            String password
    ) {}

    public record MeRes(
            @Schema(example = "1")
            Long id,

            @Schema(example = "kimjh")
            String username,

            @Schema(example = "USER")
            String role
    ) {}
}
