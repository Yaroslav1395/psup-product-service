package sakhno.psup.product_service.dto;

import lombok.Getter;

@Getter
public enum ResponseState {
    SUCCESS("Успешно"),
    INFO("Информационная"),
    FAIL("Неуспешно"),
    ERROR("Произошла ошибка");

    private final String description;

    ResponseState(String description) {
        this.description = description;
    }
}
