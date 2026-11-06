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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Bar row with Theme Configuration (Dark / Light)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onToggleDarkMode,
                modifier = Modifier.testTag("theme_toggle_button"),
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

        // Hero Header Card
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
                    .padding(26.dp),
                contentAlignment = Alignment.Center
            ) {
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
                        Text(
                            text = "🏠",
                            fontSize = 38.sp
                        )
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

        Text(
            text = "Escolha como deseja acessar:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 2.dp)
        )

        // Card: Sou Responsável
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("parent_access_button"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = parentCardBg
            ),
            border = parentCardBorder,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

        // Card: Sou Criança
        val kidCardBg = if (isDarkMode) MaterialTheme.colorScheme.surface else Color(0xFFFFF7ED)
        val kidCardBorder = if (isDarkMode) null else BorderStroke(1.5.dp, Color(0xFFFED7AA))
        val kidTitleColor = if (isDarkMode) MaterialTheme.colorScheme.onSurface else Color(0xFF9A3412)
        val kidDescColor = if (isDarkMode) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF854D0E)

        Card(
            onClick = { onNavigate(AppScreen.KIDS_SELECT) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("kids_access_button"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = kidCardBg
            ),
            border = kidCardBorder,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

        // Secondary Action: Cadastrar / Editar Família
        OutlinedButton(
            onClick = { onNavigate(AppScreen.REGISTER) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
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
                fontSize = 14.sp
            )
        }

        // Secondary Action: Histórico e Saldo
        OutlinedButton(
            onClick = { onNavigate(AppScreen.HISTORY) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
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
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
