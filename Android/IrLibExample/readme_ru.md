# Integrate irLib into project

- [Integrate irLib into project](#integrate-irlib-into-project)
  - [Подключение используя Maven](#подключение-используя-maven)
  - [Подключение вручную irLib](#подключение-вручную-irlib)
  - [Подключение OpenCV](#подключение-opencv)
  - [Необходимые зависимости](#необходимые-зависимости)
  - [Если в проекте используется androidX](#если-в-проекте-используется-androidx)
  - [Подключение экспорта](#подключение-экспорта)
  - [Методы](#методы)
    - [Метод init](#метод-init)
    - [Метод start](#метод-start)
    - [Метод reports](#метод-reports)
    - [Метод showSummaryReport](#метод-showsummaryreport)
  - [Использование мультипортальности](#использование-мультипортальности)
    - [Инициализация](#инициализация)
    - [Метод setPortal](#метод-setportal)

## Подключение используя Maven

Добавте в build.gradle (Project)

```gradle
maven { url "https://maven.intrtl.com/artifactory/irlib" }
```

Добавте в build.gradle (App)

```gradle
implementation 'com.intrtl:lib:+'
```

или

```gradle
implementation 'com.intrtl:lib:1.119'
```

## Подключение вручную irLib

В Android Studio откройте пункт **File - Project Structure - Dependencies**, нажмите **+** и выбирите **Import .JAR/.AAR Package**, импортируйте *ir-lib.aar*. Затем нажмите **Declared Dependencies**, затем **+** и выбирите **3. Module Dependency**, выбирете *ir-lib*, затем **Apply** или **OK**.

## Подключение OpenCV

В Android Studio откройте **File - Project Structure - Dependencies**, в разделе **Modules** нажмите **+** (New Module) и выбирите **Import .JAR/.AAR Package**, импортируйте библиотеку *openCVLibrary320.aar*.

<img src="https://gitlab.intrtl.com/examples/irlib-examples/-/raw/master/Android/IrLibExample/images/add_opencv_1.png" width="700"><br/>
<img src="https://gitlab.intrtl.com/examples/irlib-examples/-/raw/master/Android/IrLibExample/images/add_opencv_2.png" width="700">

Добавте в build.gradle (app) зависимость.

```gradle
implementation project(path: ':openCVLibrary320')
```

## Необходимые зависимости

build.gradle (App) 

```gradle
apply plugin: 'realm-android'
...
implementation project(path: ':openCVLibrary320')

implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.android.support:design:28.0.0'
implementation 'com.android.support:support-v4:28.0.0'
implementation 'com.google.android.gms:play-services-location:16.0.0'
implementation 'com.squareup.okhttp3:okhttp:3.12.0'
implementation 'com.github.PhilJay:MPAndroidChart:3.0.2'
implementation "com.microsoft.appcenter:appcenter-analytics:2.5.1"
implementation "com.microsoft.appcenter:appcenter-crashes:2.5.1"
implementation 'com.google.code.gson:gson:2.8.5'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.google.code.ksoap2-android:ksoap2-android:3.6.4'
implementation 'com.bugfender.sdk:android:3.+'
```

build.gradle (Project)

```gradle
buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.3'
        classpath 'com.google.gms:google-services:4.3.2'
        classpath "io.realm:realm-gradle-plugin:5.14.0"
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://maven.google.com/' }
        maven { url 'https://jitpack.io' }
        maven { url "https://maven.intrtl.com/artifactory/irlib" }
        maven { url 'https://oss.sonatype.org/content/repositories/ksoap2-android-releases' }
    }
}
```

## Если в проекте используется androidX

Update gradle.properties with

```gradle
android.useAndroidX=true
android.enableJetifier=true
```

## Подключение экспорта

If you need export base (for IR support) please add next lines into *Manifest*

```xml
<provider
  android:name="android.support.v4.content.FileProvider"
  android:authorities="${applicationId}.provider"
  android:exported="false"
  android:grantUriPermissions="true">
    <meta-data
      android:name="android.support.FILE_PROVIDER_PATHS"
      android:resource="@xml/provider_paths"/>
</provider>
```

создайте файл *provider_paths* в папке **res/xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```

## Методы

Метод  | Описание
--- | ---
[init](#метод-init) | Инициализация библиотеки, авторизация и загрузка справочников
[start](#метод-start) | Старт визита
[reports](#метод-reports) | Возвращает отчет по указанному визиту
[showSummaryReport](#метод-showsummaryreport) | Сводный отчет по указанному визиту
[setPortal](#метод-setportal) | Установка активного портала

### Метод init
Параметр  | Тип | Обязательно | Описание
--- | :-: | :-: | ---
user_login | String | + | Имя пользователя
user_pass | String | + | Пароль пользователя
external_user_id | String | | Идентификатор пользователя
broadcast | String | | Идентификатор бродкаста
isMultiportal | Boolean | | Если true то будет использована мультипортальность


Результат  | Обязательно | Описание
--- | ---
RESULT_OK  | Инициализация выполнена успешно
ERROR_BUSY  | Метод уже выполняется
ERROR_NO_INET  | Отсутствует интернет, невозможно провести авторизацию
ERROR_AUTH  | Ошибка авторизации
ERROR | Ошибка выполнения метода

### Метод start
Параметр  | Тип | Обязательно | Описание
--- | :-: | :-: | ---
externalStoreId | String | + | Идентификатор магазина 
externalVisitId | String | + | Идентификатор визита
externalSessionId | String | | Идентификатор сессии
externalSessionName | String | | Название сессии (выводится в интерфейсе съемки)
externalSceneGroup | String | | Идентификатор группы сцен

Результат | Описание
--- | ---
RESULT_OK  | Запуск визита выполнен успешно
ERROR_BUSY  | Метод уже выполняется
ERROR_EMPTY_PORTAL  | Если не был установлен портал в мультипортальном режиме 
ERROR_VISIT_ID_INCORRECT | Пустой идентификатор визита
ERROR_STORE_ID_INCORRECT | Пустой идентификатор торговой точки
ERROR_EMPTY_SESSION_ID  | Отсутствует идентификтор сессии при устновленом названии сессии (externalSessionName)
ERROR_INCORRECT_SESSION_NAME  | Название сессии не совпадает с ранее указанным
ERROR_TOKEN | Отсутствует токен (пользователь не авторизован)
ERROR | Ошибка выполнения метода

### Метод reports
Параметр  | Тип | Обязательно | Описание
--- | :-: | :-: | ---
externalVisitId | String | + | Идентификатор визита

Результат | Описание
--- | ---
Json String  | Отчет в json формате


### Метод showSummaryReport
Параметр  | Тип | Обязательно | Описание
--- | :-: | :-: | ---
externalVisitId | String | + | Идентификатор визита

Результат | Описание
--- | ---
RESULT_OK  | Вызов сводного отчета выполнен успешно
ERROR_NOVISIT | Отсутсвует визит с указанным идентификатором
ERROR_VISIT_ID_INCORRECT | Пустой идентификатор визита

## Использование мультипортальности

### Инициализация
Для использования мультипортальности без переавторизации пользователя установите флаг мультипортальности метода init в **true**. При использовании мультипортальности визит осуществляется в портал, устаноленный методом **setPortal**, при этом синхронизация данных выполняется для всех порталов пользователя.

```java
ir.init("user", "pass", "notificationID", true);
```
### Метод setPortal

Метод используется для установки активного портала.

Параметр  | Тип | Обязательно | Описание
--- | :-: | :-: | ---
portalId | String | + | Идентификатор портала

 Результат | Описание 
---|---
RESULT_OK | Установка портала успешна
ERROR_NOT_MULTIPORTAL_MODE  | Установка портала в не мультипортальном режиме
ERROR_PORTAL_INCORRECT  | Некорректный идентификатор портала
ERROR_EMPTY_PORTAL  | Пустой идентификатор портала

Пример использования метода **setPortal**:
```java
ir.setPortal("demoPortal");
```