package sakhno.psup.product_service.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.config.exception.GlobalExceptionHandler;
import sakhno.psup.product_service.controllers.CategoryController;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.dto.category.CategorySaveDto;
import sakhno.psup.product_service.dto.category.CategoryUpdateDto;
import sakhno.psup.product_service.exceptions.all.DuplicateEntityException;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;
import sakhno.psup.product_service.repositories.CategoryRepository;
import sakhno.psup.product_service.services.category.CategoryService;

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
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
@WebFluxTest(controllers = CategoryController.class)
@ContextConfiguration(classes = {CategoryController.class})
@AutoConfigureRestDocs
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        WebTestClient client = WebTestClient.bindToApplicationContext(context)
                .configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void getCategoryById() {
        Mockito.when(categoryService.getById(anyLong())).thenReturn(Mono.just(getValidCategoryDto()));

        webTestClient
                .get()
                .uri("/api/v1/product-service/categories/category/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("Категория")
                .jsonPath("$.data.description").isEqualTo("Категория для тестов")
                .consumeWith(document("get-category-by-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор категории")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("ID категории"),
                                fieldWithPath("data.name").description("Название категории"),
                                fieldWithPath("data.description").description("Описание категории"),
                                fieldWithPath("data.createdDate").description("Дата создания"),
                                fieldWithPath("data.updatedDate").description("Дата обновления"),
                                fieldWithPath("data.createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data.updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("message").description("Сообщение об успехе"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void getCategoryById_notFound() {
        Mockito.when(categoryService.getById(anyLong())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/product-service/categories/category/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo(String.format("Категория +%d не найдена",999L))
                .jsonPath("$.state").isEqualTo("FAIL")
                .consumeWith(document("get-category-by-id-empty",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор категории")
                        ),
                        responseFields(
                                fieldWithPath("data").description("null, так как данные не найдены").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL")
                        )
                ));
    }

    @Test
    void getAllCategories() {
        Mockito.when(categoryService.getAll()).thenReturn(Flux.fromIterable(getValidCategoryDtoList()));

        webTestClient.get()
                .uri("/api/v1/product-service/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].id").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Мягкая мебель")
                .jsonPath("$.data[1].id").isEqualTo(2)
                .jsonPath("$.data[0].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[0].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[0].createdDate").isNotEmpty()
                .jsonPath("$.data[0].updatedDate").isNotEmpty()
                .jsonPath("$.data[1].name").isEqualTo("Кухонная мебель")
                .jsonPath("$.data[1].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[1].createdUserId").isEqualTo(1L)
                .jsonPath("$.data[1].createdDate").isNotEmpty()
                .jsonPath("$.data[1].updatedDate").isNotEmpty()
                .consumeWith(document("get-all-categories",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data[].id").description("Идентификатор категории"),
                                fieldWithPath("data[].name").description("Название категории"),
                                fieldWithPath("data[].description").description("Описание категории").optional(),
                                fieldWithPath("data[].createdDate").description("Дата создания"),
                                fieldWithPath("data[].updatedDate").description("Дата обновления"),
                                fieldWithPath("data[].createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data[].updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("message").description("Сообщение об успехе").optional(),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void getAllCategories_notFound() {
        Mockito.when(categoryService.getAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/product-service/categories")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Категории не найдены")
                .jsonPath("$.state").isEqualTo("FAIL")
                .consumeWith(document("get-all-categories-empty",
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
    void saveCategory() {
        Mockito.when(categoryService.save(any(CategorySaveDto.class))).thenReturn(Mono.just(getValidCategoryDtoAfterSave()));
        ConstrainedRuFields fields = new ConstrainedRuFields(CategorySaveDto.class);
        webTestClient.post()
                .uri("/api/v1/product-service/categories/category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidCategorySaveDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data.id").isEqualTo(1L)
                .jsonPath("$.data.name").isEqualTo("Категория")
                .jsonPath("$.data.description").isEqualTo("Категория для тестов")
                .jsonPath("$.data.createdUserId").isEqualTo(1L)
                .jsonPath("$.data.updatedUserId").isEmpty()
                .jsonPath("$.data.createdDate").isNotEmpty()
                .jsonPath("$.data.updatedDate").isEmpty()
                .consumeWith(document("save-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fields.withPath("name", "Не может быть пустым. Должно содержать от 5 до 100 символов")
                                        .description("Название категории"),
                                fields.withPath("description", "Не может быть пустым. Должно содержать от 5 до 1000 символов")
                                        .description("Описание категории")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("Идентификатор категории"),
                                fieldWithPath("data.name").description("Название категории"),
                                fieldWithPath("data.description").description("Описание категории"),
                                fieldWithPath("data.createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data.updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("data.createdDate").description("Дата создания"),
                                fieldWithPath("data.updatedDate").description("Дата обновления"),
                                fieldWithPath("message").description("Сообщение об успехе"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS")
                        )
                ));
    }

    @Test
    void saveCategory_duplicate() {

        Mockito.when(categoryService.save(any(CategorySaveDto.class)))
                .thenReturn(Mono.error(new DuplicateEntityException("Категория с именем: Техника уже существует")));

        webTestClient.post()
                .uri("/api/v1/product-service/categories/category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidCategorySaveDto())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("уже существует"))
                .consumeWith(document("save-category-duplicate",
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
    void saveCategory_validationError() {
        CategorySaveDto request = new CategorySaveDto(null, "");
        webTestClient.post()
                .uri("/api/v1/product-service/categories/category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("не может быть пустым"))
                .consumeWith(document("save-category-validation-error",
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
    void updateCategory() {
        Mockito.when(categoryService.update(any(CategoryUpdateDto.class))).thenReturn(Mono.just(getValidCategoryDtoAfterUpdate()));

        ConstrainedRuFields fields = new ConstrainedRuFields(CategoryUpdateDto.class);

        webTestClient.put()
                .uri("/api/v1/product-service/categories/category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidCategoryUpdateDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data.id").isEqualTo(1L)
                .jsonPath("$.data.name").isEqualTo("Обновленная категория")
                .jsonPath("$.data.description").isEqualTo("Новое описание")
                .jsonPath("$.data.createdUserId").isEqualTo(1L)
                .jsonPath("$.data.updatedUserId").isEqualTo(1L)
                .jsonPath("$.data.createdDate").isNotEmpty()
                .jsonPath("$.data.updatedDate").isNotEmpty()
                .consumeWith(document("update-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fields.withPath("id","Не может быть пустым. Должно быть больше 0").description("ID категории, которую нужно обновить"),
                                fields.withPath("name", "Не может быть пустым. Должно содержать от 5 до 100 символов")
                                        .description("Новое название категории"),
                                fields.withPath("description", "Не может быть пустым. Должно содержать от 5 до 1000 символов")
                                        .description("Новое описание категории")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("Идентификатор категории"),
                                fieldWithPath("data.name").description("Отредактированное название категории"),
                                fieldWithPath("data.description").description("Отредактированное описание категории"),
                                fieldWithPath("data.createdUserId").description("ID пользователя, создавшего запись"),
                                fieldWithPath("data.updatedUserId").description("ID пользователя, обновившего запись"),
                                fieldWithPath("data.createdDate").description("Дата создания"),
                                fieldWithPath("data.updatedDate").description("Дата обновления"),
                                fieldWithPath("state").description("Статус ответа: SUCCESS"),
                                fieldWithPath("message").description("OK")
                        )
                ));
    }

    @Test
    void updateCategory_duplicateName() {
        Mockito.when(categoryService.update(any(CategoryUpdateDto.class)))
                .thenReturn(Mono.error(new DuplicateEntityException("Категория с таким названием уже существует: Техника")));

        webTestClient.put()
                .uri("/api/v1/product-service/categories/category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getValidCategoryUpdateDto())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("уже существует"))
                .consumeWith(document("update-category-duplicate",
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
    void updateCategory_validationError() {
        CategoryUpdateDto invalidDto = new CategoryUpdateDto(null, "", "");

        webTestClient.put()
                .uri("/api/v1/product-service/categories/category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.state").isEqualTo("FAIL")
                .jsonPath("$.message").value(containsString("не может быть пустым"))
                .consumeWith(document("update-category-validation-error",
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
    void deleteCategory() {
        Mockito.when(categoryService.deleteById(1L)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/api/v1/product-service/categories/category/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SUCCESS")
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data").isEqualTo(true)
                .consumeWith(document("delete-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Идентификатор категории")
                        ),
                        responseFields(
                                fieldWithPath("data").description("true, если категория была удалена"),
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

    CategoryDto getValidCategoryDto() {
        return CategoryDto.builder()
                .id(1L)
                .name("Категория")
                .description("Категория для тестов")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now().plusHours(1))
                .createdUserId(1L)
                .updatedUserId(1L)
                .build();
    }

    List<CategoryDto> getValidCategoryDtoList() {
        return List.of(
                CategoryDto.builder()
                        .id(1L)
                        .name("Мягкая мебель")
                        .description("Описание мягкой мебели")
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now().plusHours(1))
                        .createdUserId(1L)
                        .updatedUserId(1L)
                        .build(),
                CategoryDto.builder()
                        .id(2L)
                        .name("Кухонная мебель")
                        .description("Описание кухонной мебели")
                        .createdDate(LocalDateTime.now())
                        .updatedDate(LocalDateTime.now().plusHours(1))
                        .createdUserId(1L)
                        .updatedUserId(1L)
                        .build()
        );
    }

    CategorySaveDto getValidCategorySaveDto() {
        return CategorySaveDto.builder()
                .name("Категория")
                .description("Категория для тестов")
                .build();
    }

    CategoryDto getValidCategoryDtoAfterSave() {
        return CategoryDto.builder()
                .id(1L)
                .name("Категория")
                .description("Категория для тестов")
                .createdUserId(1L)
                .updatedUserId(null)
                .createdDate(LocalDateTime.now())
                .updatedDate(null)
                .build();
    }

    CategoryUpdateDto getValidCategoryUpdateDto() {
        return CategoryUpdateDto.builder()
                .id(1L)
                .name("Обновленная категория")
                .description("Новое описание")
                .build();
    }

    CategoryDto getValidCategoryDtoAfterUpdate() {
        return CategoryDto.builder()
                .id(1L)
                .name("Обновленная категория")
                .description("Новое описание")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now().plusHours(1))
                .updatedUserId(1L)
                .createdUserId(1L)
                .build();
    }
}
