package sakhno.psup.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto <T> {
    private T data;
    private String message;
    private ResponseState state;

    public static <T> ResponseDto<T> ok(T data) {
        return ResponseDto.<T>builder()
                .data(data)
                .state(ResponseState.SUCCESS)
                .message("OK")
                .build();
    }

    public static <T> ResponseDto<T> empty(String message) {
        return ResponseDto.<T>builder()
                .data(null)
                .state(ResponseState.FAIL)
                .message(message)
                .build();
    }

    public static <T> ResponseDto<T> validation(String message) {
        return ResponseDto.<T>builder()
                .data(null)
                .state(ResponseState.FAIL)
                .message(message)
                .build();

    }

    public static <T> ResponseDto<T> error(String message) {
        return ResponseDto.<T>builder()
                .data(null)
                .state(ResponseState.ERROR)
                .message(message)
                .build();
    }

    public static <T> ResponseDto<T> fail(String message) {
        return ResponseDto.<T>builder()
                .data(null)
                .state(ResponseState.FAIL)
                .message(message)
                .build();
    }
}
