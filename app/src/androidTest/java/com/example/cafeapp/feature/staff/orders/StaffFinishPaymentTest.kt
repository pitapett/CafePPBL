package com.example.cafeapp.feature.staff.orders

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentMethodDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun paymentMethodDialog_selectMethod_triggersCallback() {
        var selectedMethod = ""

        composeTestRule.setContent {
            PaymentMethodDialog(
                onDismiss = {},
                onMethodSelected = { method -> selectedMethod = method }
            )
        }

        composeTestRule.onNodeWithText("Select Payment Method").assertExists()
        composeTestRule.onNodeWithText("Cash").assertExists()
        composeTestRule.onNodeWithText("QRIS").assertExists()

        composeTestRule.onNodeWithText("QRIS").performClick()

        assert(selectedMethod == "QRIS")
    }
}