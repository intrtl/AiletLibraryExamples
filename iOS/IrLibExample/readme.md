# Integrate irLib into project

- [Integrate irLib into project](#integrate-irlib-into-project)
  - [Include irLib using Cocapods](#include-irlib-using-cocapods)
  - [Configure target](#configure-target)
  - [Using Multiportal functionality](#using-multiportal-functionality)
    - [Init](#init)
    - [Switch portal](#switch-portal)
    - [setPortal results](#setportal-results)

## Include irLib using Cocapods

Add in podfile header:

```
source 'https://github.com/CocoaPods/Specs.git'
source 'https://github.com/intrtl/specs'
```

And in podfile target section:
```
pod 'IRLib'
```

## Configure target

Set **Requires full screen** flag to True.

## Using Multiportal functionality

### Init
If you need using more than one portal, set to **YES** isMultiportal parameter in init:
```objC
long res = [IrView init:@"username"
               password:@"password"
             guestToken:@"your-guest-token"
           notification:@"notificationID"
          isMultiportal:YES];
```
### Switch portal
For switch portal use **setPortal** function with portal ID as parameter:
```objC
[IrView setPortal: @"demoPortal"];
```

### setPortal results

| Result | Code | Description |
|---|:-:|---|
| IR_RESULT_OK | 1 | Switch portal success |
| IR_ERROR_NOT_MULTIPORTAL_MODE  | 23 |Set portal ID in non multiportal mode |
| IR_ERROR_PORTAL_INCORRECT  | 24 | Incorrect portal ID or portal not associated with user |
| IR_ERROR_EMPTY_PORTAL  | 25 | Portal ID is null and using multiportal mode |
