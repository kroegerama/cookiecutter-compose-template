import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    namespace = "{{ cookiecutter.namespace }}"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "{{ cookiecutter.application_id }}"
        minSdk {
            version = release({{ cookiecutter.min_sdk }})
        }
        targetSdk {
            version = release(37)
        }
        versionCode = 1
        versionName = "0.9.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    base.archivesName = defaultConfig.run {
        "$applicationId-$versionName-b$versionCode"
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        debug {
            isDefault = true
            versionNameSuffix = "-dbg"
            applicationIdSuffix = ".dbg"
        }
        release {
            versionNameSuffix = "-rls"

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    // dependency bundles
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.kaiteki)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation3)

    // androidx/kotlinx
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.window)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    // dagger/hilt
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // utilities
    implementation(libs.logcat)

    // compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)

    // tooling
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // other dependencies
    coreLibraryDesugaring(libs.desugar)

    implementation(projects.network)
}

kotlin {
    val jvmVersion: String by project
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(jvmVersion)
    }
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jvmVersion)
        freeCompilerArgs.addAll(
            "-Xannotation-default-target=param-property",
            "-Xcontext-parameters"
        )
        optIn.addAll(
            "kotlin.uuid.ExperimentalUuidApi",
            "kotlin.time.ExperimentalTime",
            "kotlinx.coroutines.FlowPreview",
            "kotlinx.coroutines.ExperimentalCoroutinesApi",
            "androidx.compose.ui.text.ExperimentalTextApi",
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi"
        )
    }
}

// prepare for cases when room is added
ksp {
    arg("room.incremental", "true")
    arg("room.schemaLocation", projectDir.resolve("schemas").path)
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles.addAll(
        layout.projectDirectory.file("compose_stability.conf")
    )
}
