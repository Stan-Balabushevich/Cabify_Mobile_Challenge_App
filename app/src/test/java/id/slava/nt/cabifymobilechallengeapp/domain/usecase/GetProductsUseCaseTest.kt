package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.repository.FakeProductRepository
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProductsUseCaseTest {
    private lateinit var productRepository: FakeProductRepository
    private lateinit var getProductsUseCase: GetProductsUseCase



    @BeforeEach
    fun setUp() {
        productRepository = FakeProductRepository()
        getProductsUseCase = GetProductsUseCase(productRepository)
    }

    @Test
    fun `test invoking use case with fake repository returns success`() = runTest {
        // Arrange - Setup the repository to return successful data
        productRepository.shouldReturnError = false

        val expectedData = productRepository.productList

        // Act - Invoke the use case and collect results
        val results = getProductsUseCase().take(2).toList()

        // Assert - Verify the states are emitted as expected
        assertTrue(results[0] is Resource.Loading, "First result should be loading")
        assertTrue(results[1] is Resource.Success, "Second result should be success")
        // Optionally, verify the data in Resource.Success
        assertEquals(expectedData, (results[1] as Resource.Success).data, "Data should match expected")
    }

    @Test
    fun `test invoking use case with fake repository returns error`() = runTest {
        // Arrange
        productRepository.shouldReturnError = true

        // Act
        val results = getProductsUseCase().take(2).toList()

        // Assert
        assertTrue(results[0] is Resource.Loading, "First result should be loading")
        assertTrue(results[1] is Resource.Error, "Second result should be an error")
        assertEquals("Test error", (results[1] as Resource.Error).message, "Error message should match expected")
    }


}