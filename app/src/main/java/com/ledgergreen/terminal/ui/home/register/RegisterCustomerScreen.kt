package com.ledgergreen.terminal.ui.home.register

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.data.network.model.CustomerResponse
import com.ledgergreen.terminal.domain.scan.CustomerRegistrationForm
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.common.DatePickerField
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.PhoneTextField
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.fakeRegistrationForm
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme

@Composable
fun RegisterCustomerScreen(
    onRegistered: (custExtId: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterCustomerViewModel = hiltViewModel(),
    appBarConfig: DefaultAppBarConfig = defaultAppBarConfig(),
) {
    val state = viewModel.state.collectAsState().value

    BackHandler(
        enabled = !state.loading,
        onBack = {
            viewModel.onCancelRegistration()
            onCancel()
        },
    )

    if (state.registeredCustomerResponse != null) {
        LaunchedEffect("registered_customer") {
            onRegistered(state.registeredCustomerResponse.customerExternalId)
        }
    }

    state.error?.let {
        ErrorDialog(it, state.onErrorShown)
    }

    RegisterCustomerScreen(
        form = state.form,
        loading = state.loading,
        onFormChanged = viewModel::onFormChanged,
        onRegister = viewModel::onRegister,
        onCancel = onCancel,
        appBarConfig = appBarConfig,
        modifier = modifier,
    )
}

@Composable
fun RegisterCustomerScreen(
    form: CustomerRegistrationForm,
    loading: Boolean,
    onFormChanged: (CustomerRegistrationForm) -> Unit,
    onRegister: () -> Unit,
    onCancel: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        backgroundColor = Color.White,
        topBar = { SwitchAppBar(appBarConfig, {}, {}) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {

            Image(painter = painterResource(id = R.drawable.botton_lines),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart))

            val focusManager = LocalFocusManager.current
            Column(Modifier.fillMaxSize()) {
                RegisterFormFieldsLazyColumn(
                    form = form,
                    enabled = !loading,
                    onFormChanged = onFormChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                )
                Row(Modifier.padding(8.dp)) {
//                    TextButton(
//                        modifier = Modifier.weight(1f),
//                        onClick = {
//                            focusManager.clearFocus()
//                            onCancel()
//                        },
//                        content = { Text(stringResource(R.string.cancel), color = Color.White) },
//                    )
                    Spacer(Modifier.height(8.dp))
                    val context = LocalContext.current

                    var showToast by remember { mutableStateOf(false) }

                    if (showToast) {
                        Toast.makeText(context, "Fields are empty", Toast.LENGTH_LONG).show()
                        showToast = false
                    }
                    Button(
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF0043A5)),
                        onClick = {

                            if (!form.firstName.isNullOrEmpty()
                                && !form.lastName.isNullOrEmpty()
                                && form.birthdate != null
                                && !form.country.isNullOrEmpty()
                                && !form.state.isNullOrEmpty()
                                && !form.city.isNullOrEmpty()
                                && !form.address1.isNullOrEmpty()
                            ) {
                                onRegister()
                                showToast = false
                            }
                            else
                                showToast = true
                        },
                        content = {
                            Text(
                                stringResource(R.string.register),
                                color = Color.White,
                            )
                        },
                    )
                }
            }

            if (loading) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
fun RegisterFormFieldsLazyColumn(
    form: CustomerRegistrationForm,
    onFormChanged: (CustomerRegistrationForm) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {

    val formToView = remember {
        form.copy()
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item("header") {
            Text(
                stringResource(id = R.string.register_new_customer),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        item("phone") {
            val phoneNumber = form.phoneNumber ?: PhoneNumber.default
            PhoneTextField(
                modifier = Modifier.fillMaxWidth(),
                phoneNumber = phoneNumber,
                isError = !phoneNumber.isValidPhoneLength(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
                enabled = enabled,
                onPhoneChanged = {
                    val newPhoneNumber = phoneNumber.copy(phone = it)
                    onFormChanged(form.copy(phoneNumber = newPhoneNumber))
                },
                onCountryChanged = {
                    val newPhoneNumber = phoneNumber.copy(countryCode = it)
                    onFormChanged(form.copy(phoneNumber = newPhoneNumber))
                },
            )
        }

        item("document") {
            Row(modifier = Modifier.fillMaxWidth()) {
                SimpleTextField(
                    value = form.idType.name,
                    label = stringResource(id = R.string.id_type),
                    onValueChange = { },
                    enabled = false,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                SimpleTextField(
                    value = form.idNumber,
                    label = stringResource(id = R.string.id_number),
                    onValueChange = {
                        onFormChanged(form.copy(idNumber = it))
                    },
                    enabled = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item("firstname") {
            SimpleTextField(
                value = form.firstName,
                label = stringResource(id = R.string.firstname),
                isError = formToView.firstName.isNullOrEmpty(),
                onValueChange = {
                    onFormChanged(form.copy(firstName = it))
                },
                enabled = (formToView.firstName == null || formToView.firstName == ""),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { it.isFocused },
            )
        }

        item("middlename") {
            SimpleTextField(
                value = form.middleName,
                label = stringResource(id = R.string.middlename),
                onValueChange = {
                    onFormChanged(form.copy(middleName = it))
                },
                enabled = (formToView.middleName == null || formToView.middleName == ""),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item("lastname") {
            SimpleTextField(
                value = form.lastName,
                label = stringResource(id = R.string.lastname),
                onValueChange = {
                    onFormChanged(form.copy(lastName = it))
                },
                isError = formToView.lastName.isNullOrEmpty(),
                enabled = (formToView.lastName == null || formToView.lastName == ""),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item("sex") {

            var expanded by remember { mutableStateOf(false) }
//            var selectedOption by remember { mutableStateOf("Male") }

            Box {
                Column {
                    Text(
                        text = "Sex",
                        modifier = Modifier
                            .padding(start = 10.dp, bottom = 2.dp)
                            .onGloballyPositioned { }
                            .padding(start = 5.dp),
                        fontSize = 12.sp,
                        color = Color.Black,
                    )
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF042C57),
                                shape = RoundedCornerShape(5.dp),
                            ),
                    ) {
//                Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = form.gender ?: "",
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .onGloballyPositioned { }
                                .padding(start = 5.dp)
                                .align(Alignment.CenterVertically),
                            fontSize = 14.sp,
                            color = if (formToView.gender != null && formToView.gender != "") Color(
                                0xFF5B85B3,
                            ) else Color.Black,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.drop_arrow),
                            contentDescription = null,
                        )
                    }

                    DropdownMenu(
                        expanded = if (formToView.gender != null && formToView.gender != "") false else expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier,
                    ) {
                        DropdownMenuItem(
                            onClick = {
//                                selectedOption = "Male"
                                expanded = false
                                onFormChanged(form.copy(gender = "Male"))
//                        updateSelectedOption(selectedOption)
                            },
                        ) {
                            Text("Male")
                        }
                        DropdownMenuItem(
                            onClick = {
//                                selectedOption = "Female"
                                expanded = false
                                onFormChanged(form.copy(gender = "Female"))
//                        updateSelectedOption(selectedOption)
                            },
                        ) {
                            Text("Female")
                        }
                        DropdownMenuItem(
                            onClick = {
//                                selectedOption = "Female"
                                expanded = false
                                onFormChanged(form.copy(gender = "Others"))
//                        updateSelectedOption(selectedOption)
                            },
                        ) {
                            Text("Others")
                        }
                    }

                }
            }
        }


        item("document_dates") {
            Row(Modifier.fillMaxWidth()) {
                DatePickerField(
                    value = form.issueDate,
                    label = { Text(stringResource(R.string.issue_date), color = Color.Black) },
                    onChanged = {
                        onFormChanged(form.copy(issueDate = it))
                    },
                    isError = formToView.issueDate == null,
                    enabled = false,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                DatePickerField(
                    value = form.expirationDate,
                    label = { Text(stringResource(R.string.expiry_date), color = Color.Black) },
                    onChanged = {
                        onFormChanged(form.copy(expirationDate = it))
                    },
                    enabled = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item("dob") {
            DatePickerField(
                value = form.birthdate,
                readOnly = false,
                label = { Text(stringResource(R.string.birthdate), color = Color.Black) },
                onChanged = {
                    onFormChanged(form.copy(birthdate = it))
                },
                isError = formToView.birthdate==null,
                enabled = (formToView.birthdate == null),
                modifier = Modifier.fillMaxWidth(1f),
            )
        }

        item("country") {
            SimpleTextField(
                value = form.country,
                label = stringResource(id = R.string.country),
                onValueChange = {
                    onFormChanged(form.copy(country = it))
                },
                isError = formToView.country.isNullOrEmpty(),
                enabled = (formToView.country == null || formToView.country == ""),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item("state") {
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = form.state,
                isError = formToView.state.isNullOrEmpty(),
                onValueChange = {
                    onFormChanged(form.copy(state = it))
                },
                label = stringResource(R.string.state),
                enabled = (formToView.state == null || formToView.state == ""),
            )
        }

        item("city") {
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = form.city,
                onValueChange = {
                    onFormChanged(form.copy(city = it))
                },
                isError = formToView.city.isNullOrEmpty(),
                enabled = (formToView.city == null || formToView.city == ""),
                label = stringResource(R.string.city),
            )
        }

        item("address") {
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = form.address1,
                onValueChange = {
                    onFormChanged(form.copy(address1 = it))
                },
                isError = formToView.address1.isNullOrEmpty(),
                enabled = (formToView.address1 == null || formToView.address1 == ""),
                label = stringResource(R.string.address),
            )
        }

        item("postal_code") {
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = form.postalCode,
                onValueChange = {
                    onFormChanged(form.copy(postalCode = it))
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                isError = formToView.postalCode.isNullOrEmpty(),
                enabled = (formToView.postalCode == null || formToView.postalCode == ""),
                label = stringResource(R.string.postal_code),
            )
        }
    }
}

@Composable
fun SimpleTextField(
    value: String?,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
    ),
) {

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Black, // Set the focused outline color
        unfocusedBorderColor = Color.Black, // Set the unfocused outline color
        cursorColor = Color.Black,
        textColor = Color.Black,// Set the cursor color
    )
    OutlinedTextField(
        modifier = modifier,
        value = value ?: "",
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Black) },
        keyboardOptions = keyboardOptions,
        enabled = enabled,
        singleLine = true,
        colors = colors,
        isError = isError,
    )
}

@NexgoN6Preview
@Composable
fun RegisterCustomerScreenPreview() {
    LedgerGreenTheme {
        RegisterCustomerScreen(
            form = fakeRegistrationForm,
            loading = true,
            appBarConfig = DefaultAppBarConfig.preview,
            onFormChanged = { },
            onRegister = { },
            onCancel = { },
        )
    }
}
