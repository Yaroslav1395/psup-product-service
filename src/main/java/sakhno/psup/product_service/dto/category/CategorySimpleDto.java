package sakhno.psup.product_service.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект содержит информацию о категории. Облегченная версия для объекта "Подкатегории"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySimpleDto {
    private Long id;
    private String name;
}
