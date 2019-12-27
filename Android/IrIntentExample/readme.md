# Взаимодействие через Интенты
Позволяет использовать приложение IR без интеграции библиотеки, достаточно что бы приложение IR было устновлено на устройстве.

## Перечень методов

Метод  | Описание 
------------- | -------------
visit  |Создание/редактирование визита (activity)   
report |Отчет по визиту (json)
summaryReport |Сводный отчет по визиту (activity)

## Вызов
```java
Intent intent = getPackageManager().getLaunchIntentForPackage(com.intelligenceretail.www.pilot);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "visit");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("visit_id", visit_id);
                    intent.putExtra("store_id", store_id);//Только для метода visit
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_VISIT);
                }
```

## Ответ
Поле  | Описание
------------- | -------------
error  |Статус выполнения операции
json | Результат операции, если имеется

## Статусы выполнения операции
Статус  | Описание
------------- | -------------
RESULT_OK | Успешно
ERROR | Ошибка
ERROR_TOKEN | Ошибка токена
ERROR_STORE_ID_INCORRECT | Некорректный ИД торновой точки
ERROR_VISIT_ID_INCORRECT | Неорректный ИД визита
ERROR_NOVISIT | Визита не существует
ERROR_STORES_EMPTY | Справочник торговых точек пустой
ERROR_INCORRECT_INPUT_PARAMS | Неверные входные параметры
ERROR_INCORRECT_METHOD | Неверный метод
