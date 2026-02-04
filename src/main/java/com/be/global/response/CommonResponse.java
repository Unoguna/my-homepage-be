package com.be.global.response;

import com.be.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

    @Schema(example = "true")
    private boolean success;

    @Schema(example = "SUCCESS")
    private String code;

    @Schema(example = "OK")
    private String message;

    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, "SUCCESS", "OK", data);
    }

    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return new CommonResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }
}
