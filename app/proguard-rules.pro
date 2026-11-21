# Add project specific ProGuard rules here.
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature

# Keep Shopify DTOs for Moshi KotlinJsonAdapterFactory reflection
-keep class com.example.data.** { *; }
-keep class com.example.model.** { *; }
