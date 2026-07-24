package com.example.cafeapp.feature.admin.menu.ui

import android.app.Application
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: OrderRepository
    private lateinit var viewModel: ManageMenuViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        repository = mockk()
        viewModel = ManageMenuViewModel(application, repository) // ✅
    }

    @Test
    fun loadMenu_shouldUpdateMenuState() = runTest {
        val menu1 = mockk<MenuEntity>()
        val menu2 = mockk<MenuEntity>()
        val fakeMenus = listOf(menu1, menu2)

        coEvery { repository.syncMenu() } returns Unit
        every { repository.getMenuStream() } returns flowOf(fakeMenus)

        viewModel.loadMenu()

        assertEquals(fakeMenus, viewModel.menuState.value)
        coVerify(exactly = 1) { repository.syncMenu() }
        verify(exactly = 1) { repository.getMenuStream() }
    }
}