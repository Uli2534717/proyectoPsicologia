# Pruebas manuales

Marca cada casilla al probar y anota el resultado. Las pruebas de alarma,
sensores y No molestar deben hacerse en **dos teléfonos reales de marcas
distintas** (sección 15 del documento).

| Dato | Teléfono A | Teléfono B |
|---|---|---|
| Marca y modelo | | |
| Versión de Android | | |
| Probado por | | |
| Fecha | | |

## Fase 0 · Navegación (emulador o teléfono)

- [ ] Primera apertura: aparece **Bienvenida** con los 4 permisos explicados.
- [ ] "Comenzar" lleva a **Inicio**. Al cerrar y abrir la app, abre directo en Inicio.
- [ ] Inicio muestra el estado vacío de "Tu última noche".
- [ ] "Preparar mi noche" recorre los pasos 1 → 5 con "Siguiente" o "Continuar".
- [ ] "Omitir paso" funciona en los pasos 1 a 4.
- [ ] Paso 1: se pueden agregar pendientes y marcarlos.
- [ ] Paso 3: "Siguiente" recorre los 6 grupos y del último pasa al paso 4.
- [ ] Paso 5: tocar la hora abre el selector. Cambiar hora, ventana o fuente se conserva al volver.
- [ ] "Activar modo noche" muestra la pantalla oscura con la hora actual.
- [ ] "Detener" pide confirmación y lleva al **resumen de la mañana**.
- [ ] "Volver al inicio" regresa a Inicio.
- [ ] La barra inferior solo aparece en Inicio y Aprender; sus 4 pestañas funcionan.
- [ ] El engrane de Inicio abre Ajustes, y los cambios de sensibilidad se conservan.
- [ ] Con TalkBack activado, los botones de solo ícono se leen con su nombre.
- [ ] Con letra grande (Ajustes de Android → Tamaño de fuente al máximo) nada se corta.

## Fase 1 · Alarma de respaldo (teléfono real)

Atajo: en Ajustes, activa **Modo desarrollador** y toca **Probar alarma en 10
segundos**; después bloquea el teléfono.

En el emulador (Pixel 9, API 37) ya se comprobó: la alarma de prueba suena con el
teléfono bloqueado, aparece la pantalla de timbre, "Apagar" abre el resumen, y
tras reiniciar la alarma de las 07:00 se vuelve a programar.

- [ ] Paso 5 → "Activar modo noche": en Android 13+ pide el permiso de notificaciones.
- [ ] Aparece el ícono de alarma ⏰ en la barra de estado (alarma programada).
- [ ] Con el teléfono **bloqueado**, la alarma enciende la pantalla y muestra "Es hora de despertar".
- [ ] El volumen empieza bajo y sube durante ~1 minuto; el teléfono vibra.
- [ ] Con el volumen de alarmas en 0, aun así se escucha.
- [ ] "Apagar" detiene el sonido y abre el resumen de la mañana.
- [ ] "Posponer" la vuelve a hacer sonar tras los minutos elegidos en Ajustes.
- [ ] Los botones Apagar/Posponer de la **notificación** también funcionan.
- [ ] **Negando** las notificaciones: la alarma suena igual, y el modo noche muestra el aviso "falta un permiso".
- [ ] Con No molestar del sistema activado, la alarma suena.
- [ ] **Reinicio:** activar el modo noche, reiniciar, desbloquear: la alarma sigue programada.
- [ ] Cerrar la app deslizándola desde "recientes": la alarma suena igual.
- [ ] "Detener" en el modo noche cancela la alarma (desaparece el ⏰).
- [ ] Si nadie la apaga, se calla sola a los 10 minutos.

> "Forzar detención" desde Ajustes de Android **cancela todas las alarmas de la
> app**: así funciona Android y ninguna app lo puede evitar. Por eso no se
> incluye como prueba.

## Fases siguientes (se completan en su fase)

- [ ] Alarma inteligente con el simulador (noche inquieta y noche tranquila).
- [ ] Una noche real de prueba.
- [ ] No molestar se activa y se restaura al detener.
- [ ] Sonido toda la noche con la pantalla apagada y apagado gradual.
- [ ] Consumo de batería en 8 horas de sesión: ____ %.
- [ ] Negar cada permiso uno por uno: la app sigue funcionando y lo explica.
