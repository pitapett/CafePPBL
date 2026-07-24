package com.example.cafeapp.feature.staff.orders

import android.app.Application
import com.example.cafeapp.MainDispatcherRule
import com.example.cafeapp.data.repository.OrderRepository
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
class ActiveOrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: OrderRepository
    private lateinit var viewModel: ActiveOrdersViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        repository = mockk()

        viewModel = ActiveOrdersViewModel(application)
        viewModel.repository = repository
    }

    @Test
    fun fetchActiveOrders_whenApiFailed_shouldSetError() = runTest {

        // Arrange
        coEvery {
            repository.getProcessOrders()
        } returns Response.error(
            500,
            "Internal Server Error"
                .toResponseBody(
                    "text/plain".toMediaType()
                )
        )

        // Act
        viewModel.fetchActiveOrders()

        // Assert
        assertTrue(
            viewModel.orders.value is Resource.Error
        )

        val result =
            viewModel.orders.value as Resource.Error

        assertEquals(
            "Failed to load orders",
            result.message
        )
    }
}