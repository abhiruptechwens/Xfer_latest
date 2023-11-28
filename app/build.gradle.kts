import com.datadog.gradle.plugin.DatadogSite

plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.datadogGradlePlugin)
}

datadog {
    site = DatadogSite.US5.name
    serviceName = "com.ledgergreen.terminal"
    checkProjectDependencies = com.datadog.gradle.plugin.SdkCheckLevel.NONE
}

android {
    namespace = "com.ledgergreen.terminal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xfer.terminal"
        minSdk = 25
        targetSdk = 34
        versionCode = 578
        versionName = "1.5.8"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "datadogToken", "\"pub23a0c18bcaf8029cdf06ba5148614620\"")
        buildConfigField(
            "String",
            "datadogApplicationId",
            "\"9a896981-5734-4d54-a6b3-97e752048e0c\"",
        )
    }

//    signingConfigs {
//        getByName("debug") {
//            val keystoreFilePath = System.getenv("KEYSTORE_FILE")
//            val localKeystoreFile = file("../signing/ledgergreen_dev.keystore")
//            if (keystoreFilePath != null) {
//                file(keystoreFilePath).renameTo(localKeystoreFile)
//            }
//
//            keyAlias = System.getenv("KEY_ALIAS") ?: "android"
//            keyPassword = System.getenv("KEY_PASSWORD") ?: "android"
//            storePassword = System.getenv("STORE_PASSWORD") ?: "android"
//            storeFile = localKeystoreFile
//        }
//    }

    flavorDimensions += "vendor"
    productFlavors {
        create("nexgo") {
            dimension = "vendor"
            versionNameSuffix = "-Xfer"
        }
        create("morefun") {
            dimension = "vendor"
            versionNameSuffix = "-mf"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")

//            buildConfigField("String", "baseUrl", "\"https://dev-api.instantxfer.com\"")
            buildConfigField("String", "baseUrl", "\"https://api.instantxfer.com\"")
//            buildConfigField("String", "baseUrl", "\"https://dev-api.ledgergreen.com\"")
//            buildConfigField("String", "baseUrl", "\"https://production-api.ledgergreen.com\"")
            buildConfigField("String", "xAccessKey", "\"ksldjsakjdj-asdkajdnaskjd-232wewqesdfds\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")

//            buildConfigField("String", "baseUrl", "\"https://production-api.ledgergreen.com\"")
            buildConfigField("String", "baseUrl", "\"https://api.instantxfer.com\"")
            buildConfigField("String", "xAccessKey", "\"ksldjsakjdj-asdkajdnaskjd-232wewqesdfds\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
    implementation(platform(libs.compose.bom))
    implementation(libs.kotlinx.dateTime)
    implementation(libs.kotlinx.collection.immutable)

    implementation(libs.mrz.parser)
    implementation(libs.nvI18n)

    implementation(libs.bundles.app.ui)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.hilt.navigationCompose)

    implementation(libs.kotlinx.serialization)

    implementation(libs.signaturePad)

    implementation(libs.timber)

    implementation(libs.gson)
    implementation(libs.jwtDecode)
    implementation(libs.datastore.preferences)
    implementation(libs.coil.compose)

    implementation(libs.bundles.ktor.common)
    implementation(libs.ktor.client.okHttp)
    implementation(libs.ktor.client.serialization)

    implementation(libs.bundles.datadog)

    implementation(libs.kotlinx.serialization)

    implementation(libs.dagger.android)
    kapt(libs.dagger.compiler)
    implementation(libs.androidx.hilt.navigationCompose)
    kapt(libs.androidx.hilt.compiler)
    testImplementation(libs.dagger.android)
    kaptTest(libs.dagger.compiler)
    androidTestImplementation(libs.dagger.android)
    kaptAndroidTest(libs.dagger.compiler)

    implementation(libs.coroutines.android)

    implementation(libs.zxing.core)

    debugImplementation(libs.leakcanary)
}
