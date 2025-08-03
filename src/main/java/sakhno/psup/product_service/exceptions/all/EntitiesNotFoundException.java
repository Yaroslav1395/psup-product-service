package sakhno.psup.product_service.exceptions.all;

import lombok.Getter;

@Getter
public class EntitiesNotFoundException extends RuntimeException {
    private final String message;

    public EntitiesNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
