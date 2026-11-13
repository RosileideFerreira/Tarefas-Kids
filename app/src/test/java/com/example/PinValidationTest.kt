package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.ui.TarefasViewModel
import com.example.ui.screens.ParentPinDialog
import com.example.ui.theme.TarefasKidsTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PinValidationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyPinDoesNotAllowAccess() {
        var successCalled = false
        composeTestRule.setContent {
            TarefasKidsTheme {
                ParentPinDialog(
                    correctPin = "2011",
                    onPinSuccess = { successCalled = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("pin_confirm_button").performClick()

        assertFalse(successCalled)
        composeTestRule.onNodeWithTag("parent_pin_error").assertIsDisplayed()
    }

    @Test
    fun default1234DoesNotBypassDifferentPin() {
        var successCalled = false
        composeTestRule.setContent {
            TarefasKidsTheme {
                ParentPinDialog(
                    correctPin = "2011",
                    onPinSuccess = { successCalled = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("parent_pin_input").performTextInput("1234")
        composeTestRule.onNodeWithTag("pin_confirm_button").performClick()

        assertFalse(successCalled)
        composeTestRule.onNodeWithTag("parent_pin_error").assertIsDisplayed()
    }

    @Test
    fun partialPinShowsErrorAndBlocksAccess() {
        var successCalled = false
        composeTestRule.setContent {
            TarefasKidsTheme {
                ParentPinDialog(
                    correctPin = "2011",
                    onPinSuccess = { successCalled = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("parent_pin_input").performTextInput("20")
        composeTestRule.onNodeWithTag("pin_confirm_button").performClick()

        assertFalse(successCalled)
        composeTestRule.onNodeWithTag("parent_pin_error").assertIsDisplayed()
    }

    @Test
    fun exactPinAllowsAccess() {
        var successCalled = false
        composeTestRule.setContent {
            TarefasKidsTheme {
                ParentPinDialog(
                    correctPin = "2011",
                    onPinSuccess = { successCalled = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("parent_pin_input").performTextInput("2011")

        assertTrue(successCalled)
    }

    @Test
    fun legacy1234PinMigratesTo2011AndCanBeChanged() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val prefs = app.getSharedPreferences("tarefas_kids_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .clear()
            .putString("pref_parent_email", "pais@exemplo.com")
            .putString("pref_parent_pin", "1234")
            .commit()

        val viewModel = TarefasViewModel(app)

        assertEquals("2011", viewModel.parentPin.value)
        assertFalse(viewModel.verifyParentPin("1234"))
        assertTrue(viewModel.verifyParentPin("2011"))

        assertFalse(viewModel.changeParentPin("1234", "9999"))
        assertTrue(viewModel.changeParentPin("2011", "4321"))
        assertEquals("4321", viewModel.parentPin.value)
        assertTrue(viewModel.verifyParentPin("4321"))
    }
}
