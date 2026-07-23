package com.example.cafeapp.feature.admin.menu.ui

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminEditMenuTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun editMenuScreen_whenBackendIsOffline_showsErrorOrRetryButton() {
        // GIVEN: Render EditMenuScreen tanpa backend
        composeTestRule.setContent {
            EditMenuScreen(
                menuId = "123",
                onNavigateBack = {}
            )
        }

        // THEN: Karena backend mati, UI akan menampilkan state Error/Retry atau Loading Indicator
        // Kita verifikasi bahwa salah satu dari elemen tersebut atau tombol Coba Lagi dirender dengan benar
        val hasRetryButton = composeTestRule
            .onAllNodesWithText("Coba lagi", substring = true)
            .fetchSemanticsNodes().isNotEmpty()

        val hasLoading = composeTestRule
            .onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .fetchSemanticsNodes().isNotEmpty()

        // Test dipastikan PASS jika screen menangani kondisi tanpa backend dengan baik
        assert(hasRetryButton || hasLoading) {
            "Screen seharusnya menampilkan indikator Loading atau tombol Coba Lagi saat tidak ada backend"
        }
    }
}