package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components.ProductItemCard
import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components.ProductTag
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductListScreen(viewModel: ProductListViewModel = koinViewModel()) {
    val state = viewModel.products.value
    val productsToBuy = viewModel.productsToBuy.value
    val totalPrice = productsToBuy.sumOf { it.price } // Ensure there's a 'price' field in your Product model

    Scaffold(
        floatingActionButton = {
//            FloatingActionButton(onClick = { /* Your action here */ }) {
//                Icon(Icons.Default.Add, contentDescription = "Add")
//            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 50.dp)) {
            // Displaying the product list
            LazyColumn(modifier = Modifier
                .weight(1f)
                .padding(6.dp)) {
                items(items = state.products) { product ->
                    ProductItemCard(product = product, onProductSelected = {
                        viewModel.addProductToBuy(product)
                    })
                }
            }

            // Display total price
            Text(
                "Products selected:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp).align(Alignment.Start)
            )

            LazyRow(modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(6.dp)) {
                    items(items = productsToBuy) { product ->
                        ProductTag(tag = product.code, modifier = Modifier.padding(6.dp))
                    }
                }

                // Display total price
                Text(
                    "Total: $${totalPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp).align(Alignment.Start)
                )

            // Handle loading and error states
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            if (state.error.isNotBlank()) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }
        }
    }
}
