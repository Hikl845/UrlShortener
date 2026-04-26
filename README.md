**# URL Shortener API

## 📌 Description
REST API service for shortening URLs with authentication and statistics.

## 🚀 Features
- User registration & login (JWT)
- Create short links
- Redirect using short link
- Track click statistics
- Expiration for links
- Role-based access

## 🛠 Tech Stack
- Java 17
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Flyway
- Docker
- JUnit + Mockito
- Testcontainers

---

## ⚙️ Environment variables
**# 🔗 URL Shortener

## 📌 Опис

URL Shortener — це REST API сервіс для скорочення довгих URL-адрес.

Користувач може:
- створювати короткі посилання
- переходити за ними
- переглядати статистику
- керувати своїми посиланнями

---

## 🚀 Основний функціонал

- ✅ Реєстрація та авторизація (JWT)
- ✅ Створення коротких посилань
- ✅ Редірект на оригінальний URL
- ✅ Підрахунок кліків
- ✅ Перегляд всіх посилань
- ✅ Перегляд тільки активних посилань
- ✅ Видалення посилань
- ✅ Статистика по посиланню
- ✅ Термін дії (expires)

---

## 🛠 Технології

- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI (Swagger)
- JUnit + Mockito
- Gradle

---

## ⚙️ Налаштування

### 🔐 Необхідні змінні оточення

Перед запуском потрібно задати:

```env
DB_URL=jdbc:postgresql://localhost:5432/url_shortener
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your_secret_key
