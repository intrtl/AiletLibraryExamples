# Ailet Lib Future Enhancements

## Using Ailet functionality

Use `AiletInteractionManager` methods to work with Ailet features. 

### Methods:

#### Initial Setup

Call `setup` method to load all the necessary for further work data. You will get result asynchronously in a completion closure. 

##### Swift:
In Swift you will receive the result in Result and AiletError enums.
```swift
AiletInteractionManager.setup(
    username: "username",
    password: "password",
    guestToken: "your-guest-token", 
    notification: "notificationId",
    isMultiportal: false,
    completion: { result in
        switch result {
            case .success:
                // handle success here. you can call `start` method in this case
            case .failure(let error):
                // handle errors from AiletError enum
                if error == AiletError.noConnection { }
        }
    }
)
```
##### Obj-C:
In Obj-C you will have to use nullable NSError to handle the result.
```objectivec
[AiletInteractionManager setupUsername:@"username" 
                              password:@"password" 
                            guestToken:@"your-guest-token"
                          notification:@"notificationId"
                         isMultiportal:NO, 
                            completion:^(NSError *error) {
    if (error) {
        // handle error codes here
        if (error.code == IR_ERROR_NO_INET) { }
        return;
    }
    if (error == nil) {
        // Handle success here. Now you can call `start` method
    }
}];
```

#### Start
Call `start` method to open Ailet camera for shooting and recognition. 

##### Swift
Handle start method errors with do-catch block
```swift
do {
    try AiletInteractionManager.start(
            externalStoreId: "storeId",
            externalVisitId: "externalVisitId"
        )        
} catch {
    // handle error codes here
    guard let error = error as? AiletError else { return }
    switch error {
        case .noConnection:
            print("no connection")
        default:
            print(error)
    }
}
```

##### Objc-C
Handle start method errors with returned nullable NSError code:

```objectivec
NSError *error = [AiletInteractManager  startWithExternalStoreId:@"externalStoreId"  externalVisitId:@"externalVisitId"];

if (error) {
    // handle error codes here
    if (error.code == IR_ERROR_NO_INET) { }
    return;
}
```
