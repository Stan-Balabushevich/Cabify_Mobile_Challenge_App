package id.slava.nt.cabifymobilechallengeapp.presentation.products_list

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.slava.nt.cabifymobilechallengeapp.R
import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components.DiscountDialog
import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components.ProductItemCard
import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components.ProductTag
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductListScreen(viewModel: ProductListViewModel = koinViewModel()) {

    val state = viewModel.products.value
    val cartProducts = viewModel.cartProducts.collectAsState()
    val totalPrice = cartProducts.value.sumOf { it.price}
    val showDiscountDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current


    // Display discount dialog
    if (showDiscountDialog.value) {
        val counts = viewModel.countSpecificItems()
        val total = viewModel.calculateTotal()
        DiscountDialog(counts = counts, total = total,
            onDismissDialog = { showDiscountDialog.value = false },
            onAcceptButton = {
                Toast.makeText(context, context.getString(R.string.payment_made), Toast.LENGTH_SHORT).show()
                viewModel.removeAllCartProducts()
                showDiscountDialog.value = false
            })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDiscountDialog.value = true
            }) {
                Text(text = stringResource(R.string.apply_discount),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp))
            }
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)) {
            Text(
                stringResource(R.string.products_to_select),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )

            // Displaying the product to select list
            LazyColumn(modifier = Modifier
                .weight(1f)
                .padding(6.dp)) {
                items(items = state.products) { product ->
                    ProductItemCard(product = product, onProductSelected = {
                        viewModel.addCartProduct(product)
                    })
                }
            }

            Text(
                text = stringResource(R.string.products_selected, cartProducts.value.size),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )
            // Displaying the cart products
            LazyRow(modifier = Modifier
                .weight(1f, fill = false)
                .padding(6.dp)) {
                    items(items = cartProducts.value) { cartProduct ->
                        ProductTag(product = cartProduct, modifier = Modifier.padding(6.dp), onProductTagClicked = {
                            viewModel.removeCartProduct(cartProduct)
                        })
                    }
                }

                // Display total price
                Text(
                    stringResource(R.string.price_without_discount, totalPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Start)
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
