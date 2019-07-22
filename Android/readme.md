# For native Android project
Add ir-lib.aar into folder ir-lib before compile.

# Integrate IrLib in React Native project

## Table of contents
- [For native Android project](#For-native-Android-project)
- [Integrate IrLib in React Native project](#Integrate-IrLib-in-React-Native-project)
  - [Table of contents](#Table-of-contents)
  - [Include libraries](#Include-libraries)
  - [Configure android project](#Configure-android-project)
    - [In build.gradle (app)](#In-buildgradle-app)
    - [In build.gradle (android)](#In-buildgradle-android)
  - [Android Bridge](#Android-Bridge)
  - [Use IrLib in React Native project (Apps.js)](#Use-IrLib-in-React-Native-project-Appsjs)

## Include libraries
Copy libraries ir-lib.aar and openCVLibrary320.aar to the folder android/app/libs.

## Configure android project

### In build.gradle (app)
```gradle
apply plugin: 'realm-android'
android {
    ...

    defaultConfig {
       ...
        multiDexEnabled true

        dexOptions {
            javaMaxHeapSize "4g"
        }
    }
}

...

repositories {
    mavenCentral()
    maven {
        url "https://mint.splunk.com/gradle/"
    }
    maven { url "https://jitpack.io" }

    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    implementation fileTree(dir: "libs", include: ["*.aar"])
    implementation "com.facebook.react:react-native:+"
    implementation files('libs/ir-lib.aar') 
    implementation files('libs/openCVLibrary320.aar')

    implementation 'com.android.support:multidex:1.0.3'
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.google.guava:guava:20.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.github.PhilJay:MPAndroidChart:v3.0.2'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.squareup.picasso:picasso:2.5.2'
    implementation 'com.android.support:design:28.0.0'
    testCompile 'junit:junit:4.12'
    implementation 'com.google.firebase:firebase-core:16.0.9'
    implementation 'com.google.firebase:firebase-auth:17.0.0'
    implementation 'com.google.firebase:firebase-config:17.0.0'
    implementation 'com.google.firebase:firebase-crash:16.2.1'
    implementation 'com.android.support:cardview-v7:28.0.0'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation 'com.squareup.okhttp3:okhttp:3.11.0'
    implementation 'io.sentry:sentry-android:1.7.16'
    implementation 'org.slf4j:slf4j-nop:1.7.25'

    def appCenterSdkVersion = '2.0.0'
    implementation "com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}"
    implementation "com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}"
}
```

### In build.gradle (android) 
```gradle
buildscript {
    ext {
        buildToolsVersion = "28.0.3"
        minSdkVersion = 16
        compileSdkVersion = 28
        targetSdkVersion = 28
        supportLibVersion = "28.0.0"
    }
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath('com.android.tools.build:gradle:3.4.1')
        classpath "io.realm:realm-gradle-plugin:5.3.1"
        classpath 'com.google.gms:google-services:4.0.0'        
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        maven {            
            url "$rootDir/../node_modules/react-native/android"
        }   

        maven {
            url 'https://maven.google.com/'
        }
    }
}
```

## Android Bridge
Include classes IrLibModule and IrLibPackage in android project.

**IrLibPackage**
```Java
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IrLibPackage implements ReactPackage {

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();        
        modules.add(new IrLibModule(reactContext));
        return modules;
    }
}
```

**IrLibModule**

This class describes all interactions with the IrLib library.

```Java
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.intelligenceretail.www.lib.IntRtl;
import org.json.JSONObject;

public class IrLibModule extends ReactContextBaseJavaModule {

    public IrLibModule(ReactApplicationContext reactContext) {
        super(reactContext); 
    }

    @Override    
    public String getName() {
        return "IrModule";
    }
    
    @ReactMethod
    public IntRtl.Results start(String user_name,
                                String password,
                                String broadcast,
                                String store_id,
                                String visit_id) {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        IntRtl.Results res = ir.init(user_name, password, broadcast);
        if (res == IntRtl.Results.RESULT_OK) {
            res = ir.start(store_id, visit_id);
        }

        return res;
    }

    @ReactMethod
    public IntRtl.Results showSummaryReport(String external_visit_id) {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.showSummaryReport(external_visit_id);
    }

    @ReactMethod
    public JSONObject reports(String external_visit_id) {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.reports(external_visit_id);
    }

    public IntRtl.IrLastVisit getLastVisit(String external_store_id){
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.getLastVisit(external_store_id);
    }

    public String getVersion() {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.getVersion();
    }

    public IntRtl.Results syncData() {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.syncData();
    }
}
```

Add IrLibPackage in MainApplication.

```Java
@Override
protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
            new IrLibPackage()
    );
}
```

## Use IrLib in React Native project (Apps.js)

```js
import {NativeModules} from 'react-native';
var IrModule = NativeModules.IrModule;
 
type Props = {};
export default class App extends Component<Props> {
  test() {
    IrModule.reports("tp1");
  }
 
  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={ this.test }>
              <Text>Tap me</Text>
         </TouchableOpacity>
      </View>
    );
  }
}
```
