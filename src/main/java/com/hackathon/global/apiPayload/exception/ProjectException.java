package com.hackathon.global.apiPayload.exception;

import com.hackathon.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class ProjectException extends RuntimeException {
    private final BaseErrorCode code;

    public ProjectException(BaseErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}