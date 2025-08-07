package sakhno.psup.product_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.config.exception.GlobalExceptionHandler;
import sakhno.psup.product_service.controllers.SubcategoryController;
import sakhno.psup.product_service.dto.category.CategorySimpleDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryDto;
import sakhno.psup.product_service.dto.subcategory.SubcategorySaveDto;
import sakhno.psup.product_service.dto.subcategory.SubcategoryUpdateDto;
import sakhno.psup.product_service.exceptions.all.DuplicateEntityException;
import sakhno.psup.product_service.exceptions.all.EntitiesNotFoundException;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;
import sakhno.psup.product_service.mappers.subcategory.SubcategoryMapper;
import sakhno.psup.product_service.repositories.CategoryRepository;
import sakhno.psup.product_service.repositories.SubcategoryRepository;
import sakhno.psup.product_service.services.subcategory.SubcategoryService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@ExtendWith(RestDocumentationExtension.class)
@WebFluxTest(controllers = SubcategoryController.class)
@ContextConfiguration(classes = {SubcategoryController.class})
@AutoConfigureRestDocs
@Import(GlobalExceptionHandler.class)
public class SubcategoryControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SubcategoryService subcategoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private SubcategoryRepository subcategoryRepository;

    @MockitoBean
    private SubcategoryMapper subcategoryMapper;

    @Autowired
    private ApplicationContext context;

    @Test
    void getSubcategoryById() {
        SubcategoryDto subcategoryDto = getValidSubcategoryDto();
        Mockito.when(subcategoryService.getById(1L)).thenReturn(Mono.just(subcategoryDto));
        webTestClient
                .get()
                .uri("/api/v1/product-service/subcategories/subcategory/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("Подкатегория")
                .jsonPath("$.data.description").isEqualTo("Подкатегория для тестов")
                .jsonPath("$.data.category.id").isEqualTo(1L)
                .jsonPath("$.data.category.name").isEqualTo("Категория")
                .jsonPath("$.data.createdDate").isNotEmpty()
                .jsonPath("$.data.updatedDate").isNotEmpty()
                .jsonPath("$.data.createdUserId").isEqualTo(1L)
                .jsonPath("$.data.updatedUserId").isEqualTo(1L)
                .consumeWith(document("subcategory/get-subcategory-by-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор подкатегории")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("ID подкатегории"),
                                fieldWithPath("data.name").description("Название подкатегории"),
                                fieldWithPath("data.description").description("Описание подкатегории"),
                                fieldWithPath("data.category.id").description("ID категории, к которой принадлежит подкатегория"),
                                fieldWithPath("data.category.name").description("Название категории"),
                                fieldWithPath("data.createdDate").description("Дата создания подкатегории"),
                                fieldWithPath("data.updatedDate").description("Дата обновления подкатегории"),
                                fieldWithPath("data.createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data.updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("message").description("Сообщение об успехе"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void getSubcategoryById_notFound() {
        Mockito.when(subcategoryService.getById(anyLong())).thenReturn(Mono.error(
                new EntityNotFoundException("Подкатегория с id %d не найдена".formatted(999L))));

        webTestClient.get()
                .uri("/api/v1/product-service/subcategories/subcategory/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo(String.format("Подкатегория с id %d не найдена",999L))
                .jsonPath("$.state").isEqualTo("FAIL")
                .consumeWith(document("subcategory/get-subcategory-by-id-empty",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор подкатегории")
                        ),
                        responseFields(
                                fieldWithPath("data").description("null, так как данные не найдены").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void getAllSubcategories() {
        Mockito.when(subcategoryService.getAll()).thenReturn(Flux.fromIterable(getValidSubcategoryDtoList()));

        webTestClient.get()
                .uri("/api/v1/product-service/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Прямые диваны")
                .jsonPath("$.data[0].description").isNotEmpty()
                .jsonPath("$.data[0].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[0].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[0].createdDate").isNotEmpty()
                .jsonPath("$.data[0].updatedDate").isNotEmpty()
                .jsonPath("$.data[1].id").isEqualTo(2)
                .jsonPath("$.data[1].name").isEqualTo("Угловые диваны")
                .jsonPath("$.data[1].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[1].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[1].createdDate").isNotEmpty()
                .jsonPath("$.data[1].updatedDate").isNotEmpty()
                .consumeWith(document("subcategory/get-all-subcategories",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data[].id").description("Идентификатор подкатегории"),
                                fieldWithPath("data[].name").description("Название подкатегории"),
                                fieldWithPath("data[].description").description("Описание подкатегории").optional(),
                                fieldWithPath("data[].createdDate").description("Дата создания"),
                                fieldWithPath("data[].updatedDate").description("Дата обновления"),
                                fieldWithPath("data[].createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data[].updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("data[].category.id").description("ID категории, к которой принадлежит подкатегория"),
                                fieldWithPath("data[].category.name").description("Название категории"),
                                fieldWithPath("message").description("Сообщение об успехе").optional(),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void getAllSubcategories_notFound() {
        Mockito.when(subcategoryService.getAll()).thenReturn(Flux.error(new EntitiesNotFoundException("Подкатегории не найдены")));

        webTestClient.get()
                .uri("/api/v1/product-service/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Подкатегории не найдены")
                .jsonPath("$.state").isEqualTo("FAIL")
                .consumeWith(document("subcategory/get-all-subcategories-empty",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("null, так как данные не найдены").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void getSubcategoriesByCategoryId() {
        Long categoryId = 1L;
        List<SubcategoryDto> subcategories = getValidSubcategoryDtoList();
        Mockito.when(subcategoryService.getByCategoryId(categoryId))
                .thenReturn(Flux.fromIterable(subcategories));

        webTestClient.get()
                .uri("/api/v1/product-service/subcategories/category/{id}", categoryId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Прямые диваны")
                .jsonPath("$.data[0].description").isNotEmpty()
                .jsonPath("$.data[0].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[0].updatedUserId").isEqualTo(1L)
                .jsonPath("$.data[0].createdDate").isNotEmpty()
                .jsonPath("$.data[0].updatedDate").isNotEmpty()
                .jsonPath("$.data[0].category.id").isEqualTo(1)
                .jsonPath("$.data[0].category.name").isEqualTo("Мягкая мебель")
                .jsonPath("$.data[1].id").isEqualTo(2)
                .jsonPath("$.data[1].name").isEqualTo("Угловые диваны")
                .jsonPath("$.data[1].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[1].updatedUserId").isEqualTo(1L)
                .jsonPath("$.data[1].createdDate").isNotEmpty()
                .jsonPath("$.data[1].updatedDate").isNotEmpty()
                .consumeWith(document("subcategory/get-by-category-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор категории")
                        ),
                        responseFields(
                                fieldWithPath("data[].id").description("Идентификатор подкатегории"),
                                fieldWithPath("data[].name").description("Название подкатегории"),
                                fieldWithPath("data[].description").description("Описание подкатегории").optional(),
                                fieldWithPath("data[].createdDate").description("Дата создания"),
                                fieldWithPath("data[].updatedDate").description("Дата обновления"),
                                fieldWithPath("data[].createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data[].updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("data[].category.id").description("ID категории, к которой принадлежит подкатегория"),
                                fieldWithPath("data[].category.name").description("Название категории"),
                                fieldWithPath("message").description("Сообщение об успехе").optional(),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void saveSubcategory() {
        Mockito.when(subcategoryService.save(any(SubcategorySaveDto.class)))
                .thenReturn(Mono.just(getValidSubcategoryDtoAfterSave()));
        ConstrainedRuFields fields = new ConstrainedRuFields(SubcategorySaveDto.class);

        webTestClient.post()
                .uri("/api/v1/product-service/subcategories/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidSubcategorySaveDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data.id").isEqualTo(1L)
                .jsonPath("$.data.name").isEqualTo("Подкатегория")
                .jsonPath("$.data.description").isEqualTo("Подкатегория для тестов")
                .jsonPath("$.data.createdUserId").isEqualTo(1L)
                .jsonPath("$.data.updatedUserId").isEmpty()
                .jsonPath("$.data.createdDate").isNotEmpty()
                .jsonPath("$.data.updatedDate").isEmpty()
                .jsonPath("$.data.category.id").isEqualTo(1L)
                .jsonPath("$.data.category.name").isEqualTo("Категория")
                .consumeWith(document("subcategory/save-subcategory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fields.withPath("name", "Не может быть пустым. Должно содержать от 5 до 256 символов")
                                        .description("Название подкатегории"),
                                fields.withPath("description", "Не может быть пустым. Должно содержать от 5 до 2000 символов")
                                        .description("Описание подкатегории"),
                                fields.withPath("categoryId", "Не может быть пустым. Должно быть положительным числом")
                                        .description("Идентификатор подкатегории")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("Идентификатор подкатегории"),
                                fieldWithPath("data.name").description("Название подкатегории"),
                                fieldWithPath("data.description").description("Описание подкатегории"),
                                fieldWithPath("data.createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data.updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("data.createdDate").description("Дата создания"),
                                fieldWithPath("data.updatedDate").description("Дата обновления"),
                                fieldWithPath("data.category.id").description("ID категории, к которой принадлежит подкатегория"),
                                fieldWithPath("data.category.name").description("Название категории"),
                                fieldWithPath("message").description("Сообщение об успехе"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void saveSubcategory_duplicate() {
        Mockito.when(subcategoryService.save(any(SubcategorySaveDto.class)))
                .thenReturn(Mono.error(new DuplicateEntityException("Подкатегория с именем: Прямые диваны уже существует")));
        webTestClient.post()
                .uri("/api/v1/product-service/subcategories/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidSubcategorySaveDto())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("уже существует"))
                .consumeWith(document("subcategory/save-subcategory-duplicate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("null, так как сохранение не удалось").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void saveSubcategory_validationError() {
        SubcategorySaveDto request = new SubcategorySaveDto(null, "", -1L);
        webTestClient.post()
                .uri("/api/v1/product-service/subcategories/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("не может быть пустым"))
                .consumeWith(document("subcategory/save-subcategory-validation-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("null, так как сохранение не удалось").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void updateSubcategory() {
        SubcategoryDto subcategoryAfterUpdate = getValidSubcategoryDtoAfterUpdate();
        SubcategoryUpdateDto subcategoryForUpdate = getValidSubcategoryUpdateDto();
        Mockito.when(subcategoryService.update(any(SubcategoryUpdateDto.class)))
                .thenReturn(Mono.just(getValidSubcategoryDtoAfterUpdate()));

        ConstrainedRuFields fields = new ConstrainedRuFields(CategoryUpdateDto.class);

        webTestClient.put()
                .uri("/api/v1/product-service/subcategories/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(subcategoryForUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data.id").isEqualTo(subcategoryAfterUpdate.getId())
                .jsonPath("$.data.name").isEqualTo(subcategoryAfterUpdate.getName())
                .jsonPath("$.data.description").isEqualTo(subcategoryAfterUpdate.getDescription())
                .jsonPath("$.data.createdUserId").isEqualTo(subcategoryAfterUpdate.getCreatedUserId())
                .jsonPath("$.data.updatedUserId").isEqualTo(subcategoryAfterUpdate.getUpdatedUserId())
                .jsonPath("$.data.createdDate").isNotEmpty()
                .jsonPath("$.data.updatedDate").isNotEmpty()
                .consumeWith(document("subcategory/update-subcategory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fields.withPath("id","Не может быть пустым. Должно быть больше 0")
                                        .description("ID подкатегории, которую нужно обновить"),
                                fields.withPath("name", "Не может быть пустым. Должно содержать от 5 до 256 символов")
                                        .description("Новое название подкатегории"),
                                fields.withPath("description", "Не может быть пустым. Должно содержать от 5 до 2000 символов")
                                        .description("Новое описание подкатегории"),
                                fields.withPath("categoryId", "Не может быть пустым. Должно быть положительным")
                                        .description("ID новой категории")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("Идентификатор подкатегории"),
                                fieldWithPath("data.name").description("Отредактированное название подкатегории"),
                                fieldWithPath("data.description").description("Отредактированное описание подкатегории"),
                                fieldWithPath("data.createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data.updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("data.createdDate").description("Дата создания"),
                                fieldWithPath("data.updatedDate").description("Дата обновления"),
                                fieldWithPath("data.category.id").description("ID категории, к которой принадлежит подкатегория"),
                                fieldWithPath("data.category.name").description("Название категории"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS"),
                                fieldWithPath("message").description("OK")
                        )
                ));
    }

    @Test
    void updateSubcategory_duplicateName() {
        Mockito.when(subcategoryService.update(any(SubcategoryUpdateDto.class)))
                .thenReturn(Mono.error(new DuplicateEntityException("Категория с таким названием уже существует: Прямые диваны")));

        webTestClient.put()
                .uri("/api/v1/product-service/subcategories/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidSubcategoryUpdateDto())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("уже существует"))
                .consumeWith(document("subcategory/update-subcategory-duplicate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("null, так как обновление не удалось").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void updateSubcategory_validationError() {
        SubcategoryUpdateDto invalidDto = new SubcategoryUpdateDto(null, "", "", -1L);

        webTestClient.put()
                .uri("/api/v1/product-service/subcategories/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("не может быть пустым"))
                .consumeWith(document("subcategory/update-subcategory-validation-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("null, так как обновление не удалось").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void deleteSubcategory() {
        Mockito.when(subcategoryService.deleteById(1L)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/api/v1/product-service/subcategories/subcategory/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data").isEqualTo(true)
                .consumeWith(document("subcategory/delete-subcategory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор подкатегории")
                        ),
                        responseFields(
                                fieldWithPath("data").description("true, если подкатегория была удалена"),
                                fieldWithPath("message").description("Сообщение об успехе"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    private static class ConstrainedRuFields {
        private final ConstraintDescriptions constraintDescriptions;

        public ConstrainedRuFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path, String description) {
            return fieldWithPath(path).attributes(key("constraints").value(description));
        }
    }

    private SubcategoryDto getValidSubcategoryDto() {
        return SubcategoryDto.builder()
                .id(1L)
                .name("Подкатегория")
                .description("Подкатегория для тестов")
                .category(new CategorySimpleDto(1L, "Категория"))
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .createdUserId(1L)
                .updatedUserId(1L)
                .build();
    }

    private List<SubcategoryDto> getValidSubcategoryDtoList() {
        CategorySimpleDto category = new CategorySimpleDto(1L, "Мягкая мебель");
        return List.of(
                SubcategoryDto.builder()
                        .id(1L)
                        .name("Прямые диваны")
                        .description("Классические диваны для любой комнаты, компактные и удобные")
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now().plusHours(1))
                        .createdUserId(1L)
                        .updatedUserId(1L)
                        .category(category)
                        .build(),
                SubcategoryDto.builder()
                        .id(2L)
                        .name("Угловые диваны")
                        .description("Просторные диваны для больших помещений и комфортного отдыха")
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now().plusHours(1))
                        .createdUserId(1L)
                        .updatedUserId(1L)
                        .category(category)
                        .build()
        );
    }

    private SubcategoryDto getValidSubcategoryDtoAfterSave() {
        return SubcategoryDto.builder()
                .id(1L)
                .name("Подкатегория")
                .description("Подкатегория для тестов")
                .createdUserId(1L)
                .updatedUserId(null)
                .createdDate(LocalDateTime.now())
                .updatedDate(null)
                .category(new CategorySimpleDto(1L, "Категория"))
                .build();
    }

    private SubcategorySaveDto getValidSubcategorySaveDto() {
        return SubcategorySaveDto.builder()
                .name("Подкатегория")
                .description("Подкатегория для тестов")
                .categoryId(1L)
                .build();
    }

    private SubcategoryDto getValidSubcategoryDtoAfterUpdate() {
        return SubcategoryDto.builder()
                .id(1L)
                .name("Обновленная подкатегория")
                .description("Новое описание")
                .category(new CategorySimpleDto(1L, "Категория"))
                .createdUserId(1L)
                .updatedUserId(1L)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now().plusHours(1))
                .build();
    }

    private SubcategoryUpdateDto getValidSubcategoryUpdateDto() {
        return SubcategoryUpdateDto.builder()
                .id(1L)
                .name("Обновленная подкатегория")
                .description("Новое описание")
                .categoryId(1L)
                .build();
    }

}
