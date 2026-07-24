package com.example.cafeapp.feature.admin.menu.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createMenuScreen_inputsAcceptText() {
        composeTestRule.setContent {
            CreateMenuScreen(
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithText("Nama Menu")
            .performTextInput("Caramel Macchiato")

        composeTestRule.onNodeWithText("Harga")
            .performTextInput("35000")

        composeTestRule.onNodeWithText("Deskripsi")
            .performTextInput("Espresso with vanilla and caramel")

        composeTestRule.onNodeWithText("Caramel Macchiato").assertExists()
        composeTestRule.onNodeWithText("35000").assertExists()
        composeTestRule.onNodeWithText("Espresso with vanilla and caramel").assertExists()
    }
}