// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath ("com.google.protobuf:protobuf-gradle-plugin:0.9.1")
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    //id ("com.android.application") version "7.3.1" apply false
    //id ("com.android.library") version "7.3.1" apply false
    //id ("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.5.21" apply false
}