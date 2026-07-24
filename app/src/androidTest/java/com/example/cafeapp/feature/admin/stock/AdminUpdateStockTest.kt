package com.example.cafeapp.feature.admin.stock

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cafeapp.data.remote.dto.StockResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class StockDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stockDialog_editMode_updatesDataAndTriggersSave() {
        var savedName = ""
        var savedAmount = 0

        val existingStock = StockResponse(id = "1", ingredient_name = "Milk", amount = 10)

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

        composeTestRule.onNodeWithText("Edit Stock").assertExists()

        // Cari berdasarkan label TextField-nya, lalu ganti teksnya
        composeTestRule.onNodeWithText("Quantity")
            .performTextClearance()
        composeTestRule.onNodeWithText("Quantity")
            .performTextInput("50")

        // Trigger save
        composeTestRule.onNodeWithText("Save").performClick()

        // Pakai assertEquals dari JUnit
        assertEquals("Milk", savedName)
        assertEquals(50, savedAmount)
    }
}