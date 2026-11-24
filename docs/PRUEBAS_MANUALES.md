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

## Fase 2 · Servicio nocturno y motor

Simulador: en Ajustes, con **Modo desarrollador** activado, toca **Simular noche
inquieta** o **Simular noche tranquila** y bloquea el teléfono. Cada una dura 1 minuto.

En el emulador (API 37) ya se comprobó: la inquieta suena a los ~41 s (dentro de
la ventana) con motivo `SUENO_LIGERO`, y la tranquila a los 60 s con `HORA_LIMITE`.
Con el acelerómetro real se guardó una época por minuto con la pantalla apagada.

- [ ] Noche inquieta simulada: suena antes del minuto.
- [ ] Noche tranquila simulada: suena justo al minuto.
- [ ] Al activar el modo noche aparece la notificación discreta "Modo noche activo".
- [ ] El modo noche muestra "Midiendo movimiento: N min" y el número sube cada minuto.
- [ ] Cerrar la app y volver a abrirla durante la noche: abre directo en el modo noche.
- [ ] Con la alarma sonando y las notificaciones negadas, abrir la app muestra la pantalla de timbre.
- [ ] **Noche real:** teléfono sobre el colchón; anotar a qué hora sonó y si fue antes de la hora límite: ______
- [ ] Probar las 3 sensibilidades en noches distintas; anotar la que se sintió mejor: ______

## Fase 3 · No molestar

En el emulador (API 37) ya se comprobó: sin permiso se muestra el aviso; con
permiso se enciende el modo "Do Not Disturb (Descansa)" en solo alarmas, la
alarma suena igual, y al apagarla el modo se desactiva.

- [ ] Sin el permiso: el modo noche dice "No molestar: falta el permiso" y "Dar permiso" abre Ajustes de Android.
- [ ] Dar el permiso y regresar: el aviso cambia a "No molestar activo" y aparece el ícono de No molestar.
- [ ] Durante la noche, una llamada o mensaje de prueba NO suena.
- [ ] La alarma SÍ suena con el No molestar activo.
- [ ] Al apagar la alarma, el No molestar se quita solo.
- [ ] "Detener" también lo quita.
- [ ] "Posponer" lo mantiene hasta que se apague la alarma.
- [ ] Con el No molestar del usuario ya activo antes de dormir: al despertar sigue como estaba.
- [ ] Desactivar "No molestar" en el paso 5: la noche no lo enciende.

## Fases siguientes (se completan en su fase)

- [ ] Sonido toda la noche con la pantalla apagada y apagado gradual.
- [ ] Consumo de batería en 8 horas de sesión: ____ %.
- [ ] Negar cada permiso uno por uno: la app sigue funcionando y lo explica.
