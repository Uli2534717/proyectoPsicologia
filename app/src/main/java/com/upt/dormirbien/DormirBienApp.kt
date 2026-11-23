package com.upt.dormirbien

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application: lo primero que Android crea al abrir la app.
 *
 * `@HiltAndroidApp` activa Hilt: genera el "contenedor" de dependencias que
 * vive mientras la app esté abierta. Se registra en AndroidManifest.xml
 * con `android:name=".DormirBienApp"`.
 */
@HiltAndroidApp
class DormirBienApp : Application()
