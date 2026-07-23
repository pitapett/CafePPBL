package com.example.cafeapp.feature.admin.menu.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminManageMenuTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun manageMenuScreen_whenNoBackend_displaysEmptyStateAndFab() {
        // GIVEN: Render ManageMenuScreen
        composeTestRule.setContent {
            ManageMenuScreen(
                onNavigateToCreate = {},
                onNavigateToEdit = {}
            )
        }

        // 💡 PAKAI TIMEOUT 15 Detik (memberi waktu OkHttp koneksi timeout)
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("No menu available", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // THEN: Verifikasi teks "No menu available" & FAB
        composeTestRule.onNodeWithText("No menu available", substring = true).assertExists()
        composeTestRule.onNodeWithText("+").assertExists()
    }

    @Test
    fun manageMenuScreen_fabClick_triggersNavigateToCreate() {
        var createNavigated = false

        // GIVEN: Render ManageMenuScreen
        composeTestRule.setContent {
            ManageMenuScreen(
                onNavigateToCreate = { createNavigated = true },
                onNavigateToEdit = {}
            )
        }

        // WHEN: Klik tombol Floating Action Button ("+")
        composeTestRule.onNodeWithText("+").performClick()

        // THEN: Memastikan callback navigasi ke halaman Create terpanggil
        assert(createNavigated) {
            "Tombol + harus menembak callback onNavigateToCreate"
        }
    }
}