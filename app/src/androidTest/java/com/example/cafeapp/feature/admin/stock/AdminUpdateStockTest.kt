package com.example.cafeapp.feature.admin.stock

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cafeapp.data.remote.dto.StockResponse
import org.junit.Assert.assertEquals
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
        var savedUnit = ""

        val existingStock = StockResponse(
            id = "1",
            ingredientName = "Milk", // Sesuaikan nama properti di StockResponse DTO kamu
            amount = 10,
            unit = "Bungkus"
        )

        composeTestRule.setContent {
            StockDialog(
                item = existingStock,
                onDismiss = {},
                // ✅ Update lambda dengan 3 parameter (name, amount, unit)
                onSave = { name, amount, unit ->
                    savedName = name
                    savedAmount = amount
                    savedUnit = unit
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

        // Verifikasi hasil dengan JUnit assertEquals
        assertEquals("Milk", savedName)
        assertEquals(50, savedAmount)
        assertEquals("Bungkus", savedUnit)
    }
}