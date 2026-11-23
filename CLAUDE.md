# CLAUDE.md — Reglas del proyecto DormirBien

Este archivo lo lee Claude Code al empezar cada sesión. Todo el equipo trabaja
con las mismas reglas: si cambias algo aquí, hazlo en un PR y avisa al equipo.

## 1. Qué es el proyecto

App Android **DormirBien**: asistente para mejorar el sueño y la salud mental
(alarma inteligente de fase ligera, sonidos relajantes adaptativos, vaciado
mental, registro emocional y estadísticas). Proyecto escolar de la Universidad
Politécnica de Tulancingo (ITIID403), materia de Ética profesional.

- Paquete: `com.upt.dormirbien`
- Repositorio: https://github.com/Uli2534717/proyectoPsicologia

## 2. Cómo trabajar con Claude en este repo

- Los integrantes **están aprendiendo**: explica el código, comenta en
  **español** la lógica importante y resume al final qué hiciste.
- Trabaja **por fases** y detente al final de cada una. Antes de escribir
  código, di qué encontraste y qué vas a hacer.
- **No inventes requisitos.** Si algo es ambiguo, pregunta. Si tienes que
  suponer, márcalo con `// SUPOSICIÓN:` y avisa.
- Si algo no es posible técnicamente o hay una alternativa mejor, dilo
  **antes** de implementarlo.
- Nunca hagas `git push`, cambies de rama compartida ni borres archivos de
  otros módulos sin que el integrante lo pida.
- Al terminar: `./gradlew assembleDebug testDebugUnitTest` debe pasar.

## 3. Stack (no cambiarlo sin acuerdo del equipo)

| Pieza | Elección |
|---|---|
| Lenguaje | Kotlin (AGP 9 con Kotlin integrado) |
| UI | Jetpack Compose + Material 3. Tema **oscuro por defecto** |
| Arquitectura | MVVM + Clean Architecture ligera (`ui` → `domain` ← `data`) |
| DI | Hilt (KSP) |
| BD local | Room (esquemas exportados en `app/schemas/`) |
| Preferencias | DataStore Preferences |
| Navegación | Navigation Compose con rutas `@Serializable` (type-safe) |
| Asincronía | Coroutines + Flow |
| Audio | Media3 / ExoPlayer (Fase 3) |
| Alarmas | AlarmManager `setAlarmClock()` + WorkManager (Fase 4) |
| Smartwatch | Health Connect (historial de sueño, Fase 4) |
| Gráficas | Vico (Fase 5) |
| Pruebas | JUnit 4, MockK, Turbine, coroutines-test, Compose UI Test |
| Versiones | `gradle/libs.versions.toml` (Version Catalog), solo estables |
| SDK | minSdk 28, compileSdk/targetSdk 37 |

Nota: el BOM de Compose está en `2026.09.00` porque el `2026.11.x` exige
compileSdk 37.1. Se puede subir cuando todos instalen ese SDK.

## 4. Estructura de carpetas

```
app/src/main/java/com/upt/dormirbien/
├── DormirBienApp.kt      # @HiltAndroidApp
├── MainActivity.kt       # única Activity; aplica el tema y la navegación
├── core/
│   ├── theme/            # colores, tipografía, DormirBienTheme
│   ├── designsystem/     # componentes reutilizables (PantallaEnConstruccion...)
│   └── navigation/       # Rutas.kt, DestinoPrincipal.kt, DormirBienNavegacion.kt
├── data/
│   ├── local/            # Room: DormirBienDatabase, entity/, dao/
│   ├── prefs/            # DataStore
│   └── di/               # módulos de Hilt (DataModule, RepositoryModule)
├── domain/
│   ├── model/            # modelos puros de Kotlin (sin Android)
│   ├── repository/       # interfaces de repositorios
│   └── usecase/<tema>/   # casos de uso
└── feature/<modulo>/     # una carpeta por módulo: Screen + ViewModel + UiState
```

Módulos en `feature/`: onboarding, home, vaciadomental, diariosueno, sonidos,
alarma, emociones, estadisticas, aprende, respiracion, rutina, ajustes.

## 5. Convenciones de código

- **Capas:** `feature` usa casos de uso de `domain`; `domain` no importa nada
  de Android ni de `data`; `data` implementa las interfaces de `domain`.
- **Nombres en español** para clases, funciones y variables del dominio
  (`EntradaVaciado`, `alSeleccionarModoTema`). Sufijos técnicos en inglés
  cuando son convención: `Screen`, `ViewModel`, `UiState`, `Dao`, `Entity`,
  `Repository`, `RepositoryImpl`, `UseCase`.
- **Pantallas:** `XScreen` (con estado, obtiene el ViewModel con
  `hiltViewModel()`) + `XContenido` (sin estado, recibe `uiState` y lambdas).
  Cada pantalla tiene un `@Preview`.
- **ViewModels:** exponen un `StateFlow<XUiState>` con `stateIn(...)`; la UI lo
  lee con `collectAsStateWithLifecycle()`. Eventos como funciones `alHacerAlgo()`.
- **Textos:** todos en `res/values/strings.xml`, en español, con el prefijo del
  módulo (`sonidos_`, `alarma_`...). Nada escrito directo en el código.
- **Accesibilidad:** `contentDescription` en íconos que comunican algo
  (`null` si son decorativos), `semantics { heading() }` en títulos, áreas
  táctiles de al menos 48 dp, texto en `sp`, buen contraste.
- **Estados:** toda pantalla maneja carga, vacío, error y "sin permiso".
- **Una instrucción por línea**, indentación de 4 espacios, estilo oficial
  de Kotlin (`kotlin.code.style=official`).
- **Base de datos:** al cambiar una entidad, subir `version` y escribir
  migración; subir el JSON de `app/schemas/`.

## 6. Ética y privacidad (requisito de la materia)

- Datos **solo en el dispositivo**. Sin internet ni servidores
  (`allowBackup="false"`).
- Pedir permisos **uno por uno, explicando para qué** antes del diálogo del sistema.
- La app **no diagnostica**: las estadísticas son correlaciones y debe decirse
  en la interfaz. Aviso visible de que no sustituye atención profesional.
- Nada de diseño adictivo: sin notificaciones insistentes ni castigos por rachas.
- El usuario puede **borrar todos sus datos** y exportarlos.

## 7. Git

- Ramas: `main` (estable) ← `develop` (integración) ← `feature/<modulo>`,
  `fix/<descripcion>`, `docs/<descripcion>`, `chore/<descripcion>`.
- Nadie trabaja directo en `main` ni en `develop`: todo entra por Pull Request.
- Commits con Conventional Commits en español:
  `feat(alarma): agrega ventana de despertar`.
- Detalles en `CONTRIBUTING.md`.

## 8. Comandos

```bash
./gradlew assembleDebug             # compilar
./gradlew testDebugUnitTest         # pruebas unitarias
./gradlew connectedDebugAndroidTest # pruebas de UI (emulador abierto)
```

En Windows fuera de Android Studio, define antes
`JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`.
