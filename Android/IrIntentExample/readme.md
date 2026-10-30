# Integration via Android Intent

You can call the Ailet app from your app with an Android Intent and skip the Ailet library.

**Requirement:** the Ailet app (`com.intrtl.app`) is installed on the device.

**On-device** recognition is available in the **Ailet Offline 4.23** build and later, package `tech.jedai.offline`.

To [integrate the Ailet library](../library/readme.md), use a separate guide.

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
- [On-device recognition](#on-device-recognition)
  - [Build and settings](#build-and-settings)
  - [ACTION_SYNC_PALOMNA](#action_sync_palomna)
  - [Store selection](#store-selection)
  - [ACTION_SYNC_PALOMNA errors](#action_sync_palomna-errors)
  - [Report fields and ActivityResult](#report-fields-and-activityresult)
  - [Working without internet](#working-without-internet)
  - [Recommended call order](#recommended-call-order)
- [Integration issues](#integration-issues)
  - [Android 11 specifics](#android-11-specifics)

## What you need

- The Ailet app is installed on the device.
- The project declares the `com.intrtl.app` package.
- For on-device, the project declares the `tech.jedai.offline` package and Ailet Offline **4.23** or later is installed.

To open the Ailet app on Android 11 and later (`targetSdkVersion` 30 and higher), add a `<queries>` block to `AndroidManifest.xml`:

```xml
<queries>
    <package android:name="com.intrtl.app" />
</queries>
```

For Ailet Offline, declare the build package:

```xml
<queries>
    <package android:name="tech.jedai.offline" />
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
| `com.ailet.ACTION_SYNC_PALOMNA` | Downloads and updates on-device models, classes, and reference data. Ailet Offline build **4.23+** | `RESULT_OK` or an error in extras |

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
| `external_ids` | No | `ACTION_SYNC_PALOMNA` | External store IDs. `ArrayList<String>` or a comma-separated string. Empty list by default |
| `store_ids` | No | `ACTION_SYNC_PALOMNA` | Internal store IDs. `int[]`, `long[]`, or `ArrayList<Integer>`. Empty list by default |

How `external_ids` and `store_ids` select stores for matrices is described in [Store selection](#store-selection).

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

`ACTION_SYNC_PALOMNA` example:

```java
Intent intent = new Intent("com.ailet.ACTION_SYNC_PALOMNA");
intent.setPackage("tech.jedai.offline");
intent.putExtra("login", user);
intent.putExtra("password", password);
intent.putStringArrayListExtra("external_ids", externalIds);
startActivityForResult(intent, ACTIVITY_RESULT_SYNC_PALOMNA);
```

## How to get the response

Ailet returns the result through FileProvider. `Intent.getData()` contains a `Uri` of a JSON file.

| Field | Description |
| --- | --- |
| `error` | Error text if `resultCode == RESULT_CANCELED` |
| `data` | `Uri` of the operation result file |

### Response data format

See the [report format](#report-example). The `status` field is always present. The other fields are missing from the `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` responses.

| Field | Description | When it is present |
| --- | --- | --- |
| `status` | Method execution status | Always |
| `user_id` | User ID | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `external_user_id` | User ID from the client system | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `store_id` | Store ID | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `task_id` | Task ID | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `visit_id` | Visit ID | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `internal_visit_id` | Internal visit ID | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `install_id` | Install ID | Except `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` |
| `photosCounter` | Number of photos taken | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` and not `ACTION_SYNC_PALOMNA` |
| `scenesCounter` | Number of scenes | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` and not `ACTION_SYNC_PALOMNA` |
| `notDetectedPhotosCounter` | Number of photos with no data | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` and not `ACTION_SYNC_PALOMNA` |
| `notDetectedScenesCounter` | Number of scenes with no data | If `status != ERROR_VISIT_ID_INCORRECT` and the method is not `ACTION_SYNC` and not `ACTION_SYNC_PALOMNA` |
| `report` | Report | In the response file. `ACTION_SYNC` and `ACTION_SYNC_PALOMNA` have no file |

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

[Report example](../library/report_exaple.json) is a historical online report. `report.result` now includes `source`, and `visit_stats.photo` includes `completed_on_device`.

Report fragment after on-device recognition, when all photos are processed and the visit has a widget:

```json
{
  "status": "RESULT_OK",
  "visit_id": "visit-1",
  "photosCounter": 1,
  "report": {
    "result": {
      "source": "on-device",
      "visit_id": "visit-1",
      "total_photos": 1,
      "sended_photos": 0,
      "recognized_photos": 1,
      "code": "RESULT_OK",
      "codeInt": 1,
      "message": "Successfully processed"
    },
    "visit_stats": {
      "photo": {
        "completed": 1,
        "completed_online": 0,
        "completed_on_device": 1,
        "status": "RESULT_OK"
      }
    }
  }
}
```

`recognized_photos` is present in `report.result` when Palomna is enabled. `completed_on_device` is the number of photos processed on-device.

## Scenario example

To get a visit report:

1. Call the Ailet app with `com.intrtl.app.ACTION_VISIT`.
2. Take several photos in the visit.
3. Leave the Ailet app.
4. Read the status in the response:
    * If the status is `RESULT_INPROGRESS`, wait for the `com.intrtl.app.BROADCAST_VISIT_COMPLETED` broadcast.
    * If the status is `RESULT_OK`, process the report from the file.
5. To open the report or the summary report, call `ACTION_REPORT` or `ACTION_SUMMARY_REPORT`.

To get a report without internet, follow the [recommended call order](#recommended-call-order): call `ACTION_SYNC_PALOMNA` first, then `ACTION_VISIT`.

## On-device recognition

Starting with version **4.23**, the Ailet Offline build recognizes photos on the device and returns a report without internet. The app package is `tech.jedai.offline`.

The same build still supports `com.intrtl.app.ACTION_VISIT`, `com.intrtl.app.ACTION_REPORT`, `com.intrtl.app.ACTION_SUMMARY_REPORT`, and `com.intrtl.app.ACTION_SYNC`.

### Build and settings

- Install Ailet Offline **4.23** or later.
- Declare the `tech.jedai.offline` package in `<queries>`.
- In the portal mobile settings (`/api/v1/md/mobile_settings/`), on-device recognition is enabled and these lists are filled:
  - `other.offline_models.android` — models;
  - `other.offline_classes.android` — classes.

If recognition is disabled or either list is empty, `ACTION_SYNC_PALOMNA` fails with `ONDEVICE_NOT_AVAILABLE`.

### ACTION_SYNC_PALOMNA

The method downloads and updates models, classes, and reference data for on-device recognition: matrices, matrix types, and metrics.

While models and classes are not downloaded yet, the method downloads them together with the reference data. Once they are on the device, the method checks for a newer version of models and classes. If the server version is newer, the app shows an update dialog. Confirmation starts the download. A decline returns `ONDEVICE_USER_CANCELED_UPDATE`.

Reference data is updated at most once per UTC calendar day. A repeated call on the same day checks models and classes and skips matrices and metrics that were already updated today. Call the method at least once a day while the device has a network.

On Wi-Fi the download starts immediately. On a mobile network the app asks for confirmation. A decline returns `ONDEVICE_USER_CANCELED_WITH_MOBILE`.

If the download is interrupted, the app offers a retry. A decline returns `ONDEVICE_DOWNLOAD_FAILED`.

Success: `resultCode == RESULT_OK`, extras contain `action`, and there is no report file.

### Store selection

The `external_ids` and `store_ids` parameters are optional. An empty list is handled the same way as a missing parameter. The app applies the first matching rule:

1. `store_ids` contains one ID — matrices for the 1000 stores nearest to that store's coordinates.
2. `external_ids` contains one ID — matrices for the 1000 stores nearest to that store's coordinates. This rule also applies when `store_ids` contains several IDs.
3. `store_ids` contains several IDs — matrices only for those stores.
4. `external_ids` contains several IDs — matrices only for those stores.
5. Both lists are empty — the 1000 stores nearest to the current location. If there is no location, the first 1000 stores from the catalog.

If one `store_id` and any `external_ids` are passed, `store_id` is used. If several `store_ids` and several `external_ids` are passed, `store_ids` are used. If several `store_ids` and one `external_id` are passed, `external_id` is used.

A store without coordinates, or a store missing from the catalog, causes a download error. After the user declines a retry, the result is `ONDEVICE_DOWNLOAD_FAILED`. The `message` text is `Store with id <id> not found` or `Store with externalId <id> not found`.

### ACTION_SYNC_PALOMNA errors

Errors arrive with `resultCode == RESULT_CANCELED` in the `error` and `message` extras.

| `error` | `message` | When |
| --- | --- | --- |
| `ONDEVICE_USER_CANCELED_WITH_MOBILE` | `user don't wont download via mobile network` | The user declined the download over a mobile network |
| `ONDEVICE_DOWNLOAD_FAILED` | `download failed` or the exception text | The download failed and the user declined a retry |
| `ONDEVICE_USER_CANCELED_UPDATE` | `user canceled update` | The user declined the models and classes update |
| `ONDEVICE_NOT_AVAILABLE` | `incorrect app or no on-device data in settings` | The build has no on-device support, recognition is disabled, or settings have no models or classes |

### Report fields and ActivityResult

Starting with version **4.23**, the report file and the extras of a successful response contain the data source. The `code`, `codeInt`, `source`, and `message` extras are copied from `report.result`. They are present for `ACTION_REPORT` and for the response the app returns when leaving `ACTION_VISIT` and `ACTION_SUMMARY_REPORT`.

`source`:

- `on-device` — the visit has at least one photo in the Palomna pipeline that the server has not recalculated yet.
- `online` — there are no such photos.

`visit_stats.photo.completed_on_device` is the number of photos processed on-device.
`completed_online` is the number of photos processed online.
The `visit_stats` block is present for `RESULT_OK`, `RESULT_INPROGRESS`, and `RESULT_INPROGRESS_OFFLINE`. It is absent for `RESULT_EMPTY`.

The widget for `on-device` comes from the visit on-device widget. The widget for `online` comes from the server.

| `code` | `codeInt` | When |
| --- | --- | --- |
| `RESULT_EMPTY` | `2` | There are no valid photos and no answers to questions |
| `RESULT_INPROGRESS_OFFLINE` | `37` | Processing is not finished, the device is offline, and Palomna is not activated |
| `RESULT_INPROGRESS` | `16` | Processing is not finished in every other case, including offline with Palomna active |
| `RESULT_OK` | `1` | All photos are processed by the rule from `source`, and the visit has a widget from that source |

| `error` | `message` | When |
| --- | --- | --- |
| `ONDEVICE_DATA_NOT_LOADED` | `models or classes not loaded` | There is no network, on-device is enabled in settings, models are not downloaded, and the visit has unprocessed photos |

### Working without internet

`ACTION_VISIT` opens the same visit screen as Ailet Offline. If models and classes are downloaded, Palomna turns on by itself when there is no network, and photos are processed on the device. If models are not downloaded, the visit still opens, and report generation returns `ONDEVICE_DATA_NOT_LOADED`.

`ACTION_SUMMARY_REPORT` opens the same summary report. If the visit is already on the device, the screen opens without a network and shows on-device data when Palomna is active. If the visit is not on the device and there is no network, the historical visit is not downloaded. The response is `RESULT_CANCELED`, and `message` is `No visit/Offline.`

### Recommended call order

Prepare data on the device first, then shoot the visit. Account sign-in happens inside the call.

1. Install Ailet Offline **4.23+** and declare the `tech.jedai.offline` package in `<queries>`.
2. While the device has a network, call `com.ailet.ACTION_SYNC_PALOMNA` at least once a day and pass the route stores. On Wi-Fi the download starts immediately.
3. Call `ACTION_VISIT` and take photos. After a successful `ACTION_SYNC_PALOMNA`, shooting works without internet.
4. Read the `code`, `codeInt`, `source`, and `message` extras and the report file. Or wait for the broadcast and read the same JSON from the `result` extra.
5. To open the report again, call `ACTION_REPORT` or `ACTION_SUMMARY_REPORT`.

`ACTION_SYNC` uploads photos that were already taken and fetches the server result. It does not download models.

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

For Ailet Offline:

```xml
<queries>
    <package android:name="tech.jedai.offline" />
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
    <intent>
        <action android:name="com.ailet.ACTION_SYNC_PALOMNA" />
    </intent>
</queries>
```

The `QUERY_ALL_PACKAGES` permission exposes the list of all installed apps. Google Play accepts it only for a narrow set of use cases, so use `<queries>` for Ailet integration.
