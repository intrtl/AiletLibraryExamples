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

## Generating URL

Generate a valid URL in any convenient way. Below is the example of the possible URL format: 

`intellingenceretail:?param1=value1&param2=value2`

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

### Response Example

**Without task_id:**

```json
    {
        "status": "IR_RESULT_OK",
        "scenesCounter": 1,
        "photosCounter": 1,
        "report": {
            "photos": {
                "602cb5b7098ee-9451": {
                    "scene_id": "602cb5b7098b0-1403",
                    "scene_type": "Not recognized in the main photo",
                    "image_path": "/Documents/14310/2021-02-17-10-20-39-rn-o.jpg",
                    "image_url": "https://.../2021-02-17-06-20-40-4132-o.jpg",
                    "error": {
                        "codeInt": 1,
                        "message": "Successfully processed",
                        "code": 1
                    },
                    "products": [
                        {
                            "facing": 2,
                            "price_type": 0,
                            "category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                            "price": "46",
                            "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                            "width": {
                                "cm": 16
                            },
                            "category_name": "Dairy",
                            "facing_group": 2,
                            "name": "Agusha Puree apple-raspberry-rosehip, doypack, .090"
                        },                    
                        {
                            "facing": 1,
                            "price_type": 0,
                            "category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                            "price": "0",
                            "external_id": "",
                            "width": {
                                "cm": 18
                            },
                            "category_name": "Dairy",
                            "facing_group": 1,
                            "name": "Other product (package), LP, .250"
                        }
                    ]
                }
            },
            "result": {
                "sended_photos": 1,
                "code": 1,
                "message": "Successfully processed",
                "total_photos": 1,
                "codeInt": 1,
                "internal_visit_id": "602cb5b408cda-4745",
                "visit_id": "q"
            },
            "assortment_achievement": [
                {
                    "id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                    "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                    "facing_plan": 0,
                    "brand_name": "Agusha",
                    "facing_real": 2,
                    "facing_fact": 2,
                    "price": "46",
                    "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                    "price_type": false,
                    "category_name": "Dairy",
                    "product_category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                    "name": "Agusha Puree apple-raspberry-rosehip, doypack, .090"
                },                   
                {
                    "price_type": false,
                    "brand_name": "Other",
                    "product_category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                    "id": "cedbc3f4-636e-11e7-965b-000d3a250e47",
                    "facing_real": 1,
                    "price": "0",
                    "category_name": "Dairy",
                    "facing_plan": 0,
                    "facing_fact": 1,
                    "name": "Other product (package), LP, .250",
                    "brand_id": "cc2b6315-5356-11e7-94af-000d3a250e47"
                }
            ],
            "share_shelf": {
                "share_shelf_by_categories": [],
                "share_shelf_by_visit": [
                    {
                        "visit_id": "602cb5b408cda-4745",
                        "value": 0,
                        "value_previous": 0
                    }
                ],
                "share_shelf_by_brands": []
            },
            "custom": [],
            "visit_stats": {
                "photo_deleted": [],
                "photo_retake": [],
                "photo": {
                    "retake": 0,
                    "sent": 1,
                    "deleted": 0,
                    "wait": 0,
                    "created": 1,
                    "completed": 1,
                    "uncompressed": 0
                },
                "photo_wait": []
            }
        },
        "notDetectedPhotosCounter": 0,
        "notDetectedScenesCounter": 0
    }
```

**With task_id:**

```json
    {
        "status": "IR_RESULT_OK",
        "scenesCounter": 1,
        "photosCounter": 1,
        "task_id": "TASKID1",
        "report": {
            "photos": {
                "602cb5b7098ee-9451": {
                    "scene_id": "602cb5b7098b0-1403",
                    "scene_type": "Not recognized in the main photo",
                    "image_path": "/Documents/14310/2021-02-17-10-20-39-rn-o.jpg",
                    "image_url": "https://.../2021-02-17-06-20-40-4132-o.jpg",
                    "error": {
                        "codeInt": 1,
                        "message": "Successfully processed",
                        "code": 1
                    },
                    "products": [
                        {
                            "facing": 2,
                            "price_type": 0,
                            "category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                            "price": "46",
                            "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                            "width": {
                                "cm": 16
                            },
                            "category_name": "Dairy",
                            "facing_group": 2,
                            "name": "Agusha Puree apple-raspberry-rosehip, doypack, .090"
                        }
                    ]
                }
            },
            "result": {
                "sended_photos": 1,
                "code": 1,
                "message": "Successfully processed",
                "total_photos": 1,
                "codeInt": 1,
                "internal_visit_id": "602cb5b408cda-4745",
                "visit_id": "q"
            },
            "assortment_achievement": [
                {
                    "id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                    "external_id": "006AA8BA-C9D0-49DD-93B2-666E71A407A1",
                    "facing_plan": 0,
                    "brand_name": "Agusha",
                    "facing_real": 2,
                    "facing_fact": 2,
                    "price": "46",
                    "brand_id": "60b9c6c6-c681-42ea-b93c-019766c0fd0d",
                    "price_type": false,
                    "category_name": "Dairy",
                    "product_category_id": "8f0f1b83-8368-40f2-a908-1adeddc7923d",
                    "name": "Agusha Puree apple-raspberry-rosehip, doypack, .090"
                }
            ],
            "share_shelf": {
                "share_shelf_by_categories": [],
                "share_shelf_by_visit": [
                    {
                        "visit_id": "602cb5b408cda-4745",
                        "value": 0,
                        "value_previous": 0
                    }
                ],
                "share_shelf_by_brands": []
            },
            "custom": [],
            "visit_stats": {
                "photo_deleted": [],
                "photo_retake": [],
                "photo": {
                    "retake": 0,
                    "sent": 1,
                    "deleted": 0,
                    "wait": 0,
                    "created": 1,
                    "completed": 1,
                    "uncompressed": 0
                },
                "photo_wait": []
            }
        },
        "notDetectedPhotosCounter": 0,
        "notDetectedScenesCounter": 0
    }
```
