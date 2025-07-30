package sakhno.psup.product_service.services.category;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;

public interface CategoryService {

    /**
     * Метод позволяет получить категорию по идентификатору
     * @param id - идентификатор категории
     * @return - DTO категории
     */
    Mono<CategoryDto> getById(Long id);

    /**
     * Метод позволяет получить все категории
     * @return - список категорий
     */
    Flux<CategoryDto> getAll();

    /**
     * Метод позволяет сохранить категорию. В случае если запись с таким же именем существует, выбросит исключение
     * @param categorySaveDto - категория для сохранения
     * @return - сохраненная категория
     */
    Mono<CategoryDto> save(CategorySaveDto categorySaveDto);

    /**
     * Метод позволяет отредактировать категорию. В случае если запись с таким же именем существует, выбросит исключение.
     * В случае если по идентификатору категории не существует, выбросит исключение
     * @param categoryUpdateDto - категория для редактирования
     * @return - отредактированная категория
     */
    Mono<CategoryDto> update(CategoryUpdateDto categoryUpdateDto);

    /**
     * Метод позволяет удалить категорию по идентификатору
     * @param id - идентификатор категории
     * @return - флаг удаления
     */
    Mono<Boolean> deleteById(Long id);
}
