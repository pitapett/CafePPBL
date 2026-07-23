package com.example.cafeapp.feature.admin.stock

import android.app.Application
import com.example.cafeapp.MainDispatcherRule
import com.example.cafeapp.data.repository.StockRepository
import com.example.cafeapp.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ManageStockViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: StockRepository
    private lateinit var viewModel: ManageStockViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        repository = mockk()

        viewModel = ManageStockViewModel(
            application,
            repository
        )
    }

    @Test
    fun fetchStock_whenApiFailed_shouldSetError() = runTest {

        // Arrange
        coEvery {
            repository.getAllStock()
        } returns Response.error(
            500,
            "Internal Server Error"
                .toResponseBody("text/plain".toMediaType())
        )

        // Act
        viewModel.fetchStock()

        // Assert
        assertTrue(
            viewModel.stockList.value is Resource.Error
        )

        val result = viewModel.stockList.value as Resource.Error

        assertEquals(
            "Failed to load stock",
            result.message
        )
    }
}