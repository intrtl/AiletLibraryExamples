# Взаимодействие через Интенты
Позволяет использовать приложение IR без интеграции библиотеки, достаточно что бы приложение IR было устновлено на устройстве.

## Перечень методов
- visit - открытие режима фото (activity)   
- report - отчет по визиту (json результат)
- summaryReport - сводный отчет по визиту (activity)

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
Методы visit и summaryReport в ответе содержат поле error, которое будет содержать код заверешения операции.
Метод report так же содержит поле json, в котором находится отчет.