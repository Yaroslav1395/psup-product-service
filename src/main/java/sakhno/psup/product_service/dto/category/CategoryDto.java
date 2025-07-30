package sakhno.psup.product_service.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Объект содержит информацию о категории
 */
//TODO: Заменить UserId на объект с id и ФИО
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long createdUserId;
    private Long updatedUserId;
}
