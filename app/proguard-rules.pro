# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep location-related classes
-keep class com.fakelocation.app.location.** { *; }

# Keep model classes
-keep class com.fakelocation.app.domain.model.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Kotlin
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
