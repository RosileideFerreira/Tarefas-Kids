package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    isRegistered: Boolean,
    familyName: String,
    onToggleDarkMode: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        AdaptiveContainer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) { adaptive ->
            val maxContentWidth = when (adaptive.widthClass) {
                ScreenWidthClass.Compact -> adaptive.maxWidth
                ScreenWidthClass.Medium -> 760.dp
                ScreenWidthClass.Expanded -> 960.dp
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                        .padding(horizontal = adaptive.horizontalPadding, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (adaptive.isCompactHeight) 14.dp else 18.dp)
                ) {
                    // Top Bar row with Theme Configuration (Dark / Light)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = onToggleDarkMode,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("theme_toggle_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Mudar tema de cor",
                                tint = if (isDarkMode) GamificacaoOuro else BotaoPrimarioAzul,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDarkMode) "Modo Claro ☀️" else "Modo Escuro 🌙",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Hero Header Card (adapts to compact height / landscape)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hero_header_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(BotaoPrimarioAzul, Color(0xFF1D4ED8))
                                    )
                                )
                                .padding(if (adaptive.isCompactHeight) 18.dp else 26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (adaptive.isCompactHeight) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🏠", fontSize = 28.sp)
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Tarefas Kids",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Cada tarefa da casa vale uma recompensa.",
                                            color = Color.White.copy(alpha = 0.95f),
                                            fontSize = 14.sp
                                        )
                                    }
                                    if (isRegistered && familyName.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color.White.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = "🏡 $familyName",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🏠", fontSize = 38.sp)
                                    }

                                    Text(
                                        text = "Tarefas Kids",
                                        color = Color.White,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "Cada tarefa da casa vale uma recompensa.",
                                        color = Color.White.copy(alpha = 0.95f),
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    if (isRegistered && familyName.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color.White.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = "🏡 $familyName",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "Escolha como deseja acessar:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp, top = 2.dp)
                    )

                    // Role access cards: 2 columns on Medium/Expanded, stacked on Compact
                    if (adaptive.isMediumOrExpanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ParentAccessCard(
                                isDarkMode = isDarkMode,
                                isRegistered = isRegistered,
                                onNavigate = onNavigate,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                            KidAccessCard(
                                isDarkMode = isDarkMode,
                                onNavigate = onNavigate,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    } else {
                        ParentAccessCard(
                            isDarkMode = isDarkMode,
                            isRegistered = isRegistered,
                            onNavigate = onNavigate,
                            modifier = Modifier.fillMaxWidth()
                        )
                        KidAccessCard(
                            isDarkMode = isDarkMode,
                            onNavigate = onNavigate,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Secondary Actions: 2 columns on Medium/Expanded, stacked on Compact
                    if (adaptive.isMediumOrExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            RegisterNavButton(
                                isRegistered = isRegistered,
                                onNavigate = onNavigate,
                                modifier = Modifier.weight(1f)
                            )
                            HistoryNavButton(
                                onNavigate = onNavigate,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        RegisterNavButton(
                            isRegistered = isRegistered,
                            onNavigate = onNavigate,
                            modifier = Modifier.fillMaxWidth()
                        )
                        HistoryNavButton(
                            onNavigate = onNavigate,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ParentAccessCard(
    isDarkMode: Boolean,
    isRegistered: Boolean,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val parentCardBg = if (isDarkMode) MaterialTheme.colorScheme.surface else Color(0xFFEBF3FE)
    val parentCardBorder = if (isDarkMode) null else BorderStroke(1.5.dp, Color(0xFFBED8F9))
    val parentTitleColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color(0xFF1E3A8A)
    val parentDescColor = if (isDarkMode) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF2C5282)

    Card(
        onClick = {
            if (!isRegistered) {
                onNavigate(AppScreen.REGISTER)
            } else {
                onNavigate(AppScreen.PARENT_PIN)
            }
        },
        modifier = modifier.testTag("parent_access_button"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = parentCardBg),
        border = parentCardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) BotaoPrimarioAzul.copy(alpha = 0.15f) else Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FamilyRestroom,
                    contentDescription = "Ícone Responsável",
                    tint = BotaoPrimarioAzul,
                    modifier = Modifier.size(30.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "👨‍👩‍👧‍👦 Sou responsável",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = parentTitleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Cadastre tarefas, defina para quem é cada uma, veja as telas dos filhos e aprove atividades",
                    fontSize = 13.sp,
                    color = parentDescColor
                )
            }
        }
    }
}

@Composable
private fun KidAccessCard(
    isDarkMode: Boolean,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val kidCardBg = if (isDarkMode) MaterialTheme.colorScheme.surface else Color(0xFFFFF7ED)
    val kidCardBorder = if (isDarkMode) null else BorderStroke(1.5.dp, Color(0xFFFED7AA))
    val kidTitleColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color(0xFF9A3412)
    val kidDescColor = if (isDarkMode) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF854D0E)

    Card(
        onClick = { onNavigate(AppScreen.KIDS_SELECT) },
        modifier = modifier.testTag("kids_access_button"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = kidCardBg),
        border = kidCardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isDarkMode) SucessoVerdeAgua.copy(alpha = 0.15f) else Color(0xFFFFEDD5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Ícone Criança",
                    tint = if (isDarkMode) SucessoVerdeAgua else Color(0xFFEA580C),
                    modifier = Modifier.size(30.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🧒 Sou criança",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = kidTitleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Veja suas tarefas, marque o que fez e peça recompensas!",
                    fontSize = 13.sp,
                    color = kidDescColor
                )
            }
        }
    }
}

@Composable
private fun RegisterNavButton(
    isRegistered: Boolean,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { onNavigate(AppScreen.REGISTER) },
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .testTag("register_nav_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.HowToReg,
            contentDescription = null,
            tint = BotaoPrimarioAzul,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isRegistered) "✨ Cadastrar Nova Criança ou Família" else "✨ Cadastrar Família",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryNavButton(
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { onNavigate(AppScreen.HISTORY) },
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .testTag("history_nav_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = LinkTextoClicavel,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "📜 Histórico e Saldo",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
