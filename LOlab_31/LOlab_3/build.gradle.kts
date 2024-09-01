

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

extra["roomVersion"] = "2.6.1"
extra["archLifecycleVersion"] = "2.2.0"
extra["materialVersion"] = "1.5.0"
extra["kotlinVersion"] = "1.5.10"