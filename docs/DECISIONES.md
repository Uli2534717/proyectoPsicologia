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
| Reinicio | `ArranqueReceiver` (`BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`) reprograma la hora guardada en SharedPreferences |
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
  BroadcastReceiver, donde conviene una lectura inmediata.

**Limitaciones conocidas:**
- Antes de desbloquear el teléfono por primera vez tras reiniciar, Android no
  entrega `BOOT_COMPLETED`; la alarma se reprograma al desbloquear.
- El botón "Probar alarma en 10 segundos" (modo desarrollador) reemplaza la
  alarma que esté programada.
- Si se abre la app durante la noche, empieza en Inicio y no en el modo noche.
  Se resolverá en la Fase 2, cuando exista el servicio nocturno.

---

## Decisiones previstas para las siguientes fases

Se confirmarán con la documentación oficial vigente antes de implementarlas:

- **Fase 2:** tipo de servicio en primer plano para el sensor (`health` o
  `specialUse`) y sus permisos. Medida de actividad: desviación estándar de
  |a|/g por minuto.
- **Fase 3:** comportamiento de los "Modos" de Android 15+ frente a
  `setInterruptionFilter`.
