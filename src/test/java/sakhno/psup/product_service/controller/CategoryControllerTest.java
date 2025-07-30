package sakhno.psup.product_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.controllers.CategoryController;
import sakhno.psup.product_service.dto.category.CategoryDto;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;
import sakhno.psup.product_service.repositories.CategoryRepository;
import sakhno.psup.product_service.services.category.CategoryService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
@WebFluxTest(controllers = CategoryController.class)
@ContextConfiguration(classes = {CategoryController.class})
@AutoConfigureRestDocs
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
                                fieldWithPath("message").ignored(),
                                fieldWithPath("state").ignored()
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
                .jsonPath("$.message").isEqualTo("Категория не найдена")
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
                .jsonPath("$.data[1].name").isEqualTo("Кухонная мебель")
                .consumeWith(document("get-all-categories-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data[].id").description("Идентификатор категории"),
                                fieldWithPath("data[].name").description("Название категории"),
                                fieldWithPath("data[].description").description("Описание категории").optional(),
                                fieldWithPath("data[].imageUrl").description("URL изображения категории").optional(),
                                fieldWithPath("message").description("Сообщение (может быть null)").optional(),
                                fieldWithPath("state").description("Статус ответа: SUCCESS, FAIL, ERROR, VALIDATION")
                        )
                ));
    }

    @Test
    void getAllCategories_notFound() {
        Mockito.when(categoryService.getAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/product-service/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Категории не найдены")
                .jsonPath("$.state").isEqualTo("FAIL")
                .consumeWith(document("get-all-categories-empty",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("null, так как данные не найдены").optional(),
                                fieldWithPath("message").description("Сообщение об ошибке"),
                                fieldWithPath("state").description("Статус ответа: FAIL, ERROR, VALIDATION")
                        )
                ));
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
                        .build(),
                CategoryDto.builder()
                        .id(2L)
                        .name("Кухонная мебель")
                        .description("Описание кухонной мебели")
                        .build()
        );
    }
}
