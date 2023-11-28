@file:OptIn(ExperimentalPermissionsApi::class)

package com.ledgergreen.terminal.ui.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.data.model.UserInfo
import com.ledgergreen.terminal.data.model.phone.CountryCodes
import com.ledgergreen.terminal.data.model.phone.PhoneNumber
import com.ledgergreen.terminal.data.network.model.CustomerIdType
import com.ledgergreen.terminal.domain.scan.CustomerRegistrationForm
import com.ledgergreen.terminal.monitoring.Clicks
import com.ledgergreen.terminal.monitoring.trackClick
import com.ledgergreen.terminal.ui.common.BackPressSealed
import com.ledgergreen.terminal.ui.common.ErrorDialog
import com.ledgergreen.terminal.ui.common.LedgerGreenIcons
import com.ledgergreen.terminal.ui.common.NexgoN6Preview
import com.ledgergreen.terminal.ui.common.ledgergreenicons.IcScanButton
import com.ledgergreen.terminal.ui.common.toVisibility
import com.ledgergreen.terminal.ui.common.topbar.DefaultAppBarConfig
import com.ledgergreen.terminal.ui.common.topbar.SwitchAppBar
import com.ledgergreen.terminal.ui.common.topbar.defaultAppBarConfig
import com.ledgergreen.terminal.ui.home.dialogs.CustomerConfirmationDialog
import com.ledgergreen.terminal.ui.home.dialogs.DocScanActiveDialog
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navigateToAmount: () -> Unit,
    navigateToWallet: (customerId : String?) -> Unit,
    navigateToContactless: () -> Unit,
    navigateToRecentTransactions: () -> Unit,
    navigateToRegisterCustomer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsState().value
    if (state.navigateNext) {
        LaunchedEffect("navi_scanner") {
            viewModel.onNavigateNextConsumed()

//            navigateToAmount()
//            navigateToWallet(AppState1.customerResponse?.id.toString())
            navigateToWallet(state.customerResponse?.customerExternalId.toString())
        }
    }

    if (state.registrationRequired) {
        LaunchedEffect("navi_register") {
            viewModel.onNavigateRegisterConsumed()
            navigateToRegisterCustomer()
        }
    }

    val permissionState = rememberCameraPermissionState()
    LaunchedEffect("camera") {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    HomeScreen(
        state = state,
        onDocScan = trackClick(
            targetName = Clicks.scanDriverLicense,
            onClick = viewModel::onDocScan,
        ),
        onConfirmCustomer = viewModel::onConfirmCustomer,
        onContactless = trackClick(
            targetName = Clicks.contactless,
            onClick = navigateToContactless,
        ),
        onRecentTransactions = navigateToRecentTransactions,
        onRejectScan = viewModel::clearScan,
        appBarConfig = defaultAppBarConfig(),
        modifier = modifier,
        isGrantedCameraPermission = permissionState.status.isGranted,
        onScanCancel = viewModel::onScanCancel,
    )
}

@Composable
private fun BackPress() {
    var showToast by remember { mutableStateOf(false) }
    var backPressState by remember { mutableStateOf<BackPressSealed>(BackPressSealed.Idle) }
    val context = LocalContext.current

    if (showToast) {
        Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show()
        showToast = false
    }

    LaunchedEffect(key1 = backPressState) {
        if (backPressState == BackPressSealed.InitialTouch) {
            delay(2000)
            backPressState = BackPressSealed.Idle
        }
    }

    BackHandler(backPressState == BackPressSealed.Idle) {
        backPressState = BackPressSealed.InitialTouch
        showToast = true
        if(backPressState == BackPressSealed.Idle)
            System.exit(0)
    }
}

@Composable
fun HomeScreen(
    state: HomeState,
    onDocScan: () -> Unit,
    onConfirmCustomer: () -> Unit,
    onContactless: () -> Unit,
    onRecentTransactions: () -> Unit,
    onRejectScan: () -> Unit,
    onScanCancel: () -> Unit,
    appBarConfig: DefaultAppBarConfig,
    isGrantedCameraPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    state.errorMessage?.let {
        ErrorDialog(it, state.onErrorShown)
    }

    BackPress()

//    if (state.scannerStarted && state.showScannerHint) {
//        DocScanActiveDialog(onScanCancel = onScanCancel)
//    }

    state.customerResponse?.let {
        it.state?.let { it1 ->
            it.phone?.let { it2 ->
                CustomerConfirmationDialog(

                    document = it.customerIdNumber,
                    state = it1,
                    phoneNo = it2,
                    name = it.fullName,
                    onConfirmCustomer = onConfirmCustomer,
                    onReject = onRejectScan,
                    onScanAgain = onDocScan,
                )
            }
        }
    }

    Scaffold(
        modifier = modifier,
        backgroundColor = Color(0xFF06478D),
        topBar = { SwitchAppBar(appBarConfig,{})},
    ) { paddingValues ->


        Column {
//            Text(
//
//                color = Color(0xFFFFFFFF),
//                modifier = Modifier
//                    .padding(start = 16.dp),
//                text = stringResource(R.string.store) + "\n"+ state.userInfo.store,
//                style = MaterialTheme.typography.caption,
//            )


            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 15.dp, start = 15.dp, end = 15.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
            ) {


                Spacer(modifier = Modifier.weight(1f))
                if (isGrantedCameraPermission) {
//                Column(
//                    Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Text(
//                        color = Color(0xFFFFFFFF),
//                        text = stringResource(R.string.welcome_associate),
//                        style = MaterialTheme.typography.h5,
//                    )
//                    Text(
//                        color = Color(0xFFFFFFFF),
//                        modifier = Modifier.padding(top = 8.dp),
//                        text = state.userInfo.username,
//                        style = MaterialTheme.typography.h5,
//                    )
//                }

                    val stroke = Stroke(width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )

                    Column(
                        modifier = Modifier
                            .drawBehind {
                                drawRoundRect(
                                    color = Color.Gray,
                                    style = stroke,
                                    cornerRadius = CornerRadius(15.dp.toPx())
                                )
                            }
//                            .border(
//                                BorderStroke(0.3.dp, color = Color.White),
//                                shape = RoundedCornerShape(15.dp),
//
//                                )
                            .padding(
                                10.dp,
                            ),
                    ) {
                        Spacer(Modifier.height(5.dp))

//                    Text(
//                        color = Color(0xFFFFFFFF),
//                        text = stringResource(R.string.scan_driver_s_license),
//                        style = MaterialTheme.typography.subtitle1,
//                    )
                        Text(

                            color = Color(0xFFFFFFFF),
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .align(Alignment.CenterHorizontally),
                            text = stringResource(R.string.authenticate_using_phone_or_id),
                            style = MaterialTheme.typography.caption,
                        )
                        DocScanButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = !state.loading,
                            onScan = onDocScan,
                        )

//                    Text(
//                        modifier = Modifier
//                            .align(Alignment.CenterHorizontally)
//                            .padding(start = 8.dp, end = 14.dp, top = 10.dp),
//                        text = "OR",
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight(600),
//                            color = Color(0xFFFFFFFF),
//
//                            )
//                    )

//                    PhoneNumberRegister(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        enabled = !state.loading,
//                        onClick = onContactless,
//                    )

                    }

                    RemotePayButton(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        enabled = !state.loading,
                        onClick = onContactless,
                    )

                    RecentTransactionsButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(60.dp),
                        enabled = !state.loading,
                        onClick = onRecentTransactions,
                    )

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .alpha(state.loading.toVisibility()),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }


                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 10.dp),
                        text = stringResource(id = R.string.xfer_powered_by_west_town_bank_trust),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFFFFFFFF),

                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                        ),
                    )

                } else {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = stringResource(R.string.camera_permission_is_required),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 10.dp),
                            text = "Terms & conditions & Privacy policy",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),

                                textAlign = TextAlign.Center,
                                textDecoration = TextDecoration.Underline,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentTransactionsButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 14.dp, top = 38.dp)
            .border(
                width = 3.dp,
                color = Color.White,
                shape = RoundedCornerShape(size = 20.dp),
            )
            .width(333.dp)
            .height(60.dp)
            .background(color = Color(0xE2FFFFFF), shape = RoundedCornerShape(size = 20.dp))
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
            .clickable {
//                Toast
//                    .makeText(context, "Coming Soon...", Toast.LENGTH_SHORT)
//                    .show()
                onClick()
            },
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.recent_transactions),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(600),
                color = Color(0xE2083364 ),
                textAlign = TextAlign.Center,
            ),
        )

    }
//    Button(
//        colors = ButtonDefaults.buttonColors(Color(0xFF61BAF5)),
//        modifier = modifier,
//        onClick = onClick,
//        enabled = enabled,
//        content = {
//            Text(
//                stringResource(R.string.recent_transactions),
//                color = Color(0xFFFFFFFF),
//                style = LocalTextStyle.current.copy(
//                    fontSize = 24.sp,
//                ),
//            )
//        },
//    )
}

@Composable
fun DocScanButton(
    enabled: Boolean,
    onScan: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 14.dp, top = 15.dp)
            .border(
                width = 3.dp,
                color = Color.White,
                shape = RoundedCornerShape(size = 10.dp),
            )
            .width(333.dp)
            .height(60.dp)
            .background(color = Color(0xE2FFFFFF), shape = RoundedCornerShape(size = 10.dp))
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
            .clickable { onScan() },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.driver_s_license_passport),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(600),
                color = Color(0xE2083364),
                textAlign = TextAlign.Center,
            ),
        )

    }
//    Button(
//        colors = ButtonDefaults.buttonColors(Color(0xFF61BAF5)),
//        modifier = modifier,
//        shape = RoundedCornerShape(5.dp),
//        enabled = enabled,
//        content = {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Icon(
//                    modifier = Modifier.size(24.dp),
//                    imageVector = LedgerGreenIcons.IcScanButton,
//                    tint = Color(0xFFFFFFFF),
//                    contentDescription = null,
//                )
//                Spacer(Modifier.width(16.dp))
//                Text(
//                    stringResource(id = R.string.scan_now),
//                    color = Color(0xFFFFFFFF),
//                    style = LocalTextStyle.current.copy(
//                        fontSize = 24.sp,
//                    ),
//                )
//            }
//        },
//        onClick = onScan,
//    )
}

@Composable
fun RemotePayButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var showToast by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 14.dp, top = 38.dp)
            .border(
                width = 3.dp,
                color = Color.White,
                shape = RoundedCornerShape(size = 20.dp),
            )
            .width(333.dp)
            .height(60.dp)
            .background(color = Color(0xE2FFFFFF), shape = RoundedCornerShape(size = 20.dp))
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
            .clickable { onClick() },
    ) {

        if (showToast) {
            ToastMessage(message = "Hello, Jetpack Compose!")
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.contactless),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(600),
                color = Color(0xE2083364 ),
                textAlign = TextAlign.Center,
            ),
        )

    }
//    Button(
//        colors = ButtonDefaults.buttonColors(Color(0xFF61BAF5)),
//        modifier = modifier,
//        enabled = enabled,
//        onClick = onClick,
//        content = {
//            Text(
//                stringResource(id = R.string.contactless),
//                color = Color(0xFFFFFFFF),
//                style = LocalTextStyle.current.copy(
//                    fontSize = 24.sp,
//                ),
//            )
//        },
//    )
}

@Composable
fun PhoneNumberRegister(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 14.dp, top = 15.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF6DA9FF),
                shape = RoundedCornerShape(size = 20.dp),
            )
            .width(333.dp)
            .height(60.dp)
            .background(color = Color(0x66001F43), shape = RoundedCornerShape(size = 20.dp))
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
            .clickable { },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.phone_number),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
            ),
        )

    }
//    Button(
//        colors = ButtonDefaults.buttonColors(Color(0xFF61BAF5)),
//        modifier = modifier,
//        enabled = enabled,
//        onClick = onClick,
//        content = {
//            Text(
//                stringResource(id = R.string.contactless),
//                color = Color(0xFFFFFFFF),
//                style = LocalTextStyle.current.copy(
//                    fontSize = 24.sp,
//                ),
//            )
//        },
//    )
}

@Composable
fun rememberCameraPermissionState(
    onPermissionResult: (Boolean) -> Unit = {},
): PermissionState = rememberPermissionState(Manifest.permission.CAMERA, onPermissionResult)

@NexgoN6Preview
@Composable
fun HomeScreenCameraGrantedPreview() {
    HomeScreenPreview(true)
}

@NexgoN6Preview
@Composable
fun HomeScreenCameraNotGrantedPreview() {
    HomeScreenPreview(false)
}

@Composable
fun HomeScreenPreview(isGrantedCamera: Boolean) {
    LedgerGreenTheme {
        HomeScreen(
            state = HomeState(
                userInfo = UserInfo("Casino Royale", "Arnold  Schwarzenegger"),
                loading = false,
                scannerStarted = false,
                showScannerHint = false,
                errorMessage = null,
                walletResponse = null,
                registrationRequired = false,
                customerResponse = null,
                onErrorShown = { },
                navigateNext = false,
                isDialogShown = false,
            ),
            onDocScan = { },
            onConfirmCustomer = { },
            onContactless = { },
            onRecentTransactions = { },
            onRejectScan = { },
            appBarConfig = DefaultAppBarConfig.preview,
            isGrantedCameraPermission = isGrantedCamera,
            onScanCancel = {},
        )
    }
}

val fakeRegistrationForm = CustomerRegistrationForm(
    idType = CustomerIdType.DriverLicense,
    firstName = "John",
    middleName = "",
    lastName = "Doe",
    idNumber = "9999999",
    address1 = "454 APRICOT RD LITTLE ROCK",
    birthdate = LocalDate(1980, 4, 20),
    issueDate = LocalDate(2000, 10, 10),
    expirationDate = LocalDate(2040, 1, 1),
    city = "LITTLE ROCK",
    state = "CA",
    country = "USA",
    gender = "MALE",
    postalCode = "27870-3154",
    phoneNumber = PhoneNumber("12345678", CountryCodes.defaultUSPhoneCode),
)
@Composable
fun ToastMessage(message: String, duration: Int = Toast.LENGTH_SHORT) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val view = LocalView.current

    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(isVisible) {
        delay(duration.toLong())

        // Wait for the duration and then hide the Toast
        isVisible = false
    }

    if (isVisible) {
        // Draw a Box with the message at the bottom of the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .offset(y = (50 * density).dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.Black)
                    .padding(16.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display the message
                Text(
                    text = message,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                // You can add an icon or close button if needed
            }
        }
    }
}
