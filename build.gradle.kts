buildscript {
    repositories {
        google()
        mavenCentral()
    }

}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.application) apply(false)
    alias(libs.plugins.kotlin) apply(false)
    alias(libs.plugins.hilt) apply(false)
    id("com.github.ben-manes.versions") version("0.43.0")
    id("nl.littlerobots.version-catalog-update") version("0.7.0")  
}