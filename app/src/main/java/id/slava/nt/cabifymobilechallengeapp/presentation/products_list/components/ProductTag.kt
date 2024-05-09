package id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.slava.nt.cabifymobilechallengeapp.R
import id.slava.nt.cabifymobilechallengeapp.domain.model.Product

@Composable
fun ProductTag(
    product: Product,
    modifier: Modifier = Modifier,
    onProductTagClicked: (Product) -> Unit
) {
    Box(
        modifier = modifier
            .then(
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(100.dp)
                )
            )
            .padding(10.dp)
            .clickable { onProductTagClicked(product) }
    ) {
        Row {
            Text(
                text = product.code,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Remove Product",
                tint = MaterialTheme.colorScheme.primary
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductTagPreview() {
    ProductTag(product = Product(id = 2, code = "123456", name = "test", price = 123.0),
        onProductTagClicked = {})
}