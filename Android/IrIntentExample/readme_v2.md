# Взаимодействие через Интенты

Позволяет использовать приложение IR без интеграции библиотеки, достаточно что бы приложение IR было устновлено на устройстве.

- [Взаимодействие через Интенты](#%d0%92%d0%b7%d0%b0%d0%b8%d0%bc%d0%be%d0%b4%d0%b5%d0%b9%d1%81%d1%82%d0%b2%d0%b8%d0%b5-%d1%87%d0%b5%d1%80%d0%b5%d0%b7-%d0%98%d0%bd%d1%82%d0%b5%d0%bd%d1%82%d1%8b)
  - [Вызов](#%d0%92%d1%8b%d0%b7%d0%be%d0%b2)
    - [Методы](#%d0%9c%d0%b5%d1%82%d0%be%d0%b4%d1%8b)
    - [Параметры вызова](#%d0%9f%d0%b0%d1%80%d0%b0%d0%bc%d0%b5%d1%82%d1%80%d1%8b-%d0%b2%d1%8b%d0%b7%d0%be%d0%b2%d0%b0)
    - [Пример вызова метода](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%b2%d1%8b%d0%b7%d0%be%d0%b2%d0%b0-%d0%bc%d0%b5%d1%82%d0%be%d0%b4%d0%b0)
  - [Ответ](#%d0%9e%d1%82%d0%b2%d0%b5%d1%82)
    - [Формат поля json](#%d0%a4%d0%be%d1%80%d0%bc%d0%b0%d1%82-%d0%bf%d0%be%d0%bb%d1%8f-json)
    - [Пример поля json](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%bf%d0%be%d0%bb%d1%8f-json)
    - [Статусы](#%d0%a1%d1%82%d0%b0%d1%82%d1%83%d1%81%d1%8b)
    - [Пример обработки ответа](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%be%d0%b1%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b8-%d0%be%d1%82%d0%b2%d0%b5%d1%82%d0%b0)
  - [Broadcast-сообщение](#broadcast-%d1%81%d0%be%d0%be%d0%b1%d1%89%d0%b5%d0%bd%d0%b8%d0%b5)
    - [Содержимое broadcast-сообщения](#%d0%a1%d0%be%d0%b4%d0%b5%d1%80%d0%b6%d0%b8%d0%bc%d0%be%d0%b5-broadcast-%d1%81%d0%be%d0%be%d0%b1%d1%89%d0%b5%d0%bd%d0%b8%d1%8f)
    - [Пример обработки broadcast-сообщения](#%d0%9f%d1%80%d0%b8%d0%bc%d0%b5%d1%80-%d0%be%d0%b1%d1%80%d0%b0%d0%b1%d0%be%d1%82%d0%ba%d0%b8-broadcast-%d1%81%d0%be%d0%be%d0%b1%d1%89%d0%b5%d0%bd%d0%b8%d1%8f)

## Вызов

### Методы

Метод  | Описание
------------- | -------------
com.intrtl.lib2.ACTION_VISIT  | Создание/редактирование визита (activity)
com.intrtl.lib2.ACTION_REPORT | Отчет по визиту (json)
com.intrtl.lib2.ACTION_SUMMARY_REPORT | Сводный отчет по визиту (activity)
com.intrtl.lib2.ACTION_SYNC | Запуск фонового процесса передачи фото и получения результатов

### Параметры вызова

Поле  | Описание | Обязательно для методов
------------- | ------------- | -------------
**Параметры**  | |
action  | Метод | 
**Extra**  | | 
login | Логин пользователя | для всех
password | Пароль пользователя | для всех
id | ИД пользователя | для всех, если используется технический пользователь
visit_id | ИД визита | visit, report, summaryReport
task_id | ИД задачи | visit, report, summaryReport
store_id | ИД торговой точки | visit

### Пример вызова метода

```java
Intent intent = new Intent();
if (intent != null) {
    intent.setAction("com.intrtl.lib2.ACTION_VISIT");
    intent.setFlags(0);                    
    intent.putExtra("login", user);
    intent.putExtra("password", password);
    intent.putExtra("id", user_id);
    intent.putExtra("visit_id", visit_id);
    intent.putExtra("store_id", store_id);
    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_VISIT);
}
```

## Ответ

Для возврата результата используется FileProvider, intent в атрибуте data содержит Uri файла с данными.

Поле  | Описание
------------- | -------------
error  | Ошибка, при resultCode == RESULT_CANCELED
data | Uri с файлом результата операции

### Формат данных ответа
Поле  | Описание | Наличие в ответе
------------- | ------------- | -------------
status | Статус выполнения метода | всегда
user_id | Идентификатор пользователя | всегда
store_id | Идентификатор магазина | всегда
task_id | Идентификатор задачи | всегда
visit_id | Идентификатор визита | всегда
local_visit_id | Внутренний идентификатор визита | всегда
photosCounter | Количество сделанных фото | при status != ошибке
scenesCounter | Количество сцен | при status != ошибке
notDetectedPhotosCounter | Количество фото, по которым не получены данные | при status != ошибке
notDetectedScenesCounter | Количество сцен, по которым не получены данные | при status != ошибке
report | Отчет (формат отчета в документации) | при status == RESULT_OK

### Пример данных ответа

```json
{
    "photosCounter": 1,
    "scenesCounter": 1,
    "notDetectedPhotosCounter": 0,
    "notDetectedScenesCounter": 0,    
    "report": {...},
    "status": "RESULT_OK"
}
```
### Пример report

```json
{
    "photos": {
        "5fb53a19025de-2083-PHOTO-000001": {
            "error": {
                "code": "RESULT_OK",
                "codeInt": 1,
                "message": "Успешно обработан"
            },
            "products": [],
            "scene_id": "5fb53a19025de-2083-SCENE-000001",
            "scene_type": "Холодильник Данон",
            "image_path": "\\/data\\/user\\/0\\/com.intrtl.app\\/app_Images\\/5fb53a19025de-2083-PHOTO-000001-ROTATED.jpg"
        },
        "5fb53a19025de-2083-PHOTO-000003": {
            "error": {
                "code": "IR_ERROR_PHOTO",
                "codeInt": 37,
                "message": "Ошибка: ERROR"
            },
            "products": [],
            "scene_id": "5fb53a19025de-2083-SCENE-000003",
            "scene_type": "Напольное оборудование (стойки,железные торцы)",
            "image_path": "\\/data\\/user\\/0\\/com.intrtl.app\\/app_Images\\/5fb53a19025de-2083-PHOTO-000003-ROTATED.jpg"
        }
    },
    "assortment_achievement": [
        {
            "facing_fact": 5,
            "facing_plan": 0,
            "facing_real": 5,
            "id": "1e2d809f-6421-11e7-965b-000d3a250e47",
            "price": "0",
            "price_type": "0",
            "name": "Другой продукт, Бутылка, .310",
            "category_name": "молочная продукция"
        },
        {
            "facing_fact": 1,
            "facing_plan": 0,
            "facing_real": 1,
            "id": "c1770468-122d-4693-97ea-a6c8042fb0fa",
            "price": "69",
            "price_type": "1",
            "name": "Other Традиционная мол прод, Бутылка, .680",
            "category_name": "молочная продукция"
        }
    ],
    "share_shelf": {
        "share_shelf_by_visit": [
            {
                "value": 0,
                "value_previous": 0
            }
        ],
        "share_shelf_by_brands": [],
        "share_shelf_by_categories": []
    },
    "result": {
        "visit_id": "1",
        "total_photos": 2,
        "sended_photos": 2,
        "code": "RESULT_OK",
        "codeInt": 1,
        "message": "Успешно обработан"
    }
}
```

### Статусы 

Статус  | Описание
------------- | -------------
RESULT_OK | Успешно
RESULT_INPROGRESS | Данные в обработке
RESULT_INPROGRESS_OFFLINE | Данные в обработке (приложение в режиме оффлайн)
RESULT_EMPTY | Пустой отчет
ERROR_NOVISIT | Визита не существует
ERROR_INCORRECT_INPUT_PARAMS | Неверные входные параметры
ERROR_VISIT_ID_INCORRECT | Неорректный ИД визита
ERROR_AUTH | Ошибка авторизации
ERROR_VISIT_ID_INCORRECT | Неорректный ИД визита
ERROR_PHOTO | Фото с ошибкой

### Пример обработки ответа

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String mode = "";
            switch (requestCode) {

                case (ACTIVITY_RESULT_START_IR_REPORT):
                    mode = "reports";
                    break;

                case (ACTIVITY_RESULT_START_IR_VISIT):
                    mode = "visit";
                    break;

                case (ACTIVITY_RESULT_START_IR_SUMMARYREPORT):
                    mode = "summaryReport";
                    break;
            }

            if (data.getData() != null) {
                String result = readFromUri(data.getData());                
                try {
                    JSONObject json = new JSONObject(result);
                    Log.i("report", json.toString());                                        
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (data.getExtras() != null)
                Toast.makeText(getBaseContext(), "ERROR_ACTIVITY_RESULT " + data.getExtras().getString("error"), Toast.LENGTH_LONG).show();
        }
    }

    private String readFromUri(Uri uri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isReader);
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = reader.readLine())!= null){
                sb.append(str);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
```