import com.datadog.gradle.plugin.DatadogSite

plugins {
//    id("newrelic")
//    id("io.sentry.android.gradle") version "4.0.0"
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


//sentry {
//    // Disables or enables debug log output, e.g. for for sentry-cli.
//    // Default is disabled.
//    debug.set(false)
//
//    // The slug of the Sentry organization to use for uploading proguard mappings/source contexts.
//    org.set("com.ledgergreen.terminal")
//
//    // The slug of the Sentry project to use for uploading proguard mappings/source contexts.
//    projectName.set("com.ledgergreen.terminal")
//
//    // The authentication token to use for uploading proguard mappings/source contexts.
//    // WARNING: Do not expose this token in your build.gradle files, but rather set an environment
//    // variable and read it into this property.
//    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
//
//    // The url of your Sentry instance. If you're using SAAS (not self hosting) you do not have to
//    // set this. If you are self hosting you can set your URL here
////    url = "https://2b66406a6701f46993bcf6c105748028@o4506336403783680.ingest.sentry.io/4506337850621952"
//
//    // Disables or enables the handling of Proguard mapping for Sentry.
//    // If enabled the plugin will generate a UUID and will take care of
//    // uploading the mapping to Sentry. If disabled, all the logic
//    // related to proguard mapping will be excluded.
//    // Default is enabled.
//    includeProguardMapping.set(true)
//
//    // Whether the plugin should attempt to auto-upload the mapping file to Sentry or not.
//    // If disabled the plugin will run a dry-run and just generate a UUID.
//    // The mapping file has to be uploaded manually via sentry-cli in this case.
//    // Default is enabled.
//    autoUploadProguardMapping.set(true)
//
//    // Experimental flag to turn on support for GuardSquare's tools integration (Dexguard and External Proguard).
//    // If enabled, the plugin will try to consume and upload the mapping file produced by Dexguard and External Proguard.
//    // Default is disabled.
//    dexguardEnabled.set(false)
//
//    // Disables or enables the automatic configuration of Native Symbols
//    // for Sentry. This executes sentry-cli automatically so
//    // you don't need to do it manually.
//    // Default is disabled.
//    uploadNativeSymbols.set(false)
//
//    // Whether the plugin should attempt to auto-upload the native debug symbols to Sentry or not.
//    // If disabled the plugin will run a dry-run.
//    // Default is enabled.
//    autoUploadNativeSymbols.set(true)
//
//    // Does or doesn't include the source code of native code for Sentry.
//    // This executes sentry-cli with the --include-sources param. automatically so
//    // you don't need to do it manually.
//    // Default is disabled.
//    includeNativeSources.set(false)
//
//    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
//    // This enables source context, allowing you to see your source
//    // code as part of your stack traces in Sentry.
//    includeSourceContext.set(false)
//
//    // Configure additional directories to be included in the source bundle which is used for
//    // source context. The directories should be specified relative to the Gradle module/project's
//    // root. For example, if you have a custom source set alongside 'main', the parameter would be
//    // 'src/custom/java'.
//    additionalSourceDirsForSourceContext.set(emptySet())
//
//    // Enable or disable the tracing instrumentation.
//    // Does auto instrumentation for specified features through bytecode manipulation.
//    // Default is enabled.
//    tracingInstrumentation {
//        enabled.set(true)
//
//        // Specifies a set of instrumentation features that are eligible for bytecode manipulation.
//        // Defaults to all available values of InstrumentationFeature enum class.
////        features.set(setOf(InstrumentationFeature.DATABASE, InstrumentationFeature.FILE_IO, InstrumentationFeature.OKHTTP, InstrumentationFeature.COMPOSE))
//
//        // Enable or disable logcat instrumentation through bytecode manipulation.
//        // Default is enabled.
//        logcat {
//            enabled.set(true)
//
//            // Specifies a minimum log level for the logcat breadcrumb logging.
//            // Defaults to LogcatLevel.WARNING.
//            minLevel.set(io.sentry.android.gradle.instrumentation.logcat.LogcatLevel.WARNING)
//        }
//
//        // The set of glob patterns to exclude from instrumentation. Classes matching any of these
//        // patterns in the project's sources and dependencies JARs won't be instrumented by the Sentry
//        // Gradle plugin.
//        //
//        // Don't include the file extension. Filtering is done on compiled classes and
//        // the .class suffix isn't included in the pattern matching.
//        //
//        // Example usage:
//        // ```
//        // excludes.set(setOf("com/example/donotinstrument/**", "**/*Test"))
//        // ```
//        //
//        // Only supported when using Android Gradle plugin (AGP) version 7.4.0 and above.
//        excludes.set(emptySet())
//    }
//
//    // Enable auto-installation of Sentry components (sentry-android SDK and okhttp, timber, fragment and compose integrations).
//    // Default is enabled.
//    // Only available v3.1.0 and above.
//    autoInstallation {
//        enabled.set(true)
//
//        // Specifies a version of the sentry-android SDK and fragment, timber and okhttp integrations.
//        //
//        // This is also useful, when you have the sentry-android SDK already included into a transitive dependency/module and want to
//        // align integration versions with it (if it's a direct dependency, the version will be inferred).
//        //
//        // NOTE: if you have a higher version of the sentry-android SDK or integrations on the classpath, this setting will have no effect
//        // as Gradle will resolve it to the latest version.
//        //
//        // Defaults to the latest published Sentry version.
//        sentryVersion.set("7.0.0")
//    }
//
//    // Disables or enables dependencies metadata reporting for Sentry.
//    // If enabled, the plugin will collect external dependencies and
//    // upload them to Sentry as part of events. If disabled, all the logic
//    // related to the dependencies metadata report will be excluded.
//    //
//    // Default is enabled.
//    //
//    includeDependenciesReport.set(true)
//
//    // Whether the plugin should send telemetry data to Sentry.
//    // If disabled the plugin won't send telemetry data.
//    // This is auto disabled if running against a self hosted instance of Sentry.
//    // Default is enabled.
//    telemetry.set(true)
//}




android {
    namespace = "com.ledgergreen.terminal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xfer.terminal"
        minSdk = 25
        targetSdk = 34
        // test versioncode
//        versionCode = 609


        // prod versioncode
        versionCode = 607


        // test versionName
//        versionName = "1.6.4.609"


        // prod versionName
        versionName = "Xfer-1.6.5.607"

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
//            versionNameSuffix = "Xfer"
        }
        create("morefun") {
            dimension = "vendor"
            versionNameSuffix = "-mf"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = true
//            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
//    implementation("com.newrelic.agent.android:android-agent:7.2.0")
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
