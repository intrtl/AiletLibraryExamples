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

Update build.gradle (App level) with

```gradle
implementation 'com.intrtl:lib:+'
```

## Manual include irLib

In Android Studio open **File - Project Structure - Dependencies**, press **+** and select **Import .JAR/.AAR Package**, import *ir-lib.aar*. Then in **Declared Dependencies** press **+** and select **3. Module Dependency**, select *ir-lib*, then **Apply** changes or press **OK**.

## Include OpenCV

In Android Studio open **File - Project Structure - Dependencies**, press **+** and select **Import .JAR/.AAR Package**, import *OpenCV.aar* library. Then in **Declared Dependencies** press **+** and select **3. Module Dependency**, select *OpenCV*, then **Apply** changes or press **OK**.

## Required dependencies

```gradle
implementation 'com.google.android.gms:play-services-location:16.0.0'
implementation 'com.squareup.okhttp3:okhttp:3.11.0'
implementation 'com.github.PhilJay:MPAndroidChart:3.0.2'
implementation 'com.squareup.picasso:picasso:2.5.2'
implementation "com.microsoft.appcenter:appcenter-analytics:2.5.1"
implementation "com.microsoft.appcenter:appcenter-crashes:2.5.1"
implementation 'com.google.code.gson:gson:2.8.5'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
implementation 'com.ufreedom.kit:safejobintentservice:1.0.1'
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
For change portal use **setPortal** funcrion, with portal ID as parameter:
```java
ir.setPortal("demoPortal");
```

### setPortal results

| Result | Description |
|---|---|
| RESULT_OK | Switch portal success |
| ERROR_NOT_MULTIPORTAL_MODE  | Set portal ID in non multiportal mode |
| ERROR_PORTAL_INCORRECT  | Incorrect portal ID or portal not associated with user |
| ERROR_PORTAL_INCORRECT  | Portal ID is null and using multiportal mode |