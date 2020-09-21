# Integrate irLib into project

- [Integrate irLib into project](#integrate-irlib-into-project)
  - [Include irLib using Cocapods](#include-irlib-using-cocapods)
  - [Configure target](#configure-target)

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

