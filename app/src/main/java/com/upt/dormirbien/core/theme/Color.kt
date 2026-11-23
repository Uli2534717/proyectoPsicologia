package com.upt.dormirbien.core.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
//  Paleta de DormirBien
//  -----------------------------------------------------------------------------
//  Tema oscuro (por defecto): azul noche de fondo, lavanda como color principal,
//  verde agua como secundario y un amarillo luna suave como acento.
//  Se evitan blancos puros y azules muy saturados, que deslumbran de noche.
//  Los pares "color / onColor" (texto encima) tienen buen contraste (WCAG AA).
// =============================================================================

// --- Tema oscuro ---
val NocheFondo = Color(0xFF0E1726)
val NocheSuperficie = Color(0xFF0E1726)
val NocheContenedorBajo = Color(0xFF111A2C)
val NocheContenedor = Color(0xFF16213A)
val NocheContenedorAlto = Color(0xFF1C2741)
val NocheContenedorMasAlto = Color(0xFF243049)
val NocheSuperficieVariante = Color(0xFF27324A)
val NocheTexto = Color(0xFFE3E6EF)
val NocheTextoVariante = Color(0xFFC2C7D4)
val NocheContorno = Color(0xFF8C93A3)

val Lavanda = Color(0xFFB9B4F5)
val LavandaSobre = Color(0xFF231F5C)
val LavandaContenedor = Color(0xFF3A3577)
val LavandaSobreContenedor = Color(0xFFE3DFFF)

val VerdeAgua = Color(0xFF8FD3CC)
val VerdeAguaSobre = Color(0xFF00332F)
val VerdeAguaContenedor = Color(0xFF1F4D49)
val VerdeAguaSobreContenedor = Color(0xFFB2F0E8)

val Luna = Color(0xFFF2C47E)
val LunaSobre = Color(0xFF422C00)
val LunaContenedor = Color(0xFF5E4200)
val LunaSobreContenedor = Color(0xFFFFDEAD)

val ErrorNoche = Color(0xFFFFB4AB)
val ErrorNocheSobre = Color(0xFF690005)

// --- Tema claro ---
val DiaFondo = Color(0xFFF8F8FC)
val DiaContenedorBajo = Color(0xFFF2F2F8)
val DiaContenedor = Color(0xFFECECF4)
val DiaContenedorAlto = Color(0xFFE6E6EF)
val DiaContenedorMasAlto = Color(0xFFE0E0EA)
val DiaSuperficieVariante = Color(0xFFE2E1EC)
val DiaTexto = Color(0xFF1A1C22)
val DiaTextoVariante = Color(0xFF45464F)
val DiaContorno = Color(0xFF767680)

val LavandaOscura = Color(0xFF524DA0)
val LavandaOscuraContenedor = Color(0xFFE3DFFF)
val LavandaOscuraSobreContenedor = Color(0xFF0D0461)

val VerdeAguaOscuro = Color(0xFF006A63)
val VerdeAguaOscuroContenedor = Color(0xFFB2F0E8)
val VerdeAguaOscuroSobreContenedor = Color(0xFF00201D)

val LunaOscura = Color(0xFF7C5800)
val LunaOscuraContenedor = Color(0xFFFFDEAD)
val LunaOscuraSobreContenedor = Color(0xFF271900)

val ErrorDia = Color(0xFFBA1A1A)
