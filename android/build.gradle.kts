plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bydhealth.monitor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bydhealth.monitor"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField(
            "String",
            "SERVER_BASE_URL",
            "\"${providers.gradleProperty("BYD_SERVER_BASE_URL").orElse("https://byd-server-production.up.railway.app").get()}\""
        )
        buildConfigField(
            "String",
            "SERVER_API_KEY",
            "\"${providers.gradleProperty("BYD_SERVER_API_KEY").orElse("").get()}\""
        )
        // CI에서 -PversionName=x.y.z -PversionCode=N 으로 주입
        versionName = providers.gradleProperty("versionName").orElse(versionName ?: "1.0.0").get()
        versionCode = providers.gradleProperty("versionCode").orElse("${versionCode ?: 1}").get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            // 개인 프로젝트 배포용 — debug keystore로 서명 (DiLink ADB 설치 가능)
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // BYD Auto OpenAPI stub — 컴파일 타임만, 런타임은 차량 시스템이 주입
    // 실기기 확보 시 libs/bydauto-openapi.jar로 교체 가능
    compileOnly(project(":bydauto-stub"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.activity)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.core.ktx)
    implementation(libs.okhttp)

    debugImplementation(libs.compose.ui.tooling)
}
