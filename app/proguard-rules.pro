# ---------- Kotlin & general ----------
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, RuntimeVisibleAnnotations
-dontwarn kotlin.**

# ---------- kotlinx.serialization ----------
# Keep the serializer companion (Companion.serializer()) lookup working
-keepclasseswithmembers class **$$serializer {
    *** INSTANCE;
    *** serializer(...);
}
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <1>$<2> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all @Serializable classes and their members (DTO/model layer)
-keep,includedescriptorclasses @kotlinx.serialization.Serializable class com.project.binar.okariru.**{ *; }
-keepclassmembers class com.project.binar.okariru.** {
    <fields>;
}

# ---------- Retrofit ----------
-keepattributes Exceptions
-keep interface com.project.binar.okariru.data.**.remote.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes AnnotationDefault

# ---------- OkHttp / Okio ----------
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn javax.annotation.**

# ---------- Gson (kept in case any model still uses it) ----------
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ---------- Room ----------
-keep class com.project.binar.okariru.**.local.** { *; }

# ---------- Hilt / Dagger (usually handled by consumer rules, kept as safety net) ----------
-dontwarn dagger.hilt.**

# ---------- Data classes used in Retrofit request/response bodies ----------
-keep class com.project.binar.okariru.data.**.dto.** { *; }
-keep class com.project.binar.okariru.core.network.ApiEnvelope { *; }