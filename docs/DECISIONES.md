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

## D-03 · ~~Solo tema oscuro~~ (reemplazada por D-17)

**Qué:** no hay tema claro.

**Por qué:** el prototipo solo define tokens oscuros, la app se usa de noche
y así el contraste (4.5:1 mínimo) se revisa en un solo esquema.

## D-04 · ~~Fuente Nunito pendiente~~ (reemplazada por D-16)

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

## D-10 · Íconos (solo en controles desde D-19)

**Qué:** `material-icons-extended` (versión Outlined) en lugar de Material Symbols.

**Por qué:** son los mismos íconos de línea del prototipo y vienen como
`ImageVector`, sin archivos extra. R8 elimina los que no se usan en la versión release.

## D-11 · Permiso de alarmas exactas

**Qué:** `USE_EXACT_ALARM`, y además `SCHEDULE_EXACT_ALARM` con `maxSdkVersion="32"`.

**Por qué:** según la documentación oficial (consultada el 24/11/2026),
`USE_EXACT_ALARM` "se concede automáticamente" y "el usuario no lo puede
revocar". Está limitado a casos como las apps de despertador, que es nuestro
caso. No existe antes de Android 13; en Android 12, `SCHEDULE_EXACT_ALARM`
viene concedido. Si aun así Android no deja programar exactas, la app **no
activa la noche** y lo explica: nunca dormimos sin alarma de respaldo.

**Pendiente de verificar:** la política de Google Play para `USE_EXACT_ALARM`
solo importa si se publica en Play, lo cual no es necesario para la entrega.

## D-12 · Servicio de timbre tipo `systemExempted`

**Qué:** el sonido de la alarma lo reproduce `TimbreService`, un servicio en
primer plano de tipo `systemExempted`.

**Por qué:** la documentación permite ese tipo a "apps con SCHEDULE_EXACT_ALARM
o USE_EXACT_ALARM" y no pide permisos en tiempo de ejecución. Además, Android 17
(targetSdk 37) exige que una app que reproduce audio en segundo plano tenga un
servicio en primer plano y use el audio de alarma (`USAGE_ALARM`) con permiso de
alarmas exactas. Iniciar el servicio desde el receptor de la alarma está
permitido, porque las alarmas exactas están exentas de las restricciones de
inicio en segundo plano.

## D-13 · Estado de la alarma en almacenamiento protegido por dispositivo

**Qué:** la alarma programada se guarda en `SharedPreferences` del
almacenamiento "protegido por dispositivo", no en DataStore ni Room. Los
receptores, el servicio y la pantalla de timbre son `directBootAware` y no usan Hilt.

**Por qué:** tras reiniciar un teléfono con PIN o huella, hasta desbloquearlo
solo se puede leer ese almacenamiento. Así la alarma se reprograma (con
`LOCKED_BOOT_COMPLETED`) y suena aunque nadie desbloquee el teléfono.
Solo se guardan horas (no son datos sensibles).

## D-14 · Reglas de la alarma de respaldo

- Si al reiniciar la alarma **ya debió sonar** mientras el teléfono estaba
  apagado, suena de inmediato (es mejor despertar tarde que no despertar).
- Al **cambiar la zona horaria**, la alarma normal se recalcula: 07:00 sigue
  siendo 07:00 en la hora nueva. Una alarma pospuesta conserva su momento exacto.
- La alarma **suena como máximo 30 minutos** y luego se detiene, para no agotar
  la batería si el usuario no está cerca.
- "Atrás" no apaga la alarma: solo **Detener** o **Posponer**.
- Si el usuario negó las notificaciones, la alarma suena igual y, al abrir la
  app, aparece la pantalla de timbre.

## D-15 · Limitación conocida: "Forzar detención"

Si el usuario usa **Ajustes de Android → Apps → Descansa → Forzar detención**,
Android borra todas las alarmas de la app y ninguna app puede evitarlo. Cerrar
la app desde "Recientes" **no** borra la alarma. Esto se documenta en el README
y en las pruebas manuales.

## D-16 · Rediseño "Cartel sereno" con Work Sans variable

**Qué:** el equipo pidió reemplazar el estilo del prototipo por "Cartel
sereno": lienzo blanco, Work Sans, palabras gigantes de color y botones sin
relleno. Se usa la fuente **variable** oficial de Google Fonts
(`worksans_variable.ttf` y `worksans_italic_variable.ttf`, licencia OFL), y
cada peso (400, 500, 900) se elige con `FontVariation.weight`.

**Por qué:** el repositorio oficial `google/fonts` publica Work Sans como
variable; un archivo da todos los pesos y funciona sin internet desde Android 8
(nuestro mínimo es 9). Descargada el 24/11/2026 de
`github.com/google/fonts/tree/main/ofl/worksans`.
**Nota:** el rediseño ya no coincide con el prototipo en PDF; si se evalúa
contra él, hay que avisarlo o actualizarlo.

## D-17 · Tema según el sistema, con nocturno forzado de noche

**Qué:** el tema sigue al teléfono, y en Ajustes se puede fijar en Claro,
Oscuro o Automático. **Respiración, Modo noche y la pantalla de timbre usan
siempre el tema nocturno.** Modo noche se agregó a la lista del documento de
rediseño, por la misma razón: no deslumbrar.

**Pendiente:** "Sonidos mientras reproduce" también irá en nocturno cuando
exista la reproducción (Fase 5).

## D-18 · Azul secundario en tema nocturno

**Qué:** en el tema nocturno, las acciones secundarias usan `AzulNoche`
(#8CC0EA) en lugar de `AzulCartel`.

**Por qué:** `AzulCartel` sobre el fondo nocturno da ≈3.7:1, poco para un
borde de 1.5 dp. El documento de rediseño no definía este caso.

## D-19 · Adaptaciones del rediseño a pantallas que no existían

- Las pantallas del documento de rediseño que no existen en Descansa
  (Registro emocional, Estadísticas, Diario, Rutina) no se crearon, porque el
  documento dice "no agrega funciones". Sus reglas se aplicaron a Mañana (la
  gráfica de movimiento) y a Relajación.
- Palabras: Bienvenida **DESCANSA**, Inicio **DUERME**, Pendientes **SUELTA**,
  Respiración **RESPIRA**, Relajación **CALMA**, Sonidos **SUEÑA**, Alarma y
  timbre **DESPIERTA**, Modo noche **DESCANSA**, Mañana **ÁNIMO**, Aprender
  **APRENDE**, Ajustes **AJUSTA**.
- Las opciones elegidas se marcan con borde grueso **y** ✓, porque el sistema
  prohíbe los rellenos de color y la selección no debe depender solo del color.
- La figura del cuerpo de Relajación se conserva: es una guía funcional
  (qué músculo tensar), no una ilustración decorativa.
- `FilaDeTres` también pasa a desplazamiento horizontal con letra grande del
  sistema (> 115 %), no solo en pantallas angostas: si no, las etiquetas se
  partían a mitad de palabra.
- Animaciones que dependen de funciones futuras quedan pendientes para su
  fase: "soltar" pendientes (Fase 4), barras de la mezcla de sonidos (Fase 5).

## D-20 · Contraste del amarillo en tema claro

`AmarilloMantequilla` sobre blanco tiene ≈1.2:1: las palabras gigantes
amarillas se ven muy tenues de día. Es decorativo (TalkBack no lo lee y el
título real está aparte), así que no incumple la regla de 4.5:1 para texto
funcional. Si el equipo lo quiere más visible, la opción menos invasiva es un
amarillo más saturado SOLO para el tema claro.

---

## Decisiones previstas para las siguientes fases

Se confirmarán con la documentación oficial vigente antes de implementarlas:

- **Fase 2:** tipo de servicio en primer plano para el sensor. La
  documentación describe `health` para "apps de fitness" y le exige
  `ACTIVITY_RECOGNITION` o `HIGH_SAMPLING_RATE_SENSORS`; hay que evaluar si
  encaja o si conviene `specialUse`. Medida de actividad: desviación estándar de
  |a|/g por minuto.
- **Fase 3:** comportamiento de los "Modos" de Android 15+ frente a
  `setInterruptionFilter`.

## D-21 · Morado en lugar de verde

**Qué:** el equipo pidió que la interfaz fuera morada en lugar de verde:
- Interactivo del tema claro: `MoradoProfundo` #4B2E83 (antes VerdeBosque).
- Fondo nocturno: `MoradoNoche` #160F26 (antes verde casi negro).
- Interactivo nocturno: `LavandaNoche` #B9A8FF (antes turquesa).

**Por qué estos tonos:** conservan el contraste de la guía. El morado sobre
blanco da ≈10:1 y la lavanda sobre el fondo nocturno ≈9:1 (mínimo pedido:
4.5:1). Además, el morado recuerda la lavanda del prototipo original.
Turquesa y amarillo siguen siendo SOLO decorativos (palabras gigantes y separadores).
