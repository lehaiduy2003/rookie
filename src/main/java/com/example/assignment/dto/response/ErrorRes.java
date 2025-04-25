package com.example.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorRes {
    private String error;
    private String cause;
    private String message;
}
