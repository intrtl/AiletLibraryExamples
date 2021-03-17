# Взаимодействие с приложением Intelligence Retail с помощью deeplinks.

- [Взаимодействие с приложением Intelligence Retail с помощью deeplinks.](#взаимодействие-с-приложением-intelligence-retail-с-помощью-deeplinks)
  - [Вызов метода](#вызов-метода)
    - [Создание URL](#создание-url)
    - [Параметры вызова:](#параметры-вызова)
    - [Описание методов](#описание-методов)
    - [Поведение методов в зависимости от парамтера task_id](#поведение-методов-в-зависимости-от-парамтера-task_id)
  - [Результат выполнения метода](#результат-выполнения-метода)
    - [Статус выполенения метода](#статус-выполенения-метода)
    - [Пример использования для iOS13 и SwiftUI:](#пример-использования-для-ios13-и-swiftui)
    - [Пример использования для iOS ниже 13-й версии или без SwiftUI:](#пример-использования-для-ios-ниже-13-й-версии-или-без-swiftui)
    - [Пример отчета](#пример-отчета)
    - [Пример отчета (указан task_id)](#пример-отчета-указан-task_id)

## Вызов метода
### Создание URL
Сгенерируйте URL вида `intellingenceretail:?param1=value1&param2=value2` любым удобным для вас способом, например через `URLComponents` и откройте его через UIApplication.shared.open:
```swift
var components = URLComponents()
components.scheme = "intelligenceretail"
components.queryItems = [URLQueryItem(name: "method", value: methodName),
                         URLQueryItem(name: "login", value: login),
                         URLQueryItem(name: "password", value: password),
                         URLQueryItem(name: "user_id", value: userId),
                         URLQueryItem(name: "store_id", value: storeId),
                         URLQueryItem(name: "visit_id", value: visitId),
                         URLQueryItem(name: "task_id", value: taskId),
                         URLQueryItem(name: "back_url_scheme", value: "integrationtestapp")]
let url = components.url!
UIApplication.shared.open(url, options: [:]) { (completed) in
    // Handle completion if needed
}
```

### Параметры вызова:

| Параметр | Описание |
| ------ | ------ |
| method | Наименование метода (visit, report, summaryReport, sync) |
| login | Логин пользователя |
| password | Пароль пользователя |
| user_id | внешний идентификатор пользователя. Обязателен для пользователей, использующих внешний id |
| store_id | идентификатор торговой точки. Обязателен только для метода visit |
| visit_id | идентификатор визита. Обязателен только для методов visit, report, summaryReport |
| task_id | идентификатор задачи. Опциональный, используется в методах visit, report, summaryReport |
| back_url_scheme | значение кастомной URL схемы вашего приложения. Параметр обязателен только для метода report |

### Описание методов 

| Метод | Описание | Параметры |
| ------ | ------ | ------ |
| visit | Создание/редактирование визита. Открывает экран в режиме съёмки. Пока пользователь не получит отчеты по всем фото из визита, кнопка Назад не будет работать. При отсутствии соединения с интернетом и отсутствия у пользователя неподтвержденных фото, приложение откроет стороннее приложение со статусом IR_ERROR_NO_INET  | method, login, password, user_id, visit_id, store_id, task_id |
| report | Отчет по визиту. Открывает ваше приложение через URL с отчетом в виде JSON-строки в параметре "report". | method, login, password, user_id, task_id, visit_id, back_url_scheme |
| summaryReport | Открытие экрана со сводным отчётом. | method, login, password, user_id, visit_id, task_id |
| sync | Запуск фонового процесса передачи фото и получения результатов. | method, login, password, user_id |

### Поведение методов в зависимости от парамтера task_id

| Содержимое task_id | Наличие задач | Поведение в зависимости от метода|
| ------ | :------: | ------ |
| ID несуществующией задачи | не имеет значения | **visit** - откроется съемка визита с учетом указанной задачи<br>**report**, **summaryReport** - отчет по всему визиту |
| ID существующей задачи | есть | **visit** - откроется карточка указанной задачи<br>**report**, **summaryReport** - отчет в разрезе указанной задачи |
| ID существующей задачи | нет | **visit** - откроется съемка визита с учетом указанной задачи<br> **report**, **summaryReport** - отчет по всему визиту |
| нет | есть | **visit** - откроется карточка торговой точки со списком задач<br>**report**, **summaryReport** - отчет по всему визиту |
| нет | нет | **visit** - откроется съемка визита<br>**report**, **summaryReport** - отчет по всему визиту |

## Результат выполнения метода

Для передачи результатов отчёта приложение Intelligence Retail открывает url вида `{ваша_кастомная_url_схема}:?report={значение_в_виде_json}`.  Кастомная url схема передается через параметр `back_url_scheme`, описанный выше. Подробнее про использование url схем можно прочитать [в документации Apple](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app?language=swift).

Для обработки результатов отчета используйте метод `scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>)` в `SceneDelegate` для **iOS 13 и выше и использовании SwiftUI** или `application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any])` в `AppDelegate` во всех остальных случаях.
Ответ приходит в ключе "result" виде JSON, в котором есть следующие значения:

| Ключ | Тип | Обязательно | Описание |
| --- | :-: | :-: | --- |
| photosCounter | Int | Нет | Количество фото (в визите, если указан task_id - в задаче) |
| scenesCounter | Int | Нет | Количество сцен (в визите, если указан task_id - в задаче) |
| notDetectedScenesCounter | Int | Нет | количество нераспознанных сцен (сцен, где есть как минимум одна нераспознанная фото) |
| notDetectedPhotosCounter | Int | Нет | фото, по которым не получен отчёт, с учётом не отправленных  (в визите, если указан task_id - в задаче)|
| status | String | Да | Статус IR_RESULT_OK или ошибка для визита|
| report | JSON | Нет | Отчёт по визиту, если указан task_id - по задаче |


### Статус выполенения метода

| Статус | Код | Описание |
|---|:-:|---|
| IR_RESULT_OK | 1 | Успешно |
| IR_RESULT_EMPTY | 2 | Нет данных |
| IR_RESULT_INPROGRESS | 16 | Данные в обработке |
| IR_ERROR_NO_INET | 6 | Отсутствует интернет |
| IR_ERROR_TOKEN | 7 | Ошибка токена |
| IR_ERROR_STORE_ID_INCORRECT | 10 | Некорректный ИД ТТ|
| IR_ERROR_VISIT_ID_INCORRECT | 12 | Некорректный ИД визита |
| IR_ERROR_AUTH | 13 | Ошибка авторизации |
| IR_ERROR_NOVISIT | 17 | Отсутствует визит с указанным ИД |


### Пример использования для iOS13 и SwiftUI:

```swift
func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
    guard 
        let url = URLContexts.first?.url,
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
        let reportQueryItem = components.queryItems?.filter({ $0.name == "result" }).first,
        let report = reportQueryItem.value
        else { return }
    // Do something with report string.
}
```

### Пример использования для iOS ниже 13-й версии или без SwiftUI:

```swift 
func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
    guard 
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
        let reportQueryItem = components.queryItems?.filter({ $0.name == "result" }).first,
        let report = reportQueryItem.value
    else { return false }
    // Do something with report string.
    return true
}
```

### Пример отчета

```json
{
    "status": "IR_RESULT_OK",
    "scenesCounter": 1,
    "photosCounter": 1,
    "report": {
        "photos": {
            "602cb5b7098ee-9451": {
                "scene_id": "602cb5b7098b0-1403",
                "scene_type": "Не распознано на основном фото",
                "image_path": "/Documents/14310/2021-02-17-10-20-39-rn-o.jpg",
                "image_url": "https://.../2021-02-17-06-20-40-4132-o.jpg",
                "error": {
                    "codeInt": 1,
                    "message": "Успешно обработан",
                    "code": 1
                },
                "products": [
                    {
                        "facing": 2,
                        "price_type": 0,
                        "category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                        "price": "46",
                        "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                        "width": {
                            "cm": 16
                        },
                        "category_name": "Dairy",
                        "facing_group": 2,
                        "name": "Агуша Пюре яблоко-малина-шиповник, doypack, .090"
                    },                    
                    {
                        "facing": 1,
                        "price_type": 0,
                        "category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                        "price": "0",
                        "external_id": "",
                        "width": {
                            "cm": 18
                        },
                        "category_name": "Dairy",
                        "facing_group": 1,
                        "name": "Другой продукт (пакет), LP, .250"
                    }
                ]
            }
        },
        "result": {
            "sended_photos": 1,
            "code": 1,
            "message": "Успешно обработан",
            "total_photos": 1,
            "codeInt": 1,
            "internal_visit_id": "602cb5b408cda-4745",
            "visit_id": "q"
        },
        "assortment_achievement": [
            {
                "id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                "facing_plan": 0,
                "brand_name": "Агуша",
                "facing_real": 2,
                "facing_fact": 2,
                "price": "46",
                "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                "price_type": false,
                "category_name": "Dairy",
                "product_category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                "name": "Агуша Пюре яблоко-малина-шиповник, doypack, .090"
            },                   
            {
                "price_type": false,
                "brand_name": "Другой",
                "product_category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                "id": "cedbc3f4-636e-11e7-965b-000d3a250e47",
                "facing_real": 1,
                "price": "0",
                "category_name": "Dairy",
                "facing_plan": 0,
                "facing_fact": 1,
                "name": "Другой продукт (пакет), LP, .250",
                "brand_id": "cc2b6315-5356-11e7-94af-000d3a250e47"
            }
        ],
        "share_shelf": {
            "share_shelf_by_categories": [],
            "share_shelf_by_visit": [
                {
                    "visit_id": "602cb5b408cda-4745",
                    "value": 0,
                    "value_previous": 0
                }
            ],
            "share_shelf_by_brands": []
        },
        "custom": [],
        "visit_stats": {
            "photo_deleted": [],
            "photo_retake": [],
            "photo": {
                "retake": 0,
                "sent": 1,
                "deleted": 0,
                "wait": 0,
                "created": 1,
                "completed": 1,
                "uncompressed": 0
            },
            "photo_wait": []
        }
    },
    "notDetectedPhotosCounter": 0,
    "notDetectedScenesCounter": 0
}
```

### Пример отчета (указан task_id)

```json
{
    "status": "IR_RESULT_OK",
    "scenesCounter": 1,
    "photosCounter": 1,
    "task_id": "TASKID1",
    "report": {
        "photos": {
            "602cb5b7098ee-9451": {
                "scene_id": "602cb5b7098b0-1403",
                "scene_type": "Не распознано на основном фото",
                "image_path": "/Documents/14310/2021-02-17-10-20-39-rn-o.jpg",
                "image_url": "https://.../2021-02-17-06-20-40-4132-o.jpg",
                "error": {
                    "codeInt": 1,
                    "message": "Успешно обработан",
                    "code": 1
                },
                "products": [
                    {
                        "facing": 2,
                        "price_type": 0,
                        "category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                        "price": "46",
                        "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                        "width": {
                            "cm": 16
                        },
                        "category_name": "Dairy",
                        "facing_group": 2,
                        "name": "Агуша Пюре яблоко-малина-шиповник, doypack, .090"
                    }
                ]
            }
        },
        "result": {
            "sended_photos": 1,
            "code": 1,
            "message": "Успешно обработан",
            "total_photos": 1,
            "codeInt": 1,
            "internal_visit_id": "602cb5b408cda-4745",
            "visit_id": "q"
        },
        "assortment_achievement": [
            {
                "id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                "facing_plan": 0,
                "brand_name": "Агуша",
                "facing_real": 2,
                "facing_fact": 2,
                "price": "46",
                "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                "price_type": false,
                "category_name": "Dairy",
                "product_category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                "name": "Агуша Пюре яблоко-малина-шиповник, doypack, .090"
            }
        ],
        "share_shelf": {
            "share_shelf_by_categories": [],
            "share_shelf_by_visit": [
                {
                    "visit_id": "602cb5b408cda-4745",
                    "value": 0,
                    "value_previous": 0
                }
            ],
            "share_shelf_by_brands": []
        },
        "custom": [],
        "visit_stats": {
            "photo_deleted": [],
            "photo_retake": [],
            "photo": {
                "retake": 0,
                "sent": 1,
                "deleted": 0,
                "wait": 0,
                "created": 1,
                "completed": 1,
                "uncompressed": 0
            },
            "photo_wait": []
        }
    },
    "notDetectedPhotosCounter": 0,
    "notDetectedScenesCounter": 0
}
```