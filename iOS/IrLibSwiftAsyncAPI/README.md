# New API
Starting from `5.10` version new asynchronous API is available and preferrable to communicate with `IrLibSwift` framework. 
Use `IRInteractManager` for making calls via new API. Detailed documentation of new methods and classes is available for [Swift](IrLibSwift-docs-swift.md) and [Objective-C](IrLibSwift-docs-objc.md)

# Legacy API compatibility
You can continue to use old synchronous API using `IRLib` framework, though new async API is more preferrable, reliable and convinient. 

# Installation
If you want to use new asynchronous API, the only framework is needed for you is `IrLibSwift`, whether you're working on a swift or obj-c project.

## Installation via [Cocoapods](https://cocoapods.org) ##

1. Add repo with Intelligence Retail specs and official Cocoapods specs into your project `Podfile` :

```
     source 'https://github.com/CocoaPods/Specs.git'
     source 'https://github.com/intrtl/specs'
```

2. Add  `use_frameworks!` param to your `Podfile`.

3. Add `IrLibSwift` pod as a dependency to your projects targets:

```
  target 'YourTarget' do
    pod 'IrLibSwift'
  end
```

4. Run `pod install` via terminal in directory with your project.

5. To update version of previously installed framework run `pod update IrLibSwift --repo-update` via terminal in directory with your project.