package sakhno.psup.product_service.mappers.subcategory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.dto.subcategory.SubcategorySaveDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryUpdateDto;
import sakhno.psup.product_service.mappers.category.CategoryMapper;
import sakhno.psup.product_service.models.CategoryEntity;
import sakhno.psup.product_service.models.SubcategoryEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface SubcategoryMapper {

    /**
     * Метод позволяет преобразовать сущность подкатегорию в DTO
     * @param subcategory - сущность подкатегории
     * @param category - сущность категории
     * @return - DTO подкатегории
     */
    @Mapping(target = "id", source = "subcategory.id")
    @Mapping(target = "name", source = "subcategory.name")
    @Mapping(target = "description", source = "subcategory.description")
    @Mapping(target = "createdDate", source = "subcategory.createdDate")
    @Mapping(target = "updatedDate", source = "subcategory.updatedDate")
    @Mapping(target = "createdUserId", source = "subcategory.createdUserId")
    @Mapping(target = "updatedUserId", source = "subcategory.updatedUserId")
    @Mapping(target = "category", source = "category")
    SubcategoryDto mapToSubCategoryDto(SubcategoryEntity subcategory, CategoryEntity category);

    /**
     * Метод позволяет преобразовать DTO подкатегории в сущность для сохранения
     * @param subcategorySaveDto - DTO подкатегории
     * @param categoryId - идентификатор категории
     * @return - сущность подкатегории
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedUserId", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdDate", expression = "java(getCurrentDateTime())")
    @Mapping(target = "createdUserId", expression  = "java(getStaticUserId())")
    SubcategoryEntity mapToSubCategoryEntity(SubcategorySaveDto subcategorySaveDto, Long categoryId);

    /**
     * Метод позволяет отредактировать сущность подкатегории на основе DTO
     * @param subcategoryEntity - сущность подкатегории
     * @param subcategoryUpdateDto - DTO подкатегории для редактирования
     * @param categoryId - идентификатор категории
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedUserId", expression = "java(getStaticUserId())")
    @Mapping(target = "updatedDate", expression = "java(getCurrentDateTime())")
    @Mapping(target = "createdUserId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "categoryId", source = "categoryId")
    void updateSubcategoryEntity(@MappingTarget SubcategoryEntity subcategoryEntity, SubcategoryUpdateDto subcategoryUpdateDto, Long categoryId);

    //TODO: удалить после интеграции с пользовательским сервисом
    @Named("getStaticUserId")
    default Long getStaticUserId() {
        return 1L;
    }

    /**
     * Метод возвращает текущее время. Используется при сохранении новой категории
     * @return - текущее время
     */
    default LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now();
    }
}
