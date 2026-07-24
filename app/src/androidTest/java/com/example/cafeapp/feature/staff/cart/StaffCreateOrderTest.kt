package com.example.cafeapp.feature.staff.cart

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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

        // Verify the total is formatted and displayed correctly
        composeTestRule.onNodeWithText("75000", substring = true).assertExists()

        // Click the confirm buttonx
        composeTestRule.onNodeWithText("Confirm Checkout").performClick()

        // Assert the callback was fired
        assert(checkoutTriggered)
    }

    @Test
    fun checkoutBottomBar_loadingState_disablesButton() {
        var checkoutTriggered = false

        composeTestRule.setContent {
            CheckoutBottomBar(
                total = 75000.0,
                isCheckingOut = true, // Force loading state
                onCheckoutClicked = { checkoutTriggered = true }
            )
        }

        // Try to click the button while it is loading.
        // Because the text "Confirm Checkout" is hidden during loading, we target the button by its click action property.
        composeTestRule.onNode(hasClickAction()).performClick()

        // Assert the callback was blocked because the button should be disabled
        assert(!checkoutTriggered)
    }
}