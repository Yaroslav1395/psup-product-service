package sakhno.psup.product_service.models;

//TODO: добавить хранение картинки

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Сущность является категорией продуктов
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("subcategories")
public class SubcategoryEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    @Column("category_id")
    private Long categoryId;
    @Column("created_date")
    private LocalDateTime createdDate;
    @Column("updated_date")
    private LocalDateTime updatedDate;
    @Column("created_user_id")
    private Long createdUserId;
    @Column("updated_user_id")
    private Long updatedUserId;
}
