//import com.google.protobuf.gradle.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    //id ("com.android.application")
    //id ("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.5.21" // Add this line
    id ("kotlinx-serialization")
    id ("com.google.protobuf")
}


android {
    namespace = "com.example.prago"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prago"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    applicationVariants.all {
        this.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                //val variant = this.buildType.name
                val apkName = "prago-v0.1.0.apk"
//                var apkName =
//                    this.flavorName[0].uppercase() + this.flavorName.substring(1) + "_" + this.versionName
//                if (variant.isNotEmpty()) apkName += "_$variant"
//                apkName += ".apk"
//                println("ApkName=$apkName ${this.buildType.name}")
                output.outputFileName = apkName
            }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
    protobuf{
        protoc{
            artifact = "com.google.protobuf:protoc:3.20.1"
        }

        generateProtoTasks{
            all().forEach { task ->
                task.builtins {
                    create("java") {
                        option("lite")
                    }
                    create("kotlin") {
                        option("lite")
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.google.material)
    implementation(libs.khttp)
    implementation(libs.viewmodel.compose)
    implementation(libs.datastore)
    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.runtime.livedata)
    implementation(libs.datastore.preferences)
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.numberpicker)
}
