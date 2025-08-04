package sakhno.psup.product_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import sakhno.psup.product_service.models.SubcategoryEntity;

@Repository
public interface SubcategoryRepository extends ReactiveCrudRepository<SubcategoryEntity, Long> {

    /**
     * Метод позволяет получить все подкатегории по id категории
     * @param categoryId - идентификатор категории
     * @return - список подкатегорий
     */
    Flux<SubcategoryEntity> findByCategoryId(Long categoryId);
}
