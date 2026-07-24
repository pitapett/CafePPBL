package com.example.cafeapp.feature.staff.cart

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckoutBottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkoutBottomBar_displaysTotalAndTriggersCheckout() {
        var checkoutTriggered = false

        composeTestRule.setContent {
            CheckoutBottomBar(
                total = 75000.0,
                isCheckingOut = false,
                onCheckoutClicked = { checkoutTriggered = true }
            )
        }

        // Pastikan total harga muncul
        composeTestRule.onNodeWithText("75000", substring = true).assertExists()

        // Klik tombol confirm checkout
        composeTestRule.onNodeWithText("Confirm Checkout").performClick()

        // Verifikasi callback terpanggil
        assertTrue(checkoutTriggered)
    }

    @Test
    fun checkoutBottomBar_loadingState_disablesButton() {
        var checkoutTriggered = false

        composeTestRule.setContent {
            CheckoutBottomBar(
                total = 75000.0,
                isCheckingOut = true, // Status loading aktif
                onCheckoutClicked = { checkoutTriggered = true }
            )
        }

        // Verifikasi bahwa tombol dalam keadaan Disabled saat loading
        composeTestRule.onNode(hasClickAction())
            .assertIsNotEnabled()

        // Callback pastinya tidak pernah dipanggil
        assertFalse(checkoutTriggered)
    }
}