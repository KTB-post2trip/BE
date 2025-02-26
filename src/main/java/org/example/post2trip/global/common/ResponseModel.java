package org.example.post2trip.global.common;


import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseModel<T> {

    private HttpStatus status;
    private T data;

    public static <T> ResponseModel<T> success(T data) {
        return new ResponseModel<>(HttpStatus.OK, data);
    }

    public static <T> ResponseModel<T> success(HttpStatus status, T data) {
        return new ResponseModel<>(status, data);
    }
}