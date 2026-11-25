# Decisiones del proyecto Descansa

Registro de lo que se decidió cuando el documento del proyecto no lo definía
o cuando hubo que cambiarlo, con su motivo. Formato: **qué** → **por qué**.
Las más recientes van al final.

---

## D-01 · Kotlin + Jetpack Compose en lugar de Flutter

**Qué:** la app es Android nativa: Kotlin y Jetpack Compose (Material 3). No se usa Flutter.

**Por qué:**
- El propio documento (sección 2) exige que todo lo crítico (sensor, motor,
  alarma y No molestar) se haga en Kotlin, dentro de un servicio en primer plano.
  Con Compose, la interfaz también es Kotlin, así que no hace falta el puente
  `MethodChannel`/`EventChannel` entre Dart y Kotlin, que sería una fuente
  extra de errores.
- La entrega es el 2 de diciembre. Ya había una base en Kotlin compilando y
  probada; Flutter no estaba instalado y el equipo tendría que aprender Dart.
- Lo único que se pierde es la opción de iOS a futuro, que el documento deja
  fuera del alcance actual.

Decidido con el equipo (Ulises) el 23 de noviembre de 2026.

**Equivalencias con el stack del documento:**

| Documento (Flutter) | Descansa (Kotlin) |
|---|---|
| Flutter + Material 3 | Jetpack Compose + Material 3 |
| Riverpod | ViewModel + StateFlow + Hilt |
| go_router | Navigation Compose con rutas `@Serializable` |
| drift (SQLite) | Room (SQLite) con migraciones y esquemas exportados |
| shared_preferences | DataStore Preferences |
| just_audio / audio_service | Media3 (ExoPlayer + MediaSessionService) |
| MethodChannel / EventChannel | No hace falta (todo es Kotlin) |
| `.arb` + flutter_localizations | `res/values/strings.xml` (español de México) |
| `flutter analyze` | `./gradlew lint` + advertencias del compilador |
| `flutter_test` | JUnit + MockK + Turbine + Compose UI Test |

## D-02 · Nombre y paquete

**Qué:** la app se llama **Descansa** y el paquete es `com.upt.descansa`.
El repositorio de GitHub sigue siendo `proyectoPsicologia`.

**Por qué:** es el nombre del prototipo de interfaces.
Nota: la carpeta local del proyecto se llama `DormirBien` (nombre anterior);
no afecta en nada a la app.

## D-03 · Solo tema oscuro

**Qué:** no hay tema claro.

**Por qué:** el prototipo solo define tokens oscuros, la app se usa de noche
y así el contraste (4.5:1 mínimo) se revisa en un solo esquema.

## D-04 · Fuente Nunito pendiente

**Qué:** mientras no estén los archivos `.ttf` de Nunito en `res/font/`, se usa
la fuente del sistema. Todo está centralizado en `core/theme/Type.kt`.

**Por qué:** descargar archivos requiere el permiso del equipo. Nunito tiene
licencia SIL Open Font License, que permite empaquetarla.

## D-05 · Nombres de tablas en español

**Qué:** las tablas de la sección 5 se nombran en español, con las mismas
columnas:

| Documento | Room |
|---|---|
| `nights` | `noches` |
| `epochs` | `epocas` |
| `tasks` | `pendientes` |
| `sound_stats` | `estadisticas_sonido` |
| `settings` | DataStore `ajustes` |

**Por qué:** la convención del equipo es nombrar el dominio en español.
Fechas en UTC (epoch ms); la hora límite se guarda como "minutos desde las 00:00"
para que no dependa de la zona horaria.

## D-06 · Acceso a Ajustes y "No molestar" en Inicio

**Qué:** Inicio tiene un ícono de engrane (arriba a la derecha) que abre Ajustes.
La herramienta "No molestar" de la cuadrícula también abre Ajustes.

**Por qué:** el prototipo no muestra cómo llegar a Ajustes, y el documento
pide el interruptor de No molestar en Alarma y en Ajustes.

## D-07 · El paso 5 no se puede omitir

**Qué:** los pasos 1 a 4 tienen "Omitir paso"; el 5 (Alarma) no.

**Por qué:** el paso 5 es el que activa el modo noche. Omitirlo sería lo mismo
que no preparar la noche (para eso está la flecha de regreso).

## D-08 · Datos de ejemplo marcados

**Qué:** en la Fase 0, el resumen de la mañana usa datos de ejemplo y lo dice
en pantalla.

**Por qué:** principio de honestidad: nunca presentar datos falsos como reales.

## D-09 · Versión del BOM de Compose

**Qué:** Compose BOM `2026.09.00` (Compose 1.12.1, Material 3 1.4.0).

**Por qué:** el BOM `2026.11.x` trae Compose 1.13, que exige compileSdk 37.1
(no instalado). Se puede subir cuando todo el equipo instale ese SDK.

## D-10 · Íconos

**Qué:** `material-icons-extended` (versión Outlined) en lugar de Material Symbols.

**Por qué:** son los mismos íconos de línea del prototipo y vienen como
`ImageVector`, sin archivos extra. R8 elimina los que no se usan en la versión release.

## D-11 · Alarma de respaldo (Fase 1)

Revisado contra la documentación oficial de Android el 23 de noviembre de 2026
(alarmas exactas, tipos de servicio en primer plano, cambios de Android 14 y 15).

**Qué:**

| Pieza | Elección |
|---|---|
| Programar | `AlarmManager.setAlarmClock()` en `AlarmSchedulerImpl` |
| Permiso de alarmas exactas | `USE_EXACT_ALARM` (Android 13+) y `SCHEDULE_EXACT_ALARM` con `maxSdkVersion="32"` (Android 12) |
| Sonar | `TimbreService`, servicio en primer plano de tipo `systemExempted\|mediaPlayback` |
| Pantalla | `TimbreActivity` por notificación de pantalla completa (`USE_FULL_SCREEN_INTENT`) |
| Reinicio | `ArranqueReceiver` (`LOCKED_BOOT_COMPLETED`, `BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`) reprograma la hora guardada; ver D-14 |
| Volumen | empieza al 10 % y sube durante 60 s; si el volumen de alarmas está en 0, se sube a la mitad |
| Sin respuesta | se calla a los 10 min y la noche se cierra con motivo `HORA_LIMITE` |
| Posponer | los minutos de Ajustes (5, 10 o 15); la noche sigue abierta |

**Por qué:**
- `setAlarmClock` es la única API que Android trata como despertador: no la
  retrasa Doze ni el sistema. `USE_EXACT_ALARM` es la que la documentación
  indica para apps de despertador y se concede al instalar. Play solo la acepta
  en apps cuya función principal es de alarma o calendario: Descansa entra ahí.
- El **sonido** no depende de ningún permiso que el usuario pueda negar: si niega
  las notificaciones, no aparece la pantalla de timbre, pero el servicio suena
  igual (probado en el emulador con API 37). La pantalla de modo noche avisa si
  falta un permiso y lleva a Ajustes de Android.
- `systemExempted` lo permite Android a las apps con permiso de alarmas exactas.
  Se agregó `mediaPlayback` porque, sin él, Android 16+ registra que "silenciaría"
  el audio de una app en segundo plano (`AudioHardening`); con él, ese aviso desaparece.
- Desde Android 15 no se puede iniciar un servicio `mediaPlayback` desde
  `BOOT_COMPLETED`; por eso el receptor de arranque solo reprograma la alarma.
- Si el teléfono estuvo apagado a la hora de la alarma, al encender suena solo
  si pasó menos de 1 hora (sonar horas después no tendría sentido).
- La hora se guarda en SharedPreferences (no en DataStore) porque se lee desde un
  BroadcastReceiver, donde conviene una lectura inmediata. Desde D-14 vive en el
  almacenamiento protegido por dispositivo.

**Limitaciones conocidas:**
- ~~Antes de desbloquear el teléfono por primera vez tras reiniciar, Android no
  entrega `BOOT_COMPLETED`; la alarma se reprograma al desbloquear.~~ Resuelto en D-14.
- El botón "Probar alarma en 10 segundos" (modo desarrollador) reemplaza la
  alarma que esté programada.
- ~~Si se abre la app durante la noche, empieza en Inicio y no en el modo noche.~~
  Resuelto en la Fase 2: abre directo en el modo noche.

## D-12 · Servicio nocturno y motor de decisión (Fase 2)

El documento de requisitos (tabla de la sección 6.5) no está en el repositorio,
así que los umbrales de abajo se decidieron con criterio. Si el equipo tiene esa
tabla, basta con cambiar `Sensibilidad.parametros()` en `motor/MotorDecision.kt`
y las constantes del mismo archivo; las pruebas indican qué se rompe.

**Qué:**

| Pieza | Elección |
|---|---|
| Servicio | `ServicioNocturno`, en primer plano, tipo `health` (+ `HIGH_SAMPLING_RATE_SENSORS`) |
| Sensor | acelerómetro a `SENSOR_DELAY_NORMAL` (~5 Hz) con wake lock parcial hasta la hora límite + 30 min |
| Época | 1 minuto; actividad = desviación estándar de \|a\|/g (`AgregadorEpocas`) |
| Línea base | mediana de la actividad de la noche (sin épocas TOMADO), mínimo 0.002 g; con menos de 10 épocas se usa ese piso |
| TOMADO | actividad ≥ 0.1 g (alguien levantó el teléfono) |
| Alarma inteligente | `ProgramadorAlarma.dispararAhora()` con OTRO PendingIntent: la de respaldo sigue programada |
| Versión del motor | 1 (se guarda en `noches.version_motor`) |

**Sensibilidad:**

| Sensibilidad | INQUIETA si actividad > base × | Despierta con |
|---|---|---|
| Baja | 3.0 | 3 de las últimas 3 épocas de la ventana |
| Media (por defecto) | 2.0 | 2 de las últimas 3 |
| Alta | 1.5 | 1 de las últimas 2 |

Además, si el teléfono es TOMADO dentro de la ventana, suena (la persona ya está despierta).
Fuera de la ventana el motor nunca despierta; para eso está la alarma de respaldo.

**Por qué:**
- `health` es el tipo de la documentación para servicios largos de bienestar.
  Su requisito se cumple declarando `HIGH_SAMPLING_RATE_SENSORS`, un permiso
  normal (sin diálogo). Pedir `ACTIVITY_RECOGNITION` agregaría un diálogo más.
- La línea base usa la **mediana** para que unas pocas vueltas grandes no la
  suban, y es **por noche** para adaptarse a cada persona y colchón.
- El motor es Kotlin puro y compara marcas de tiempo, no minutos: así el
  **simulador** (1 época por segundo) prueba exactamente el mismo código.
- La alarma inteligente no reemplaza a la de respaldo: si fallara, la de la hora límite sigue en pie.

**Simulador (modo desarrollador):** Ajustes → "Simular noche inquieta/tranquila".
Noche de 60 "minutos" que dura 60 s, con ventana de 20. La inquieta debe sonar
dentro de la ventana con motivo `SUENO_LIGERO`; la tranquila, a la hora límite
con `HORA_LIMITE`. Probado en el emulador (API 37), junto con 2 minutos reales
de acelerómetro con la pantalla apagada.

**Limitaciones conocidas:**
- Con sensibilidad Alta, la noche tranquila simulada puede sonar antes: una sola
  vuelta dentro de la ventana basta. Es lo esperado para esa sensibilidad.
- Leer el sensor toda la noche con wake lock gasta batería; se medirá en la Fase 7.
- La fuente "Smartwatch" todavía usa el acelerómetro del teléfono (Fase 8).
- Si se abre la app mientras suena la alarma, se muestra la pantalla de timbre
  (así se puede apagar aunque se hayan negado las notificaciones).

## D-13 · No molestar (Fase 3)

Revisado contra la documentación oficial (cambios de Android 15 y 16,
`NotificationManager`, `AutomaticZenRule`) el 23 de noviembre de 2026.

**Qué:**
- Si "No molestar" está activado en el paso 5 o en Ajustes, al activar la noche
  se pone el filtro **solo alarmas** (`INTERRUPTION_FILTER_ALARMS`). Al apagar la
  alarma o al detener la noche se restaura. Al posponer se mantiene.
- **Android 15 o más nuevo** (nuestra targetSdk es 37): una app ya no puede cambiar
  el No molestar global. `setInterruptionFilter()` enciende un **modo propio de
  la app** ("Do Not Disturb (Descansa)") y el sistema aplica el más restrictivo.
  Restaurar = apagar ese modo. El No molestar del usuario nunca se toca.
- **Android 9 a 14:** se guarda el filtro previo en SharedPreferences y se
  restaura después. Si el usuario ya tenía No molestar, no se toca (podríamos
  hacerlo menos estricto). Si lo cambió a mano durante la noche, se respeta.
- El permiso ("Acceso a No molestar") lo da el usuario a mano en Ajustes de
  Android. Sin él, la noche funciona igual y el modo noche dice
  "No molestar: falta el permiso", con un botón para darlo. Al volver con el
  permiso recién dado, se activa en ese momento.
- Toda la lógica está en `GestorNoMolestar` (Kotlin puro, 9 pruebas JUnit).

**Por qué:**
- "Solo alarmas" deja sonar nuestra alarma (usa el canal de alarmas) y silencia lo demás.
- Si quitan el permiso a media noche, Android lanza `SecurityException`; se atrapa
  para que nunca impida que suene la alarma.
- Error encontrado en el emulador y corregido: al apagar la alarma, el modo noche
  se reanudaba un instante y volvía a encender el No molestar. Ahora solo se
  reactiva cuando el permiso se acaba de dar (prueba `ModoNocheViewModelTest`).

**Limitación:** en Android 15+ el usuario ve el modo "Do Not Disturb (Descansa)"
en Ajustes → Modos; el nombre lo pone el sistema.

## D-14 · Integración con la Fase 1 de `develop`: Direct Boot, cambio de hora y respaldos de sonido

La alarma de respaldo se implementó dos veces en paralelo (rama de Uli en
`develop` y `Rodrigo/desarrollo`). Se conservó la de `Rodrigo/desarrollo`,
porque las Fases 2 y 3 dependen de ella, y se le portaron las mejoras de la otra:

| Mejora | Cómo |
|---|---|
| **Reinicio sin desbloquear** (PIN o huella) | La hora de la alarma vive en el almacenamiento *protegido por dispositivo*, legible antes del desbloqueo. `ArranqueReceiver` escucha `LOCKED_BOOT_COMPLETED`. `AlarmaReceiver`, `ArranqueReceiver`, `TimbreService` y `TimbreActivity` son `directBootAware`. |
| **Apagar o posponer antes del desbloqueo** | Room, DataStore y las preferencias normales aún no se pueden leer: `TimbreService` recibe sus dependencias como `Lazy` y no las crea. Apagar solo cancela la alarma y anota la hora; al desbloquear (`BOOT_COMPLETED`) se cierra la noche y se restaura el No molestar. Posponer usa 5 minutos (no puede leer Ajustes). |
| **Cambio de hora o zona** (`TIME_SET`, `TIMEZONE_CHANGED`) | Se guarda también la hora "de reloj" (07:00). Al cambiar, se reprograma a la ocurrencia de esa hora más cercana a la anterior, así no se salta un día (`ocurrenciaMasCercana`, con pruebas). |
| **Pitido de respaldo** | Si ningún tono se puede abrir (por ejemplo, antes del desbloqueo), `ToneGenerator` pita por el canal de alarma. |
| **Foco de audio** | La alarma pide el foco: la música o los sonidos de la Fase 5 se pausan mientras suena. |

Se mantienen de la versión de Rodrigo: tolerancia de 1 hora para una alarma
perdida con el teléfono apagado, silencio a los 10 min, `mediaPlayback` y subir
el volumen de alarmas si está en 0.

**Probado en el emulador (API 37, 25/11/2026):** con PIN, reinicio sin
desbloquear: la alarma se reprogramó estando el usuario en `RUNNING_LOCKED`, sonó
y mostró la pantalla de timbre; "Apagar" la detuvo sin desbloquear, y al
desbloquear la noche quedó cerrada con la hora en que sonó. Cambio de zona de
Bogotá a Ciudad de México con alarma a las 23:00: siguió a las 23:00 locales.

**Error encontrado en la prueba y corregido:** si la alarma sonaba con la app
abierta detrás de la pantalla de bloqueo, `MainActivity` intentaba abrir la
pantalla de timbre, Android lo bloqueaba y no lo volvía a intentar al desbloquear.
Ahora se revisa en cada `onResume`.

---

## Decisiones previstas para las siguientes fases

Se confirmarán con la documentación oficial vigente antes de implementarlas:

