package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.domain.Product;
import ru.mentee.power.crm.spring.repository.ProductJpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Test
    void shouldSaveAndFindProduct_whenValidData() {
        // Given
        Product product = new Product();
        product.setName("Консультация по архитектуре");
        product.setSku("CONSULT-ARCH-001");
        product.setPrice(new BigDecimal("50000.00"));
        product.setActive(true);

        // When
        Product saved = productRepository.save(product);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSku()).isEqualTo("CONSULT-ARCH-001");
    }

    @Test
    void shouldFindBySku_whenValidData() {
        // Given
        Product product = new Product();
        product.setName("Консультация по архитектуре");
        product.setSku("CONSULT-ARCH-001");
        product.setPrice(new BigDecimal("50000.00"));
        product.setActive(true);

        // When
        Product saved = productRepository.save(product);

        assertThat(productRepository.findBySku("CONSULT-ARCH-001").get()).isEqualTo(saved);
    }

    @Test
    void shouldFindByActive_whenValidData() {
        Product product1 = new Product();
        product1.setName("Консультация по архитектуре");
        product1.setSku("CONSULT-ARCH-001");
        product1.setPrice(new BigDecimal("50000.00"));
        product1.setActive(true);

        Product product2 = new Product();
        product2.setName("Разработка ПО");
        product2.setSku("DEVELOP-001");
        product2.setPrice(new BigDecimal("50000.00"));
        product2.setActive(true);

        Product product3 = new Product();
        product3.setName("Создание сайта");
        product3.setSku("CREATE-SITE-001");
        product3.setPrice(new BigDecimal("50000.00"));

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        assertThat(productRepository.findByActiveTrue()).contains(product1, product2);
    }


    // TODO: реализуйте тест для unique constraint на SKU
    // Given продукт с SKU "TEST-001" сохранён
    // When пытаемся сохранить второй продукт с тем же SKU
    // Then выбрасывается DataIntegrityViolationException

    @Test
    void shouldThrowException_whenSavedDuplicate(){
        Product product1 = new Product();
        product1.setName("Консультация по архитектуре");
        product1.setSku("CONSULT-ARCH-001");
        product1.setPrice(new BigDecimal("50000.00"));
        product1.setActive(true);

        Product product2 = new Product();
        product2.setName("Разработка ПО");
        product2.setSku("CONSULT-ARCH-001");
        product2.setPrice(new BigDecimal("50000.00"));
        product2.setActive(true);

        productRepository.save(product1);
        productRepository.flush();

        assertThatThrownBy(() -> {
            productRepository.save(product2);
            productRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}