package sakhno.psup.product_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import sakhno.psup.product_service.models.SubcategoryEntity;

@Repository
public interface SubcategoryRepository extends ReactiveCrudRepository<SubcategoryEntity, Long> {
}
