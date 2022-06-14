# Взаимодействие через Интенты

Позволяет использовать приложение IR без интеграции библиотеки, достаточно что бы приложение IR было устновлено на устройстве.

- [Взаимодействие через Интенты](#взаимодействие-через-интенты)
  - [Вызов](#вызов)
    - [Методы](#методы)
    - [Параметры вызова](#параметры-вызова)
    - [Пример вызова метода](#пример-вызова-метода)
  - [Ответ](#ответ)
    - [Формат данных ответа](#формат-данных-ответа)
    - [Получение изображений из report](#получение-изображений-из-report)
    - [Статусы](#статусы)
    - [Пример обработки ответа](#пример-обработки-ответа)
  - [Broadcast-сообщение](#broadcast-сообщение)
    - [Содержимое broadcast-сообщения](#содержимое-broadcast-сообщения)
    - [Пример обработки broadcast-сообщения](#пример-обработки-broadcast-сообщения)
  - [Пример отчета (поле result в broadcast и getData() в onActivityResult)](#пример-отчета-поле-result-в-broadcast-и-getdata-в-onactivityresult)
  - [Пример взаимодействия](#пример-взаимодействия)
  - [Возможные проблемы при интеграции](#возможные-проблемы-при-интеграции)
    - [Особенности Android 11](#особенности-android-11)

## Вызов

### Методы

Метод  | Описание
------------- | -------------
com.intrtl.app.ACTION_VISIT  | Создание/редактирование визита (activity)
com.intrtl.app.ACTION_REPORT | Отчет по визиту (json)
com.intrtl.app.ACTION_SUMMARY_REPORT | Сводный отчет по визиту (activity)
com.intrtl.app.ACTION_SYNC | Запуск фонового процесса передачи фото и получения результатов

### Параметры вызова

Поле  | Описание | Обязательно для методов | Необязательно для методов
------------- | ------------- | ------------- | -------------
**Параметры**  | |
action  | Метод | 
**Extra**  | | 
login | Логин пользователя | для всех
password | Пароль пользователя | для всех
id | ИД пользователя | для всех, если используется технический пользователь
visit_id | ИД визита | visit, report, summaryReport
task_id | ИД задачи | | visit, report, summaryReport
store_id | ИД торговой точки | visit

### Пример вызова метода

```java
Intent intent = new Intent();
if (intent != null) {
    intent.setAction("com.intrtl.app.ACTION_VISIT");
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

[Пример](#пример-отчета-поле-result-в-broadcast-и-getdata-в-onactivityresult)

Поле  | Описание | Наличие в ответе
------------- | ------------- | -------------
status | Статус выполнения метода | всегда
user_id | Идентификатор пользователя | кроме метода ACTION_SYNC
store_id | Идентификатор магазина | кроме метода ACTION_SYNC
task_id | Идентификатор задачи | кроме метода ACTION_SYNC
visit_id | Идентификатор визита | кроме метода ACTION_SYNC
internal_visit_id | Внутренний идентификатор визита | кроме метода ACTION_SYNC
install_id | ИД установки | кроме метода ACTION_SYNC
photosCounter | Количество сделанных фото | при status != ERROR_VISIT_ID_INCORRECT и методе != ACTION_SYNC
scenesCounter | Количество сцен | при status != ERROR_VISIT_ID_INCORRECT и методе != ACTION_SYNC
notDetectedPhotosCounter | Количество фото, по которым не получены данные | при status != ERROR_VISIT_ID_INCORRECT и методе != ACTION_SYNC
notDetectedScenesCounter | Количество сцен, по которым не получены данные | при status != ERROR_VISIT_ID_INCORRECT и методе != ACTION_SYNC
report | Отчет | при status == RESULT_OK и методе != ACTION_SYNC

### Получение изображений из report

В новых версиях ОС Android (9 и новее) для получения пути файла изображения вместо image_path необходимо использовать image_uri. 
Пример получения изображение:

```kotlin
private fun readBitmapFromUri(uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(this.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    }
}
```

```kotlin
val photosJSON = json.getJSONObject("report").getJSONObject("photos")
val photoNamesList = ArrayList<String>()
for ( i in 0 until photosJSON.length()) {
    photoNamesList.add(photosJSON.names()[i] as String)
}
val arrayOfBitmap = photoNamesList.map {
    val photoUri = Uri.parse(photosJSON.getString(it))
    readBitmapFromUri(photoUri)
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
ERROR_READONLY_VISIT | Визита только для чтения
ERROR_INCORRECT_INPUT_PARAMS | Неверные входные параметры
ERROR_VISIT_ID_INCORRECT | Неорректный ИД визита
ERROR_AUTH | Ошибка авторизации
ERROR_VISIT_ID_INCORRECT | Неорректный ИД визита
ERROR_PHOTO | Фото с ошибкой
ERROR_BUSY | Метод уже выполняется
ERROR_CANT_LOAD_VISIT | Невозможно загрузить визит, так как отсуствует интернет

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
            //On Error
        }
    }

    private String readFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String string;
            while ((string = reader.readLine()) != null) {
                stringBuffer.append(string);
            }
            reader.close();
            inputStreamReader.close();
            inputStream.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
```

## Broadcast-сообщение

При вызове метода visit и создании фото запускается фоновый процесс передачи фото и получения отчетов, который по завершении формирует broadcast сообщение **com.intrtl.app.BROADCAST_VISIT_COMPLETED**.

### Содержимое broadcast-сообщения

Поле  | Описание
------------- | -------------
visit_id | ИД визита
internal_visit_id | внутренний ИД визита
user_id | ИД пользователя
store_id | ИД торговой точки
total_photos | общее количество фото (не входят фото плохого качества)
completed_photos | количество обработанных фото
result | URI (строка) файла с [отчетом](#пример-отчета-поле-result-в-broadcast-и-getdata-в-onactivityresult)

### Пример обработки broadcast-сообщения

```java
BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                String reportString = readFromUri(new Uri().parse(extras.getString("result")))
                JSONObject reportJson = new JSONObject(reportString);                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
};

this.registerReceiver(broadcastReceiver, new IntentFilter("com.intrtl.app.BROADCAST_VISIT_COMPLETED"));
```

## Пример отчета (поле result в broadcast и getData() в onActivityResult)

```json
{
    "photosCounter": 1,
    "scenesCounter": 1,
    "notDetectedPhotosCounter": 0,
    "notDetectedScenesCounter": 0,
    "local_visit_id": "5fba8e1114ed8-7381",
    "visit_id": "VISITID1",
    "store_id": "1",
    "user_id": "USERID1",
    "report": {
        "result": {
            "visit_id": "q",
            "total_photos": 2,
            "sended_photos": 2,
            "code": "RESULT_INPROGRESS",
            "codeInt": 14,
            "message": "Данные в обработке"
        },
        "photos": {
            "627e94940a021-9c4538fd-PHOTO-000001": {
                "products": [],
                "scene_type": "Твин/Тандем: Зона A’",
                "error": {
                    "code": "RESULT_OK",
                    "codeInt": 1,
                    "message": "Успешно обработан"
                },
                "scene_id": "627e94940a021-9c4538fd-SCENE-000001",
                "image_path": "/data/user/0/com.intrtl.lib2test/files/files/a/ac/ac0/ac0b/ac0be/ac0be330c6d6e78b803e0f8cbaf6fb9d.jpg",
                "image_url": "https://dairy.intrtl.tech/api/photo_raw/2022/05/13/627e94940a021-9c4538fd/2022-05-13-17-25-58-4668-o.jpg"
            },
            "627e94940a021-9c4538fd-PHOTO-000003": {
                "products": [],
                "scene_type": "Твин/Тандем: Зона A’",
                "error": {
                    "code": "RESULT_OK",
                    "codeInt": 1,
                    "message": "Успешно обработан"
                },
                "scene_id": "627e94940a021-9c4538fd-SCENE-000001",
                "image_path": "/data/user/0/com.intrtl.lib2test/files/files/6/64/644/6448/6448f/6448f32805274d437f18f7b24b5a7297.jpg",
                "image_url": "https://dairy.intrtl.tech/api/photo_raw/2022/05/13/627e94940a021-9c4538fd/2022-05-13-17-30-49-5861-o.jpg"
            }
        },
        "assortment_achievement": [
            {
                "id": "5d63e7081283c-5118",
                "external_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                "facing_fact": 0,
                "facing_plan": 1,
                "price": 0,
                "price_type": 0,
                "price_range_max": 0,
                "price_range_min": 0,
                "price_status": 0,
                "name": "ACTIVIA ложковый йогурт ГРАНАТ-КРАСНЫЙ ЧАЙ, формованный стакан, .170",
                "category_name": "Modern Dairy"
            },
            {
                "id": "b76ced83-790d-4226-8f39-b0351af77bbd",
                "external_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                "facing_fact": 0,
                "facing_plan": 1,
                "price": 0,
                "price_type": 0,
                "price_range_max": 0,
                "price_range_min": 0,
                "price_status": 0,
                "name": "Activia Другое Пит, Бутылка, .290",
                "category_name": "Modern Dairy"
            }
        ],
        "assortment_achievement_by_metrics": [
            {
                "products": [
                    {
                        "id": "5d63e7081283c-5118",
                        "external_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "facing_fact": 0,
                        "facing_plan": 1,
                        "price": 0,
                        "price_type": 0,
                        "price_range_max": 0,
                        "price_range_min": 0,
                        "price_status": 0,
                        "name": "ACTIVIA ложковый йогурт ГРАНАТ-КРАСНЫЙ ЧАЙ, формованный стакан, .170",
                        "category_name": "Modern Dairy"
                    },
                    {
                        "id": "b76ced83-790d-4226-8f39-b0351af77bbd",
                        "external_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "facing_fact": 0,
                        "facing_plan": 1,
                        "price": 0,
                        "price_type": 0,
                        "price_range_max": 0,
                        "price_range_min": 0,
                        "price_status": 0,
                        "name": "Activia Другое Пит, Бутылка, .290",
                        "category_name": "Modern Dairy"
                    }
                ],
                "assortment_achievement_name": "FilterTwoGroupScene"
            },
            {
                "products": [
                    {
                        "id": "5cee51d262c55-7223",
                        "external_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "facing_fact": 3,
                        "facing_plan": 0,
                        "price": 79,
                        "price_type": 1,
                        "price_range_max": 0,
                        "price_range_min": 0,
                        "price_status": 0,
                        "name": "ACTIMEL Функциональный напиток МАНГО-МАТЕ-ЖЕНЬШЕНЬ, Бутылка, .100",
                        "category_name": "Modern Dairy"
                    }
                ],
                "assortment_achievement_name": "FilterGroupScene"
            }
        ],
        "share_shelf": {
            "share_shelf_by_visit": [
                {
                    "plan": 0,
                    "percent": 0,
                    "value": 89,
                    "value_previous": 80,
                    "numerator": 8,
                    "denominator": 9
                }
            ],
            "share_shelf_by_macrocategories": [
                {
                    "product_macro_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                    "product_macro_category_name": "Tradi Dairy",
                    "value": 9,
                    "percent": 88.9,
                    "matched": 8
                }
            ],
            "share_shelf_by_categories": [
                {
                    "macro_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                    "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                    "product_category_name": "Tradi Dairy",
                    "value": 9,
                    "percent": 88.9,
                    "matched": 8
                }
            ],
            "share_shelf_by_brands": [
                {
                    "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                    "brand_id": "b7967cd8-77ea-41c5-bcc4-b69e8a65c223",
                    "brand_name": "Prostokvashino",
                    "is_own": 1,
                    "value": 8,
                    "percent": 88.9
                },
                {
                    "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                    "brand_id": "a45f430d-9e3b-11e7-a5c2-000d3a250e47",
                    "brand_name": "Другое",
                    "is_own": 0,
                    "value": 1,
                    "percent": 11.1
                }
            ],
            "share_shelf_type": "facing",
            "share_shelf_name": "Tradi Dairy_милютина"
        },
        "share_shelf_by_metrics": [
            {
                "share_shelf_by_visit": [
                    {
                        "plan": 0,
                        "percent": 0,
                        "value": 89,
                        "value_previous": 80,
                        "numerator": 8,
                        "denominator": 9
                    }
                ],
                "share_shelf_by_macrocategories": [
                    {
                        "product_macro_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "product_macro_category_name": "Tradi Dairy",
                        "value": 9,
                        "percent": 88.9,
                        "matched": 8
                    }
                ],
                "share_shelf_by_categories": [
                    {
                        "macro_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "product_category_name": "Tradi Dairy",
                        "value": 9,
                        "percent": 88.9,
                        "matched": 8
                    }
                ],
                "share_shelf_by_brands": [
                    {
                        "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "brand_id": "b7967cd8-77ea-41c5-bcc4-b69e8a65c223",
                        "brand_name": "Prostokvashino",
                        "is_own": 1,
                        "value": 8,
                        "percent": 88.9
                    },
                    {
                        "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "brand_id": "a45f430d-9e3b-11e7-a5c2-000d3a250e47",
                        "brand_name": "Другое",
                        "is_own": 0,
                        "value": 1,
                        "percent": 11.1
                    }
                ],
                "share_shelf_type": "facing",
                "share_shelf_name": "Tradi Dairy_милютина"
            },
            {
                "share_shelf_by_visit": [
                    {
                        "plan": 0,
                        "percent": 0,
                        "value": 0,
                        "value_previous": 0,
                        "numerator": 0,
                        "denominator": 103
                    }
                ],
                "share_shelf_by_macrocategories": [
                    {
                        "product_macro_category_id": "60dc6900aa1ae-2303",
                        "product_macro_category_name": "Baby&Kids ambient",
                        "value": 1,
                        "percent": 0,
                        "matched": 0
                    },
                    {
                        "product_macro_category_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "product_macro_category_name": "Modern Dairy",
                        "value": 94,
                        "percent": 0,
                        "matched": 0
                    },
                    {
                        "product_macro_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "product_macro_category_name": "Tradi Dairy",
                        "value": 8,
                        "percent": 0,
                        "matched": 0
                    }
                ],
                "share_shelf_by_categories": [
                    {
                        "macro_category_id": "60dc6900aa1ae-2303",
                        "product_category_id": "60dc6900aa1ae-2303",
                        "product_category_name": "Baby&Kids ambient",
                        "value": 1,
                        "percent": 0,
                        "matched": 0
                    },
                    {
                        "macro_category_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "product_category_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "product_category_name": "Modern Dairy",
                        "value": 94,
                        "percent": 0,
                        "matched": 0
                    },
                    {
                        "macro_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "product_category_id": "df44a7b9-58ff-4a95-a2fc-9a6d1015ce35",
                        "product_category_name": "Tradi Dairy",
                        "value": 8,
                        "percent": 0,
                        "matched": 0
                    }
                ],
                "share_shelf_by_brands": [
                    {
                        "product_category_id": "60dc6900aa1ae-2303",
                        "brand_id": "86c15f23-8b3a-4dcf-a2e4-b2aba9c1041d",
                        "brand_name": "Tema",
                        "is_own": 1,
                        "value": 1,
                        "percent": 100
                    },
                    {
                        "product_category_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                        "brand_id": "7591ae4b-84bc-4061-a518-6ae939a4ed6b",
                        "brand_name": "Activia",
                        "is_own": 1,
                        "value": 28,
                        "percent": 29.8
                    }
                ],
                "share_shelf_type": "facing",
                "share_shelf_name": "12_1"
            }
        ],
        "perfectstore": {
            "tasks": [
                {
                    "kpis": [
                        {
                            "name": "OSA SKU all",
                            "metric_type": "osa_sku",
                            "matrix_type": "general",
                            "plan_value": 25,
                            "fact_value": 4,
                            "percentage": 0.16,
                            "score_value": 0
                        },
                        {
                            "name": "OSA Facing all",
                            "metric_type": "osa_facing",
                            "matrix_type": "FilterOneScene",
                            "plan_value": 26,
                            "fact_value": 8,
                            "percentage": 0.30769232,
                            "score_value": 0
                        }
                    ],
                    "questions": [],
                    "id": "624312c514e54-7fe3ff5f",
                    "name": "Portovaya19_task_all",
                    "percentage": 1,
                    "total_score": 60
                },
                {
                    "kpis": [],
                    "questions": [
                        {
                            "index": 1,
                            "type": "select",
                            "name": "Категория распологается в радиусе 3-х метров от кассы или на основной полке непосредственно за спиной продавца",
                            "answers": [
                                {
                                    "index": 2,
                                    "name": "НЕТ - Категория распологается в радиусе 3-х метров от кассы или на основной полке непосредственно за спиной продавца",
                                    "point": 0
                                }
                            ]
                        },
                        {
                            "index": 2,
                            "type": "text",
                            "name": "Категория распологается в радиусе 3-х метров от кассы или на основной полке непосредственно за спиной продавца 22",
                            "answers": [
                                {
                                    "index": 0,
                                    "name": "а",
                                    "point": 0
                                }
                            ]
                        },
                        {
                            "index": 3,
                            "type": "text",
                            "name": "Категория распологается в радиусе 3-х метров от кассы или на основной полке непосредственно за спиной продавца 22",
                            "answers": [
                                {
                                    "index": 0,
                                    "name": "5",
                                    "point": 0
                                }
                            ]
                        }
                    ],
                    "id": "624312c514e54-3cff9776",
                    "name": "Task_port_with_quest",
                    "percentage": 0,
                    "total_score": 0
                },
                {
                    "kpis": [
                        {
                            "name": "OSA SKU task1",
                            "metric_type": "osa_sku",
                            "matrix_type": "general",
                            "plan_value": 11,
                            "fact_value": 2,
                            "percentage": 0.18181819,
                            "score_value": 2
                        },
                        {
                            "name": "OSA Facing task1",
                            "metric_type": "osa_facing",
                            "matrix_type": "FilterOneScene",
                            "plan_value": 10,
                            "fact_value": 5,
                            "percentage": 0.5,
                            "score_value": 2
                        }
                    ],
                    "questions": [
                        {
                            "index": 1,
                            "type": "select",
                            "name": "Вопрос с одним вариантом",
                            "answers": [
                                {
                                    "index": 1,
                                    "name": "11",
                                    "point": 0
                                }
                            ]
                        },
                        {
                            "index": 2,
                            "type": "multiselect",
                            "name": "Вопрос с множ. выбором",
                            "answers": [
                                {
                                    "index": 1,
                                    "name": "test1",
                                    "point": 0
                                }
                            ]
                        },
                        {
                            "index": 3,
                            "type": "text",
                            "name": "Вопрос со свободным ответом",
                            "answers": [
                                {
                                    "index": 0,
                                    "name": "п",
                                    "point": 0
                                }
                            ]
                        }
                    ],
                    "id": "624312c514e54-ff774568",
                    "name": "Portovaya19_task_1",
                    "percentage": 0.7522503,
                    "total_score": 164
                }
            ],
            "total_visit_score": 224
        }
    },
    "status": "RESULT_OK"
}
```

## Пример взаимодействия

- Вызовать приложение Ailet с методом visit 
- Выполнить визит с несколькими фото
- Выйти из приложения Ailet
- Проверить результат, если RESULT_INPROGRESS, то необходимо ожидать бродкаст сообщение о готовности, если RESULT_OK, то отчет содержит готовые данные
- При получении бродкаста со статусом RESULT_OK обработать отчет, он содержит готовые данные, так же можно вызвать приложение Ailet с методом report или summaryReport

## Возможные проблемы при интеграции

### Особенности Android 11

Если в проекте используется targetSdkVersion 30, то может возникнуть проблема с вызовом приложение Ailet, для ее решения есть несколько сопособов:

- добавить queries в AndroidManifest (предпочтительный вариант)

    ```xml
    <queries>
        <package android:name="com.intrtl.app" />
    </queries>
    ```

- добавить queries в AndroidManifest 

    ```xml
    <queries>
        <intent>
            <action android:name="com.intrtl.app.ACTION_VISIT" />
        </intent>
        <intent>
            <action android:name="com.intrtl.app.ACTION_REPORT" />
        </intent>
        <intent>
            <action android:name="com.intrtl.app.ACTION_SUMMARY_REPORT" />
        </intent>
        <intent>
            <action android:name="com.intrtl.app.ACTION_SYNC" />
        </intent>
    </queries>
    ```

- добавить QUERY_ALL_PACKAGES в AndroidManifest 
  
    ```xml
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
    ```

- понизить targetSdkVersion до версии 29
