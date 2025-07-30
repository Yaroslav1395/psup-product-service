package sakhno.psup.product_service.exceptions.all;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final String message;

    public EntityNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
