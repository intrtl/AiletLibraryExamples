# Взаимодействие через Интенты

Позволяет использовать приложение IR без интеграции библиотеки, достаточно что бы приложение IR было устновлено на устройстве.

- [Взаимодействие через Интенты](#взаимодействие-через-интенты)
  - [Вызов](#вызов)
    - [Методы](#методы)
    - [Параметры вызова](#параметры-вызова)
    - [Пример вызова метода](#пример-вызова-метода)
  - [Ответ](#ответ)
    - [Формат данных ответа](#формат-данных-ответа)
    - [Статусы](#статусы)
    - [Пример обработки ответа](#пример-обработки-ответа)
  - [Broadcast-сообщение](#broadcast-сообщение)
    - [Содержимое broadcast-сообщения](#содержимое-broadcast-сообщения)
    - [Пример обработки broadcast-сообщения](#пример-обработки-broadcast-сообщения)
  - [Пример отчета (поле result в broadcast и getData() в onActivityResult)](#пример-отчета-поле-result-в-broadcast-и-getdata-в-onactivityresult)

## Вызов

### Методы

Метод  | Описание
------------- | -------------
com.intrtl.app.ACTION_VISIT  | Создание/редактирование визита (activity)
com.intrtl.app.ACTION_REPORT | Отчет по визиту (json)
com.intrtl.app.ACTION_SUMMARY_REPORT | Сводный отчет по визиту (activity)
com.intrtl.app.ACTION_SYNC | Запуск фонового процесса передачи фото и получения результатов

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
task_id | ИД задачи | visit
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
local_visit_id | Внутренний идентификатор визита | кроме метода ACTION_SYNC
photosCounter | Количество сделанных фото | при status != ошибке и методе != ACTION_SYNC
scenesCounter | Количество сцен | при status != ошибке и методе != ACTION_SYNC
notDetectedPhotosCounter | Количество фото, по которым не получены данные | при status != ошибке и методе != ACTION_SYNC
notDetectedScenesCounter | Количество сцен, по которым не получены данные | при status != ошибке и методе != ACTION_SYNC
report | Отчет | при status == RESULT_OK и методе != ACTION_SYNC

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
ERROR_BUSY | Метод уже выполняется

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
local_visit_id | внутренний ИД визита
user_id | ИД пользователя
store_id | ИД торговой точки
task_id | ИД задачи
total_photos | общее количество фото (не входят фото плохого качества)
completed_photos | количество обработанных фото
result | URI (строка) файла с [отчетом](#пример-отчета-поле-result-в-broadcast-и-getdata-в-onactivityresult)

### Пример обработки broadcast-сообщения

```java
new BroadcastReceiver() {
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

this.registerReceiver(shareShelfBroadcast, new IntentFilter("com.intrtl.app.BROADCAST_VISIT_COMPLETED"));
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
                    "value": 0,
                    "value_previous": 0
                }
            ],
            "share_shelf_by_brands": [],
            "share_shelf_by_categories": []
        },
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

