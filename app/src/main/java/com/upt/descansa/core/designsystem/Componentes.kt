package com.upt.descansa.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upt.descansa.core.theme.Borde
import com.upt.descansa.core.theme.BordeTenue
import com.upt.descansa.core.theme.Lavanda
import com.upt.descansa.core.theme.Superficie
import com.upt.descansa.core.theme.SuperficieSecundaria
import com.upt.descansa.core.theme.SuperficieSeleccionada
import com.upt.descansa.core.theme.Texto
import com.upt.descansa.core.theme.TextoAtenuado
import com.upt.descansa.core.theme.TextoSobreLavanda
import com.upt.descansa.core.theme.Turquesa

// =============================================================================
//  Componentes visuales de Descansa, copiados del prototipo.
//  Úsalos en todas las pantallas en vez de crear botones o tarjetas nuevos:
//  así la app se ve igual en todos lados y un cambio aquí se aplica a todo.
// =============================================================================

/** Forma de píldora de los botones principales (radio 28 según el prototipo). */
val FormaPildora = RoundedCornerShape(28.dp)

/** Alto mínimo del botón principal (56) y de cualquier objetivo táctil (48). */
private val AltoBotonPrincipal = 56.dp
val AltoMinimoTactil = 48.dp

/**
 * Botón principal: fondo lavanda, texto oscuro, 18 sp, peso 800, forma de píldora.
 * Va al pie de casi todas las pantallas ("Comenzar", "Siguiente", "Activar modo noche").
 */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    habilitado: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = FormaPildora,
        colors = ButtonDefaults.buttonColors(containerColor = Lavanda, contentColor = TextoSobreLavanda),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AltoBotonPrincipal),
    ) {
        if (icono != null) {
            // Decorativo: el texto del botón ya dice qué hace.
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(12.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Botón secundario: misma forma, fondo de tarjeta con borde ("Anterior", "Detener"). */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        shape = FormaPildora,
        border = BorderStroke(1.5.dp, Borde),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Superficie, contentColor = Texto),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AltoBotonPrincipal),
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge.copy(color = Texto))
    }
}

/**
 * Tarjeta del prototipo: fondo de superficie y esquinas de 20.
 * Si `seleccionada` es verdadero, se resalta con borde lavanda (como "Recomendado
 * para ti" o la fuente de datos elegida).
 */
@Composable
fun TarjetaDescansa(
    modifier: Modifier = Modifier,
    seleccionada: Boolean = false,
    onClick: (() -> Unit)? = null,
    relleno: PaddingValues = PaddingValues(20.dp),
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val color = if (seleccionada) SuperficieSeleccionada else Superficie
    val borde = if (seleccionada) BorderStroke(2.dp, Lavanda) else null
    val forma = MaterialTheme.shapes.medium
    if (onClick != null) {
        Surface(onClick = onClick, shape = forma, color = color, border = borde, modifier = modifier) {
            Column(Modifier.padding(relleno), content = contenido)
        }
    } else {
        Surface(shape = forma, color = color, border = borde, modifier = modifier) {
            Column(Modifier.padding(relleno), content = contenido)
        }
    }
}

/** Etiqueta de sección en mayúsculas y turquesa ("TU ÚLTIMA NOCHE", "HORA LÍMITE"). */
@Composable
fun EtiquetaSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Turquesa,
        modifier = modifier.semantics { heading() },
    )
}

/**
 * Chip de opción con forma de píldora ("2 min / 5 min / 10 min").
 * TalkBack lo anuncia como botón de opción y dice si está seleccionado.
 */
@Composable
fun ChipOpcion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = FormaPildora,
        color = if (seleccionado) SuperficieSeleccionada else Superficie,
        border = BorderStroke(if (seleccionado) 2.dp else 1.dp, if (seleccionado) Lavanda else Borde),
        modifier = modifier
            .heightIn(min = AltoMinimoTactil)
            .semantics {
                role = Role.RadioButton
                selected = seleccionado
            },
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                color = if (seleccionado) Texto else TextoAtenuado,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Fila de chips del mismo ancho; solo uno puede estar elegido. */
@Composable
fun <T> FilaChips(
    opciones: List<T>,
    seleccionada: T,
    // @Composable para que la etiqueta pueda usar stringResource(...).
    etiqueta: @Composable (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        opciones.forEach { opcion ->
            ChipOpcion(
                texto = etiqueta(opcion),
                seleccionado = opcion == seleccionada,
                onClick = { onSeleccionar(opcion) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Caja de aviso con ícono ("Si no detectamos sueño ligero, sonará a la hora límite."). */
@Composable
fun AvisoTarjeta(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    colorIcono: androidx.compose.ui.graphics.Color = Turquesa,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = SuperficieSecundaria,
        border = BorderStroke(1.dp, BordeTenue),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(texto, style = MaterialTheme.typography.bodyLarge, color = Texto)
        }
    }
}

/** Título de pantalla grande + subtítulo atenuado opcional. */
@Composable
fun TituloPantalla(titulo: String, modifier: Modifier = Modifier, subtitulo: String? = null) {
    Column(modifier) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium,
            color = Texto,
            modifier = Modifier.semantics { heading() },
        )
        if (subtitulo != null) {
            Spacer(Modifier.size(6.dp))
            Text(subtitulo, style = MaterialTheme.typography.bodyLarge, color = TextoAtenuado)
        }
    }
}
