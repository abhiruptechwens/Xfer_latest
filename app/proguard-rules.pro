# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class com.ledgergreen.terminal.** { *; }
# nexgo sdk
-keep class com.nexgo.** { *; }
-dontwarn com.nexgo.**
-keep class com.srt.decoder.** { *; }
-dontwarn com.srt.decoder.**
-keep class com.xinguodu.** { *; }
-dontwarn com.xinguodu.**
-keep class com.a.** { *; }
-dontwarn com.a.**
-keep class com.xgd.** { *; }
-dontwarn com.xgd.**

# YSDK
-keep class com.morefun.yapi.** { *; }

#
-keep class com.ledgergreen.terminal.data.network.** { *; }
-keep class com.ledgergreen.terminal.data.** { *; } # just for test purposes

# auth0
-keep class com.auth0.** { *; }
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Serializer for classes with named companion objects are retrieved using `getDeclaredClasses`.
# If you have any, replace classes with those containing named companion objects.
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.

# TODO: add kotlinx.serialization rules from https://github.com/Kotlin/kotlinx.serialization#android

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
