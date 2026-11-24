# 🌙 Descansa

App Android que te acompaña antes de dormir, durante la noche y al despertar,
para que duermas mejor y despiertes con menos aturdimiento.

> **Aviso:** Descansa no es un producto médico y no sustituye la atención
> médica. Las etapas de sueño que estima son aproximaciones.

## Qué hace

| Momento | Herramientas |
|---|---|
| **Antes de dormir** | Pendientes para soltar la mente, respiración guiada y relajación muscular progresiva |
| **Al dormir** | Sonidos relajantes adaptativos con apagado gradual y modo No molestar |
| **Al despertar** | Alarma inteligente que suena dentro de una ventana cuando detecta sueño ligero, con alarma de respaldo garantizada a la hora límite |

Además: resumen de la mañana con gráfica de movimiento, contenido educativo
y control total de tus datos (exportar y borrar).

🔒 **Privacidad:** todo se guarda solo en tu teléfono. Sin cuentas, sin
servidores, sin anuncios. Ver [docs/PRIVACIDAD.md](docs/PRIVACIDAD.md).

## Capturas

| Bienvenida | Inicio | Alarma | Modo noche | Mañana |
|---|---|---|---|---|
| _pendiente_ | _pendiente_ | _pendiente_ | _pendiente_ | _pendiente_ |

## Estado

| Fase | Contenido | Estado |
|---|---|---|
| 0 | Base, tema, navegación por las 11 pantallas, base de datos | ✅ |
| 1 | Alarma de respaldo | 🧪 programada, falta prueba en teléfono real |
| 2 | Servicio nocturno y motor de decisión | ⏳ |
| 3 | No molestar | ⏳ |
| 4 | Pendientes, respiración y relajación | ⏳ |
| 5 | Sonidos | ⏳ |
| 6 | Resumen, Aprender y Ajustes | ⏳ |
| 7 | Pulido y calidad | ⏳ |
| 8 | Smartwatch y Health Connect (opcional) | ⏳ |

## Stack

Kotlin · Jetpack Compose · Material 3 · MVVM · Hilt · Room · DataStore ·
Navigation Compose · Coroutines/Flow · Media3 · AlarmManager · JUnit · MockK ·
Turbine. Por qué Kotlin y no Flutter: [docs/DECISIONES.md](docs/DECISIONES.md#d-01--kotlin--jetpack-compose-en-lugar-de-flutter).

## Requisitos

- Android Studio reciente, con Android SDK 37
- Teléfono o emulador con **Android 9 (API 28)** o superior

## Cómo compilar, ejecutar y probar

```bash
git clone https://github.com/Uli2534717/proyectoPsicologia.git
```

1. En Android Studio: **File → Open** y elige la carpeta.
2. Espera el **Gradle Sync** y presiona ▶ **Run**.

```bash
./gradlew assembleDebug             # APK en app/build/outputs/apk/debug/
./gradlew testDebugUnitTest         # pruebas unitarias
./gradlew connectedDebugAndroidTest # pruebas de UI (con emulador)
```

Pruebas manuales en teléfono real: [docs/PRUEBAS_MANUALES.md](docs/PRUEBAS_MANUALES.md).

## Equipo

Universidad Politécnica de Tulancingo · Ingeniería en Tecnologías de la
Información e Innovación Digital · Grupo ITIID403.

| Integrante | Módulos propuestos |
|---|---|
| Rodrigo Ortiz Leal | Alarma de respaldo, servicio nocturno, motor de decisión, No molestar (Fases 1–3) |
| Ángel Ulises Ramos Vera | Respiración y relajación (Fase 4), sonidos (Fase 5) |
| Miguel Martínez Mendoza | Resumen de la mañana con gráfica, exportar y borrar datos (Fase 6), Health Connect (Fase 8) |
| Sebastián Muñoz Ríos | Bienvenida y permisos, pendientes (Fase 4), Aprender y Ajustes (Fase 6) |

> Reparto propuesto a partir del anterior. El equipo debe confirmarlo.

## Documentación

- [CONTRIBUTING.md](CONTRIBUTING.md): cómo trabajar con Git en equipo
- [docs/DECISIONES.md](docs/DECISIONES.md): decisiones y su porqué
- [docs/PRIVACIDAD.md](docs/PRIVACIDAD.md): aviso de privacidad
- [docs/PRUEBAS_MANUALES.md](docs/PRUEBAS_MANUALES.md): lista de pruebas en teléfono real
- [CREDITS.md](CREDITS.md): licencias de sonidos y fuentes

## Limitaciones conocidas

Se mantendrá una lista honesta aquí conforme avancen las fases.

- La detección de sueño ligero por movimiento del teléfono es una
  aproximación, no un estudio clínico.
- Si usas "Forzar detención" en los ajustes de Android, el sistema borra la
  alarma programada (ninguna app lo puede evitar). Cerrar la app desde
  "Recientes" no la afecta.
- Muchos relojes no transmiten la frecuencia cardíaca en tiempo real; el modo
  smartwatch puede no estar disponible con tu reloj.
