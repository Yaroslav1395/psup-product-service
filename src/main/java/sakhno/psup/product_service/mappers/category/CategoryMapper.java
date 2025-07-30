package sakhno.psup.product_service.mappers.category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdUserId", ignore = true)
    @Mapping(target = "updatedDate", expression = "java(getCurrentDateTime())")
    @Mapping(target = "updatedUserId", expression  = "java(getStaticUserId())")
    CategoryEntity mapCategoryUpdateDtoToEntity(CategoryUpdateDto categoryUpdateDto, @MappingTarget CategoryEntity categoryEntity);

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
