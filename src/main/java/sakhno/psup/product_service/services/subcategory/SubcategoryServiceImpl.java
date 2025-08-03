package sakhno.psup.product_service.services.subcategory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.exceptions.all.EntitiesNotFoundException;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;
import sakhno.psup.product_service.mappers.subcategory.SubcategoryMapper;
import sakhno.psup.product_service.models.CategoryEntity;
import sakhno.psup.product_service.models.SubcategoryEntity;
import sakhno.psup.product_service.repositories.CategoryRepository;
import sakhno.psup.product_service.repositories.SubcategoryRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubcategoryServiceImpl implements SubcategoryService  {
    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryMapper subcategoryMapper;


    /**
     * Метод позволяет получить подкатегорию по идентификатору
     * @param id - идентификатор
     * @return - найденная подкатегория
     */
    public Mono<SubcategoryDto> getById(Long id) {
        return subcategoryRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("Поиск подкатегории по id: {}", id))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Подкатегория с id %d не найдена".formatted(id))))
                .doOnNext(subcategory -> log.info("Поиск категории по id: {}", subcategory.getCategoryId()))
                .flatMap(subcategory -> categoryRepository.findById(subcategory.getCategoryId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException(
                                "Категория с id %d не найдена".formatted(subcategory.getCategoryId()))))
                        .doOnNext(category -> log.info("Преобразование подкатегории и категории в DTO"))
                        .map(category -> subcategoryMapper.mapToSubCategoryDto(subcategory, category))
                )
                .doOnSuccess(dto -> log.info("DTO подкатегории успешно собран"));
    }

    /**
     * Метод позволяет получить все подкатегории. Для избежания запросов N + 1 в базу реализованна сложная логика
     * преобразования. Читайте комментарии к методам, использованных в конвейере потока.
     * @return - список подкатегорий
     */
    @Override
    public Flux<SubcategoryDto> getAll() {
        return subcategoryRepository.findAll()
                .doOnSubscribe(subscription -> log.info("Поиск всех подкатегорий продукции"))
                .switchIfEmpty(Mono.error(new EntitiesNotFoundException("Подкатегории не найдены")))
                .collectList()
                .flatMapMany(this::getCategoryAndMappingToDtoFor)
                .doOnComplete(() -> log.info("Преобразование списка сущностей подкатегорий в DTO завершено"));
    }


    /**
     * Метод ищет категории для подкатегорий и затем преобразовывает сущность подкатегории в DTO
     * @param subcategories - список сущностей подкатегорий
     * @return - список DTO подкатегорий
     */
    private Flux<SubcategoryDto> getCategoryAndMappingToDtoFor(List<SubcategoryEntity> subcategories){
        return categoryRepository.findAllById(getCategoriesIdFrom(subcategories))
                .doOnSubscribe(subscription -> log.info("Поиск категорий"))
                .switchIfEmpty(Mono.error(new EntitiesNotFoundException("Категории не найдены")))
                .collectMap(CategoryEntity::getId, Function.identity())
                .doOnNext(categories -> log.info("Подстановка категорий в подкатегории"))
                .flatMapMany(categoryMap -> Flux.fromIterable(subcategories)
                        .map(sub ->
                                subcategoryMapper.mapToSubCategoryDto(sub, categoryMap.get(sub.getCategoryId())))
                );
    }

    /**
     * Метод дает возможность получить список идентификаторов категорий на которые ссылаются подкатегории
     * @param subcategories - список подкатегорий
     * @return - список идентификаторов подкатегорий
     */
    private List<Long> getCategoriesIdFrom(List<SubcategoryEntity> subcategories){
        return subcategories.stream()
                .map(SubcategoryEntity::getCategoryId)
                .distinct()
                .toList();
    }


    /**
     * Метод позволяет получить подкатегории по id категории
     * @param categoryId - идентификатор категории
     * @return - список подкатегорий
     */
    @Override
    public Flux<SubcategoryDto> getBuCategoryId(Long categoryId) {
        return null;
    }
}
