# This file is a helper for Android Education Knowledge Graph App project, you could follow the below steps to install and enjoy our system

### Before starting this program

You should firstly open the backend tomcat server(Run the ``startup.bat`` in ``./bin/``, don't forget wo run the ``shutdown.bat`` in ``./bin/`` when you do not use the server) and ensure your PC(if the tomcat server is opened on PC end) and your mobile phone are in the same local network.

Successful backend server starting page should be like:

![](./doc/successful.png)

There are some ways to connect your mobile phone to the local network of the PC end:

1. To open the hotspot of the mobile phone and you could connect the PC to the local network of the mobile phone.

2. To use a proxy link the computer and mobile phone to a static IP.

### When starting the program

You have two choice, either to use an Android Studio and a emulator to run the app on your PC end, or you could directly connect your phone to PC and run that on your mobile phone. (Note that if you want to directly run the app program file directory, you should firstly install the Android Studio)

You should firstly modify the url in AppSingleton, and add your own password and name in corresponding slots.

```
public static final String URL_PREFIX = "$the url of the tomcat backend";
public static final String EDUKG_PREFIX = "$the prefix url of edukg";
public static final String EUDKG_PHONE = "$your phone number to edukg";
public static final String EDUKG_PASSWORD = "$your password to edukg";
```

### After starting the program

See the toast notification whether the network is working fine.