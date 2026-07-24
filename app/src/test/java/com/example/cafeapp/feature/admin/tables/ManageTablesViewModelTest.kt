package com.example.cafeapp.feature.admin.tables

import android.app.Application
import com.example.cafeapp.MainDispatcherRule
import com.example.cafeapp.data.repository.TableRepository
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
class ManageTablesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var application: Application
    private lateinit var repository: TableRepository
    private lateinit var viewModel: ManageTablesViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        repository = mockk()

        viewModel = ManageTablesViewModel(
            application,
            repository
        )
    }

    @Test
    fun fetchTables_whenApiFailed_shouldSetError() = runTest {

        // Arrange
        coEvery {
            repository.getAllTables()
        } returns Response.error(
            500,
            "Internal Server Error"
                .toResponseBody("text/plain".toMediaType())
        )

        // Act
        viewModel.fetchTables()

        // Assert
        assertTrue(
            viewModel.tables.value is Resource.Error
        )

        val result = viewModel.tables.value as Resource.Error

        assertEquals(
            "Failed to fetch tables",
            result.message
        )
    }
}