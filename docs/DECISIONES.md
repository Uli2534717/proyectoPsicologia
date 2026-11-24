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

---

## Decisiones previstas para las siguientes fases

Se confirmarán con la documentación oficial vigente antes de implementarlas:

- **Fase 1:** permiso de alarmas exactas. Una app de despertador puede declarar
  `USE_EXACT_ALARM`; se verificará contra targetSdk 37 y la política de Play.
  También `USE_FULL_SCREEN_INTENT` para la pantalla de timbre.
- **Fase 2:** tipo de servicio en primer plano para el sensor (`health` o
  `specialUse`) y sus permisos. Medida de actividad: desviación estándar de
  |a|/g por minuto.
- **Fase 3:** comportamiento de los "Modos" de Android 15+ frente a
  `setInterruptionFilter`.
