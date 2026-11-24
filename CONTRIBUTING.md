# Cómo contribuir a Descansa

Guía paso a paso para trabajar en equipo sin pisarnos.

## Ramas del repositorio

```
main      ← versión estable (solo recibe PR desde develop)
 └─ develop   ← integración: aquí se juntan los módulos
     ├─ feature/sonidos
     ├─ feature/alarma
     ├─ fix/temporizador-no-apaga
     └─ docs/readme-capturas
```

**Nadie hace commit directo en `main` ni en `develop`.** Todo entra por
Pull Request (PR).

## 1. Clonar (solo la primera vez)

```bash
git clone https://github.com/Uli2534717/proyectoPsicologia.git
cd proyectoPsicologia
git switch develop
```

Configura tu nombre y correo (los que usas en GitHub):

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu-correo@ejemplo.com"
```

## 2. Crear tu rama

Siempre parte de un `develop` actualizado:

```bash
git switch develop
git pull
git switch -c feature/sonidos
```

Nombres de ramas:

| Prefijo | Para qué | Ejemplo |
|---|---|---|
| `feature/` | Función nueva | `feature/mezclador` |
| `fix/` | Corregir un error | `fix/alarma-no-suena-bloqueado` |
| `docs/` | Solo documentación | `docs/etica` |
| `chore/` | Mantenimiento (versiones, config) | `chore/actualiza-compose` |

## 3. Hacer commits

Usamos **Conventional Commits en español**:

```
<tipo>(<modulo>): <qué hace, en presente>
```

Ejemplos:

```
feat(alarma): agrega ventana de despertar
fix(sonidos): corrige fade-out que no bajaba el volumen
docs(readme): agrega capturas de inicio
test(alarma): agrega pruebas del motor de decisión
refactor(core): mueve colores a Color.kt
chore(deps): actualiza Room a 2.8.5
```

Tipos: `feat`, `fix`, `docs`, `test`, `refactor`, `style`, `chore`.

```bash
git add app/src/main/java/com/upt/descansa/feature/noche/sonidos
git commit -m "feat(sonidos): agrega biblioteca de sonidos"
```

Consejos:

- Commits pequeños y frecuentes (uno por idea).
- Antes de cada commit: `./gradlew assembleDebug testDebugUnitTest`.
- Revisa con `git status` que no subas archivos que no son tuyos.

## 4. Subir tu rama y abrir un Pull Request

```bash
git push -u origin feature/sonidos
```

En GitHub aparecerá el botón **Compare & pull request**:

1. **base:** `develop` ← **compare:** `feature/sonidos`
2. Llena la plantilla (qué hace, cómo probarlo, capturas).
3. Pide revisión a **al menos un compañero**.
4. Espera a que el CI (GitHub Actions) salga en ✅ verde.
5. Cuando lo aprueben, usa **Squash and merge** y borra la rama.

## 5. Mantener tu rama al día

Si `develop` avanzó mientras trabajabas:

```bash
git switch develop
git pull
git switch feature/sonidos
git merge develop
```

## 6. Resolver conflictos

Un conflicto pasa cuando dos personas cambiaron las mismas líneas. Git marca
el archivo así:

```
<<<<<<< HEAD
tu versión
=======
la versión de develop
>>>>>>> develop
```

Pasos:

1. En Android Studio: **Git → Resolve Conflicts…**. Te muestra las dos
   versiones lado a lado; elige o combina.
2. Borra las marcas `<<<<<<<`, `=======` y `>>>>>>>` si lo haces a mano.
3. Compila y corre las pruebas.
4. Termina el merge:
   ```bash
   git add <archivo>
   git commit
   ```
5. Si no sabes qué versión conservar, **pregunta** al autor del otro cambio.

### Archivos "compartidos" donde más hay conflictos

| Archivo | Cómo evitarlo |
|---|---|
| `strings.xml` | Agrega tus textos en el bloque de tu módulo, con su prefijo |
| `DescansaNavegacion.kt` | Toca solo la línea `composable<...>` de tu módulo |
| `DescansaDatabase.kt` | Avisa en el grupo antes de subir la `version` |
| `libs.versions.toml` | Agrega librerías al final de su sección |
| `DataModule.kt` | Agrega tu `@Binds` al final |

## 7. Protección de ramas (lo configura el dueño del repo)

En GitHub: **Settings → Branches → Add branch ruleset** para `main` y `develop`:

- ✅ Require a pull request before merging (1 aprobación)
- ✅ Require status checks to pass → `compilar-y-probar`
- ✅ Block force pushes
