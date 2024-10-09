package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import app.cash.turbine.test
import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.CalculateTotalWithDiscountUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.CountSpecificItemsUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetDiscountRulesUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetProductsUseCase
import id.slava.nt.cabifymobilechallengeapp.presentation.resource.ResourceProvider
import id.slava.nt.cabifymobilechallengeapp.util.productBulkDiscount
import id.slava.nt.cabifymobilechallengeapp.util.productBuyXGetYFreeDiscount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlinx.coroutines.delay

@ExperimentalCoroutinesApi
class ProductListViewModelTest {

    private lateinit var viewModel: ProductListViewModel
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getDiscountRulesUseCase: GetDiscountRulesUseCase
    private lateinit var calculateTotalWithDiscountUseCase: CalculateTotalWithDiscountUseCase
    private lateinit var countSpecificItemsUseCase: CountSpecificItemsUseCase
    private lateinit var resourceProvider: ResourceProvider

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var discountConfig: DiscountConfig
    private lateinit var products: List<Product>

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getProductsUseCase = mock()
        getDiscountRulesUseCase = mock()
        calculateTotalWithDiscountUseCase = mock()
        countSpecificItemsUseCase = mock()
        resourceProvider = mock()

        // Initialize shared variables
        discountConfig = DiscountConfig(
            mapOf(
                "VOUCHER" to productBuyXGetYFreeDiscount(),
                "TSHIRT" to productBulkDiscount()
            )
        )

        products = listOf(
            Product(1, "VOUCHER", "Cabify Voucher", 5.0),
            Product(2, "TSHIRT", "Cabify T-Shirt", 20.0),
            Product(3, "MUG", "Cabify Coffee Mug", 7.5)
        )

    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts emits loading and then success state`() = runTest {

        whenever(getProductsUseCase()).thenReturn(flow {
            emit(Resource.Loading())
            emit(Resource.Success(products))
        })
        whenever(getDiscountRulesUseCase()).thenReturn(flow {
            emit(Resource.Loading())

            emit(Resource.Success(discountConfig))
        })


        // Act
        viewModel = ProductListViewModel(
            getProductsUseCase,
            getDiscountRulesUseCase,
            calculateTotalWithDiscountUseCase,
            countSpecificItemsUseCase,
            resourceProvider
        )

        // Assert
        viewModel.products.test {
            assertEquals(ProductListState(), awaitItem()) // Initial state
            assertEquals(ProductListState(isLoading = true), awaitItem()) // Loading state
            assertEquals(ProductListState(isLoading = false, products = products), awaitItem()) // Success state
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadProducts emits loading and then error state`() = runTest {
        // Arrange
        val errorMessage = "An error occurred"
        whenever(getProductsUseCase()).thenReturn(flow {
            emit(Resource.Loading())
            delay(100) // Simulate delay to ensure state transition
            emit(Resource.Error(errorMessage))
        })
        whenever(getDiscountRulesUseCase()).thenReturn(flow {
            emit(Resource.Loading())
            delay(100) // Simulate delay to ensure state transition
            emit(Resource.Error(errorMessage))
        })

        // Act
        viewModel = ProductListViewModel(getProductsUseCase, getDiscountRulesUseCase, calculateTotalWithDiscountUseCase, countSpecificItemsUseCase, resourceProvider)

        // Assert
        viewModel.products.test {
            assertEquals(ProductListState(), awaitItem()) // Initial state
            assertEquals(ProductListState(isLoading = true), awaitItem()) // Loading state
            assertEquals(ProductListState(isLoading = false, error = errorMessage), awaitItem()) // Error state
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addCartProduct adds product to cart and updates state`() = runTest {
        // Arrange
        val product = products[0]

        whenever(getProductsUseCase()).thenReturn(flow {
        })
        whenever(getDiscountRulesUseCase()).thenReturn(flow {
        })

        viewModel = ProductListViewModel(getProductsUseCase, getDiscountRulesUseCase, calculateTotalWithDiscountUseCase, countSpecificItemsUseCase, resourceProvider)


        // Act
        viewModel.addCartProduct(product)

        // Assert
        viewModel.cartProducts.test {
            assertEquals(listOf(product), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(1, viewModel.countSpecificItems()["VOUCHER"])
    }

    @Test
    fun `removeCartProduct removes product from cart and updates state`() = runTest {
        // Arrange
        val product = products[0]
        whenever(getProductsUseCase()).thenReturn(flow {
        })
        whenever(getDiscountRulesUseCase()).thenReturn(flow {
        })
        viewModel = ProductListViewModel(getProductsUseCase, getDiscountRulesUseCase, calculateTotalWithDiscountUseCase, countSpecificItemsUseCase, resourceProvider)
        viewModel.addCartProduct(product)

        // Act
        viewModel.removeCartProduct(product)

        // Assert
        viewModel.cartProducts.test {
            assertEquals(emptyList<Product>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(0, viewModel.countSpecificItems()["VOUCHER"])
    }

}
