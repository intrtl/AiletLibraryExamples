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
        "photos": {
            "5fba8e1114ed8-7381-PHOTO-000001": {
                "error": {
                    "code": "RESULT_OK",
                    "codeInt": 1,
                    "message": "Успешно обработан"
                },
                "products": [],
                "scene_id": "5fba8e1114ed8-7381-SCENE-000001",
                "task_id": "TASKID1",
                "scene_type": "TG",
                "image_path": "/data/user/0/com.intrtl.app/app_Images/5fba8e1114ed8-7381-PHOTO-000001-ROTATED.jpg"
            }
        },
        "assortment_achievement": [
            {
                "external_id": "141e9f56-d7ed-4137-9c68-fbe61dfb0e36",
                "facing_fact": 0,
                "facing_plan": 1,
                "facing_real": 0,
                "id": "141e9f56-d7ed-4137-9c68-fbe61dfb0e36",
                "price": "",
                "price_type": "",
                "name": "Activia Пит с дыней и земляникой, Бутылка, .290",
                "category_name": "Питьевой йогурт"
            },        
            {
                "external_id": "CD0B91D9-A93A-4A40-ACEB-346CE90B8A0A",
                "facing_fact": 0,
                "facing_plan": 1,
                "facing_real": 0,
                "id": "CD0B91D9-A93A-4A40-ACEB-346CE90B8A0A",
                "price": "",
                "price_type": "",
                "name": "Activia Пит злаки-семена, Стакан, .250",
                "category_name": "Питьевой йогурт"
            }
        ],
        "share_shelf": {
            "share_shelf_by_visit": [
                {
                    "value": 10,
                    "value_previous": 10
                }
            ],
            "share_shelf_by_macrocategories": [
                {
                    "product_macro_category_id": "5e52367cb9dca-4987",
                    "product_macro_category_name": "BABY FOOD",
                    "value": 0,
                    "value_previous": 0,
                    "facing": 0
                },
                {
                    "product_macro_category_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                    "product_macro_category_name": "Modern Dairy",
                    "value": 17,
                    "value_previous": 0,
                    "facing": 0
                }
            ],
            "share_shelf_by_categories": [
                {
                    "product_category_id": "5e52367cb9dca-4987",
                    "macro_category_id": "5e52367cb9dca-4987",
                    "product_category_name": "BABY FOOD",
                    "value": 0,
                    "value_previous": 0,
                    "facing": "0"
                },
                {
                    "product_category_id": "15bc3f11-4146-11e8-8479-000d3a29f071",
                    "macro_category_id": "df9e03dd-e7a2-45d6-8e4d-fa80a03ecc5e",
                    "product_category_name": "BABY_MD",
                    "value": 4,
                    "value_previous": 0,
                    "facing": "0"
                }
            ],
            "share_shelf_by_brands": [
                {
                    "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                    "product_category_id": "5e52367cb9dca-4987",
                    "brand_name": "Агуша",
                    "value": 0,
                    "value_previous": 0,
                    "facing": "0",
                    "is_own": 0
                },
                {
                    "brand_id": "86c15f23-8b3a-4dcf-a2e4-b2aba9c1041d",
                    "product_category_id": "15bc3f11-4146-11e8-8479-000d3a29f071",
                    "brand_name": "Tema",
                    "value": 0,
                    "value_previous": 0,
                    "facing": "0",
                    "is_own": 1
                }
            ],
            "share_shelf_type": "facing",
            "share_shelf_name": "12_1"
        },
        "share_shelf_by_metrics": [
            {
                "share_shelf_by_visit": [
                    {
                        "value": 10,
                        "value_previous": 10
                    }
                ],
                "share_shelf_by_macrocategories": [
                    {
                        "product_macro_category_id": "5e52367cb9dca-4987",
                        "product_macro_category_name": "BABY FOOD",
                        "value": 0,
                        "value_previous": 0,
                        "facing": 0
                    }
                ],
                "share_shelf_by_categories": [
                    {
                        "product_category_id": "5e52367cb9dca-4987",
                        "macro_category_id": "5e52367cb9dca-4987",
                        "product_category_name": "BABY FOOD",
                        "value": 0,
                        "value_previous": 0,
                        "facing": "0"
                    }
                ],
                "share_shelf_by_brands": [
                    {
                        "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                        "product_category_id": "5e52367cb9dca-4987",
                        "brand_name": "Агуша",
                        "value": 0,
                        "value_previous": 0,
                        "facing": "0",
                        "is_own": 0                    
                    }
                ],
                "share_shelf_type": "facing",
                "share_shelf_name": "12_1"
            },
            {
                "share_shelf_by_visit": [
                    {
                        "value": 0,
                        "value_previous": 0
                    }
                ],
                "share_shelf_by_macrocategories": [
                    {
                        "product_macro_category_id": "5e52367cb9dca-4987",
                        "product_macro_category_name": "BABY FOOD",
                        "value": 0,
                        "value_previous": 0,
                        "facing": 0
                    }
                ],
                "share_shelf_by_categories": [
                    {
                        "product_category_id": "5e52367cb9dca-4987",
                        "macro_category_id": "5e52367cb9dca-4987",
                        "product_category_name": "BABY FOOD",
                        "value": 0,
                        "value_previous": 0,
                        "facing": "0"
                    }
                ],
                "share_shelf_by_brands": [
                    {
                        "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                        "product_category_id": "5e52367cb9dca-4987",
                        "brand_name": "Агуша",
                        "value": 0,
                        "value_previous": 0,
                        "facing": "0",
                        "is_own": 0
                    }
                ],
                "share_shelf_type": "column_cm",
                "share_shelf_name": "ailet_metrica_stolb_sm"
            }
        ],
        "result": {
            "visit_id": "VISITID1",
            "total_photos": 1,
            "sended_photos": 1,
            "code": "RESULT_OK",
            "codeInt": 1,
            "message": "Успешно обработан"
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
