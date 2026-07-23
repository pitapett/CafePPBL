package com.example.cafeapp.feature.admin.menu.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// 1. The Runner: Tells JUnit to execute this using the Android testing environment
@RunWith(AndroidJUnit4::class)
class CreateMenuScreenTest {

    // 2. The Rule: Bootstraps the Compose test environment and provides the setContent context
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createMenuScreen_inputsAcceptText() {
        // 3. setContent is now safely called against the instantiated rule
        composeTestRule.setContent {
            CreateMenuScreen(
                onNavigateBack = {},
                viewModel = MenuViewModel()
            )
        }

        // Target the text fields using their labels
        composeTestRule.onNodeWithText("Nama Menu")
            .performTextInput("Caramel Macchiato")

        composeTestRule.onNodeWithText("Harga")
            .performTextInput("35000")

        composeTestRule.onNodeWithText("Deskripsi")
            .performTextInput("Espresso with vanilla and caramel")

        // Assert the text was actually inputted into the UI
        composeTestRule.onNodeWithText("Caramel Macchiato").assertExists()
        composeTestRule.onNodeWithText("35000").assertExists()
        composeTestRule.onNodeWithText("Espresso with vanilla and caramel").assertExists()
    }
}