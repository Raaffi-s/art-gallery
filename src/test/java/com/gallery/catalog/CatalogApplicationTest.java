package com.gallery.catalog;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class CatalogApplicationTest {

    @Test
    void canCreateApplicationInstance() {
        assert new CatalogApplication() != null;
    }

    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            CatalogApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(CatalogApplication.class, args));
        }
    }
}
