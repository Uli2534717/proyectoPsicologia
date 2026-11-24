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

Preparación: Ajustes de la app → activar **Modo desarrollador** → aparece
**"Probar alarma en 2 minutos"**.

- [ ] **Permisos en contexto:** la primera vez que se pulsa "Activar modo
      noche" aparece la explicación de notificaciones ANTES del diálogo de Android.
- [ ] **Modo noche** muestra "Alarma de respaldo a las HH:mm" con la hora real.
      En la barra de estado aparece el ícono de reloj de Android.
- [ ] **Teléfono bloqueado:** programar la prueba, bloquear con el botón de
      encendido y esperar. La pantalla se enciende y aparece "Hora de despertar"
      sobre la pantalla de bloqueo, con volumen creciente y vibración suave.
- [ ] **Con PIN o huella:** lo mismo; se puede Detener o Posponer sin desbloquear.
- [ ] **Volumen multimedia en 0 y modo silencio/vibración:** la alarma suena igual.
- [ ] **Posponer:** deja de sonar y vuelve a sonar después de los minutos configurados.
- [ ] **Detener:** deja de sonar; al desbloquear se abre "Buenos días".
- [ ] **Detener desde la notificación** (si se estaba usando el teléfono):
      la alarma aparece como notificación emergente y sus botones funcionan.
- [ ] **Reinicio sin desbloquear:** programar la prueba, reiniciar el teléfono
      y NO desbloquearlo. Suena a la hora.
- [ ] **Reinicio y desbloqueo:** igual, desbloqueando después de encender.
- [ ] **App cerrada desde Recientes:** suena igual.
- [ ] **Cambio de zona horaria** con alarma de 07:00 programada: sigue a las
      07:00 de la nueva zona (ver Modo noche).
- [ ] **"Forzar detención"** (limitación de Android, D-15): la alarma se
      pierde. Anotar que se comprobó.
- [ ] La alarma deja de sonar sola a los 30 minutos.

## Fases siguientes (se completan en su fase)

- [ ] Alarma inteligente con el simulador (noche inquieta y noche tranquila).
- [ ] Una noche real de prueba.
- [ ] No molestar se activa y se restaura al detener.
- [ ] Sonido toda la noche con la pantalla apagada y apagado gradual.
- [ ] Consumo de batería en 8 horas de sesión: ____ %.
- [ ] Negar cada permiso uno por uno: la app sigue funcionando y lo explica.
