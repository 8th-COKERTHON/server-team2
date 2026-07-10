package com.hackathon.global.apiPayload.exception;

import com.hackathon.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProjectException extends RuntimeException {
    private final BaseErrorCode code;
}
