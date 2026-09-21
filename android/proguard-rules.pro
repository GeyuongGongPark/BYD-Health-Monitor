# BYD Auto OpenAPI — Reflection 대상 전체 보존 (없으면 런타임 실패)
-keep class android.hardware.** { *; }
-keep class android.hardware.bydauto.** { *; }
-keepclassmembers class android.hardware.bydauto.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keepclasseswithmembers class * { @javax.inject.Inject <init>(...); }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Kotlin data class — 필드 제거 방지
-keep class com.bydhealth.monitor.domain.model.** { *; }
-keep class com.bydhealth.monitor.data.network.UpdateInfo { *; }

# Kotlin Coroutines
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-keep class kotlinx.coroutines.** { *; }

# JSON 직렬화 (org.json)
-keep class org.json.** { *; }
