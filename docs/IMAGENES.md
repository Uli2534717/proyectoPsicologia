# Imágenes de la app

Estilo "Cartel sereno": **fotografías de objetos sobre fondo blanco**, sin
personas ni escenas. Las imágenes de prueba actuales son marcadores vectoriales
(`.xml`) con el mismo nombre que tendrá la foto definitiva.

## Cómo reemplazar una imagen

1. Toma o consigue la foto: el objeto centrado sobre fondo blanco, en formato cuadrado.
2. Conviértela a **WebP**. En Android Studio: clic derecho sobre el PNG o JPG →
   **Convert to WebP**.
3. Ponla en `app/src/main/res/drawable/` con **el mismo nombre** de la tabla.
4. **Borra el `.xml`** del mismo nombre. Si hay dos archivos con el mismo
   nombre, el proyecto no compila.
5. Si la foto es de terceros, anota autor y licencia en `CREDITS.md`.

## Lista

| Archivo | Pantalla | Foto que debe ir | Descripción para TalkBack |
|---|---|---|---|
| `img_bienvenida_lampara_luna` | Bienvenida | Lámpara con forma de luna | Lámpara con forma de luna |
| `img_inicio_almohada` | Inicio | Almohada blanca | Una almohada |
| `img_vaciado_cuaderno` | Pendientes (paso 1) e Inicio | Cuaderno abierto con lápiz | Un cuaderno abierto |
| `img_respiracion_vela` | Respiración (paso 2) e Inicio | Vela encendida | Una vela encendida |
| `img_relajacion_manta` | Relajación (paso 3) | Manta doblada | Una manta doblada |
| `img_sonido_lluvia` | Sonidos (paso 4) e Inicio | Paraguas cerrado o gotas en vidrio | Lluvia |
| `img_sonido_ruido_blanco` | Sonidos | Ventilador pequeño | Un ventilador |
| `img_sonido_olas` | Sonidos | Caracola de mar | Una caracola de mar |
| `img_sonido_bosque` | Sonidos | Piña de pino | Una piña de pino |
| `img_alarma_despertador` | Alarma (paso 5) y timbre | Despertador de campanas | Un despertador |
| `img_modo_noche_lampara` | Modo noche | Lámpara de buró apagada | Una lámpara de buró |
| `img_manana_taza` | Resumen de la mañana | Taza de té o café | Una taza |
| `img_aprender_lampara` | Aprender: por qué dormir bien | Lámpara de luna | Una lámpara |
| `img_aprender_reloj_arena` | Aprender: etapas del sueño | Reloj de arena | Un reloj de arena |
| `img_aprender_te` | Aprender: hábitos | Taza de té con bolsita | Una taza de té |
| `img_aprender_cuaderno` | Aprender: cuando la mente no para | Cuaderno cerrado | Un cuaderno |
| `img_ajustes_audifonos` | Ajustes | Audífonos | Unos audífonos |

> Las descripciones para TalkBack están en `strings.xml` (prefijo `img_`).
