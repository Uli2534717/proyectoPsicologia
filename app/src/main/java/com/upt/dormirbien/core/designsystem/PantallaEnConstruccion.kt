package com.upt.dormirbien.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upt.dormirbien.R
import com.upt.dormirbien.core.theme.DormirBienTheme

/**
 * Pantalla temporal para los módulos que todavía no se programan.
 *
 * Cada integrante la reemplaza por su pantalla real en su fase. Sirve para que
 * la navegación funcione desde el principio y nadie dependa de otro para probar.
 *
 * @param titulo nombre del módulo (viene de strings.xml).
 * @param descripcion qué hará el módulo cuando esté listo.
 */
@Composable
fun PantallaEnConstruccion(
    titulo: String,
    descripcion: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag(TAG_PANTALLA_EN_CONSTRUCCION),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Build,
            // El ícono es decorativo: el texto ya explica la pantalla.
            // `null` hace que TalkBack lo ignore en lugar de leer algo inútil.
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            // Marca el texto como encabezado para TalkBack.
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.en_construccion),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

const val TAG_PANTALLA_EN_CONSTRUCCION = "pantalla_en_construccion"

@Preview(showBackground = true, backgroundColor = 0xFF0E1726)
@Composable
private fun PantallaEnConstruccionPreview() {
    DormirBienTheme {
        PantallaEnConstruccion(titulo = "Sonidos", descripcion = "Mezcla sonidos para dormir.")
    }
}
