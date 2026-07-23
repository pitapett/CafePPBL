package com.example.cafeapp.feature.admin.menu.ui

import com.example.cafeapp.MainDispatcherRule
import com.example.cafeapp.data.remote.CafeApiService
import com.example.cafeapp.data.remote.dto.MenuResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var api: CafeApiService
    private lateinit var viewModel: MenuViewModel

    @Before
    fun setup() {
        api = mockk()
        viewModel = MenuViewModel(api)
    }

    @Test
    fun getMenuById_whenApiFailed_shouldSetError() = runTest {

        // Arrange
        coEvery {
            api.getMenuById("menu-001")
        } returns Response.error(
            404,
            "Menu not found"
                .toResponseBody("text/plain".toMediaType())
        )

        // Act
        viewModel.getMenuById("menu-001")

        // Assert
        assertNull(viewModel.menu.value)
        assertEquals(
            true,
            viewModel.error.value?.contains("404")
        )
        assertFalse(viewModel.isLoading.value)
    }
}