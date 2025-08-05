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
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.dto.subcategory.SubcategorySaveDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryUpdateDto;
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

    @GetMapping("/category/{id}")
    private Mono<ResponseEntity<ResponseDto<List<SubcategoryDto>>>> getSubcategoriesByCategory(
            @PathVariable @Positive(message = "Идентификатор категории должен быть положительным") Long id) {
        return subcategoryService.getByCategoryId(id)
                .doFirst(() -> log.info("Запрос на получение подкатегорий по идентификатору категории: {}", id))
                .collectList()
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subcategory")
    private Mono<ResponseEntity<ResponseDto<SubcategoryDto>>> saveSubcategory(
            @RequestBody @Valid SubcategorySaveDto subcategorySaveDto) {
        return subcategoryService.save(subcategorySaveDto)
                .doFirst(() -> log.info("Запрос на сохранение подкатегории: {}", subcategorySaveDto))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/subcategory")
    private Mono<ResponseEntity<ResponseDto<SubcategoryDto>>> updateSubcategory(
            @RequestBody @Valid SubcategoryUpdateDto subcategoryUpdateDto) {
        return subcategoryService.update(subcategoryUpdateDto)
                .doFirst(() -> log.info("Запрос на обновление подкатегории: {}", subcategoryUpdateDto))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/subcategory/{id}")
    private Mono<ResponseEntity<ResponseDto<Boolean>>> deleteSubcategory(
            @PathVariable @Positive(message = "Идентификатор подкатегории должен быть положительным") Long id) {
        return subcategoryService.deleteById(id)
                .doFirst(() -> log.info("Запрос на удаление подкатегории по идентификатору: {}", id))
                .map(ResponseDto::ok)
                .map(ResponseEntity::ok);
    }
}
