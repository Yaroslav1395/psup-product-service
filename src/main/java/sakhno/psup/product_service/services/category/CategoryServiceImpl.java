package sakhno.psup.product_service.services.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.exceptions.all.DuplicateEntityException;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;
import sakhno.psup.product_service.mappers.category.CategoryMapper;
import sakhno.psup.product_service.models.CategoryEntity;
import sakhno.psup.product_service.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ReactiveRedisTemplate<String, CategoryDto> categoryRedisTemplate;
    private final ReactiveRedisTemplate<String, List<CategoryDto>> categoriesRedisTemplate;
    private static final String CATEGORY_CACHE_PREFIX = "category:";
    private static final String CATEGORIES_CACHE_PREFIX = "categories:";

    /**
     * Метод позволяет получить категорию по идентификатору
     * @param id - идентификатор категории
     * @return - DTO категории
     */
    @Override
    public Mono<CategoryDto> getById(Long id) {
        Mono<CategoryDto> categoryDtoMono = categoryRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("Поиск категории по идентификатору в базе: {}", id))
                .doOnRequest(category -> log.info("Преобразование сущности категории в DTO"))
                .map(categoryMapper::mapCategoryEntityToDto)
                .doOnSuccess(category -> log.info("Преобразование сущности категории в DTO завершено"))
                .flatMap(categoryDto -> categoryRedisTemplate.opsForValue()
                        .set(CATEGORY_CACHE_PREFIX + id, categoryDto)
                        .thenReturn(categoryDto)
                );
        return categoryRedisTemplate.opsForValue().get(CATEGORY_CACHE_PREFIX + id)
                .doOnSubscribe(subscription -> log.info("Поиск категории по идентификатору в кэше: {}", id))
                .switchIfEmpty(categoryDtoMono);
    }

    /**
     * Метод позволяет получить все категории
     * @return - список категорий
     */
    @Override
    public Flux<CategoryDto> getAll() {
        Mono<List<CategoryDto>> categoryDtoMono = categoryRepository.findAll()
                .doOnSubscribe(subscription -> log.info("Поиск всех категорий продукции в базе"))
                .doOnRequest(categories -> log.info("Преобразование списка сущностей категорий в DTO"))
                .map(categoryMapper::mapCategoryEntityToDto)
                .doOnComplete(() -> log.info("Преобразование списка сущностей категорий в DTO завершено"))
                .collectList()
                .flatMap(categoryList -> categoriesRedisTemplate.opsForValue()
                        .set(CATEGORIES_CACHE_PREFIX, categoryList)
                        .thenReturn(categoryList));

        return categoriesRedisTemplate.opsForValue().get(CATEGORIES_CACHE_PREFIX)
                .doOnSubscribe(subscription -> log.info("Поиск всех категорий продукции в кэше"))
                .switchIfEmpty(categoryDtoMono)
                .flatMapMany(Flux::fromIterable);
    }

    /**
     * Метод позволяет сохранить категорию.  В случае если запись с таким же именем существует, выбросит исключение.
     * @param categorySaveDto - категория для сохранения
     * @return - сохраненная категория
     */
    @Override
    public Mono<CategoryDto> save(CategorySaveDto categorySaveDto) {

        Mono<CategoryDto> categoryDtoMono = Mono.just(categorySaveDto)
                .doOnSubscribe(subscription -> log.info("Сохранение новой категории"))
                .doOnRequest(r -> log.info("Преобразование DTO категории в сущность"))
                .map(categoryMapper::mapCategorySaveDtoToEntity)
                .flatMap(categoryRepository::save)
                .doOnNext(saved -> log.info("Категория сохранена с ID: {}", saved.getId()))
                .map(categoryMapper::mapCategoryEntityToDto)
                .doOnSuccess(dto -> logCategoryMappingEnd())
                .flatMap(this::updateCategoryCacheAfterSave);

        return categoryRepository.findByName(categorySaveDto.getName())
                .flatMap(existing -> Mono.<CategoryDto>error(new DuplicateEntityException(
                        "Категория с именем: " + categorySaveDto.getName() + " уже существует")))
                .switchIfEmpty(categoryDtoMono);
    }

    /**
     * Метод позволяет отредактировать категорию. В случае если запись с таким же именем существует, выбросит исключение.
     * В случае если по идентификатору категории не существует, выбросит исключение
     * @param categoryUpdateDto - категория для редактирования
     * @return - отредактированная категория
     */
    @Override
    public Mono<CategoryDto> update(CategoryUpdateDto categoryUpdateDto) {
        Mono<CategoryDto> categoryDtoMono = categoryRepository.findById(categoryUpdateDto.getId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Категория не найдена по id: " + categoryUpdateDto.getId())))
                .flatMap(existingCategory -> updateCategoryEntity(categoryUpdateDto, existingCategory))
                .doOnNext(saved -> log.info("Категория отредактирована"))
                .map(categoryMapper::mapCategoryEntityToDto)
                .doOnSuccess(dto -> logCategoryMappingEnd())
                .flatMap(this::updateCategoryCacheAfterSave);

        return categoryRepository.findByNameAndIdNot(categoryUpdateDto.getName(), categoryUpdateDto.getId())
                .flatMap(duplicate -> Mono.<CategoryDto>error(new DuplicateEntityException(
                        "Категория с таким названием уже существует: " + categoryUpdateDto.getName())))
                .switchIfEmpty(categoryDtoMono);
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
                .then(categoriesRedisTemplate
                        .opsForValue()
                        .delete(CATEGORY_CACHE_PREFIX + id)
                        .doOnSuccess(deleted -> log.info("Категория {} удалена из кэша: {}", id, deleted))
                )
                .then(deleteCategoryFromCache(id))
                .then(categoryRepository.findById(id))
                .map(found -> false)
                .defaultIfEmpty(true);
    }

    /**
     * Метод позволяет обновить категорию в базе на основе новых данных
     * @param categoryUpdateDto - новые данные по категории
     * @param existingCategory - существующая категория
     * @return - отредактированная категория
     */
    private Mono<CategoryEntity> updateCategoryEntity(CategoryUpdateDto categoryUpdateDto, CategoryEntity existingCategory) {
        log.info("Категория найдена. Производится обновление");
        categoryMapper.updateCategoryEntity(categoryUpdateDto, existingCategory);
        return categoryRepository.save(existingCategory);
    }


    /**
     * Метод позволяет добавить категорию в кэш (category и categories) после сохранения в базе
     * @param dto - сохраненная категория
     * @return - категория
     */
    private Mono<CategoryDto> updateCategoryCacheAfterSave(CategoryDto dto) {
        return Mono.when(updateSingleCategoryCache(dto), updateListCategoryCache(dto))
                .doOnSubscribe(subscription -> log.info("Добавление категории в кэш после сохранения"))
                .thenReturn(dto);
    }

    /**
     * Метод позволяет удалить категорию из кэша (category и categories)
     * @param categoryId - id категория для удаления
     * @return - категория
     */
    private Mono<Long> deleteCategoryFromCache(Long categoryId) {
        return Mono.when(deleteCategoryFromSingleCache(categoryId), deleteCategoryFromListCache(categoryId))
                .doOnSubscribe(subscription -> log.info("Удаление категории из кэша"))
                .thenReturn(categoryId);
    }

    /**
     * Метод позволяет обновить категорию в кэше
     * @param categoryDto - категория для обновления
     * @return - флаг обновления
     */
    private Mono<Boolean> updateSingleCategoryCache(CategoryDto categoryDto) {
        return categoryRedisTemplate.opsForValue()
                .set(CATEGORY_CACHE_PREFIX + categoryDto.getId(), categoryDto);
    }

    /**
     * Метод позволяет удалить категорию из кэша
     * @param categoryId - категория для удаления
     * @return - флаг удаления
     */
    private Mono<Boolean> deleteCategoryFromSingleCache(Long categoryId) {
        return categoryRedisTemplate.opsForValue()
                .delete(CATEGORY_CACHE_PREFIX + categoryId)
                .doOnSubscribe(subscription -> log.info("Удаление категории из кэша: {}", categoryId));
    }

    /**
     * Метод позволяет обновить список категорий в кэше
     * @param categoryDto - категория для обновления
     * @return - флаг обновления
     */
    private Mono<Boolean> updateListCategoryCache(CategoryDto categoryDto) {
        return categoriesRedisTemplate.opsForValue()
                .get(CATEGORIES_CACHE_PREFIX)
                .defaultIfEmpty(new ArrayList<>())
                .map(list -> {
                    List<CategoryDto> newList = new ArrayList<>(list);
                    newList.add(categoryDto);
                    return newList;
                })
                .flatMap(updatedList -> categoriesRedisTemplate.opsForValue()
                        .set(CATEGORIES_CACHE_PREFIX, updatedList));
    }

    /**
     * Метод позволяет удалить категорию из списка категорий в кэше
     * @param categoryId - категория для удаления
     * @return - флаг удаления
     */
    private Mono<Boolean> deleteCategoryFromListCache(Long categoryId) {
        return categoriesRedisTemplate.opsForValue()
                .get(CATEGORIES_CACHE_PREFIX)
                .defaultIfEmpty(new ArrayList<>())
                .map(list -> list.stream()
                        .filter(c -> !categoryId.equals(c.getId()))
                        .toList())
                .flatMap(updatedList -> categoriesRedisTemplate.opsForValue()
                        .set(CATEGORIES_CACHE_PREFIX, updatedList));
    }


    /**
     * Логирует преобразование категории в DTO. Для избежания warning
     */
    private void logCategoryMappingEnd() {
        log.info("Преобразование сущности в DTO завершено");
    }
}
