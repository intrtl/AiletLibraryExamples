# IrLibSwift (Swift)

Use `IRInteractManager` class for communicate with IrLibSwift framework. 

**[Methods](#methods)**

- [Setup](#setup) 
- [Start shooting](#start-shooting)
- [Show report](#show-report)
- [Force data sync](#force-client-server-data-synchronization)
- [Use all reports data](#use-reports-data-in-your-app)
- [Use specific visit report data](#retrieve-report-data-for-specific-visit)
- [Multiportal mode: update active project](#multiportal-mode-update-active-project)
- [Get IrLibSwift version](#get-irlibswift-version)
- [Subscribe for notifications](#subscribe-for-notifications)

**[Classes](#classes)**

- [IRError](#irerror)
- [IRVisitReport](#irvisitreport)
- [IRReport](#irreport)
- [IRReportVisitStats](#irreportvisitstats)
- [IRReportVisitPhotoStats](#irreportvisitphotostats)
- [IRReportVisitStatsNeedSend](#irreportvisitstatsneedsend)
- [IRReportOSADetails](#irreportosadetails)
- [IRReportPriceType](#irreportpricetype)
- [IRReportShareShelf](#irreportshareshelf)
- [IRReportShareShelfBrand](#irreportshareshelfbrand)
- [IRReportShareShelfCategory](#irreportshareshelfcategory)
- [IRReportShareShelfMacroCategory](#irreportshareshelfmacrocategory)
- [IRReportShareShelfVisit](#irreportshareshelfvisit)
- [IRReportPhoto](#irreportphoto)
- [IRReportPhotoError](#irreportphotoerror)
- [IRReportPhotoProduct](#irreportphotoproduct)
- [IRReportPhotoProductWidth](#irreportphotoproductwidth)
- [IRReportPerfectStore](#irreportperfectstore)
- [IRReportPerfectStoreTask](#irreportperfectstoretask)
- [IRReportPerfectStoreTaskKPI](#irreportperfectstoretaskkpi)
- [IRReportPerfectStoreTaskQuestion](#irreportperfectstoretaskquestion)
- [IRReportPerfectStoreTaskQuestionAnswer](#irreportperfectstoretaskquestionanswer)
- [IRReportResult](#irreportresult)
- [IRNotification](#irnotification)


# Methods

## Setup
`setup(username:password:guestToken:externalUserId:notification:domainName:isMultiportal:completion:)`

#### Description

This method authorizes user and downloads all initial data required for further work with IrLibSwift.

#### Parameters

- `username`: The username for the account. *(String. Required.)*
- `password`: The password for the account *(String. Required.)*
- `guestToken`: A token used for API access. Must be received from company support manager. *(String. Required.)*
- `externalUserId`: additional user id, if there are multiple users under one login. *(String. Optional. Default is nil)*
- `notification`: prefix String for receiving notifications on photo recognitions updates. Currently this parameter exists only for backward compatibility with legacy classes. Don't pass anything and use `IRNotification` class for subscription on photo update. *(String. Optional. Default is nil.)* 
- `domainName`: if user has multiple projects to log in, you can use this parameter to login on specific project. This doesn't work with multiportal mode on. *(String. Optional. Default is nil.)* 
- `isMultiportal`: pass `true` to activate multiportal mode, when IrLibSwift authorizes for every project available for user. *(Bool. Optional. Default is `false`.)* 
- `completion`: A completion handler that asynchronously returns result of setup method (`Result<Void, IRError>`). *(IRResultCompletion. Optional. Default is nil.)* 

#### Return Value

This method does not return a value but calls the completion handler with the result of the setup operation.

#### Examples

<details>
  <summary>Click to expand the `setup` method usage examples</summary>

Call with all parameters:
```swift
IRInteractManager.setup(
    username: "user123",
    password: "securePassword",
    guestToken: "guestToken123",
    externalUserId: "externalUser456",
    notification: nil,
    domainName: "example.com",
    isMultiportal: true
    result: { result in
        switch result {
            case .success:
                // handle success scenario
            case .failure(let error):
                // handle received error
        }
    }
)
```

or use only required:
```swift
IRInteractManager.setup(
    username: "user123",
    password: "securePassword",
    guestToken: "guestToken123",
    result: { result in
        switch result {
            case .success:
                // handle success scenario
            case .failure(let error):
                // handle received error
        }
    }
)
```
</details>




## Start shooting
`startShooting(in:externalStoreId:externalVisitId:)`

#### Description

Presents the IrLibSwift camera from the passed `UIViewController`.

#### Parameters

- `in presentingVC`: UIViewController to present the camera from. *(UIViewController, required)*
- `externalStoreId`: The ID of the store to make the shooting for *(String, required)*
- `externalVisitId`: The ID of the visit to make the shooting for. This visit ID must be unique for every store and every day of shooting *(String, required)*

#### Throws

Throws an [`IRError`](#irerror) if something goes wrong during the process.

#### Examples

<details>
  <summary>Click to expand the `startShooting` method usage examples</summary>

```swift
do {
    try IRInteractManager.startShooting(
        in: viewController,
        externalStoreId: "store123",
        externalVisitId: "visit456"
    )
} catch let error {
    // handle error
}
```
</details>

## Show report

`showSummaryReport(in:visitId:completion:)`

##### Description

Asynchronously downloads all the data required for a summary report and presents a `UIViewController` with the report.

##### Parameters

- `in presentingViewController`: The `UIViewController` where the summary report `UIViewController` will be presented from *(UIViewController, required)*
- `visitId`: The ID of a visit to generate the summary report for. *(String, required)*
- `completion`: A callback function that asynchronously returns `Result<Void, IRError>`. It returns `.success` if the `UIViewController` is presented successfully, and `.failure` with [`IRError`](#irerror) in case of any error. *(IRResultCompletion, optional)*

#### Return Value

This method does not return a value but calls the completion handler with the result of the showing report.

##### Examples

<details>
<summary>Click here to see `showSummaryReport` usage examples</summary>

```swift
IRInteractManager.showSummaryReport(
    in: viewController,
    visitId: "visit123"
) { result in
    switch result {
    case .success:
        // handle success scenario if needed 
    case .failure(let error):
        // handle error 
    }
}
```

</details>

## Force client-server data synchronization

`syncData(completion:)`

##### Description

Checks if any data is not uploaded or downloaded and forces a restart of all requests. 

##### Parameters

- `completion`: A callback function that asynchronously returns `.success` when data synchronization is finished (including when there is no data to sync). It returns `.failure` with [`IRError`](#irerror) if any errors occur during synchronization. *(IRResultCompletion, optional)*

##### Examples
<details>
<summary>Click here to expand `syncData` method usage examples</summary>

```swift
IRInteractManager.syncData { result in
    switch result {
    case .success:
        // handle success scenario
    case .failure(let error):
        // handle received error
    }
}
```
</details>

## Use reports data in your app

`reports() -> [IRVisitReport]`

##### Description

Returns an array with a report for all visits existing locally in IrLibSwift.

##### Return Value

An array of `IRVisitReport`.

##### Examples

<details>
<summary>Click here to expand `reports` method usage examples</summary>

```swift
let visitReports = IRInteractManager.reports()
for report in visitReports {
    print("Visit ID: \(report.visitId), Report: \(report.details)")
}
```
</details>

## Retrieve report data for specific visit
`report(visitId:) throws -> IRReport`

##### Description

Returns a report for a specific visit existing locally in IrLibSwift.

##### Parameters

- `visitId`: The ID of the visit to generate a report for *(String, required)*.

##### Return Value

An instance of `IRReport` class representing data for the specified visit.

##### Throws

Throws an error if the report cannot be generated for the specified visit.

##### Examples

<details>
<summary>Click here to expand `report` method usage examples</summary>

```swift
do {
    let report = try IRInteractManager.report(visitId: "visit123")
    print("Visit ID: \(report.visitId), Report Details: \(report.details)")
} catch let error {
    // handler received error
}
```
</details>



## Multiportal mode: update active project

`updateActivePortal(_:) throws`

##### Description

Sets the active project to work with.

##### Important

Only for multiportal mode. IrLibSwift will throw an error if this method is called without activating multiportal mode in `setup` method.

##### Parameters

- `portalId`: The root domain. Don't pass 'dairy.intrtl.com', pass 'dairy'. *(String, required)*

##### Throws

Throws an [`IRError`](#irerror) if `portalId` is in an incorrect format or if a project with such `portalId` has not been authorized during the `setup` method.

##### Examples

<details>
<summary>Click here to expand `updateActivePortal` method usage examples</summary>

```swift
do {
    try IRInteractManager.updateActivePortal("dairy")
} catch let error {
    // Handle received error
}
```
</details>

## Get IrLibSwift version

`frameworkVersion() -> String`

##### Description

Returns the IrLibSwift version number.

##### Return Value

A `String` containing the version number.

##### Examples

<details>
<summary>Click here to expand `frameworkVersion` method usage examples</summary>

```swift
let version = IRInteractManager.frameworkVersion()
print("IrLibSwift version: \(version)")
```
</details>

## Subscribe for notifications

Use [`IRNotification`](#irnotification) class static properties to subscribe to updates via `NotificationCenter`:

```swift
 NotificationCenter.default.addObserver(forName: IRNotification.photoRecognizedNotification, object: nil, queue: nil, using: { [weak self] notification in
  let time = Date.now
  DispatchQueue.main.async {
    self?.logs.append(Log(time: time, text: "🔈 Notification. Photo with id: \(notification.userInfo!["photoId"] ?? "") recognized"))
  }
})
```


# Classes

## IRError

##### Description

Error thrown by `IRInteractManager`.

##### Enum Cases

<details>
<summary>Click to expand the list of IRError enum cases</summary>

- **busy**  
  IrLibSwift is in the process of downloading or uploading critical data. Try to repeat the method call later.  
  **Code:** 0

- **unknown**  
  An unknown error occurred.  
  **Code:** 5

- **noConnection**  
  IrLibSwift detected a lack of internet connection and couldn't process data. Handle the internet connection and call the method again once it is available.  
  **Code:** 6

- **catalogsError**  
  IrLibSwift couldn't download critically important catalogs. Try to call the `setup` method with the same data again to trigger catalogs download again.  
  **Code:** 9

- **tokenError**  
  The authorization token is invalid. Try to call the `setup` method with the same data again.  
  **Code:** 7

- **incorrectStoreId**  
  IrLibSwift couldn't find the input store ID in the local database. Make sure you pass a correct parameter.  
  **Code:** 10

- **incorrectVisitId**  
  IrLibSwift couldn't find the input visit ID in the local database. Make sure you pass a correct parameter.  
  **Code:** 12

- **authError**  
  IrLibSwift could not authorize for a project. Make sure you pass correct parameters and call the `setup` method again.  
  **Code:** 13

- **inProgress**  
  An operation is already in progress.  
  **Code:** 16

- **notMultiportalMode**  
  You tried to call a method meant for multiportal mode only without activating it in the setup method.  
  **Code:** 23

- **incorrectPortal**  
  While in multiportal mode, you passed a non-existent portal to IrLibSwift.  
  **Code:** 24

- **emptyPortal**  
  While in multiportal mode, you didn't pass any portal to use. Use the method `updatePortal` to set the active portal.  
  **Code:** 25

- **serverError**  
  IrLibSwift received a server error.  
  **Code:** 20

</details>

## IRVisitReport

##### Description

A class representing an interrelation between visit and report in IrLibSwift.

##### Properties
<details>
<summary>Click here to expand properties</summary>

- `visitId`: The ID of the visit. *(String)*
- `report`: The report associated with the visit. *([IRReport](#irreport))*

</details>

## IRReport

##### Description

A class representing a detailed report for a visit, containing various statistics, specific reports metrics (SOS, OSA), and results.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `visitStats`: The visit statistics (how many photos created, waiting, etc). *([IRReportVisitStats](#irreportvisitstats))*
- `onShelfAvailability`: OSA metric detailed data. *(Array<[IRReportOSADetails](#irreportosadetails)>)*
- `shareShelf`: SOS metric detailed data. *([IRReportShareShelf](#irreportshareshelf), optional)*
- `shareShelfByMetrics`: SOS reports data. *(Array<[IRReportShareShelf](#irreportshareshelf)>, optional)*
- `photos`: Information about the photos related to the report. A dictionary with ID of the photo as key and detailed photo information as value. *(Dictionary<String, [IRReportPhoto](#irreportphoto)>, optional)*
- `perfectStore`: The perfect store report data. *([IRReportPerfectStore](#irreportperfectstore), optional)*
- `result`: The result of the report. *([IRReportResult](#irreportresult))*

</details>

---

## IRReportVisitStats

##### Description

A class representing the visit statistics within a report, including photo statistics and various photo ID lists.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `photo`: Photos in visit statistics. *([IRReportVisitPhotoStats](#irreportvisitphotostats))*
- `waitPhotoIds`: The IDs of photos that are waiting to be recognized. *(Array<String>)*
- `deletedPhotoIds`: The IDs of photos that have been deleted. *(Array<String>)*
- `retakenPhotoIds`: The IDs of photos that have been retaken. *(Array<String>)*
- `needSend`: Information about data needed to be uploaded/downloaded. *([IRReportVisitStatsNeedSend](#irreportvisitstatsneedsend))*

</details>

---

## IRReportVisitPhotoStats

##### Description

A class representing the photo statistics within a visit report, including counts for various photo states.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `created`: The number of photos created. *(Int)*
- `completed`: The number of photos completely finished to be recognized. *(Int)*
- `uncompressed`: The number of photos that were created and saved locally but have not been analyzed for errors yet. *(Int)*
- `sent`: The sum of sent and recognized photos. *(Int)*
- `wait`: The number of photos sent on server and waiting to be recognized. *(Int)*
- `deleted`: The number of photos deleted. *(Int)*
- `retake`: The number of photos retaken. *(Int)*

</details>

---

## IRReportVisitStatsNeedSend

##### Description

A class representing the statistics of data needed to be uploaded/downloaded.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `notAnalyzedPhotos`: The number of photos not analyzed. *(Int)*
- `deletedNotSentPhotos`: The number of deleted photos that were not sent. *(Int)*
- `notSentPhotos`: The number of photos that were not sent. *(Int)*
- `notRecognizedPhotos`: The number of photos that were not recognized. *(Int)*
- `notSentAssortmentCorrect`: The number of assortment corrections that were not sent. *(Int)*
- `notSentLackOfAssortmentReasons`: The number of reasons for lack of assortment that were not sent. *(Int)*

</details>

---

## IRReportOSADetails

##### Description

OSA metric detailed data.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `id`: The ID of the product recognized in OSA report. *(String)*
- `name`: The name of the product recognized in OSA report. *(String)*
- `brandId`: The ID of the product brand. *(String)*
- `brandName`: The name of the product brand. *(String)*
- `categoryName`: The name of the category. *(String)*
- `externalId`: The external ID of the product. *(String)*
- `facingFact`: Number of facings of the product recognized. *(Int)*
- `facingPlan`: The planned facings number. *(Int)*
- `facingReal`: Number of facings in context of OSA metric. *(Int)*
- `price`: The price of the product. *(String)*
- `priceType`: The type of the price (normal / promo). *([IRReportPriceType](#irreportpricetype))*
- `productCategoryId`: The ID of the product category. *(String)*
- `maxPriceRange`: The maximum price range. *(Double, optional)*
- `minPriceRange`: The minimum price range. *(Double, optional)*
- `priceStatus`: The price status. *(Int, optional)*
- `sceneTypeId`: The ID of the scene type. *(Int, optional)*

</details>

---

## IRReportPriceType

##### Description

Price type.

##### Cases

- `normal`: Normal price. *(rawValue: 0)*
- `promo`: Promotional price. *(rawValue: 1)*

---

## IRReportShareShelf

##### Description

SOS metric detailed data.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `brands`: The brands included in the SOS report. *(Array<[IRReportShareShelfBrand](#irreportshareshelfbrand)>)*
- `categories`: The categories included in the SOS report. *(Array<[IRReportShareShelfCategory](#irreportshareshelfcategory)>)*
- `macrocategories`: The macro categories included in the SOS report. *(Array<[IRReportShareShelfMacroCategory](#irreportshareshelfmacrocategory)>, optional)*
- `visit`: The visit details included in the SOS report. *(Array<[IRReportShareShelfVisit](#irreportshareshelfvisit)>)*
- `reportType`: The type of the report. *(String, optional)*
- `reportName`: The name of the report. *(String, optional)*

</details>

---

## IRReportShareShelfBrand

##### Description

A class representing the brand details within an SOS report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `brandId`: The ID of the brand. *(String)*
- `brandName`: The name of the brand. *(String)*
- `brandOwnerId`: The ID of the brand owner. *(String)*
- `facing`: The facing value of the brand. *(String)*
- `isOwn`: Indicator if the brand is owned by client. *(Bool)*
- `productCategoryId`: The ID of the product category. *(String)*
- `value`: SOS value in context of the brand. *(Int)*
- `previousValue`: Previous SOS value in context of the brand. *(Int)*
- `visitId`: The ID of the visit. *(String, optional)*
- `percent`: The percentage in context of the brand. *(Double, optional)*

</details>

---

## IRReportShareShelfCategory

##### Description

A class representing the category details within an SOS report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `facing`: The facing value of the category. *(String)*
- `productCategoryId`: The ID of the product category. *(String)*
- `productCategoryName`: The name of the product category. *(String)*
- `value`: SOS value in context of the category. *(Int)*
- `previousValue`: The previous SOS value in context of the category. *(Int)*
- `visitId`: The ID of the visit. *(String, optional)*
- `matched`: How many matched according to plan. *(Int, optional)*
- `percent`: The percentage in context of the category. *(Double, optional)*

</details>

---

## IRReportShareShelfMacroCategory

##### Description

A class representing the macro category details within an SOS report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `id`: The ID of the macro category. *(String)*
- `name`: The name of the macro category. *(String)*
- `value`: SOS value in the context of the macro category. *(Int)*
- `previousValue`: The previous SOS value in the context of the macro category. *(Int)*
- `facing`: The facing value of the macro category. *(String)*
- `matched`: How many matched according to plan. *(Int, optional)*
- `percent`: The percentage in the context of the macro category. *(Double, optional)*

</details>

---

## IRReportShareShelfVisit

##### Description

A class representing the visit details within an SOS report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `value`: The SOS value in the context of the visit. *(Int)*
- `previousValue`: The previous SOS value in the context of the visit. *(Int)*
- `visitId`: The ID of the visit. *(String, optional)*
- `plan`: The planned value. *(Double, optional)*
- `percent`: The percentage in the context of the visit. *(Double, optional)*
- `numerator`: The numerator value for custom SOS formula. *(Double, optional)*
- `denominator`: The denominator value for custom SOS formula. *(Double, optional)*

</details>

---

## IRReportPhoto

##### Description

A class representing the photo details within a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `error`: The error found on the photo. *([IRReportPhotoError](#irreportphotoerror))*
- `imagePath`: The local path to the image. *(String)*
- `imageUrl`: The URL to the image (available when it was sent to server). *(String)*
- `sceneId`: The ID of the scene where photo was made. *(String)*
- `sceneType`: The name of scene type where photo was made. *(String)*
- `sceneTypeId`: The ID of the scene type where photo was made. *(Int)*
- `products`: The products recognized within the photo. *(Array<[IRReportPhotoProduct](#irreportphotoproduct)>)*
 
</details>

---

## IRReportPhotoError

##### Description

A class representing the error details associated with a photo in the report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `code`: The error code. *(Int)*
- `message`: The error message. *(String)*

</details>

---

## IRReportPhotoProduct

##### Description

A class representing the details of product found within a photo in the report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `productId`: The ID of the product. *(String, optional)*
- `productExternalId`: The external ID of the product. *(String)*
- `name`: The name of the product. *(String)*
- `categoryId`: The ID of the category. *(String)*
- `categoryName`: The name of the category. *(String)*
- `brandId`: The ID of the brand. *(String)*
- `brandName`: The name of the brand. *(String)*
- `facing`: The facing value of the product. *(Int)*
- `facingGroup`: The facing group value of the product. *(Int)*
- `price`: The price of the product. *(String, optional)*
- `priceType`: The type of the price (normal / promo). *([IRReportPriceType](#irreportpricetype))*
- `width`: The width of the product. *([IRReportPhotoProductWidth](#irreportphotoproductwidth), optional)*

</details>

---

## IRReportPhotoProductWidth

##### Description

A class representing the width of a product.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `cm`: The width of the product in centimeters. *(Double)*

</details>

---

## IRReportPerfectStore

##### Description

A class representing the perfect store metric details within a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `totalVisitScore`: The total visit score. *(Double, optional)*
- `tasks`: The tasks within the perfect store metric. *(Array<[IRReportPerfectStoreTask](#irreportperfectstoretask)>)*
  
</details>

---

## IRReportPerfectStoreTask

##### Description

A class representing a task within the perfect store metric in a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `id`: The ID of the task. *(String)*
- `name`: The name of the task. *(String)*
- `totalScore`: The total score of the task. *(Double)*
- `percentage`: The percentage score of the task. *(Double)*
- `kpis`: The KPIs associated with the task. *(Array<[IRReportPerfectStoreTaskKPI](#irreportperfectstoretaskkpi)>)*
- `questions`: The questions associated with the task. *(Array<[IRReportPerfectStoreTaskQuestion](#irreportperfectstoretaskquestion)>)*
  
</details>

---

## IRReportPerfectStoreTaskKPI

##### Description

A class representing a KPI within a perfect store task in a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `id`: The ID of the KPI. *(Int, optional)*
- `name`: The name of the KPI. *(String)*
- `metricType`: The type of metric for the KPI. *(String)*
- `matrixType`: The type of assortment matrix for the KPI. *(String)*
- `planValue`: The planned value for the KPI. *(Double)*
- `factValue`: The actual value for the KPI. *(Double)*
- `percentage`: The percentage score for the KPI. *(Double)*
- `scoreValue`: The score value (number of points) for the KPI. *(Double)*

</details>

---

## IRReportPerfectStoreTaskQuestion

##### Description

A class representing a question within a perfect store task in a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `id`: The ID of the question. *(Int, optional)*
- `index`: The index of the question. *(Int)*
- `type`: The type of the question. *(String)*
- `name`: The name of the question. *(String)*
- `required`: Indicates whether the question is required. *(Bool)*
- `answers`: The answers associated with the question. *(Array<[IRReportPerfectStoreTaskQuestionAnswer](#irreportperfectstoretaskquestionanswer)>)*
  
</details>

---

## IRReportPerfectStoreTaskQuestionAnswer

##### Description

A class representing an answer to a question within a perfect store task in a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `index`: The index of the answer. *(Int)*
- `name`: The name of the answer. *(String)*
- `point`: The point value of the answer. *(Int)*

</details>

---

## IRReportResult

##### Description

A class representing summary of a report.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `code`: The result code. *(Int)*
- `codeInt`: The integer representation of the result code. *(Int)*
- `message`: The result message. *(String)*
- `externalVisitId`: The external ID of the visit. *(String, optional)*
- `internalVisitId`: The internal ID of the visit. *(String, optional)*
- `sendedPhotos`: The number of photos sent (both sent only and recognized). *(Int)*
- `totalPhotos`: The total number of photos. *(Int)*

</details>

## IRNotification

##### Description

A class containing static properties for notification names used within the IRLibSwift framework.

##### Properties
<details>
  <summary>Click to expand properties</summary>

- `photoRecognizedNotification`: Notification name for when a photo is recognized. *(Notification.Name)*
- `authErrorNotification`: Notification name for when there is an authentication error in any network request in IrLibSwift. *(Notification.Name)*

</details>

