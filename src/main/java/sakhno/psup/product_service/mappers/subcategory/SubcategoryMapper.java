package sakhno.psup.product_service.mappers.subcategory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.mappers.category.CategoryMapper;
import sakhno.psup.product_service.models.CategoryEntity;
import sakhno.psup.product_service.models.SubcategoryEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface SubcategoryMapper {

    @Mapping(target = "id", source = "subcategory.id")
    @Mapping(target = "name", source = "subcategory.name")
    @Mapping(target = "description", source = "subcategory.description")
    @Mapping(target = "createdDate", source = "subcategory.createdDate")
    @Mapping(target = "updatedDate", source = "subcategory.updatedDate")
    @Mapping(target = "createdUserId", source = "subcategory.createdUserId")
    @Mapping(target = "updatedUserId", source = "subcategory.updatedUserId")
    @Mapping(target = "category", source = "category")
    SubcategoryDto mapToSubCategoryDto(SubcategoryEntity subcategory, CategoryEntity category);
}
