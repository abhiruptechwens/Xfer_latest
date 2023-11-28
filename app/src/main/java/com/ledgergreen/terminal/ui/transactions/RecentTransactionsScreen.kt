package com.ledgergreen.terminal.ui.transactions

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ChipDefaults.filterChipColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import com.ledgergreen.terminal.data.network.model.Associate
import com.ledgergreen.terminal.data.network.model.Transaction
import com.ledgergreen.terminal.data.network.model.TransactionStatus
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import com.ledgergreen.terminal.ui.theme.success
import com.ledgergreen.terminal.ui.theme.warning
import com.ledgergreen.terminal.ui.transactions.domain.TransactionsFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun RecentTransactionsScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    viewModel: RecentTransactionsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value

    RecentTransactionsScreen(
        updateSelectedOption = { option ->
            viewModel.updateSelectedOption(option)
        },
        state = state,
        onFilterClick = { state.eventSink(RecentTransactionsEvent.FilterSelected(it)) },
        onSearchChange = { state.eventSink(RecentTransactionsEvent.SearchChanged(it)) },
        onRefresh = { state.eventSink(RecentTransactionsEvent.Refresh) },
        onErrorShown = { state.eventSink(RecentTransactionsEvent.OnErrorShown) },
        appBarConfig = defaultAppBarConfig(),
        modifier = modifier,
        navigateToHome = {
            navigateToHome()
        }
    )
}

@Composable
fun RecentTransactionsScreen(
    navigateToHome: () -> Unit,
    updateSelectedOption: (String) -> Unit,
    state: RecentTransactionsState,
    appBarConfig: DefaultAppBarConfig,
    onFilterClick: (TransactionsFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    state.error?.let {
        ErrorDialog(it, onErrorShown)
    }

    Scaffold(
        modifier = modifier,
        backgroundColor = Color(0xFF06478D),
        topBar = { SwitchAppBar(appBarConfig,navigateToHome) },
    ) { paddingValues ->

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier.clickable { focusManager.clearFocus() }
                .padding(paddingValues),
        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .height(36.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    stringResource(R.string.recent_transactions),
//                    style = MaterialTheme.typography.h6,
//                    color = Color.White,
//                )
//                IconButton(
//                    onClick = onRefresh,
//                    enabled = !state.loading,
//                    modifier = Modifier.size(36.dp),
//                ) {
//                    Icon(
//                        Icons.Default.Refresh, contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp),
//                    )
//                }
//            }

            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf("All") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SearchBar(
                    searchQuery = state.searchQuery,
                    onSearchChanged = onSearchChange,
                    modifier = Modifier
                        .weight(1.6f)
                        .padding(start = 16.dp),
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp, end = 16.dp, top = 8.dp)
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .align(Alignment.CenterVertically)
                        .clickable { expanded = true },
                ) {
//                    val focusManager = LocalFocusManager.current
//                    CompositionLocalProvider(
//                        LocalTextInputService provides null
//                    ) {
//                        TextField(
//                            value = selectedOption.replaceFirstChar { it.uppercase() },
//                            enabled = false,
//                            onValueChange = {
//                                selectedOption = it.lowercase()
//                                focusManager.clearFocus()
//                            },
//                            readOnly = true,
//                            colors = TextFieldDefaults.textFieldColors(
//                                focusedIndicatorColor = Color.Transparent,
//                                unfocusedIndicatorColor = Color.Transparent,
//                                disabledTextColor = Color.Black
//
//                            ),
//                            textStyle = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center),
//                            modifier = Modifier
//                                .align(Alignment.Center)
//                                .clickable {
//                                    expanded = true
//                                    focusManager.clearFocus()
//                                },
//                            trailingIcon = {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.drop_arrow),
//                                    contentDescription = null,
//                                    modifier = Modifier.clickable {
//                                        expanded = !expanded
//                                    }
//                                )
//                            },
//                        )
//                    }
                    Row(modifier = Modifier.height(56.dp)) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = selectedOption,
                            modifier = Modifier
                                .onGloballyPositioned { }
                                .padding(start = 5.dp)
                                .align(Alignment.CenterVertically),
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(modifier = Modifier.align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.drop_arrow),
                            contentDescription = null,
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White),
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = "All"
                                expanded = false
                                updateSelectedOption(selectedOption)
                            },
                        ) {
                            Text("All")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = "Standard"
                                expanded = false
                                updateSelectedOption(selectedOption)
                            },
                        ) {
                            Text("Standard")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedOption = "Contactless"
                                expanded = false
                                updateSelectedOption(selectedOption)
                            },
                        ) {
                            Text("Contactless")
                        }
                    }
                }
            }

//            TransactionFilter(
//                filters = state.filters,
//                onFilterClick = onFilterClick,
//                modifier = Modifier.padding(top = 4.dp),
//            )

            Box(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                if (state.transactions.isEmpty() && !state.loading) {
                    Text(
                        stringResource(R.string.recent_transactions_empty),
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontStyle = FontStyle.Italic, color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                TransactionsList(
                    transactions = state.transactions,
                    modifier = Modifier.fillMaxSize(),
                )

                if (state.loading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = Color.White,
        focusedBorderColor = Color.White, // Set the focused outline color
        unfocusedBorderColor = Color.White, // Set the unfocused outline color
        cursorColor = Color.White,
        unfocusedLabelColor = Color.White,// Set the cursor color
    )
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChanged,
        modifier = modifier,
        singleLine = true,
        label = {
            Text(
                stringResource(R.string.search_by_name),
                modifier = Modifier,
                color = Color(0xFFFFFFFF),
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
        ),
        colors = colors,
        trailingIcon = {
            AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                IconButton(
                    onClick = { onSearchChanged("") },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        },
    )
}

@Composable
fun TransactionFilter(
    filters: ImmutableList<TransactionsFilter>,
    onFilterClick: (TransactionsFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(filters) { filter ->
            when (filter) {
                is TransactionsFilter.CreatedByMeFilter -> SimpleFilterChip(
                    text = stringResource(R.string.recent_transactions_filter_associate_only),
                    selected = filter.enabled,
                    onClick = { onFilterClick(filter) },
                )

                is TransactionsFilter.TransactionStatusFilter -> SimpleFilterChip(
                    text = stringResource(filter.status.displayableResId),
                    selected = filter.enabled,
                    onClick = { onFilterClick(filter) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SimpleFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        colors = filterChipColors(
            selectedBackgroundColor = Color(0xFF61BAF5),
            selectedContentColor = MaterialTheme.colors.onPrimary,
        ),
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        selectedIcon = {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(start = 4.dp),
            )
        },
    ) { Text(text) }
}

@Composable
fun TransactionsList(
    transactions: ImmutableList<Transaction>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(transactions, key = { it.id }) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = 3.dp,
        backgroundColor = Color(0xFF06478D),
        border = BorderStroke(1.dp, Color(0xE24391B1)),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = transaction.customerName,
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = transaction.amount,
                    fontSize = 20.sp,
                    color = transaction.status.contentColor,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Text(
                    text = stringResource(transaction.status.displayableResId),
                    color = transaction.status.contentColor, // Set text color, you can customize this
                    modifier = Modifier
                        .padding(3.dp),
                    fontSize = 16.sp,
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
            ) {

                Text(
                    text = transaction.phone,
                    fontSize = 12.sp,
                    color = Color(0xFFA09D9D),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.card_last_digits),
                    color = Color(0xFFA09D9D),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = transaction.cardNumber ?: "–",
                    modifier = Modifier,
                    color = Color.White,
                )
            }


            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp, bottom = 10.dp)
                    .height(0.75.dp) // You can adjust the height of the line
                    .background(Color(0xE24391B1)),
            )
            TransactionTableCell(
                title = stringResource(R.string.associate),
                value = transaction.associate,
                modifier = Modifier,
                valueColor = Color.White,
            )
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                ) {
//                    TransactionTableCell(
//                        title = stringResource(R.string.associate),
//                        value = transaction.associate,
//                        modifier = Modifier.weight(1.7f),
//                        valueColor = Color.White,
//                    )
////                    Spacer(modifier = Modifier.weight(1f))
//                    TransactionTableCell(
//                        title = stringResource(R.string.card_last_digits),
//                        value = transaction.cardNumber ?: "–",
//                        valueColor = Color.White
//                    )
//                }
        }
    }
}

val TransactionStatus.contentColor: Color
    @Composable
    get() = when (this) {
        TransactionStatus.Paid -> MaterialTheme.colors.success
        TransactionStatus.Expired -> MaterialTheme.colors.error
        TransactionStatus.Pending -> MaterialTheme.colors.warning
    }

val TransactionStatus.displayableResId: Int
    get() = when (this) {
        TransactionStatus.Paid -> R.string.transaction_status_paid
        TransactionStatus.Expired -> R.string.transaction_status_expired
        TransactionStatus.Pending -> R.string.transaction_status_pending
    }

@Composable
private fun TransactionTableCell(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color? = null,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Color(0xFFA09D9D),
            style = MaterialTheme.typography.caption,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            modifier = Modifier,
            style = (valueColor?.let {
                MaterialTheme.typography.subtitle1.copy(color = it)
            } ?: LocalTextStyle.current).copy(fontSize = 14.sp),
        )
    }
}

val previewTransactions = List(10) { index ->
    Transaction(
        id = "id_$index",
        customerName = "Customer Name $index",
        amount = "$${10.0 + index * 5}",
        status = TransactionStatus.values()[index % 3],
        associate = "Associate Name very long name",
        associateId = 10,
        phone = index.toString().padStart(10, '1'),
        cardNumber = index.toString().padStart(4, '0'),
    )
}

@NexgoN6Preview
@Composable
fun RecentTransactionsScreenPreview() {
    LedgerGreenTheme {
        RecentTransactionsScreen(
            state = RecentTransactionsState(
                allTransactions = previewTransactions,
                transactions = previewTransactions.toImmutableList(),
                searchQuery = "Customer",
                filters = previewFilters,
                loading = false,
                error = null,
                eventSink = { },
                selectedOption = "Contactless",
            ),
            updateSelectedOption = { },
            appBarConfig = DefaultAppBarConfig.preview,
            onFilterClick = { },
            onSearchChange = { },
            onRefresh = { },
            onErrorShown = { },
            navigateToHome = { }
        )
    }
}

@NexgoN6Preview
@Composable
fun RecentTransactionsScreenEmptyPreview() {
    LedgerGreenTheme {
        RecentTransactionsScreen(
            state = RecentTransactionsState(
                allTransactions = emptyList(),
                transactions = persistentListOf(),
                searchQuery = "Customer",
                filters = previewFilters,
                loading = false,
                error = null,
                eventSink = { },
                selectedOption = "Contactless",
            ),
            updateSelectedOption = { },
            appBarConfig = DefaultAppBarConfig.preview,
            onFilterClick = { },
            onSearchChange = { },
            onRefresh = { },
            onErrorShown = { },
            navigateToHome = { }
        )
    }
}

private val previewFilters = persistentListOf(
    TransactionsFilter.TransactionStatusFilter(
        TransactionStatus.Paid, true,
    ),
    TransactionsFilter.TransactionStatusFilter(
        TransactionStatus.Expired, false,
    ),
    TransactionsFilter.TransactionStatusFilter(
        TransactionStatus.Pending, false,
    ),
    TransactionsFilter.CreatedByMeFilter(false),
)

@Preview(showBackground = true)
@Composable
fun TransactionFilter() {
    LedgerGreenTheme {
        TransactionFilter(
            filters = previewFilters,
            onFilterClick = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    LedgerGreenTheme {
        TransactionItem(
            transaction = Transaction(
                id = "id",
                customerName = "Vincent Vega",
                amount = "$50.0",
                status = TransactionStatus.Paid,
                associate = "Lance",
                associateId = 10,
                cardNumber = "8048",
                phone = "1234567890",
            ),
        )
    }
}
