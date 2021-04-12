package com.ast.app.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Замечания по проекту:
 * Тесты: почему-то класс без пакета, один тест, в котором много действий + странное получение токена через собственный же логин.
 * В пакете service не сервисы, также с dao.
 * Странное формирование именования апи.
 * Самописная паджинация.
 * System.out в отдельных форах.
 * В методе CustomerServiceImpl.deleteCustomer нет персиста, то есть метод просто не работает (аналогично в ProductServiceImpl.deleteProduct).
 * При наличии CustomerRepository используется EntityManager.
 * Используются интерфейсы - это плюс.
 *
 * Нет целостности видения проекта, местами с ошибками. Вызывает вопросы работа с базой и организация тестов.
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
