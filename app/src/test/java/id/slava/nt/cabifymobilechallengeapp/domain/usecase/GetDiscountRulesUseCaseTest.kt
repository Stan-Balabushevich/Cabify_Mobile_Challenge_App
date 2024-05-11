package id.slava.nt.cabifymobilechallengeapp.domain.usecase

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.repository.FakeProductRepository
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class GetDiscountRulesUseCaseTest {

    private lateinit var productRepository: FakeProductRepository
    private lateinit var getDiscountRulesUseCase: GetDiscountRulesUseCase



    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        productRepository = FakeProductRepository()
        getDiscountRulesUseCase = GetDiscountRulesUseCase(productRepository)
    }

    @Test
    fun `test invoking use case with fake repository`() = runTest {

        val testResult = productRepository.discountConfig

        val results = getDiscountRulesUseCase().take(1).toList()

        // Assertions
        Assertions.assertTrue(results[0] is Resource.Success)
        Assertions.assertTrue(results[0].data == testResult)

    }

    @Test
    fun `test invoking use case with fake repository returns error`() = runTest {

        productRepository.shouldReturnError = true

        val results = getDiscountRulesUseCase().take(1).toList()

        // Assert
        Assertions.assertTrue(results[0] is Resource.Error, "First result should be an error")
        Assertions.assertEquals("Test error", (results[0] as Resource.Error).message, "Error message should match expected")
    }

}
