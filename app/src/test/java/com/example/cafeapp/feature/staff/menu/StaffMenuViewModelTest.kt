package com.example.cafeapp.feature.staff.menu

import android.app.Application
import com.example.cafeapp.MainDispatcherRule
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
class StaffMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: OrderRepository
    private lateinit var viewModel: StaffMenuViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        repository = mockk()

        viewModel = StaffMenuViewModel(application)
        viewModel.repository = repository
    }

    @Test
    fun syncMenuWithServer_shouldSyncMenu() = runTest {

        coEvery {
            repository.syncMenu()
        } returns Unit

        viewModel.syncMenuWithServer()

        coVerify(exactly = 1) {
            repository.syncMenu()
        }
    }
}