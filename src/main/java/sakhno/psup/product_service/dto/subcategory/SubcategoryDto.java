package sakhno.psup.product_service.dto.subcategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sakhno.psup.product_service.dto.category.CategorySimpleDto;

import java.time.LocalDateTime;

/**
 * Объект содержит информацию о подкатегории
 */
//TODO: Заменить UserId на объект с id и ФИО

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoryDto {
    private Long id;
    private String name;
    private String description;
    private CategorySimpleDto category;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long createdUserId;
    private Long updatedUserId;
}
