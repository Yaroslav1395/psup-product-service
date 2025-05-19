package sakhno.psup.product_service.services;

import reactor.core.publisher.Flux;
import sakhno.psup.product_service.models.ProductEntity;

import java.util.List;

public interface ProductService {

    Flux<ProductEntity> getAllProducts();
}
