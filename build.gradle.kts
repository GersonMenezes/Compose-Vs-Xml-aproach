// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false // <-- Garanta que esta versão seja "2.0.0"
    alias(libs.plugins.compose.compiler) apply false
}