package com.example.cafeapp.feature.admin.menu.ui

import com.example.cafeapp.MainDispatcherRule
import com.example.cafeapp.data.local.entity.MenuEntity
import com.example.cafeapp.data.repository.OrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OrderRepository
    private lateinit var viewModel: ManageMenuViewModel

    @Before
    fun setup() {
        repository = mockk()
        viewModel = ManageMenuViewModel(repository)
    }

    @Test
    fun loadMenu_shouldUpdateMenuState() = runTest {

        // Arrange
        val menu1 = mockk<MenuEntity>()
        val menu2 = mockk<MenuEntity>()
        val fakeMenus = listOf(menu1, menu2)

        coEvery {
            repository.syncMenu()
        } returns Unit

        every {
            repository.getMenuStream()
        } returns flowOf(fakeMenus)

        // Act
        viewModel.loadMenu()

        // Assert
        Assert.assertEquals(fakeMenus, viewModel.menuState.value)

        coVerify(exactly = 1) {
            repository.syncMenu()
        }

        verify(exactly = 1) {
            repository.getMenuStream()
        }
    }
}