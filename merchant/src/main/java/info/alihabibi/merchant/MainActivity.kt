package info.alihabibi.merchant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import info.alihabibi.merchant.ui.theme.PaymentCoreTheme
import info.alihabibi.merchant.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentCoreTheme {
                MainScreen()
            }
        }
    }
}