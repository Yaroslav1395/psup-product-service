package sakhno.psup.product_service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import sakhno.psup.product_service.models.ProductEntity;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
}
