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
| Lenguaje / UI | Kotlin + Jetpack Compose + Material 3 (solo tema oscuro) |
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
│   ├── theme/          # Color.kt = tokens del prototipo (única fuente de colores)
│   ├── designsystem/   # BotonPrincipal, TarjetaDescansa, ChipOpcion, PlantillaPaso...
│   ├── navigation/     # Rutas.kt + DescansaNavegacion.kt (todo el flujo en un archivo)
│   └── util/           # Tiempo.kt (horas y ventanas que cruzan medianoche)
├── domain/             # modelos, interfaces de repositorios, casos de uso (Kotlin puro)
├── data/               # Room, DataStore, módulos de Hilt
└── feature/
    ├── bienvenida/  inicio/  modonoche/  manana/  aprender/  ajustes/
    └── noche/       # los 5 pasos: pendientes/ respiracion/ relajacion/ sonidos/ alarma/
# Fases 1–3 agregarán: sistema/alarma, sistema/noche, sistema/nomolestar y motor/ (Kotlin puro).
```

## 6. Fases (prioridad P0 primero)

| Fase | Contenido | Prioridad |
|---|---|---|
| 0 | Base, tema, navegación por las 11 pantallas, BD, bienvenida | P0 ✅ |
| 1 | Alarma de respaldo: AlarmScheduler, receivers, pantalla de timbre, volumen creciente | P0 |
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
