# Integrate irLib into project

- [Integrate irLib into project](#integrate-irlib-into-project)
  - [Include irLib using Maven](#include-irlib-using-maven)
  - [Manual include irLib](#manual-include-irlib)
  - [Include OpenCV](#include-opencv)
  - [Required dependencies](#required-dependencies)
  - [If project use androidX](#if-project-use-androidx)
  - [Export local base functionality](#export-local-base-functionality)
  - [Using Multiportal functionality](#using-multiportal-functionality)
    - [Init](#init)
    - [Switch portal](#switch-portal)
    - [setPortal results](#setportal-results)

## Include irLib using Maven

Update build.gradle (Project) with

```gradle
maven { url "https://maven.intrtl.com/artifactory/irlib" }
```

Update build.gradle (App) with

```gradle
implementation 'com.intrtl:lib:+'
```

or

```gradle
implementation 'com.intrtl:lib:1.119'
```

## Manual include irLib

In Android Studio open **File - Project Structure - Dependencies**, press **+** and select **Import .JAR/.AAR Package**, import *ir-lib.aar*. Then in **Declared Dependencies** press **+** and select **3. Module Dependency**, select *ir-lib*, then **Apply** changes or press **OK**.

## Include OpenCV

In Android Studio open **File - Project Structure - Dependencies**, in **Modules** press **+** (New Module) and select **Import .JAR/.AAR Package**, import *openCVLibrary320.aar* library.

<img src="/examples/irlib-examples/-/raw/master/Android/IrLibExample/images/add_opencv_1.png" width="400" height="100">

![Add openCV module](https://gitlab.intrtl.com/examples/irlib-examples/-/raw/master/Android/IrLibExample/images/add_opencv_2.png =400x400)


Add this line in build.gradle in app level module in dependency section

```gradle
implementation project(path: ':openCVLibrary320')
```

## Required dependencies

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

## If project use androidX

Update gradle.properties with

```gradle
android.useAndroidX=true
android.enableJetifier=true
```

## Export local base functionality

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

and create file *provider_paths* in **res/xml** folder

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```

## Using Multiportal functionality

### Init
If you need using more than one portal, set to **true** multiportal parameter in init:
```java
ir.init("user", "pass", "notificationID", true);
```
### Switch portal
For switch portal use **setPortal** function with portal ID as parameter:
```java
ir.setPortal("demoPortal");
```

### setPortal results

| Result | Description |
|---|---|
| RESULT_OK | Switch portal success |
| ERROR_NOT_MULTIPORTAL_MODE  | Set portal ID in non multiportal mode |
| ERROR_PORTAL_INCORRECT  | Incorrect portal ID or portal not associated with user |
| ERROR_EMPTY_PORTAL  | Portal ID is null and using multiportal mode |