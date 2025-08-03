package sakhno.psup.product_service.services.subcategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;

public interface SubcategoryService {
    /**
     * Метод позволяет получить подкатегорию по идентификатору
     * @param id - идентификатор 
     * @return - найденная подкатегория
     */
    Mono<SubcategoryDto> getById(Long id);

    /**
     * Метод позволяет получить все подкатегории
     * @return - список подкатегорий
     */
    Flux<SubcategoryDto> getAll();

    /**
     * Метод позволяет получить подкатегории по id категории
     * @param categoryId - идентификатор категории
     * @return - список подкатегорий
     */
    Flux<SubcategoryDto> getBuCategoryId(Long categoryId);
}
