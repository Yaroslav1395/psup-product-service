package sakhno.psup.product_service.services.subcategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.dto.subcategory.SubcategorySaveDto;

public interface SubcategoryService {
    /**
     * Метод позволяет получить подкатегорию по идентификатору
     * @param id - идентификатор 
     * @return - найденная подкатегория
     */
    Mono<SubcategoryDto> getById(Long id);

    /**
     * Метод позволяет получить все подкатегории. Для избежания запросов N + 1 в базу реализованна сложная логика
     * преобразования. Читайте комментарии к методам, использованных в конвейере потока.
     * @return - список подкатегорий
     */
    Flux<SubcategoryDto> getAll();

    /**
     * Метод позволяет получить подкатегории по id категории
     * @param categoryId - идентификатор категории
     * @return - список подкатегорий
     */
    Flux<SubcategoryDto> getByCategoryId(Long categoryId);

    /**
     * Метод позволяет сохранить новую подкатегорию
     * @param subcategorySaveDto - сохраняемая подкатегория
     * @return - сохраненная подкатегория
     */
    Mono<SubcategoryDto> save(SubcategorySaveDto subcategorySaveDto);
}
