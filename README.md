# 📦 Supply Master

**Supply Master** – это приложение для управления поставками и складскими запасами.

## 🚀 Функциональность

- Приёмка поставок от поставщиков (одна поставка может содержать несколько видов продукции)
- Формирование отчётов по поступлению продукции за выбранный период
- Поддержка категорий товаров
- Поддержка PostgreSQL и FlayWay для миграций
- GraphQL API – возможность гибкого запроса данных о товарах в конкретной поставке

## 🛠️ Стек технологий

- **Java 21**
- **Spring Boot**
- **Spring Data JPA (Hibernate)**
- **GraphQL**
- **PostgreSQL**
- **Flyway**
- **Lombok**
- **Docker**

# 📊 Диаграмма базы данных

Диаграмма базы данных доступна по следующему пути: [db_diagram.bmp](db_diagram.bmp)
## 🚀 Запуск приложения

Приложение использует **Docker Compose**, который поднимает базу данных и само приложение.

### Шаги для запуска:

1. **Клонируйте проект:**
   Склонируйте репозиторий проекта на свою локальную машину

2. **Создайте файл .env:**
   ```env
   POSTGRES_USER=your_postgres_user
   POSTGRES_PASSWORD=your_postgres_password
   ```
3. Соберите проект с помощью Maven
   ```sh
   ./mvnw package
4. Убедитесь, что установлен **Docker** и **Docker Compose**.
5. В корневой папке проекта выполните команду:
   ```sh
   docker-compose up -d
   ```

6. Приложение запустится вместе с базой данных PostgreSQL.
67. GraphQL API будет доступен по адресу: `http://localhost:8080/graphiql?path=/graphql`

После успешного запуска можно выполнять запросы через Postman.

### 1. Создание новой цены

**POST** `http://localhost:8080/api/v1/prices`

```json
{
  "productId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "supplierId": "a3f2c7d4-83b3-4b68-9f88-6d7d7e1f2c57",
  "pricePerKg": 100.50,
  "startDate": "2024-03-01",
  "endDate": "2024-04-01"
}
```

### 2. Обновление цены

**PATCH** `http://localhost:8080/api/v1/prices/{priceId}`

```json
{
  "pricePerKg": 105.75
}
```

### 3. Создание поставки

**POST** `http://localhost:8080/api/v1/shipments`
Headers:
`supplierId: a3f2c7d4-83b3-4b68-9f88-6d7d7e1f2c57`

```json
{
  "shipmentDate": "2024-03-10",
  "shipmentItems": [
    {
      "productId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
      "weightKg": 500
    }
  ]
}
```

### 4. Получение отчета о поставках за период

**GET** `http://localhost:8080/api/v1/shipments?startDate=2024-03-01&endDate=2024-03-31`

Пример ответа:

```json
[
    {
        "supplierId": "ed5622f7-c232-4cc2-ba19-6f3a15941561",
        "supplierName": "Сады Кубани",
        "shipments": [
            {
                "productId": "9efbec56-7528-4774-a06f-8f3146a6f281",
                "productName": "Груши Конференция",
                "totalWeightKg": 69.60,
                "totalPrice": 4758.55
            },
            {
                "productId": "d03a8938-74ef-4ccf-9807-0e5336a40dbd",
                "productName": "Груши Вильямс",
                "totalWeightKg": 69.60,
                "totalPrice": 6408.07
            },
            {
                "productId": "e27576b2-afeb-4518-aa93-655040e92da9",
                "productName": "Яблоки Гренни Смит",
                "totalWeightKg": 69.60,
                "totalPrice": 6959.30
            }
        ]
    }
]