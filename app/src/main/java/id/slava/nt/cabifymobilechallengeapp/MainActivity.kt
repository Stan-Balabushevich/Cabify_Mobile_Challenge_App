package id.slava.nt.cabifymobilechallengeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.ProductListScreen
import id.slava.nt.cabifymobilechallengeapp.ui.theme.CabifyMobileChallengeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CabifyMobileChallengeAppTheme {
               ProductListScreen()
            }
        }
    }
}