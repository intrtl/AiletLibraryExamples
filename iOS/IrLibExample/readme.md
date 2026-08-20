[![Latest Release](https://img.shields.io/badge/latest%20release-6.5.4-brightgreen)](https://github.com/intrtl/specs)

# Integrating the Ailet library

The Ailet library embeds visit shooting, reports, and synchronization into your iOS app.

Framework: `IrLibSwift`. API client: `IRInteractManager`. From version 5.10 this is an asynchronous API.

`IrLibSwift` is enough for the asynchronous API. You need it in both Swift and Objective-C projects.

To call Ailet without the library, use [integration via iOS deeplink](../deeplink/readme.md).

See the [Swift method reference](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-swift.md) and the [Objective-C method reference](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-objc.md).

- [Scenario example](#scenario-example)
- [What you need](#what-you-need)
- [How to install with CocoaPods](#how-to-install-with-cocoapods)
- [How to update the framework](#how-to-update-the-framework)
- [How to initialize the library](#how-to-initialize-the-library)
- [How to start shooting](#how-to-start-shooting)
- [How to get a report](#how-to-get-a-report)
- [Synchronous API](#synchronous-api)

## Scenario example

To run a visit and get a report:

1. Add `IrLibSwift` with CocoaPods.
2. Call `IRInteractManager.setup(...)`.
3. Call `IRInteractManager.startShooting(...)`.
4. Request the report with `IRInteractManager.report(visitId:)` or subscribe to `IRNotification`.

## What you need

- CocoaPods.
- An initial authorization token (`guestToken`). The Ailet team issues it.

## How to install with CocoaPods

To [install with CocoaPods](https://cocoapods.org), add the repositories, `use_frameworks!`, and the `IrLibSwift` pod to the `Podfile`:

```ruby
source 'https://github.com/CocoaPods/Specs.git'
source 'https://github.com/intrtl/specs'

use_frameworks!

target 'YourTarget' do
  pod 'IrLibSwift'
end
```

Then run this in the project directory:

```bash
pod install
```

## How to update the framework

To update an already installed `IrLibSwift`, run this in the project directory:

```bash
pod update IrLibSwift --repo-update
```

## How to initialize the library

To start work, call [`setup`](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-swift.md#setup). The method authorizes the user and downloads data the library needs.

```swift
IRInteractManager.setup(
    username: "user123",
    password: "securePassword",
    guestToken: "guestToken123"
) { result in
    switch result {
    case .success:
        // the library is ready
    case .failure(let error):
        // handle IRError
    }
}
```

## How to start shooting

To open the Ailet camera, call [`startShooting`](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-swift.md#start-shooting):

```swift
do {
    try IRInteractManager.startShooting(
        in: viewController,
        externalStoreId: "store123",
        externalVisitId: "visit456"
    )
} catch {
    // handle IRError
}
```

## How to get a report

To get a local visit report, call [`report(visitId:)`](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-swift.md#retrieve-report-data-for-specific-visit):

```swift
do {
    let report = try IRInteractManager.report(visitId: "visit123")
} catch {
    // handle the error
}
```

To receive photo recognition updates, subscribe to [`IRNotification`](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-swift.md#subscribe-for-notifications) through `NotificationCenter`.

The full list of methods, parameters, and classes is in the [Swift reference](https://github.com/intrtl/AiletLibraryExamples/blob/master/iOS/IrLibSwiftAsyncAPI/IrLibSwift-docs-swift.md).

## Synchronous API

The synchronous API remains in the `IRLib` framework. For a new integration, add only `IrLibSwift`.
