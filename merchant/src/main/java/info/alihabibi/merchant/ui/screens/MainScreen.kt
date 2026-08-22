package info.alihabibi.merchant.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .width(width = 160.dp)
                    .padding(all = 8.dp),
                value = viewModel.requestId,
                onValueChange = { viewModel.changeRequestId(it) },
                label = { Text("Request Id") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            OutlinedTextField(
                modifier = Modifier
                    .width(width = 160.dp)
                    .padding(all = 8.dp),
                value = viewModel.amount,
                onValueChange = { viewModel.changeAmount(it) },
                label = { Text("Amount") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .width(width = 160.dp)
                    .padding(all = 8.dp),
                value = viewModel.terminalId,
                onValueChange = { viewModel.changeTerminalId(it) },
                label = { Text("Terminal Id") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            OutlinedTextField(
                modifier = Modifier
                    .width(width = 160.dp)
                    .padding(all = 8.dp),
                value = viewModel.traceNumber,
                onValueChange = { viewModel.changeTraceNumber(it) },
                label = { Text("Trace Number") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Keep Service Alive? ")

            Checkbox(
                modifier = Modifier.padding(horizontal = 6.dp),
                checked = viewModel.keepServiceAlive,
                onCheckedChange = { viewModel.changeKeepServiceAlive(it) }
            )

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            OutlinedButton(
                modifier = Modifier
                    .width(width = 160.dp)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                onClick = { viewModel.getTransactionStatus() },
                border = BorderStroke(width = 1.2.dp, color = Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) { Text("Get Transaction Status") }

            OutlinedButton(
                modifier = Modifier
                    .width(width = 160.dp)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                onClick = { viewModel.startTransaction() },
                border = BorderStroke(width = 1.2.dp, color = Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) { Text("Start New Transaction") }

        }

        HorizontalDivider(
            thickness = 1.7.dp,
            color = Color.Black
        )

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .weight(weight = 1f)
                .padding(vertical = 12.dp),
            colors = CardDefaults.elevatedCardColors(contentColor = Color.White)
        ) {

            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    (fadeIn() + slideInVertically { it / 2 }) togetherWith
                            (fadeOut() + slideOutVertically { -it / 2 })
                },
            ) { state ->

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    text = when (state) {
                        is MainScreenUiState.Idle -> "No Transaction In Progress"
                        is MainScreenUiState.InProgress -> state.status
                        is MainScreenUiState.Completed -> "SUCCESS ✅"
                        is MainScreenUiState.Failed -> "FAILED ❌"
                        is MainScreenUiState.Error -> "Error Happened"
                    },
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

            }

            val transactionResult = when (val state = uiState) {
                is MainScreenUiState.Failed -> state.result
                is MainScreenUiState.Completed -> state.result
                else -> null
            }

            AnimatedVisibility(
                visible = transactionResult != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                transactionResult?.let { result ->
                    TransactionData(
                        requestId = result.requestId,
                        status = result.status,
                        responseCode = result.responseCode.orEmpty(),
                        rrn = result.rrn.orEmpty(),
                        message = result.message.orEmpty(),
                        durationMs = result.durationMs
                    )
                }

            }

            AnimatedVisibility(
                visible = viewModel.transactionStatus != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                viewModel.transactionStatus?.let {
                    TransactionData(
                        requestId = viewModel.transactionStatus?.requestId.orEmpty(),
                        status = viewModel.transactionStatus?.status.orEmpty(),
                        responseCode = viewModel.transactionStatus?.responseCode.orEmpty(),
                        rrn = viewModel.transactionStatus?.rrn.orEmpty(),
                        message = viewModel.transactionStatus?.message.orEmpty(),
                        durationMs = viewModel.transactionStatus?.durationMs ?: 0L
                    )
                }

            }

        }

    }

}

@Composable
fun TransactionData(
    requestId: String,
    status: String,
    responseCode: String,
    rrn: String,
    message: String,
    durationMs: Long
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 20.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Request Id: ")

            Spacer(modifier = Modifier.width(10.dp))

            Text(requestId)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Transaction Status: ")

            Spacer(modifier = Modifier.width(10.dp))

            Text(status)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Response Code: ")

            Spacer(modifier = Modifier.width(10.dp))

            Text(responseCode)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("RRN: ")

            Spacer(modifier = Modifier.width(10.dp))

            Text(rrn)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Message: ")

            Spacer(modifier = Modifier.width(10.dp))

            Text(message)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text("Duration Ms: ")

            Spacer(modifier = Modifier.width(10.dp))

            Text(durationMs.toString())

        }

    }

}