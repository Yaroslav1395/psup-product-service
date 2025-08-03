package sakhno.psup.product_service.controllers;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.ResponseDto;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.services.subcategory.SubcategoryService;

import java.util.List;

@RestController
@RequestMapping("api/v1/product-service/subcategories")
@RequiredArgsConstructor
@Slf4j
public class SubcategoryController {
    private final SubcategoryService subcategoryService;

    @GetMapping("/subcategory/{id}")
    private Mono<ResponseEntity<ResponseDto<SubcategoryDto>>> getSubcategoryBy(
            @PathVariable @Positive(message = "Идентификатор подкатегории должен быть положительным") Long id) {
        return subcategoryService.getById(id)
                .doFirst(() -> log.info("Запрос на получение подкатегории по идентификатору: {}", id))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    private Mono<ResponseEntity<ResponseDto<List<SubcategoryDto>>>> getAllSubcategories() {
        return subcategoryService.getAll()
                .doFirst(() -> log.info("Запрос на получение всех подкатегорий продукции"))
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }
}
