package id.slava.nt.cabifymobilechallengeapp.domain.repository

import id.slava.nt.cabifymobilechallengeapp.common.Resource
import id.slava.nt.cabifymobilechallengeapp.data.remote.dt_object.DiscountConfig
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Interface `ProductRepository` defines the data operations related to products that can be performed by the domain layer.
 * This interface is situated in the domain layer of the application but implemented in the data layer, promoting a
 * clean separation of concerns as prescribed by clean architecture principles.
 *
 * Responsibilities:
 * - `getProducts()`: This method provides a way to retrieve a list of products encapsulated within a `Resource` wrapper.
 *   The `Resource` class is used to handle the state of data loading, including success, error, and loading states.
 *
 * Why Use This Interface?
 * 1. **Abstraction**: By defining `ProductRepository` in the domain layer, we abstract away the details of the data source
 *    from the domain layer. The domain layer, which contains business logic, should not be concerned with how data is
 *    fetched or stored. It should only communicate with data sources through interfaces.
 *
 * 2. **Decoupling**: This setup decouples the domain logic from the data fetching mechanisms. Changes in the data layer
 *    implementation (e.g., switching from a network to a database-driven approach) do not affect the domain layer as long
 *    as the interface contract is satisfied.
 *
 * 3. **Testability**: With an interface separating the domain from data fetching implementation, it becomes easier to test
 *    the domain logic by mocking the data layer. This helps in writing unit tests that are not dependent on external factors
 *    like network availability or database state.
 *
 * 4. **Flexibility and Scalability**: The use of an interface allows the data layer to evolve independently of the domain
 *    layer. For example, if the application needs to fetch data from multiple sources or implement sophisticated caching
 *    strategies, these changes can be made without impacting the domain layer logic.
 *
 * Implementation Notes:
 * - The implementation of this interface (`ProductRepositoryImpl`) is located in the data layer, where specific data handling
 *   strategies (like API calls, caching, or database access) are defined.
 * - The interface should be injected into the domain layer components (like Use Cases) using dependency injection, enhancing
 *   modularity and ease of testing.
 *
 * This interface is pivotal for ensuring that the application's architecture remains robust, testable, and maintainable, adhering
 * to the principles of clean architecture.
 */
interface ProductRepository {

    suspend fun getProducts(): Flow<Resource<List<Product>>>

    suspend fun getDiscountRules(): Flow<Resource<DiscountConfig>>

}