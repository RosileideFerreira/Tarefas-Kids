package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.TarefasViewModel
import com.example.ui.screens.*
import com.example.ui.theme.TarefasKidsTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TarefasViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            TarefasKidsTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(viewModel = viewModel, isDarkMode = isDarkMode)
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: TarefasViewModel, isDarkMode: Boolean) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val children by viewModel.children.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val activeTasks by viewModel.activeTasks.collectAsStateWithLifecycle()
    val pendingCompletions by viewModel.pendingCompletions.collectAsStateWithLifecycle()
    val pendingRedemptions by viewModel.pendingRedemptions.collectAsStateWithLifecycle()
    val balances by viewModel.balances.collectAsStateWithLifecycle()
    val selectedChild by viewModel.selectedChild.collectAsStateWithLifecycle()
    val allCompletions by viewModel.allCompletions.collectAsStateWithLifecycle()
    val allRedemptions by viewModel.allRedemptions.collectAsStateWithLifecycle()
    val selectedHistoryChildId by viewModel.selectedHistoryChildId.collectAsStateWithLifecycle()
    val parentPin by viewModel.parentPin.collectAsStateWithLifecycle()
    val isParentLoggedIn by viewModel.isParentLoggedIn.collectAsStateWithLifecycle()
    val navigatedFromParent by viewModel.navigatedFromParent.collectAsStateWithLifecycle()

    var showPinDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    when (currentScreen) {
        AppScreen.HOME -> {
            HomeScreen(
                isDarkMode = isDarkMode,
                isRegistered = viewModel.isRegistered(),
                familyName = viewModel.getFamilyName(),
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onNavigate = { destination ->
                    if (destination == AppScreen.PARENT_PIN) {
                        if (isParentLoggedIn) {
                            viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
                        } else {
                            showPinDialog = true
                        }
                    } else {
                        viewModel.navigateTo(destination)
                    }
                }
            )
        }

        AppScreen.REGISTER -> {
            RegisterScreen(
                onRegisterSuccess = { name, email, pass, pin, kidsList ->
                    viewModel.registerFamily(name, email, pass, pin, kidsList) {
                        viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
                    }
                },
                onNavigateToLogin = { viewModel.navigateTo(AppScreen.PARENT_LOGIN) },
                onBack = { viewModel.navigateTo(AppScreen.HOME) }
            )
        }

        AppScreen.PARENT_LOGIN -> {
            LoginScreen(
                onLoginWithPassword = { email, pass ->
                    val ok = viewModel.loginWithPassword(email, pass)
                    if (ok) {
                        viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
                    }
                    ok
                },
                onLoginWithPin = { pin ->
                    val ok = viewModel.loginWithPin(pin)
                    if (ok) {
                        viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
                    }
                    ok
                },
                onNavigateToRegister = { viewModel.navigateTo(AppScreen.REGISTER) },
                onBack = { viewModel.navigateTo(AppScreen.HOME) }
            )
        }

        AppScreen.PARENT_PIN -> {
            if (isParentLoggedIn) {
                LaunchedEffect(Unit) {
                    viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
                }
            } else {
                LaunchedEffect(Unit) {
                    showPinDialog = true
                }
            }
            HomeScreen(
                isDarkMode = isDarkMode,
                isRegistered = viewModel.isRegistered(),
                familyName = viewModel.getFamilyName(),
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onNavigate = { destination ->
                    if (destination == AppScreen.PARENT_PIN) {
                        showPinDialog = true
                    } else {
                        viewModel.navigateTo(destination)
                    }
                }
            )
        }

        AppScreen.PARENT_DASHBOARD -> {
            ParentDashboardScreen(
                children = children,
                balances = balances,
                pendingCompletions = pendingCompletions,
                pendingRedemptions = pendingRedemptions,
                allCompletions = allCompletions,
                allTasks = allTasks,
                familyName = viewModel.getFamilyName(),
                isDarkMode = isDarkMode,
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onNavigate = { viewModel.navigateTo(it) },
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                onAddChild = { name, avatar -> viewModel.addChild(name, avatar) },
                onDeleteChild = { viewModel.deleteChild(it) },
                onOpenChildScreen = { child ->
                    viewModel.selectChild(child, fromParent = true)
                    viewModel.navigateTo(AppScreen.KIDS_DASHBOARD)
                },
                onReviewCompletion = { id, approve -> viewModel.reviewCompletion(id, approve) },
                onReviewRedemption = { id, approve -> viewModel.reviewRedemption(id, approve) },
                onLogout = { viewModel.logoutParent() }
            )
        }

        AppScreen.PARENT_TASKS_MANAGE -> {
            ParentTasksScreen(
                tasks = allTasks,
                children = children,
                onBack = { viewModel.navigateTo(AppScreen.PARENT_DASHBOARD) },
                onAddTask = { title, room, cents, desc, childId, childName, targetWeek ->
                    viewModel.addTask(title, room, cents, desc, childId, childName, targetWeek)
                },
                onUpdateTask = { viewModel.updateTask(it) },
                onDeleteTask = { viewModel.deleteTask(it) }
            )
        }

        AppScreen.KIDS_SELECT -> {
            KidsSelectScreen(
                children = children,
                onSelectChild = { child ->
                    viewModel.selectChild(child, fromParent = false)
                    viewModel.navigateTo(AppScreen.KIDS_DASHBOARD)
                },
                onBack = { viewModel.navigateTo(AppScreen.HOME) }
            )
        }

        AppScreen.KIDS_DASHBOARD -> {
            val child = selectedChild
            if (child == null) {
                LaunchedEffect(Unit) {
                    viewModel.navigateTo(AppScreen.KIDS_SELECT)
                }
            } else {
                val childBalance = balances[child.id]
                val childComps = allCompletions.filter { it.childId == child.id }
                val childReds = allRedemptions.filter { it.childId == child.id }

                KidsDashboardScreen(
                    child = child,
                    balance = childBalance,
                    tasks = activeTasks,
                    completions = childComps,
                    redemptions = childReds,
                    onCompleteTask = { task -> viewModel.completeTask(child, task) },
                    onUndoCompletion = { compId -> viewModel.cancelCompletion(compId) },
                    onRequestRedemption = { title, cents ->
                        viewModel.requestRedemption(child, title, cents)
                    },
                    onBack = {
                        if (navigatedFromParent) {
                            viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
                        } else {
                            viewModel.navigateTo(AppScreen.KIDS_SELECT)
                        }
                    }
                )
            }
        }

        AppScreen.HISTORY -> {
            HistoryScreen(
                children = children,
                balances = balances,
                allCompletions = allCompletions,
                allRedemptions = allRedemptions,
                selectedChildId = selectedHistoryChildId,
                onSelectChildId = { viewModel.setHistoryChildId(it) },
                onBack = { viewModel.navigateTo(AppScreen.HOME) }
            )
        }
    }

    if (showPinDialog) {
        ParentPinDialog(
            correctPin = parentPin,
            onPinSuccess = {
                showPinDialog = false
                viewModel.navigateTo(AppScreen.PARENT_DASHBOARD)
            },
            onDismiss = {
                showPinDialog = false
                if (currentScreen == AppScreen.PARENT_PIN) {
                    viewModel.navigateTo(AppScreen.HOME)
                }
            }
        )
    }
}
