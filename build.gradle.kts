// Archivo de build de la RAÍZ del proyecto.
// Aquí solo se declaran los plugins (con `apply false`) para que Gradle
// descargue una sola versión de cada uno; el módulo `app` es quien los aplica.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
}
