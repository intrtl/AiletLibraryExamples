# Integration via Android Intent

You can call the Ailet app from your app with an Android Intent and skip the Ailet library.

**Requirement:** the Ailet app (`com.intrtl.app`) is installed on the device.

To [integrate the Ailet library](https://github.com/intrtl/AiletLibV3Example/blob/main/readme.md), use a separate guide.

- [What you need](#what-you-need)
- [How to call a method](#how-to-call-a-method)
  - [Methods](#methods)
  - [Call parameters](#call-parameters)
  - [Method call example](#method-call-example)
- [How to get the response](#how-to-get-the-response)
  - [Response data format](#response-data-format)
  - [How to get images from report](#how-to-get-images-from-report)
  - [Statuses](#statuses)
  - [Response handling example](#response-handling-example)
- [Broadcast message](#broadcast-message)
  - [Broadcast message contents](#broadcast-message-contents)
  - [Broadcast handling example](#broadcast-handling-example)
- [Report example](#report-example)
- [Scenario example](#scenario-example)
- [Integration issues](#integration-issues)
  - [Android 11 specifics](#android-11-specifics)

## What you need

- The Ailet app is installed on the device.
- The project declares the `com.intrtl.app` package.

To open the Ailet app on Android 11 and later (`targetSdkVersion` 30 and higher), add a `<queries>` block to `AndroidManifest.xml`:

```xml
<queries>
    <package android:name="com.intrtl.app" />
</queries>
```

Other declaration options are in [Android 11 specifics](#android-11-specifics).

## How to call a method

To call an Ailet method, create an `Intent` with the required `action` and pass parameters with `putExtra`.

The examples below use `startActivityForResult`. In AndroidX the same flow uses `registerForActivityResult`.

### Methods

| Method | What it does | How it returns a result |
| --- | --- | --- |
| `com.intrtl.app.ACTION_VISIT` | Creates or edits a visit | Opens a screen (activity) |
| `com.intrtl.app.ACTION_REPORT` | Returns a visit report | JSON file via `Uri` |
| `com.intrtl.app.ACTION_SUMMARY_REPORT` | Opens a summary visit report | Opens a screen (activity) |
| `com.intrtl.app.ACTION_SYNC` | Starts background photo upload and result download | Status in the response |

### Call parameters

Set `action` with `Intent.setAction`. Pass the other fields with `Intent.putExtra`.

| Parameter | Required | Methods | Description |
| --- | --- | --- | --- |
| `login` | Yes | All | User login |
| `password` | Yes | All | User password |
| `id` | Yes if sign-in uses a technical user | All | External user ID |
| `visit_id` | Yes | `ACTION_VISIT`, `ACTION_REPORT`, `ACTION_SUMMARY_REPORT` | Visit ID |
| `task_id` | No | `ACTION_VISIT`, `ACTION_REPORT`, `ACTION_SUMMARY_REPORT` | Task ID |
| `store_id` | Yes | `ACTION_VISIT` | Store ID |

### Method call example

```java
Intent intent = new Intent("com.intrtl.app.ACTION_VISIT");
intent.putExtra("login", user);
intent.putExtra("password", password);
intent.putExtra("id", user_id);
intent.putExtra("visit_id", visit_id);
intent.putExtra("store_id", store_id);
startActivityForResult(intent, ACTIVITY_RESULT_START_IR_VISIT);
```

## How to get the response

Ailet returns the result through FileProvider. `Intent.getData()` contains a `Uri` of a JSON file.

| Field | Description |
| --- | --- |
| `error` | Error text if `resultCode == RESULT_CANCELED` |
| `data` | `Uri` of the operation result file |

### Response data format

See the [report format](#report-example). The `status` field is always present. The other fields are missing from the `ACTION_SYNC` response.

| Field | Description | When it is present |
| --- | --- | --- |
| `status` | Method execution status | Always |
| `user_id` | User ID | Except `ACTION_SYNC` |
| `external_user_id` | User ID from the client system | Except `ACTION_SYNC` |
| `store_id` | Store ID | Except `ACTION_SYNC` |
| `task_id` | Task ID | Except `ACTION_SYNC` |
| `visit_id` | Visit ID | Except `ACTION_SYNC` |
| `internal_visit_id` | Internal visit ID | Except `ACTION_SYNC` |
| `install_id` | Install ID | Except `ACTION_SYNC` |
| `photosCounter` | Number of photos taken | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` |
| `scenesCounter` | Number of scenes | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` |
| `notDetectedPhotosCounter` | Number of photos with no data | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` |
| `notDetectedScenesCounter` | Number of scenes with no data | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` |
| `report` | Report | If `status == RESULT_OK` and the method is not `ACTION_SYNC` |

### How to get images from report

On Android 9 and later, take the image path from `image_uri`, not from `image_path`.

To get images from the report:

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
for (i in 0 until photosJSON.length()) {
    photoNamesList.add(photosJSON.names()[i] as String)
}
val arrayOfBitmap = photoNamesList.map {
    val photoUri = Uri.parse(photosJSON.getString(it))
    readBitmapFromUri(photoUri)
}
```

### Statuses

| Status | Description |
| --- | --- |
| `RESULT_OK` | The method completed successfully |
| `RESULT_INPROGRESS` | Data is still being processed |
| `RESULT_INPROGRESS_OFFLINE` | Data is still being processed, the app is offline |
| `RESULT_EMPTY` | The report is empty |
| `ERROR_NOVISIT` | The visit does not exist |
| `ERROR_READONLY_VISIT` | The visit is read-only |
| `ERROR_INCORRECT_INPUT_PARAMS` | Invalid input parameters |
| `ERROR_VISIT_ID_INCORRECT` | Invalid visit ID |
| `ERROR_AUTH` | Authorization error |
| `ERROR_PHOTO` | Photo processing error |
| `ERROR_BUSY` | The method is already running |
| `ERROR_CANT_LOAD_VISIT` | Cannot load the visit: no internet |

### Response handling example

`requestCode` is the constant you passed to `startActivityForResult`. Use it to tell `ACTION_VISIT`, `ACTION_REPORT`, and `ACTION_SUMMARY_REPORT` responses apart.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data == null) {
        return;
    }

    if (resultCode == RESULT_OK && data.getData() != null) {
        String result = readFromUri(data.getData());
        try {
            JSONObject json = new JSONObject(result);
            Log.i("report", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }

    if (resultCode == RESULT_CANCELED) {
        String error = data.getStringExtra("error");
        Log.e("report", error);
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

## Broadcast message

After `ACTION_VISIT` and shooting, Ailet uploads photos and requests reports in the background. When processing finishes, the app sends a broadcast `com.intrtl.app.BROADCAST_VISIT_COMPLETED`.

### Broadcast message contents

| Field | Description |
| --- | --- |
| `visit_id` | Visit ID |
| `internal_visit_id` | Internal visit ID |
| `user_id` | User ID |
| `store_id` | Store ID |
| `total_photos` | Total number of photos. Low-quality photos are not counted |
| `completed_photos` | Number of processed photos |
| `result` | String with the `Uri` of the [report](#report-example) file |

### Broadcast handling example

On Android 13 and later, set the export flag: the broadcast comes from another app.

```java
BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            try {
                String reportString = readFromUri(Uri.parse(extras.getString("result")));
                JSONObject reportJson = new JSONObject(reportString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
};

IntentFilter intentFilter = new IntentFilter("com.intrtl.app.BROADCAST_VISIT_COMPLETED");
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED);
} else {
    registerReceiver(broadcastReceiver, intentFilter);
}
```

## Report example

The same JSON arrives in the broadcast `result` field and in `getData()` in `onActivityResult`.

[Report example](https://github.com/intrtl/AiletLibV3Example/blob/main/report_example.json)

## Scenario example

To get a visit report:

1. Call the Ailet app with `com.intrtl.app.ACTION_VISIT`.
2. Take several photos in the visit.
3. Leave the Ailet app.
4. Read the status in the response:
    * If the status is `RESULT_INPROGRESS`, wait for the `com.intrtl.app.BROADCAST_VISIT_COMPLETED` broadcast.
    * If the status is `RESULT_OK`, process the report from the file.
5. To open the report or the summary report, call `ACTION_REPORT` or `ACTION_SUMMARY_REPORT`.

## Integration issues

### Android 11 specifics

On Android 11, with `targetSdkVersion` 30 and higher, the system hides third-party packages. Without `<queries>`, the Ailet app call fails.

To open the Ailet app, add one of the options to `AndroidManifest.xml`.

**Option 1.** Declare the Ailet app package:

```xml
<queries>
    <package android:name="com.intrtl.app" />
</queries>
```

**Option 2.** Declare method intent filters:

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

The `QUERY_ALL_PACKAGES` permission exposes the list of all installed apps. Google Play accepts it only for a narrow set of use cases, so use `<queries>` for Ailet integration.
