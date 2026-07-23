package com.example.cafeapp.feature.admin.stock

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cafeapp.data.remote.dto.StockResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StockDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockDialog_editMode_updatesDataAndTriggersSave() {
        var savedName = ""
        var savedAmount = 0

        // Create a dummy item to trigger edit mode
        val existingStock = StockResponse(
            id = "1",
            ingredient_name = "Milk",
            amount = 10
        )

        composeTestRule.setContent {
            StockDialog(
                item = existingStock,
                onDismiss = {},
                onSave = { name, amount ->
                    savedName = name
                    savedAmount = amount
                },
                onDelete = {}
            )
        }

        // Verify it is in Edit mode
        composeTestRule.onNodeWithText("Edit Stock").assertExists()

        // The fields should be prepopulated with the existing data
        composeTestRule.onNodeWithText("Milk").assertExists()
        composeTestRule.onNodeWithText("10").assertExists()

        // Clear the old amount and enter a new one
        composeTestRule.onNodeWithText("10").performTextClearance()
        composeTestRule.onNodeWithText("Quantity").performTextInput("50")

        // Trigger save
        composeTestRule.onNodeWithText("Save").performClick()

        // Assert the callback received the updated payload
        assert(savedName == "Milk")
        assert(savedAmount == 50)
    }
}