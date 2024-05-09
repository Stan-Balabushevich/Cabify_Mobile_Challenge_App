package id.slava.nt.cabifymobilechallengeapp.presentation.products_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import id.slava.nt.cabifymobilechallengeapp.R
import id.slava.nt.cabifymobilechallengeapp.common.MUG
import id.slava.nt.cabifymobilechallengeapp.common.TSHIRT
import id.slava.nt.cabifymobilechallengeapp.common.VOUCHER

@Composable
fun DiscountDialog(
    counts: Map<String, Int>,
    total: Double,
    onDismissDialog: () -> Unit,
    onAcceptButton: () -> Unit,
){
    AlertDialog(
        onDismissRequest = { onDismissDialog() },
        confirmButton = {
            Button(onClick = { onAcceptButton() }) {
                Text(text = stringResource(id = R.string.action_pay))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissDialog() }) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        },
        title = { Text(text = stringResource(id = R.string.title_total_products)) },
        text = {
            Column {
                Text(stringResource(id = R.string.label_vouchers, counts[VOUCHER] ?: 0))
                Text(stringResource(id = R.string.label_tshirts, counts[TSHIRT] ?: 0))
                Text(stringResource(id = R.string.label_mugs, counts[MUG] ?: 0))
                Text(stringResource(id = R.string.price_with_discount, total.toString()))
            }
        }
    )
}
