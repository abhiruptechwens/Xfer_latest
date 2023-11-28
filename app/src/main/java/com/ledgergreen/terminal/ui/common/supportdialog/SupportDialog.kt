package com.ledgergreen.terminal.ui.common.supportdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.domain.support.SupportInfo
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme


@Composable
fun SupportDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SupportViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    SupportDialog(
        state = state,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Composable
fun SupportDialog(
    state: SupportDialogState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "info",
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.support_information).titleCase(),
                    style = MaterialTheme.typography.h6,
                )
            }
        },
        text = {
            Column(
                modifier.fillMaxWidth(),
            ) {
                val infoTextStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                )

                val supportInfo = state.supportInfo

                Text(
                    stringResource(id = R.string.support_phone_number),
                    style = infoTextStyle,
                )
                Spacer(Modifier.height(8.dp))

//                Text(
//                    text = stringResource(
//                        id = R.string.support_user_id_info,
//                        supportInfo.username ?: "–",
//                        supportInfo.userId?.toString() ?: "–",
//                    ),
//                    style = infoTextStyle,
//                )
//                Spacer(Modifier.height(8.dp))
//                Text(
//                    text = stringResource(
//                        id = R.string.support_store_id_info,
//                        supportInfo.storeName ?: "–",
//                        supportInfo.storeId?.toString() ?: "–",
//                    ),
//                    style = infoTextStyle,
//                )

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                Text(
                    text = supportInfo.versionInfo,
                    modifier.align(Alignment.CenterHorizontally),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(R.string.close).uppercase(),
                )
            }
        },
    )
}

@Preview
@Composable
fun SupportDialogInitialPreview() {
    LedgerGreenTheme {
        SupportDialog(
            state = SupportDialogState.initial,
            onDismiss = {},
        )
    }
}

@Preview
@Composable
fun SupportDialogWithUserPreview() {
    LedgerGreenTheme {
        SupportDialog(
            state = SupportDialogState(
                supportInfo =
                SupportInfo(
                    versionInfo = "Version 1.0.0 code 1",
                    userId = 1L,
                    username = "Tony Stark",
                    storeId = 34L,
                    storeName = "Grass",
                ),
            ),
            onDismiss = {},
        )
    }
}

@Preview
@Composable
fun SupportDialogWithoutUserPreview() {
    LedgerGreenTheme {
        SupportDialog(
            state = SupportDialogState(
                SupportInfo(
                    versionInfo = "Version 1.0.0 code 1",
                    storeId = null,
                    storeName = null,
                    userId = null,
                    username = null,
                ),
            ),
            onDismiss = {},
        )
    }
}
