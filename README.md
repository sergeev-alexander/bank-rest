# Тестовое задание для Effective Mobile

### Система управления банковскими картами

<details>
    <summary>
        <b>Запуск</b>
    </summary>

```ignorelang
# Клонировать репо
git clone https://github.com/sergeev-alexander/bank-rest.git

# Запустить
docker-compose up -d
```

#### Админ добавлен через миграцию
- Email: `admin@bank.com`
- Пароль: `admin123`

Адрес: http://localhost:8080
</details>

<details>
    <summary>
        <b>Тестирование</b>
    </summary>

#### Интеграционное тестирование

```ignorelang
# Запуск (должен работать docker engine)
mvn test
```
#### End-to-end тестирование API



1. Импортируйте коллекцию `Bank-Cards-API.postman_collection.json` в Postman
2. Удалите volumes `docker volume rm bank-rest_postgres_data`
3. Запустите приложение `docker-compose up -d`
4. Запустите коллекцию `Run collection`

</details>

<details>
    <summary>
        <b>Документация</b>
    </summary>

Swagger: http://localhost:8080/swagger-ui.html
</details>
