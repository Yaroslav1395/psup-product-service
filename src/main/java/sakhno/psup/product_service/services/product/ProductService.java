package sakhno.psup.product_service.services.product;

import reactor.core.publisher.Flux;
import sakhno.psup.product_service.models.ProductEntity;

public interface ProductService {

    Flux<ProductEntity> getAllProducts();
}
