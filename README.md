# dx-mfp-demo

A simple Android app for XDX Xcelerators.

Features:
  * Opens up a hardcoded URL in an Android WebView
  * Supports push notifications (from MFP console)
  * Spoofs user agent string, so Portal device detection sees it as Worklight

To customise:
  * Modify URLs in:
    * app/src/main/assets/mfpclient.properties
    * app/src/main/java/com/ibm/mfpstarterandroid/ServerConnectActivity.java
  * Modify the various application icons (ic_launcher.png) in:
    * app/src/main/res/mipmap-*
  * Use the Theme Editor in Android Studio to customise color scheme

