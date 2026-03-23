package ru.mentee.power.crm.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.entity.DealProduct;
import ru.mentee.power.crm.spring.repository.DealJpaRepository;
import ru.mentee.power.crm.spring.repository.ProductJpaRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DealProductIntegrationTest {

    @Autowired
    private DealJpaRepository dealRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testSaveDealWithProducts() {
        Deal deal = new Deal();
        deal.setAmount(new BigDecimal(150000));

        Product product1 = new Product();
        product1.setName("Ноутбук Dell");
        product1.setPrice(new BigDecimal(90000));
        product1.setSku("LAPTOP-001");

        Product product2 = new Product();
        product2.setName("Монитор LG");
        product2.setPrice(new BigDecimal(25000));
        product2.setSku("MONITOR-001");

        productRepository.save(product1);
        productRepository.save(product2);

        DealProduct dealProduct1 = new DealProduct();
        dealProduct1.setProduct(product1);
        dealProduct1.setQuantity(2);
        dealProduct1.setUnitPrice(new BigDecimal(81000));

        DealProduct dealProduct2 = new DealProduct();
        dealProduct2.setProduct(product2);
        dealProduct2.setQuantity(1);
        dealProduct2.setUnitPrice(new BigDecimal(25000));

        deal.addDealProduct(dealProduct1);
        deal.addDealProduct(dealProduct2);

        UUID dealId = dealRepository.save(deal).getId();

        Deal dealFromRepository = dealRepository.findById(dealId).get();

        assertThat(dealFromRepository.getDealProducts().size()).isEqualTo(2);

        assertThat(dealFromRepository.getDealProducts().get(0).getQuantity()).isEqualTo(2);
        assertThat(dealFromRepository.getDealProducts().get(0).getUnitPrice()).isEqualTo(new BigDecimal(81000));

        assertThat(dealFromRepository.getDealProducts().get(1).getQuantity()).isEqualTo(1);
        assertThat(dealFromRepository.getDealProducts().get(1).getUnitPrice()).isEqualTo(new BigDecimal(25000));
    }

    @Test
    void testEntityGraphSolvesNPlusOne(){
        Deal deal = new Deal();
        deal.setAmount(new BigDecimal(150000));

        Product product1 = new Product();
        product1.setName("Ноутбук Dell");
        product1.setPrice(new BigDecimal(150000));
        product1.setSku("LAPTOP-001");

        Product product2 = new Product();
        product2.setName("Монитор LG");
        product2.setPrice(new BigDecimal(25000));
        product2.setSku("MONITOR-001");

        Product product3 = new Product();
        product3.setName("Playstation 4");
        product3.setPrice(new BigDecimal(50000));
        product3.setSku("PS-001");

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        DealProduct dealProduct1 = new DealProduct();
        dealProduct1.setProduct(product1);
        dealProduct1.setQuantity(2);
        dealProduct1.setUnitPrice(new BigDecimal(81000));

        DealProduct dealProduct2 = new DealProduct();
        dealProduct2.setProduct(product2);
        dealProduct2.setQuantity(1);
        dealProduct2.setUnitPrice(new BigDecimal(20000));

        DealProduct dealProduct3 = new DealProduct();
        dealProduct3.setProduct(product3);
        dealProduct3.setQuantity(1);
        dealProduct3.setUnitPrice(new BigDecimal(50000));

        deal.addDealProduct(dealProduct1);
        deal.addDealProduct(dealProduct2);
        deal.addDealProduct(dealProduct3);

        UUID dealId = dealRepository.save(deal).getId();

        entityManager.flush();
        entityManager.clear();

        Deal dealFromRepository = dealRepository.findById(dealId).get();

        for (DealProduct dealProduct : dealFromRepository.getDealProducts()) {
            System.out.println("Product name 1: "+dealProduct.getProduct().getName());
        }

        Deal dealWithProducts = dealRepository.findDealWithProducts(dealId).get();

        for (DealProduct dealProduct : dealWithProducts.getDealProducts()) {
            System.out.println("Product name 2: "+dealProduct.getProduct().getName());
        }
    }
}
