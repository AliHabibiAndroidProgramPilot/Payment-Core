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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen() {

    val viewModel: MainScreenViewModel = viewModel<MainScreenViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = uiState.requestId,
            onValueChange = { viewModel.changeValues(UiEvent.OnChangeRequestId(it)) },
            label = { Text("Request Id") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.amount,
            onValueChange = { viewModel.changeValues(UiEvent.OnChangeAmount(it)) },
            label = { Text("Amount") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.terminalId,
            onValueChange = { viewModel.changeValues(UiEvent.OnChangeTerminalId(it)) },
            label = { Text("Terminal Id") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.traceNumber,
            onValueChange = { viewModel.changeValues(UiEvent.OnChangeTraceNumber(it)) },
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
                checked = uiState.keepServiceAlive,
                onCheckedChange = { viewModel.changeValues(UiEvent.OnChangeKeepServiceAlive(it)) }
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.startTransaction() }) { Text("Start Transaction") }

    }


}