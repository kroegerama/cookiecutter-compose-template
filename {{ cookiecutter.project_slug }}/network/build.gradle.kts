import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kmpgen)
    alias(libs.plugins.undercouch.download)
}

android {
    namespace = "{{ cookiecutter.namespace }}.api"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk {
            version = release({{ cookiecutter.min_sdk }})
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(libs.kaiteki.core)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.startup)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.logcat)

    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.noop)

    coreLibraryDesugaring(libs.desugar)
}

val specUrl = "https://raw.githubusercontent.com/OAI/learn.openapis.org/refs/heads/main/examples/v3.0/petstore-expanded.yaml"
val spec = layout.projectDirectory.file("spec.yaml")

kmpgen {
    spec(
        packageName = "{{ cookiecutter.namespace }}.api"
    ) {
        specFile = spec
    }
}

tasks.register<Download>("downloadSpec") {
    group = "kmpgen"
    description = "Download the latest OpenAPI spec"
    src(specUrl)
    dest(spec)
    overwrite(true)
}
