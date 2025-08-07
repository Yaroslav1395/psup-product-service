package sakhno.psup.product_service.mappers.category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategorySimpleDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.models.CategoryEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Метод преобразует категорию из сущности в DTO
     * @param categoryEntity - сущность категории
     * @return - DTO категории
     */
    CategoryDto mapCategoryEntityToDto(CategoryEntity categoryEntity);

    /**
     * Метод преобразует DTO категории для сохранения в сущность
     * @param categorySaveDto - категория для сохранения
     * @return - сущность категории
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedUserId", ignore = true)
    @Mapping(target = "createdUserId", expression  = "java(getStaticUserId())")
    @Mapping(target = "createdDate", expression = "java(getCurrentDateTime())")
    CategoryEntity mapCategorySaveDtoToEntity(CategorySaveDto categorySaveDto);

    /**
     * Метод редактирует сущность на основе DTO
     * @param categoryUpdateDto - объект с данными для редактирования
     * @param categoryEntity    - редактируемая сущность
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdUserId", ignore = true)
    @Mapping(target = "updatedDate", expression = "java(getCurrentDateTime())")
    @Mapping(target = "updatedUserId", expression  = "java(getStaticUserId())")
    void updateCategoryEntity(CategoryUpdateDto categoryUpdateDto, @MappingTarget CategoryEntity categoryEntity);

    /**
     * Метод преобразует сущность категорию в облегченную DTO
     * @param category - сущность категории
     * @return - облегченная DTO категории
     */
    CategorySimpleDto mapToCategorySimpleDto(CategoryEntity category);

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
