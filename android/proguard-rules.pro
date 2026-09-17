# BYD Auto OpenAPI — Reflection 대상 전체 보존 (없으면 런타임 실패)
-keep class android.hardware.** { *; }
-keep class android.hardware.bydauto.** { *; }
-keepclassmembers class android.hardware.bydauto.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
