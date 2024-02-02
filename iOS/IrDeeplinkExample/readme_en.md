# Interaction with the Ailet App Using Deeplinks

To use Deeplink API functionality you need to follow the steps described
below.

- [Interaction with the Ailet App Using Deeplinks](#interaction-with-the-ailet-app-using-deeplinks)
  - [Generating URL](#generating-url)
  - [Request Parameters](#request-parameters)
    - [Methods](#methods)
  - [Receiving Report Results](#receiving-report-results)
    - [Response Parameters](#response-parameters)
    - [Response Statuses](#response-statuses)
    - [Response Example](#response-example)
      - [Response without task_id](without_task_id_response.json)
      - [Response with task_id](with_task_id_response.json)

## Generating URL

Generate a valid URL in any convenient way. Below is the example of the possible URL format: 

`intelligenceretail:?param1=value1&param2=value2`

For example, you can do this with `URLComponents` and then open it via 
`UIApplication.shared.open` (see the code sample below).

```swift
var components = URLComponents()
components.scheme = "intelligenceretail"
components.queryItems = [URLQueryItem(name: "method", value: methodName),
                         URLQueryItem(name: "login", value: login),
                         URLQueryItem(name: "password", value: password),
                         URLQueryItem(name: "user_id", value: userId),
                         URLQueryItem(name: "store_id", value: storeId),
                         URLQueryItem(name: "visit_id", value: visitId),
                         URLQueryItem(name: "back_url_scheme", value: "integrationtestapp")]
let url = components.url!
UIApplication.shared.open(url, options: [:]) { (completed) in
    // Handle completion if needed
}
```

Make a valid request to Ailet server API with specifying of the generated URL.

## Request Parameters

Some of the parameters are required only when using the corresponding [method](#methods).

\* - *parameters that are required for individual methods*.

| **Parameters** | **Description**        | **Required** |
|--------------|--------------------------|:-------:|
| **method**   | Request method name (see the list of available methods in the table [below](#methods)). | yes |
| **login**    | User login on the Ailet server. | yes |
| **password** | User password on the Ailet server. | yes |
| **user\_id** | The external identifier of the user; required for those using external IDs.| no |
| **store\_id**| The identifier of the store (POS); required when using the **visit**  method. | yes\* |
| **visit\_id**| The identifier of the visit; required when using the following methods: **visit**, **report**, **summaryReport**.| yes\* |
| **task\_id** | The identifier of the task: used in the following methods: **visit**, **report**, **summaryReport**. | no |
| **back\_url\_scheme** | Custom URL value for your app. This parameter is required only for the **report** method.| yes\* |

### Methods

| **Name**      | **Description**     | **Parameters**   |
|------------------|------------------------------------------|-----------------------------------|
| **visit**         | Creation/edition of a visit. It opens the photo shooting screen. The "Back" button will not be available until the user receives reports on all photos of the visit. If there is no Internet connection and the user has unconfirmed photos, the Ailet app will open your application with the *[IR_ERROR_NO_INET](#response-statuses)* status. | method, login, password, user\_id, visit\_id, store\_id, task\_id |
| **report**        | A report for a visit. It opens your application via URL with a report as JSON string in the *report* parameter. | method, login, password, user\_id, visit\_id, task\_id, back\_url\_scheme |
| **summaryReport** | Opens a screen with a summary report.                                                                      | method, login, password, user\_id, visit\_id, task\_id          |
| **sync**          | Launches a background process of sending photos and receiving results.                                     | method, login, password, user\_id                               |

## Receiving Report Results

To transmit the results of the report requested by the **[report](#methods)** method, 
the Ailet app opens URL that can be of the following type: `{your_custom_URL_scheme}:?report={value_in_json}`. 

Custom URL scheme being sent via *back\_url\_scheme parameter*, that has been
described [earlier](#request-parameters). For more info on using URL schemes see [Apple
Documentation](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app?language=swift).

To process report results use the following method in `SceneDelegate` for **iOS 13** and higher while using **SwiftUI**:

`scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>)` 

In all other cases, use the following method in `AppDelegate`:

`application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any])` 

**Code Samples**

**Code Sample for iOS 13 or higher and SwiftUI:**

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

**Code Sample for iOS Below 13 or without SwiftUI:**

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

### Response Parameters

The response to a request for report results using the **report** method comes in the `result` key in the form of a JSON structure containing the following fields:

| **Key**                  | **Type** | **Required** | **Description**   |
|---------------------------------|-------|:-------------:|-----------------------------|
| photosCounter            | int      | no           | Number of photos in visit                                       |
| sceneCounter             | Int      | no           | Number of scenes in visit                                       |
| notDetectedPhotosCounter | Int      | no           | Number of non-recognized photos                                 |
| notDetectedScenesCounter | Int      | no           | Number of non-recognized scenes                                 |
| nonValidPhotosCounter    | Int      | no           | Number of unconfirmed photos with errors                        |
| status                   | String   | yes          | Response status. See possible values of error types in [Response Statuses](#response-statuses). |
| report                   | JSON     | no           | Report contents. See the example of report with or without specifying *"task\_id"* [below](#response-example).|

### Response Statuses

| **Value** | **Code** | **Description**    |
|----------------------|:----:|-------------------------|
| IR\_RESULT\_OK                  | 1  | Report successfully created.                           |
| IR\_RESULT\_EMPTY               | 2  | No data to get the report with set parameters. Check correctness of data by outlet/visit. In case the report was requested by all visits, contact tech support |
| IR\_RESULT\_INPROGRESS          | 16 | Report is being processed.                             |
| IR\_ERROR\_NO\_INET             | 6  |  No internet connection.                                |
| IR\_ERROR\_TOKEN                | 7  |  Authentication error: Incorrect token.                 |
| IR\_ERROR\_STORE\_ID\_INCORRECT | 10 |  Incorrect store ID received from external application. |
| IR\_ERROR\_VISIT\_ID\_INCORRECT | 12 | Incorrect visit id received from external application. |
| IR\_ERROR\_AUTH                 | 13 | Authentication error.                                  |
| IR\_ERROR\_NOVISIT              | 17 | There is no visit with such ID in the application.         |

### Status and available reports depending on tasks in visit, photos and their obligation.

Photos in visit are required (there are no tasks or required tasks contain any with photos required):

| Visit data | Status (code) | Available reports |
|---|:-:|---|
| Visit has photos, not all photos are processed, visit has answers for questions | 16 | visit_stats, photos, share_shelf, share_shelf_by_metrics, custom, assortment_achievement, perfect_store |
| Visit has photos, not all photos are processed, no answers for questions  | 16 | visit_stats, photos, share_shelf, share_shelf_by_metrics, custom, assortment_achievement |
| No photos, visit has answers for questions | 2 | visit_stats, perfect_store |
| No photos, no answers for questions | 2 | visit_stats |
| Visit has photos, all photos are processed, visit has answers for questions | 1 | visit_stats, assortment_achievement, share_shelf, share_shelf_by_metrics, custom, photos, perfect_store |
| Visit has photos, all photos are processed, no answers for questions | 1 | visit_stats, assortment_achievement, share_shelf, share_shelf_by_metrics, custom, photos, perfect_store |

Photos in visit are not required (there are any required tasks with required photos):

| Visit data | Status (code) | Available reports |
|---|:-:|---|
| Visit has photos, not all photos are processed, visit has answers for questions | 16 | visit_stats, photos, share_shelf, share_shelf_by_metrics, custom, assortment_achievement, perfect_store |
| Visit has photos, not all photos are processed, no answers for questions  | 16 | visit_stats, photos, share_shelf, share_shelf_by_metrics, custom, assortment_achievement |
| No photos, visit has answers for questions | 1 | visit_stats, perfect_store |
| No photos, no answers for questions | 2 | visit_stats |
| Visit has photos, all photos are processed, visit has answers for questions | 1 | visit_stats, assortment_achievement, share_shelf, share_shelf_by_metrics, custom, photos, perfect_store |
| Visit has photos, all photos are processed, no answers for questions | 1 | visit_stats, assortment_achievement, share_shelf, share_shelf_by_metrics, custom, photos, perfect_store |


### Response Example
### Without task_id
[Response without task_id](without_task_id_response.json)

### With task_id
[Response with task_id](with_task_id_response.json)

