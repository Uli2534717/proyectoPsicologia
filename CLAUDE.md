# CLAUDE.md — Reglas del proyecto Descansa

Claude Code lee este archivo al empezar cada sesión. Si cambias algo aquí,
hazlo en un PR y avisa al equipo.

## 1. Qué es el proyecto

**Descansa**: app Android que acompaña tres momentos de la noche.
- **Antes de dormir:** pendientes (para reducir la rumiación), respiración guiada y relajación muscular.
- **Al dormir:** sonidos adaptativos y No molestar.
- **Al despertar:** alarma inteligente que suena dentro de una ventana cuando detecta sueño ligero.

Proyecto de Ética profesional de la UPT (ITIID403). Entrega: **2 de diciembre de 2026**.

- Paquete: `com.upt.descansa` · Repo: https://github.com/Uli2534717/proyectoPsicologia
- Documento de requisitos: "Prompt_App_Sueno.md" + prototipo de 10 pantallas (PDF).
- Decisiones tomadas: **`docs/DECISIONES.md`** (léelo antes de cambiar algo grande).

## 2. Principios no negociables

1. **La alarma nunca falla:** siempre hay una alarma de respaldo a la hora
   límite, programada con `AlarmManager.setAlarmClock()`, independiente del motor.
2. **Privacidad por defecto:** todo se guarda solo en el teléfono. Sin cuentas,
   servidores, analíticas ni anuncios. `allowBackup="false"`.
3. **No es un producto médico:** no diagnostica. Se recomienda acudir con un
   profesional ante problemas persistentes.
4. **Honestidad:** la detección por movimiento es una aproximación. Nunca se
   presentan certezas ni datos de ejemplo como reales. Lo que no funciona de
   forma fiable se documenta; no se finge.
5. **Accesible y calmado:** contraste de 4.5:1, objetivos táctiles de 48×48,
   `contentDescription` en botones de solo ícono, respeto a "reducir animaciones".
   Tono amable y sin culpas (parte del público es menor de edad).

## 3. Cómo trabajar con Claude en este repo

- Los integrantes **están aprendiendo**: explica el código y comenta en
  español el **porqué** (no el qué).
- Trabaja **por fases** (sección 6). Al terminar una fase: compila, corre las
  pruebas, haz commit y resume en 5 líneas qué funciona y qué falta.
- Si algo no está definido, decide con criterio y **anótalo en `docs/DECISIONES.md`**.
- Antes de usar una API restringida de Android (alarmas exactas, servicios en
  primer plano, Health Connect, Bluetooth, No molestar), **consulta la
  documentación oficial vigente**. No asumas de memoria.
- Lo que no se pueda probar en emulador necesita un **modo simulador** y una
  entrada en `docs/PRUEBAS_MANUALES.md`.
- Nunca hagas `git push`, cambies de rama compartida ni borres trabajo de
  otros sin que lo pida el integrante.

## 4. Stack

| Pieza | Elección |
|---|---|
| Lenguaje / UI | Kotlin + Jetpack Compose + Material 3, sistema "Cartel sereno" (sección 10) |
| Arquitectura | MVVM + Clean Architecture ligera (`feature` → `domain` ← `data`) |
| DI | Hilt (KSP) |
| BD | Room (`noches`, `epocas`, `pendientes`, `estadisticas_sonido`) con esquemas en `app/schemas/` |
| Ajustes | DataStore Preferences (`AjustesUsuario`) |
| Navegación | Navigation Compose con rutas `@Serializable` |
| Audio | Media3 (Fase 5) |
| Alarma / noche | AlarmManager + servicio en primer plano en Kotlin (Fases 1–2) |
| Pruebas | JUnit 4, MockK, Turbine, coroutines-test, Compose UI Test |
| SDK | minSdk 28, compileSdk/targetSdk 37 |

## 5. Estructura

```
app/src/main/java/com/upt/descansa/
├── DescansaApp.kt / MainActivity.kt / MainViewModel.kt
├── core/
│   ├── designsystem/   # theme/ (colores, Work Sans, formas, temas), motion/, components/ (uno por archivo)
│   ├── navigation/     # Rutas.kt + DescansaNavegacion.kt (todo el flujo en un archivo)
│   └── util/           # Tiempo.kt (horas y ventanas que cruzan medianoche)
├── domain/             # modelos, interfaces de repositorios, casos de uso (Kotlin puro)
├── data/               # Room, DataStore, módulos de Hilt
└── feature/
    ├── bienvenida/  inicio/  modonoche/  manana/  aprender/  ajustes/
    └── noche/       # los 5 pasos: pendientes/ respiracion/ relajacion/ sonidos/ alarma/
├── sistema/            # alarma/ (Fase 1); Fases 2–3: noche/, nomolestar/ y motor/ (Kotlin puro)
```

## 6. Fases (prioridad P0 primero)

| Fase | Contenido | Prioridad |
|---|---|---|
| 0 | Base, tema, navegación por las 11 pantallas, BD, bienvenida | P0 ✅ |
| 1 | Alarma de respaldo: AlarmScheduler, receivers, pantalla de timbre, volumen creciente | P0 ✅ |
| 2 | Servicio nocturno, SensorCollector, EpochAggregator, WakeDecisionEngine + JUnit, simulador | P0 |
| 3 | No molestar con guardado y restauración | P0 |
| 4 | Pendientes, respiración y relajación completos | P0 |
| 5 | Sonidos: bucle, pantalla apagada, apagado gradual, recomendación | P1 |
| 6 | Resumen de la mañana, Aprender, Ajustes, exportar y borrar | P1 |
| 7 | Pulido, accesibilidad, batería, documentación, APK | P1 |
| 8 | Smartwatch (BLE) y Health Connect | P2 |

## 7. Convenciones

- Nombres del dominio en español; sufijos técnicos en inglés (`Screen`,
  `ViewModel`, `UiState`, `Dao`, `Entity`, `Repository`, `UseCase`).
- Pantalla = `XScreen` (obtiene el ViewModel) + `XContenido` (sin estado) + `@Preview`.
- Las pantallas no conocen al `NavController`: reciben lambdas (`onSiguiente`).
- Colores solo desde `core/theme/Color.kt`; componentes desde `core/designsystem/`.
- Textos solo en `strings.xml`, con el prefijo de su pantalla.
- Fechas en UTC (epoch ms); hora límite en "minutos desde las 00:00".
- Una instrucción por línea, 4 espacios, estilo oficial de Kotlin.

## 8. Comandos

```bash
./gradlew assembleDebug             # compilar
./gradlew testDebugUnitTest         # pruebas unitarias
./gradlew connectedDebugAndroidTest # pruebas de UI (emulador encendido)
./gradlew lint                      # análisis estático
```

En Windows, fuera de Android Studio:
`JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`.

## 9. Git

`main` ← `develop` ← `feature/<modulo>`, `fix/...`, `docs/...`. Todo entra
por PR. Commits con Conventional Commits en español. Ver `CONTRIBUTING.md`.

## 10. Sistema de diseño "Cartel sereno"

Lienzo lila, mucho espacio, **una sola tipografía (Work Sans)** y palabras
gigantes de color que identifican cada sección, como un cartel impreso.
Está inspirado en los tokens de una guía de estilo de otra marca: **no copiar
nombres, logotipos, textos ni imágenes de esa marca**.

**Dónde vive:** `core/designsystem/` → `theme/` (Color, Type, Formas, Theme),
`motion/Movimiento.kt` y `components/` (un componente por archivo, con
`@PreviewLightDark`). Las pantallas NO crean botones, tarjetas ni colores propios.

### Colores (`theme/Color.kt`)
| Token | Hex | Uso |
|---|---|---|
| `MoradoProfundo` | #4B2E83 | ÚNICO color interactivo de día: botones, enlaces, etiquetas, íconos activos |
| `VioletaSecundario` | #7A5BC4 | Solo bordes y subrayados de acciones secundarias. Nunca relleno |
| `VioletaCartel` | #8B6FE8 | Decorativo: palabras gigantes de las secciones de noche y zigzag |
| `OrquideaCartel` | #C7A6F2 | Decorativo: palabras gigantes de las secciones de día |
| `CarbonMorado` | #2B2140 | Texto y bordes de día |
| `LilaLienzo` | #F6F2FF | Fondo de día (lila muy suave) |
| Nocturno | #160F26 / #F1ECFA / #B9A8FF | Fondo (`MoradoNoche`) / texto / interactivo (`LavandaNoche`) |

- Colores decorativos: `LocalColoresCartel.current` (`noche` = violeta: Pendientes,
  Respiración, Relajación, Sonidos, Alarma, Modo noche; `dia` = orquídea: Bienvenida,
  Inicio, Mañana, Aprender, Ajustes).
- **Nunca** violeta ni orquídea decorativos en texto funcional menor a 32 sp.
- Tema: Automático (sigue al sistema) / Claro / Oscuro en Ajustes.
  **Respiración, Modo noche y la pantalla de timbre siempre usan `TemaNocturno`.**

### Tipografía (`theme/Type.kt` → `TipoCartel`)
`cartelXL` 120 sp · `cartel` 96 sp (900, interlineado 0.70, se ajustan al ancho) ·
`subtitulo` 32 sp/900 · `titulo` 20 sp/500 · `cuerpo` 17 sp/400 ·
`etiqueta` 14 sp/500 MAYÚSCULAS · `leyenda` 14 sp/400 MAYÚSCULAS · `hora` 64 sp/900.
Los tamaños de cartel **nunca** se usan para texto de interfaz. Todo en `sp`.

### Formas, espacio y elevación
- Radios SOLO: 0 (etiquetas), 4 dp (tarjetas, campos, imágenes), 20 dp
  (botones), píldora (el botón principal).
- Espacios SOLO: 4, 8, 10, 12, 16, 20, 24, 30, 50 dp (`Espacio`). Márgenes de
  pantalla 20, entre secciones 50, entre elementos 20, relleno de tarjeta 20.
- **Sin sombras, elevación, degradados, vidrio ni texturas.** Bordes de 1.5 dp.
- **Ningún botón con relleno de color.** Presionado = borde 2.5 dp + escala 0.97.
- Selección = borde 2.5 dp en color interactivo **y** ✓ (nunca solo color).

### Estructura de cada pantalla
`PantallaCartel` (o `PlantillaPaso` en los 5 pasos): encabezado (atrás o
`Logotipo` + `MenuMinimo`), `PalabraCartel` con la imagen encimada, título real
accesible (`TituloPantalla`), contenido, y **un solo `BotonPildora`**.
No hay barra inferior: la navegación es `MenuPantallaCompleta`.

### Imágenes
Fotos de objetos sobre fondo blanco, sin personas (`img_*`). Mientras no hay
foto se dibuja el objeto con un ícono de línea morado (`Ilustraciones.kt`). Lista y cómo
reemplazarlas: `docs/IMAGENES.md`. Siempre con `contentDescription` en español.
Íconos de Material solo en controles (reproducir, pausar, agregar, estado).

### Movimiento (`motion/Movimiento.kt`)
Duraciones 150 / 300 / 600 ms; `FastOutSlowInEasing` (entrada) y
`LinearOutSlowInEasing` (salida). Sin rebotes ni brillos.
Si `animacionesReducidas()` es `true`: fundido o nada, sello y separador quietos,
círculo de respiración estático.

### Accesibilidad
`PalabraCartel` y `SeparadorOnda` son decorativos (`clearAndSetSemantics`).
Objetivos táctiles ≥ 48 dp. `FilaDeTres` pasa a desplazamiento horizontal en
pantallas < 360 dp o con letra grande del sistema.
