package id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product

@Composable
fun ProductItemCard(product: Product,
                    onProductSelected: (Product) -> Unit) {
    Card (
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
    ){

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onProductSelected(product) }
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${product.code}. ${product.name} (${product.price})",
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}