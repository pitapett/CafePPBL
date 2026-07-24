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

        viewModel = CartDetailViewModel(application)

        viewModel.repository = repository
    }

    @Test
    fun updateQuantity_increase_shouldUpdateCartItem() = runTest {

        // Arrange
        val item = DraftCartEntity(
            menuId = "1",
            name = "Americano",
            price = 20000.0,
            quantity = 1,
            customization = ""
        )

        coEvery {
            repository.updateCartItem(any())
        } returns Unit

        // Act
        viewModel.updateQuantity(
            item,
            isIncrease = true
        )

        // Assert
        coVerify(timeout = 1000, exactly = 1) {
            repository.updateCartItem(
                match {
                    it.menuId == "1" &&
                            it.quantity == 2
                }
            )
        }
    }

    @Test
    fun updateQuantity_decrease_whenQuantityGreaterThanOne_shouldUpdateCartItem() = runTest {

        // Arrange
        val item = DraftCartEntity(
            menuId = "1",
            name = "Americano",
            price = 20000.0,
            quantity = 1,
            customization = ""
        )

        coEvery {
            repository.updateCartItem(any())
        } returns Unit

        // Act
        viewModel.updateQuantity(
            item,
            isIncrease = false
        )

        // Assert
        coVerify(timeout = 1000, exactly = 1) {
            repository.updateCartItem(
                match {
                    it.menuId == "1" &&
                            it.quantity == 1
                }
            )
        }
    }

    @Test
    fun updateQuantity_decrease_whenQuantityIsOne_shouldDeleteCartItem() = runTest {

        // Arrange
        val item = DraftCartEntity(
            menuId = "1",
            name = "Americano",
            price = 20000.0,
            quantity = 1,
            customization = ""
        )

        coEvery {
            repository.deleteCartItem(any())
        } returns Unit

        // Act
        viewModel.updateQuantity(
            item,
            isIncrease = false
        )

        // Assert
        coVerify(timeout = 1000, exactly = 1) {
            repository.deleteCartItem(item)
        }
    }

    @Test
    fun updateCustomization_shouldUpdateCartItem() = runTest {

        // Arrange
        val item = DraftCartEntity(
            menuId = "1",
            name = "Americano",
            price = 20000.0,
            quantity = 1,
            customization = ""
        )

        coEvery {
            repository.updateCartItem(any())
        } returns Unit

        // Act
        viewModel.updateCustomization(
            item,
            "Less sugar"
        )

        // Assert
        coVerify(timeout = 1000, exactly = 1) {
            repository.updateCartItem(
                match {
                    it.customization == "Less sugar"
                }
            )
        }
    }

    @Test
    fun removeItem_shouldDeleteCartItem() = runTest {

        // Arrange
        val item = DraftCartEntity(
            menuId = "1",
            name = "Americano",
            price = 20000.0,
            quantity = 1,
            customization = ""
        )

        coEvery {
            repository.deleteCartItem(any())
        } returns Unit

        // Act
        viewModel.removeItem(item)

        // Assert
        coVerify(timeout = 1000, exactly = 1) {
            repository.deleteCartItem(item)
        }
    }

    @Test
    fun checkoutCart_shouldProcessCheckout() = runTest {

        // Arrange
        coEvery {
            repository.processCheckout(
                "T1",
                "staff1"
            )
        } returns true

        // Act
        viewModel.checkoutCart(
            "T1",
            "staff1"
        )

        // Assert
        coVerify(exactly = 1) {
            repository.processCheckout(
                "T1",
                "staff1"
            )
        }
    }
}