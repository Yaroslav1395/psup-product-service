package sakhno.psup.product_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.models.CategoryEntity;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<CategoryEntity, Long> {
    /**
     * Метод позволяет найти категорию по названию
     * @param name - название категории
     * @return - сущность категории
     */
    Mono<CategoryEntity> findByName(String name);

    /**
     * Метод позволяет найти категорию по названию не учитывая переданный идентификатору
     * @param name - название категории
     * @param id - идентификатор категории
     * @return - сущность категории
     */
    Mono<CategoryEntity> findByNameAndIdNot(String name, Long id);
}
