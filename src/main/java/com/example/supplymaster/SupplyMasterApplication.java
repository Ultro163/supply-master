package com.example.supplymaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения SupplyMaster.
 * <p>
 * Запускает Spring Boot приложение, автоматически конфигурируя контекст.
 */
@SpringBootApplication
public class SupplyMasterApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(SupplyMasterApplication.class, args);
    }
}