# Integration via iOS deeplink

You can call the Ailet app from your app with the `intelligenceretail` URL scheme and skip the Ailet library.

**Requirement:** the Ailet app is installed on the device.

To [integrate the Ailet library](../library/readme.md), use a separate guide.

- [What you need](#what-you-need)
- [How to call a method](#how-to-call-a-method)
  - [Methods](#methods)
  - [Call parameters](#call-parameters)
  - [Method call example](#method-call-example)
  - [How task_id changes screens](#how-task_id-changes-screens)
- [How to get the response](#how-to-get-the-response)
  - [Response data format](#response-data-format)
  - [Statuses](#statuses)
  - [Which reports arrive in the response](#which-reports-arrive-in-the-response)
  - [How to handle the response in SceneDelegate](#how-to-handle-the-response-in-scenedelegate)
  - [How to handle the response in AppDelegate](#how-to-handle-the-response-in-appdelegate)
  - [How to handle the response in SwiftUI](#how-to-handle-the-response-in-swiftui)
- [How to start synchronization](#how-to-start-synchronization)
- [Scenario example](#scenario-example)
- [Report examples](#report-examples)

## What you need

- The Ailet app is installed on the device.
- Your app has a registered URL scheme. Ailet returns the result to it through the `back_url_scheme` parameter.

To [register a URL scheme](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app?language=swift), add it to `Info.plist`:

```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>yourappscheme</string>
        </array>
    </dict>
</array>
```

To check that Ailet is installed with `canOpenURL`, add the `intelligenceretail` scheme to `LSApplicationQueriesSchemes`:

```xml
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>intelligenceretail</string>
</array>
```

## How to call a method

To call an Ailet method:

1. Build a URL with the `intelligenceretail` scheme and the required parameters.
2. Open the URL with `UIApplication.shared.open`.

URL format: `intelligenceretail:?param1=value1&param2=value2`.

### Methods

| Method | What it does | How it returns a result |
| --- | --- | --- |
| `visit` | Creates or edits a visit and opens shooting. The Back button stays disabled until reports exist for every photo. If there is no internet and no unconfirmed photos, Ailet returns `IR_ERROR_NO_INET` | Returns to your app via `back_url_scheme` |
| `report` | Returns a visit report | JSON in the `result` query parameter |
| `summaryReport` | Opens a summary report | Screen in Ailet |
| `showVisitReport` | With `task_id`, opens the task report. Without `task_id`, opens the store card with the task list, or the summary report if there are no tasks | Screen in Ailet |
| `sync` | Starts background photo upload and report download | `IR_RESULT_OK` if there was data in the queue. `IR_RESULT_EMPTY` if there is nothing to send |
| `syncCatalogs` | Authorizes the user and downloads catalogs. A repeat call pulls updates | `IR_RESULT_OK` |

### Call parameters

| Parameter | Required | Methods | Description |
| --- | --- | --- | --- |
| `method` | Yes | All | Method name: `visit`, `report`, `summaryReport`, `showVisitReport`, `sync`, `syncCatalogs` |
| `login` | Yes | All | User login |
| `password` | Yes | All | User password |
| `user_id` | Yes if sign-in uses an external ID | All | External user ID |
| `store_id` | Yes | `visit` | Store ID |
| `visit_id` | Yes | `visit`, `report`, `summaryReport`, `showVisitReport` | Visit ID |
| `task_id` | No | `visit`, `report`, `summaryReport`, `showVisitReport` | Task ID |
| `back_url_scheme` | Yes for `report`, and if you need a return to your app | `report`; for a return — the other methods | Your app URL scheme |

### Method call example

```swift
var components = URLComponents()
components.scheme = "intelligenceretail"
components.queryItems = [
    URLQueryItem(name: "method", value: methodName),
    URLQueryItem(name: "login", value: login),
    URLQueryItem(name: "password", value: password),
    URLQueryItem(name: "user_id", value: userId),
    URLQueryItem(name: "store_id", value: storeId),
    URLQueryItem(name: "visit_id", value: visitId),
    URLQueryItem(name: "task_id", value: taskId),
    URLQueryItem(name: "back_url_scheme", value: "yourappscheme")
]
guard let url = components.url else { return }
UIApplication.shared.open(url, options: [:]) { completed in
    // whether the URL opened
}
```

### How task_id changes screens

The table covers `visit`, `report`, and `summaryReport`. `showVisitReport` behavior is in the [Methods](#methods) table.

| `task_id` value | Tasks on the Ailet portal | Behavior |
| --- | --- | --- |
| ID of a task that is not on the portal | Does not matter | **visit** — visit shooting for the specified task. **report**, **summaryReport** — report for the whole visit |
| ID of a portal task | Yes | **visit** — card of the specified task. **report**, **summaryReport** — report for that task |
| ID of a portal task | No | **visit** — visit shooting for the specified task. **report**, **summaryReport** — report for the whole visit |
| None | Yes | **visit** — store card with the task list. **report**, **summaryReport** — report for the whole visit |
| None | No | **visit** — visit shooting. **report**, **summaryReport** — report for the whole visit |

## How to get the response

To return a result, Ailet opens a URL of your scheme:

```text
yourappscheme://?result={json}
```

Pass the scheme in `back_url_scheme`. JSON in the query parameter arrives percent-encoded. `URLComponents` decodes it.

The return query parameter is `result`. The `report` field is inside the JSON, not in the URL parameter name.

### Response data format

See the [report examples](#report-examples).

| Key | Type | Required | Description |
| --- | --- | --- | --- |
| `status` | `String` | Yes | Method execution status |
| `photosCounter` | `Int` | No | Number of photos in the visit. If `task_id` is passed — in the task |
| `scenesCounter` | `Int` | No | Number of scenes in the visit. If `task_id` is passed — in the task |
| `notDetectedScenesCounter` | `Int` | No | Number of scenes that have at least one unrecognized photo |
| `notDetectedPhotosCounter` | `Int` | No | Number of photos with no report, including unsent ones. If `task_id` is passed — in the task |
| `report` | JSON | No | Visit report. If `task_id` is passed — task report |

### Statuses

| Status | Code | Description |
| --- | --- | --- |
| `IR_RESULT_OK` | 1 | The method completed successfully. For `sync`: there was data in the queue, synchronization started |
| `IR_RESULT_EMPTY` | 2 | No data. For `sync`: nothing to send — photos already uploaded, reports received |
| `IR_ERROR` | 5 | Unknown error |
| `IR_ERROR_NO_INET` | 6 | No internet |
| `IR_ERROR_TOKEN` | 7 | Token error |
| `IR_ERROR_STORE_ID_INCORRECT` | 10 | Invalid store ID |
| `IR_ERROR_VISIT_ID_INCORRECT` | 12 | Invalid visit ID |
| `IR_ERROR_AUTH` | 13 | Authorization error |
| `IR_RESULT_INPROGRESS` | 16 | Data is still being processed |
| `IR_ERROR_NOVISIT` | 17 | Visit with the specified ID was not found |

### Which reports arrive in the response

The contents of the `report` field depend on photos, answers to questions, and whether shooting is mandatory.

Photos are mandatory if the visit has no tasks, or if a mandatory task requires shooting:

| Visit data | Status | Reports |
| --- | --- | --- |
| Photos exist, not all processed, answers exist | `IR_RESULT_INPROGRESS` (16) | `visit_stats`, `photos`, `share_shelf`, `share_shelf_by_metrics`, `custom`, `assortment_achievement`, `perfect_store` |
| Photos exist, not all processed, no answers | `IR_RESULT_INPROGRESS` (16) | `visit_stats`, `photos`, `share_shelf`, `share_shelf_by_metrics`, `custom`, `assortment_achievement` |
| No photos, answers exist | `IR_RESULT_EMPTY` (2) | `visit_stats`, `perfect_store` |
| No photos, no answers | `IR_RESULT_EMPTY` (2) | `visit_stats` |
| Photos exist, all sent, answers exist | `IR_RESULT_OK` (1) | `visit_stats`, `assortment_achievement`, `share_shelf`, `share_shelf_by_metrics`, `custom`, `photos`, `perfect_store` |
| Photos exist, all sent, no answers | `IR_RESULT_OK` (1) | `visit_stats`, `assortment_achievement`, `share_shelf`, `share_shelf_by_metrics`, `custom`, `photos`, `perfect_store` |

If shooting is not mandatory (no mandatory task requires photos), the rows match except one: no photos, but answers exist → status `IR_RESULT_OK` (1), not `IR_RESULT_EMPTY` (2). The report set is the same: `visit_stats`, `perfect_store`.

### How to handle the response in SceneDelegate

To handle the response in an app with scenes (iOS 13 and later), implement the method in `SceneDelegate`:

```swift
func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
    guard
        let url = URLContexts.first?.url,
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
        let result = components.queryItems?.first(where: { $0.name == "result" })?.value
    else { return }
    // parse JSON from result
}
```

### How to handle the response in AppDelegate

To handle the response in an app without scenes, implement the method in `AppDelegate`:

```swift
func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
    guard
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
        let result = components.queryItems?.first(where: { $0.name == "result" })?.value
    else { return false }
    // parse JSON from result
    return true
}
```

### How to handle the response in SwiftUI

To handle the response in SwiftUI, use `.onOpenURL`:

```swift
.onOpenURL { url in
    guard
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
        let result = components.queryItems?.first(where: { $0.name == "result" })?.value
    else { return }
    // parse JSON from result
}
```

## How to start synchronization

Background tasks on iOS do not live long. If the visit ran offline, synchronization may not finish on its own.

To force photo upload and report download:

1. Call the `sync` method.
2. If the status is `IR_RESULT_OK`, synchronization started: there was data in the queue.
3. If the status is `IR_RESULT_EMPTY`, there is nothing to send: photos already uploaded, reports received.

## Scenario example

To get a visit report:

1. Register your app URL scheme in `Info.plist`.
2. Open Ailet with the `visit` method.
3. Take photos and leave Ailet.
4. Read `status` in the `result` parameter.
5. If the status is `IR_RESULT_INPROGRESS`, call `sync` and wait for the next return.
6. If the status is `IR_RESULT_OK`, parse the report JSON.
7. To open the report or the summary report, call `report` or `summaryReport`.

## Report examples

See the [report example without task_id](without_task_id_response.json) and the [report example with task_id](with_task_id_response.json).
