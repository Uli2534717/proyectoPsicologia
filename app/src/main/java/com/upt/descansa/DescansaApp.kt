package com.upt.descansa

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application: lo primero que Android crea al abrir la app.
 * `@HiltAndroidApp` activa Hilt (el contenedor de dependencias).
 * Se registra en AndroidManifest.xml con `android:name=".DescansaApp"`.
 */
@HiltAndroidApp
class DescansaApp : Application()
