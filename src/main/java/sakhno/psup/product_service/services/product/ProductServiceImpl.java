package sakhno.psup.product_service.services.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import sakhno.psup.product_service.models.ProductEntity;
import sakhno.psup.product_service.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Flux<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }
}
