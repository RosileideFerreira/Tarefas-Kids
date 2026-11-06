package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BotaoPrimarioAzul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1D4ED8),
    onPrimaryContainer = Color.White,
    secondary = SucessoVerdeAgua,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color.White,
    tertiary = DestaqueAlertaAmarelo,
    onTertiary = Color.Black,
    background = FundoPrincipalEscuro,
    onBackground = TextoClaro,
    surface = LeituraPaperEscuro,
    onSurface = TextoClaro,
    surfaceVariant = LeituraPaperEscuroCard,
    onSurfaceVariant = TextoSecundarioClaro,
    outline = Color(0xFF475569),
    outlineVariant = BotaoSecundarioDark
)

private val LightColorScheme = lightColorScheme(
    primary = BotaoPrimarioAzul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = SucessoVerdeAgua,
    onSecondary = Color.White,
    secondaryContainer = SucessoVerdeContainer,
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = DestaqueAlertaAmarelo,
    onTertiary = Color.White,
    background = FundoPrincipalClaro,
    onBackground = TextoEscuro,
    surface = LeituraPaperClaro,
    onSurface = TextoEscuro,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextoSecundarioEscuro,
    outline = BordaSuaveClara,
    outlineVariant = BotaoSecundarioCinza
)

@Composable
fun TarefasKidsTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
