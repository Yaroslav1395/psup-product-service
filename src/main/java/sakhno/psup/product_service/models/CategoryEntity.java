package sakhno.psup.product_service.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

//TODO: добавить хранение картинки

/**
 * Сущность является категорией продуктов
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("categories")
public class CategoryEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    @Column("created_date")
    private LocalDateTime createdDate;
    @Column("updated_date")
    private LocalDateTime updatedDate;
    @Column("created_user_id")
    private Long createdUserId;
    @Column("updated_user_id")
    private Long updatedUserId;
}
