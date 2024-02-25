package de.hsflensburg.certainlyuncertainse2hausarbeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class CertainlyUncertainSe2HausarbeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertainlyUncertainSe2HausarbeitApplication.class, args);
    }
}
