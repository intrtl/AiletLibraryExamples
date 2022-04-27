# 1. Integrate irLib into project

- [1. Integrate irLib into project](#1-integrate-irlib-into-project)
    - [1.1. Подключение используя Maven (GitHub)](#11-подключение-используя-maven-github)
        - [1.1.1. Создание GitHub personal access token](#111-создание-github-personal-access-token)
    - [1.2. Методы (Класс IntRtl, Deprecated)](#12-методы-класс-intrtl-deprecated)

## 1.1. Подключение используя Maven (GitHub)

### 1.1.1. Создайте GitHub personal access token

- В правом верхнем углу любой страницы щелкните фотографию своего профиля и нажмите «Settings» .
- В левой боковой панели нажмите «Developer settings»
- В левой боковой панели нажмите «Personal access tokens» и затем чтобы создать новый токен нажмите «Generate new token»
- Задайте scope ``read:packages``

### 1.1.2. Добавьте в проект репозиторий Ailet

Вариант 1 (классический). Добавьте репозиторий в корневой ``build.gradle``:

```groovy
allprojects {
    repositories {
        maven {
            url 'https://maven.pkg.github.com/intrtl/irlib'
            credentials {
                username 'your GitHub username'
                password 'personal GitHub access token'
            }
        }
    }
}
```

Вариант 2 (используя ``settings.gradle`` и ``DependencyResolutionManagement``). Добавьте репозиторий
в ``settings.gradle``:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url 'https://maven.pkg.github.com/intrtl/irlib'
            credentials {
                username 'your GitHub username'
                password 'personal GitHub access token'
            }
        }
    }
}
```

### 1.1.3. Добавьте в build.gradle модуля две зависимости:

```groovy
def ailetLibVersion = '3.0.12'
// библиотека Ailet
implementation "com.ailet.android:lib:$ailetLibVersion"
// необязательно: модуль техподдержки
implementation "com.ailet.android:lib-feature-techsupport-intercom:$ailetLibVersion"
```

## 1.2. Использование

### 1.2.1. Инициализация

Перед началом работы необходимо инициализировать объект ``Ailet`` в вашем наследнике ``Application``:

```kotlin
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // модули опционального функционала библиотеки
        val features = setOf<AiletFeature>(
                DefaultStockCameraFeature(), // модуль стоковой камеры
                IntercomTechSupportManager(this), // модуль техподдержки
                HostAppInstallInfoProviderFeature(
                        this,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE,
                        AiletLibInstallInfo
                ) // модуль идентификации (поможет при диагностике проблем)
        )

        // инициализация библиотеки с выбранными модулями
        Ailet.initialize(this, features)
    }
}
```

### 1.2.2. Использование
После инициализации вам становится доступен единый клиент библиотеки, который вы можете использовать для вызова ее методов:
```kotlin
Ailet.getClient()
```

## 1.3. Методы (Класс IntRtl, Deprecated)

В текущей версии библиотеки используется режим совместимости с методами устаревшего класса ``IntRtl``. Его методы позоволяют ограниченно использовать
вызовы [библиотеки v1](https://github.com/intrtl/AiletLibraryExamples/blob/master/Android/IrLibExample/readme.md#методы)
без дополнительной доработки приложения.

Метод  | Описание
--- | ---
[init](#метод-init) | Инициализация библиотеки, авторизация и загрузка справочников
[start](#метод-start) | Старт визита
[reports](#метод-reports) | Возвращает отчет по указанному визиту
[showSummaryReport](#метод-showsummaryreport) | Сводный отчет по указанному визиту
[setPortal](#метод-setportal) | Установка активного портала