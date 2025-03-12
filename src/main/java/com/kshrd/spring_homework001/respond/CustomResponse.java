package com.kshrd.spring_homework001.respond;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse<T> {
    private Boolean success;
    private String message;
    private HttpStatus status; // Use HttpStatus instead of String
    private T payload;
    private LocalDateTime timestamp = LocalDateTime.now();

    public CustomResponse(Boolean success, String message, HttpStatus status, T payload) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }
}
