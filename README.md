# 🌙 DormirBien

Asistente digital para la optimización del ciclo del sueño y la salud mental
mediante alarma inteligente, sonidos modulados y diario emocional.

> **Aviso:** DormirBien no sustituye la atención médica ni psicológica.
> Las estadísticas muestran correlaciones, no diagnósticos.

## Objetivo

Mejorar la calidad del descanso y el bienestar emocional del usuario:
facilitar la conciliación del sueño con sonidos adaptativos, despertarlo en
fases de sueño ligero y reducir el estrés diario con herramientas de gestión
mental nocturna.

## Capturas

<!-- Agregar capturas cuando las pantallas estén listas. -->

| Inicio | Sonidos | Alarma | Estadísticas |
|---|---|---|---|
| _pendiente_ | _pendiente_ | _pendiente_ | _pendiente_ |

## Funcionalidades

- 📝 **Vaciado mental:** escribe pendientes y preocupaciones antes de dormir;
  los pendientes aparecen al día siguiente.
- 🌧️ **Sonidos relajantes adaptativos:** mezclador, temporizador con fade-out
  y atenuación automática al detectar que te dormiste.
- ⏰ **Alarma inteligente de fase ligera:** suena dentro de una ventana de
  despertar cuando detecta sueño ligero (acelerómetro), con volumen progresivo.
- 😊 **Registro emocional y estadísticas:** ánimo, estrés y su relación con
  tu sueño.
- 📚 **Aprende sobre el sueño**, 🫁 **respiración guiada**,
  ✅ **rutina nocturna**, 📓 **diario de sueño manual**, 🔥 **rachas y metas**,
  📤 **exportar datos** (CSV/PDF).
- 🔒 **Privacidad:** todos los datos se quedan en tu teléfono.

## Stack

Kotlin · Jetpack Compose · Material 3 · MVVM + Clean Architecture ligera ·
Hilt · Room · DataStore · Navigation Compose · Coroutines/Flow · Media3 ·
AlarmManager · WorkManager · Health Connect · Vico · JUnit · MockK · Turbine.

## Requisitos

- Android Studio reciente (con soporte para AGP 9.3)
- Android SDK 37 instalado (SDK Manager)
- Un emulador o teléfono con **Android 9 (API 28)** o superior
- Para Health Connect: Android 14+ (viene integrado) o la app Health Connect

## Cómo correr el proyecto

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Uli2534717/proyectoPsicologia.git
   ```
2. En Android Studio: **File → Open** y elige la carpeta clonada.
3. Espera a que termine el **Gradle Sync**.
4. Elige un emulador y presiona ▶ **Run**.

Desde la terminal:

```bash
./gradlew assembleDebug        # compila el APK
./gradlew testDebugUnitTest    # corre las pruebas unitarias
```

## Estructura

```
app/src/main/java/com/upt/dormirbien/
├── core/      # tema, componentes reutilizables, navegación
├── data/      # Room, DataStore, Health Connect, repositorios
├── domain/    # modelos y casos de uso
└── feature/   # un paquete por módulo
```

## Equipo y reparto de módulos

Universidad Politécnica de Tulancingo · Ingeniería en Tecnologías de la
Información e Innovación Digital · Grupo ITIID403.

| Integrante | Módulos |
|---|---|
| Rodrigo Ortiz Leal | Alarma inteligente, detección de sueño, Health Connect |
| Ángel Ulises Ramos Vera | Sonidos adaptativos, mezclador, respiración guiada |
| Miguel Martínez Mendoza | Registro emocional, estadísticas y gráficas, exportar |
| Sebastián Muñoz Ríos | Vaciado mental, aprende sobre el sueño, onboarding, ajustes |
| _Por asignar_ | Diario de sueño manual, rutina nocturna y rachas |

`core/` y `data/` se trabajan primero entre todos para que los demás módulos
tengan base.

## Contribuir

Lee [CONTRIBUTING.md](CONTRIBUTING.md) antes de tu primer commit.

## Créditos

Los audios y otros recursos de terceros se documentan en [CREDITS.md](CREDITS.md).
