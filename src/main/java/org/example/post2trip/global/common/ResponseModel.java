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
    // 🔹 새 메서드 (사용자가 HTTP 상태 코드를 지정 가능)
    // 🔹 기존 메서드 (BAD_REQUEST 고정)
    public static <T> ResponseModel<T> error(T data) {
        return new ResponseModel<>(HttpStatus.BAD_REQUEST, data);
    }

    // 🔹 새 메서드 (사용자가 HTTP 상태 코드를 지정 가능)
    public static <T> ResponseModel<T> error(HttpStatus status, T data) {
        return new ResponseModel<>(status, data);
    }


    // 🔹 data가 필요 없는 경우 (null 대응)
    public static ResponseModel<Void> error(HttpStatus status) {
        return new ResponseModel<>(status, null);
    }
}