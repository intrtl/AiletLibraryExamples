# 1. Integrate irLib into project

- [1. Integrate irLib into project](#1-integrate-irlib-into-project)
  - [1.1. Подключение используя Maven (GitHub)](#11-подключение-используя-maven-github)
    - [1.1.1. Создание GitHub personal access token](#111-создание-github-personal-access-token)
  - [1.2. Методы (Класс IntRtl, Deprecated)](#12-методы-класс-intrtl-deprecated)

## 1.1. Подключение используя Maven (GitHub)

Добавте в build.gradle (Project)

```gradle 
allprojects {
    repositories {
        ...
        maven {
            url = 'https://maven.pkg.github.com/intrtl/irlib'
            credentials {
                username $githubUserName
                password $githubAccessToken
            }
        }
        maven {
            url 'https://jitpack.io'
        }
    }
}
```

### 1.1.1. Создание GitHub personal access token

- В правом верхнем углу любой страницы щелкните фотографию своего профиля и нажмите «Settings» .
- В левой боковой панели нажмите «Developer settings»
- В левой боковой панели нажмите «Personal access tokens» и затем чтобы создать новый токен нажмите «Generate new token»
- Задайте scope ``read:packages``

Добавте в build.gradle (App)

```gradle
// библиотека Ailet
implementation 'com.ailet.android:lib:3.0.5'
// не обязательно: модуль техподдержки
implementation 'com.ailet.android:lib-feature-techsupport-intercom:3.0.0'
```

## 1.2. Методы (Класс IntRtl, Deprecated)

В текущей версии библиотеки используется режим совместимости с методами класса IntRtl. Методы позоволяют использовать библиотеку без дополнительно доработки приложения.

Метод  | Описание
--- | ---
[init](#метод-init) | Инициализация библиотеки, авторизация и загрузка справочников
[start](#метод-start) | Старт визита
[reports](#метод-reports) | Возвращает отчет по указанному визиту
[showSummaryReport](#метод-showsummaryreport) | Сводный отчет по указанному визиту
[setPortal](#метод-setportal) | Установка активного портала