// Archivo de build del módulo `app` (la aplicación Android).
// Nota: con AGP 9 el soporte de Kotlin viene integrado, por eso ya no se
// aplica el plugin `org.jetbrains.kotlin.android`.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Para las rutas de navegación con tipos (@Serializable).
    alias(libs.plugins.kotlin.serialization)
    // KSP genera código en tiempo de compilación (lo usan Hilt y Room).
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

android {
    namespace = "com.upt.dormirbien"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.upt.dormirbien"
        // minSdk 28 (Android 9): mínimo que pide Health Connect.
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Room guarda aquí el esquema de la base de datos en JSON, una versión por
// archivo. Se sube a Git: sirve para revisar cambios y probar migraciones.
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // --- AndroidX base ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)

    // --- Compose + Material 3 ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // --- Navegación ---
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // --- Hilt (inyección de dependencias) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // hiltViewModel(): obtener ViewModels de Hilt desde Compose.
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // --- Room (base de datos) y DataStore (preferencias) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // --- Pruebas unitarias (corren en la computadora) ---
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    // --- Pruebas instrumentadas (corren en emulador o teléfono) ---
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // --- Solo en debug: herramientas de vista previa ---
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
