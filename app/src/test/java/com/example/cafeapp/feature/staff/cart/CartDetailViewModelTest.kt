package com.example.cafeapp.feature.staff.cart

import android.app.Application
import com.example.cafeapp.MainDispatcherRule
import com.example.cafeapp.data.local.entity.DraftCartEntity
import com.example.cafeapp.data.repository.OrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: OrderRepository
    private lateinit var viewModel: CartDetailViewModel

    @Before
    fun setup() {

        application = mockk(relaxed = true)

        repository = mockk()

        every {
            repository.getLiveCartStream()
        } returns flowOf(emptyList())

        viewModel = CartDetailViewModel(
            application,
            repository
        )
    }

    @Test
    fun removeItem_shouldDeleteItemFromCart() = runTest {

        // Arrange
        val item = mockk<DraftCartEntity>()

        coEvery {
            repository.deleteCartItem(item)
        } returns Unit

        // Act
        viewModel.removeItem(item)

        // Assert
        coVerify(timeout = 1000) {
            repository.deleteCartItem(item)
        }
    }
}