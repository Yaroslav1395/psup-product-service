package sakhno.psup.product_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.models.SubcategoryEntity;

@Repository
public interface SubcategoryRepository extends ReactiveCrudRepository<SubcategoryEntity, Long> {

    /**
     * Метод позволяет получить все подкатегории по id категории
     * @param categoryId - идентификатор категории
     * @return - список подкатегорий
     */
    Flux<SubcategoryEntity> findByCategoryId(Long categoryId);

    /**
     * Метод позволяет найти подкатегорию по названию и id категории
     * @param name - название подкатегории
     * @return - сущность подкатегории
     */
    Mono<SubcategoryEntity> findByNameAndCategoryId(String name, Long categoryId);

    /**
     * Метод позволяет найти подкатегорию по названию с указанной категории, исключая подкатегорию с указанным id.
     * @param name - название подкатегории
     * @return - сущность подкатегории
     */
    Mono<SubcategoryEntity> findByNameAndIdNotAndCategoryId(String name, Long id, Long categoryId);
}
