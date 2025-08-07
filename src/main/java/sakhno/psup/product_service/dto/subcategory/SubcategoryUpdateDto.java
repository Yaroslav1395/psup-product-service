package sakhno.psup.product_service.dto.subcategory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект содержит данные для редактирования подкатегории
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoryUpdateDto {
    @NotNull(message = "Идентификатор категории не может быть пустым")
    @Positive(message = "Идентификатор категории должен быть положительным")
    private Long id;

    @NotEmpty(message = "Имя подкатегории не может быть пустым")
    @Size(min = 5, max = 256, message = "Имя подкатегории должно содержать от 5 до 256 символов")
    private String name;

    @NotEmpty(message = "Описание подкатегории не может быть пустым")
    @Size(min = 5, max = 2000, message = "Описание подкатегории должно содержать от 5 до 2000 символов")
    private String description;

    @Positive(message = "Идентификатор категории должен быть положительным")
    @NotNull(message = "Идентификатор категории не может быть пустым")
    private Long categoryId;
}
