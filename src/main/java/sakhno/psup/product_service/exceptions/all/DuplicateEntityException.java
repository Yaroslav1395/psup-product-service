package sakhno.psup.product_service.exceptions.all;

import lombok.Getter;

@Getter
public class DuplicateEntityException extends RuntimeException {
    private final String message;

    public DuplicateEntityException(String message) {
        super(message);
        this.message = message;
    }
}
