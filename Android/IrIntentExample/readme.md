# Взаимодействие через Интенты
Позволяет использовать приложение IR без интеграции библиотеки, достаточно что бы приложение IR было устновлено на устройстве.

- [Взаимодействие через Интенты](#%d0%92%d0%b7%d0%b0%d0%b8%d0%bc%d0%be%d0%b4%d0%b5%d0%b9%d1%81%d1%82%d0%b2%d0%b8%d0%b5-%d1%87%d0%b5%d1%80%d0%b5%d0%b7-%d0%98%d0%bd%d1%82%d0%b5%d0%bd%d1%82%d1%8b)
  - [Вызов](#%d0%92%d1%8b%d0%b7%d0%be%d0%b2)
    - [Методы](#%d0%9c%d0%b5%d1%82%d0%be%d0%b4%d1%8b)
    - [Параметры вызова](#%d0%9f%d0%b0%d1%80%d0%b0%d0%bc%d0%b5%d1%82%d1%80%d1%8b-%d0%b2%d1%8b%d0%b7%d0%be%d0%b2%d0%b0)
    - [Пример вызова метода](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%b2%d1%8b%d0%b7%d0%be%d0%b2%d0%b0-%d0%bc%d0%b5%d1%82%d0%be%d0%b4%d0%b0)
  - [Ответ](#%d0%9e%d1%82%d0%b2%d0%b5%d1%82)
    - [Формат поля json](#%d0%a4%d0%be%d1%80%d0%bc%d0%b0%d1%82-%d0%bf%d0%be%d0%bb%d1%8f-json)
      - [Пример ответа](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%be%d1%82%d0%b2%d0%b5%d1%82%d0%b0)
      - [Пример обработки ответа](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%be%d0%b1%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b8-%d0%be%d1%82%d0%b2%d0%b5%d1%82%d0%b0)
      - [Статусы](#%d0%a1%d1%82%d0%b0%d1%82%d1%83%d1%81%d1%8b)

## Вызов 
### Методы
Метод  | Описание 
------------- | -------------
visit  |Создание/редактирование визита (activity)   
report |Отчет по визиту (json)
summaryReport |Сводный отчет по визиту (activity)

### Параметры вызова
Поле  | Описание | Обязательно для методов
------------- | ------------- | -------------
method  | Метод | для всех
login | Логин пользователя | для всех
password | Пароль пользователя | для всех
id | ИД пользователя | для всех, если используется технический пользователь
visit_id | ИД визита | visit, report, summaryReport
store_id | ИД торговой точки | visit

### Пример вызова метода
```java
Intent intent = getPackageManager().getLaunchIntentForPackage("com.intelligenceretail.www.pilot");
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "visit");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id);
                    intent.putExtra("store_id", store_id);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_VISIT);
                }
```

## Ответ
Поле  | Описание
------------- | -------------
error  | Ошибка, при resultCode == RESULT_CANCELED
json | Результат операции

### Формат поля json
Поле  | Описание | Наличие в ответе
------------- | ------------- | -------------
photosCounter  | Количество сделанных фото | при status != ошибке
scenesCounter  | Количество сцен | при status != ошибке
notDetectedPhotosCounter  | Количество фото, по которым не получены данные | при status != ошибке
notDetectedScenesCounter  | Количество сцен, по которым не получены данные | при status != ошибке
nonValidPhotosCounter  | Количество некачественных и не принятых пользователем фото | при status != ошибке
report  | Отчет (формат отчета в документации) | только при status == RESULT_OK
status  | Статус выполнения метода | всегда

#### Пример ответа
```json
{
    "photosCounter": 1,
    "scenesCounter": 1,
    "notDetectedPhotosCounter": 0,
    "notDetectedScenesCounter": 0,
    "nonValidPhotosCounter": 0,
    "report": {...},
    "status": "RESULT_OK"
}
```

#### Пример обработки ответа
```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case (ACTIVITY_RESULT_START_IR_VISIT):
                    if (data.getExtras() != null) {
                    try {
                        addlog(data.getExtras().getString("json"));
                        JSONObject json = new JSONObject(data.getExtras().getString("json"));
                        Toast.makeText(getBaseContext(), mode + " " + json.getString("status"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (data.getExtras() != null)
                Toast.makeText(getBaseContext(), "ERROR_ACTIVITY_RESULT " + data.getExtras().getString("error"), Toast.LENGTH_LONG).show();
        }
    }
```

#### Статусы 
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
RESULT_EMPTY | Пустой отчет
RESULT_INPROGRESS | Данные в обработке
ERROR_AUTH | Ошибка авторизации
ERROR_NO_INET | Отсутствие интернета