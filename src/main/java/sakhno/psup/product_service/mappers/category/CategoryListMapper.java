package sakhno.psup.product_service.mappers.category;

import org.mapstruct.Mapper;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.models.CategoryEntity;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface CategoryListMapper {

    /**
     * Метод позволяет преобразовать список сущностей категорий в список DTO категорий
     * @param categoryEntities - список сущностей категорий
     * @return - список DTO категорий
     */
    List<CategoryDto> mapCategoryEntityListToDto(List<CategoryEntity> categoryEntities);
}
