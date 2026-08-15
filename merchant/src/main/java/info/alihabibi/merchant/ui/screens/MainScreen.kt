package info.alihabibi.merchant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen() {

    val viewModel: MainScreenViewModel = hiltViewModel<MainScreenViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = viewModel.requestId,
            onValueChange = { viewModel.changeRequestId(it) },
            label = { Text("Request Id") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.amount,
            onValueChange = { viewModel.changeAmount(it) },
            label = { Text("Amount") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.terminalId,
            onValueChange = { viewModel.changeTerminalId(it) },
            label = { Text("Terminal Id") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.traceNumber,
            onValueChange = { viewModel.changeTraceNumber(it) },
            label = { Text("Trace Number") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Keep Service Alive? : ")

            Checkbox(
                modifier = Modifier.padding(12.dp),
                checked = viewModel.keepServiceAlive,
                onCheckedChange = { viewModel.changeKeepServiceAlive(it) }
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.startTransaction() }) { Text("Start Transaction") }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { viewModel.getTransactionStatus() }) { Text("Get Transaction") }

        Spacer(modifier = Modifier.height(10.dp))

        if (viewModel.transactionStatus.isNotEmpty()) {
            Text(
                text = viewModel.transactionStatus,
                color = Color.Green,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

        when (val state = uiState) {
            is MainScreenUiState.Idle -> Text("No Transaction", fontSize = 22.sp)
            is MainScreenUiState.InProgress -> Text("In Progress: ${state.status}", fontSize = 22.sp)
            is MainScreenUiState.Completed -> Text("Success: ${state.result}", fontSize = 22.sp)
            is MainScreenUiState.Failed -> Text("Failed: ${state.result}", fontSize = 22.sp)
            is MainScreenUiState.Error -> Text("Error: ${state.message}", fontSize = 22.sp)
        }

    }


}