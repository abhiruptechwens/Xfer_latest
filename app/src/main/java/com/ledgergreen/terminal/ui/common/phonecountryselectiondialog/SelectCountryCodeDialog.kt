package com.ledgergreen.terminal.ui.common.phonecountryselectiondialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.phone.CountryCodes
import com.ledgergreen.terminal.data.model.phone.CountryPhoneCode
import com.ledgergreen.terminal.ui.common.reportActivity
import com.ledgergreen.terminal.ui.common.titleCase
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme


@Composable
fun SelectCountryDialog(
    onClosed: (CountryPhoneCode?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SelectCountryCodeViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    SelectCountryDialog(
        onClosed,
        state,
        modifier = modifier,
    )
}

@Composable
fun SelectCountryDialog(
    onClosed: (CountryPhoneCode?) -> Unit,
    state: SelectCountryCodeState,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = { onClosed(null) }) {
        Surface(modifier.reportActivity()) {
            Column(
                Modifier
                    .height(500.dp)
                    .fillMaxWidth()
                    .padding(all = 24.dp),
            ) {
                Text(
                    text = stringResource(R.string.select_phone_country).titleCase(),
                    style = MaterialTheme.typography.h6,
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    items(state.countryCodes) { countryCode ->
                        CountryCodeItem(countryCode = countryCode, onSelected = onClosed)
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryCodeItem(
    countryCode: CountryPhoneCode,
    onSelected: (CountryPhoneCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        Modifier.clickable(
            onClick = { onSelected(countryCode) },
        ),
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = countryCode.countryName,
                    style = MaterialTheme.typography.body2,
                )
                Spacer(modifier.width(16.dp))
                Text(
                    text = "+" + countryCode.phoneCode.toString(),
                    style = MaterialTheme.typography.body2,
                )
            }
            Divider()
        }
    }
}

@Preview
@Composable
fun SupportDialogLoadingPreview() {
    LedgerGreenTheme {
        SelectCountryDialog(
            onClosed = {},
            state = SelectCountryCodeState(CountryCodes.all),
        )
    }
}
