# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/pierrot/Devel/Android/SDKs/android-studio/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Signature, LineNumberTable, SourceFile, *Annotation*, EnclosingMethod

-keep class * extends android.app.Fragment
-keep class * extends android.app.Service {*;}

-keep class sun.misc.Unsafe { *; }
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-dontwarn java.lang.invoke.*
-dontwarn sun.misc.Unsafe
-dontwarn org.mozilla.javascript.**
