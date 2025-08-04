package sakhno.psup.product_service.dto.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект необходим для сохранения категории
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySaveDto {
    @NotEmpty(message = "Название категории не может быть пустым")
    @Size(min = 5, max = 256, message = "Название категории должно содержать от 5 до 256 символов")
    private String name;
    @NotEmpty(message = "Описание категории не может быть пустым")
    @Size(min = 5, max = 2000, message = "Описание категории должно содержать от 5 до 2000 символов")
    private String description;
}
