package sakhno.psup.product_service.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.ResponseDto;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.services.category.CategoryService;

import java.util.List;


@RestController
@RequestMapping("api/v1/product-service/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category/{id}")
    private Mono<ResponseEntity<ResponseDto<CategoryDto>>> getCategoryBy(
            @PathVariable @Positive(message = "Идентификатор категории должен быть положительным") Long id) {
        return categoryService.getById(id)
                .doFirst(() -> log.info("Запрос на получение категории по идентификатору: {}", id))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.empty("Категория не найдена")));
    }

    @GetMapping
    private Mono<ResponseEntity<ResponseDto<List<CategoryDto>>>> getAllCategories() {
        return categoryService.getAll()
                .doFirst(() -> log.info("Запрос на получение всех категорий продукции"))
                .collectList()
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.empty("Категории не найдены")));
    }

    @PostMapping("/category")
    private Mono<ResponseEntity<ResponseDto<CategoryDto>>> saveCategory(@RequestBody @Valid CategorySaveDto categorySaveDto) {
        return categoryService.save(categorySaveDto)
                .doFirst(() -> log.info("Запрос на сохранение категории: {}", categorySaveDto))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(ResponseDto.fail("Категорию не удалось сохранить")));
    }

    @PutMapping("/category")
    private Mono<ResponseEntity<ResponseDto<CategoryDto>>> updateCategory(@RequestBody @Valid CategoryUpdateDto categoryUpdateDto) {
        return categoryService.update(categoryUpdateDto)
                .doFirst(() -> log.info("Запрос на обновление категории: {}", categoryUpdateDto))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(ResponseDto.fail("Категорию не удалось обновить")));
    }

    @DeleteMapping("/category/{id}")
    private Mono<ResponseEntity<ResponseDto<Boolean>>> deleteCategory(
            @PathVariable @Positive(message = "Идентификатор категории должен быть положительным") Long id) {
        return categoryService.deleteById(id)
                .doFirst(() -> log.info("Запрос на удаление категории по идентификатору: {}", id))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }

}
