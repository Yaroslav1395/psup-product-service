package sakhno.psup.product_service.services.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.exceptions.all.DuplicateEntityException;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;
import sakhno.psup.product_service.mappers.category.CategoryMapper;
import sakhno.psup.product_service.repositories.CategoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Метод позволяет получить категорию по идентификатору
     * @param id - идентификатор категории
     * @return - DTO категории
     */
    @Override
    public Mono<CategoryDto> getById(Long id) {
        return categoryRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("Поиск категории по идентификатору: {}", id))
                .doOnRequest(category -> log.info("Преобразование сущности категории в DTO"))
                .map(categoryMapper::mapCategoryEntityToDto)
                .doOnSuccess(category -> log.info("Преобразование сущности категории в DTO завершено"));
    }

    /**
     * Метод позволяет получить все категории
     * @return - список категорий
     */
    @Override
    public Flux<CategoryDto> getAll() {
        return categoryRepository.findAll()
                .doOnSubscribe(subscription -> log.info("Поиск всех категорий продукции"))
                .doOnRequest(categories -> log.info("Преобразование списка сущностей категорий в DTO"))
                .map(categoryMapper::mapCategoryEntityToDto)
                .doOnComplete(() -> log.info("Преобразование списка сущностей категорий в DTO завершено"));
    }

    /**
     * Метод позволяет сохранить категорию.  В случае если запись с таким же именем существует, выбросит исключение.
     * @param categorySaveDto - категория для сохранения
     * @return - сохраненная категория
     */
    @Override
    public Mono<CategoryDto> save(CategorySaveDto categorySaveDto) {
        return categoryRepository.findByName(categorySaveDto.getName())
                .flatMap(existing -> Mono.<CategoryDto>error(new DuplicateEntityException(
                        "Категория с именем: " + categorySaveDto.getName() + " уже существует")))
                .switchIfEmpty(Mono.just(categorySaveDto)
                        .doOnSubscribe(subscription -> log.info("Сохранение новой категории"))
                        .doOnRequest(r -> log.info("Преобразование DTO категории в сущность"))
                        .map(categoryMapper::mapCategorySaveDtoToEntity)
                        .doOnNext(entity -> log.info("Запрос на сохранение"))
                        .flatMap(categoryRepository::save)
                        .doOnNext(saved -> log.info("Категория сохранена с ID: {}", saved.getId()))
                        .map(categoryMapper::mapCategoryEntityToDto)
                        .doOnSuccess(dto -> log.info("Преобразование сущности в DTO завершено"))
                );
    }

    /**
     * Метод позволяет отредактировать категорию. В случае если запись с таким же именем существует, выбросит исключение.
     * В случае если по идентификатору категории не существует, выбросит исключение
     * @param categoryUpdateDto - категория для редактирования
     * @return - отредактированная категория
     */
    @Override
    public Mono<CategoryDto> update(CategoryUpdateDto categoryUpdateDto) {
        return categoryRepository.findByNameAndIdNot(categoryUpdateDto.getName(), categoryUpdateDto.getId())
                .flatMap(duplicate -> Mono.<CategoryDto>error(new DuplicateEntityException(
                        "Категория с таким названием уже существует: " + categoryUpdateDto.getName())))
                .switchIfEmpty(categoryRepository.findById(categoryUpdateDto.getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException(
                                "Категория не найдена по id: " + categoryUpdateDto.getId())))
                        .flatMap(existingCategory -> {
                            log.info("Категория найдена. Производится обновление");
                            categoryMapper.mapCategoryUpdateDtoToEntity(categoryUpdateDto, existingCategory);
                            return categoryRepository.save(existingCategory);
                        })
                        .doOnNext(saved -> log.info("Категория отредактирована"))
                        .map(categoryMapper::mapCategoryEntityToDto)
                );
    }

    /**
     * Метод позволяет удалить категорию по идентификатору
     * @param id - идентификатор категории
     * @return - флаг удаления
     */
    @Override
    public Mono<Boolean> deleteById(Long id) {
        return categoryRepository.deleteById(id)
                .doOnSubscribe(subscription -> log.info("Удаление категории по идентификатору: {}", id))
                .then(categoryRepository.findById(id))
                .map(found -> false)
                .defaultIfEmpty(true);
    }
}
