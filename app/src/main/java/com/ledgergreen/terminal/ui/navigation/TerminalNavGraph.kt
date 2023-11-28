package com.ledgergreen.terminal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.datadog.android.compose.ExperimentalTrackingApi
import com.datadog.android.compose.NavigationViewTrackingEffect
import com.datadog.android.rum.tracking.AcceptAllNavDestinations
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.app.AppStateViewModel
import com.ledgergreen.terminal.data.model.AuthenticationState
import com.ledgergreen.terminal.domain.GetVersionInfoUseCase
import com.ledgergreen.terminal.ui.agreement.AgreementScreen
import com.ledgergreen.terminal.ui.amount.AmountScreen
import com.ledgergreen.terminal.ui.autolockdialog.AutolockDialog
import com.ledgergreen.terminal.ui.card.details.CardDetailsScreen
import com.ledgergreen.terminal.ui.card.reader.CardReaderScreen
import com.ledgergreen.terminal.ui.common.hiltActivityViewModel
import com.ledgergreen.terminal.ui.contactless.ContactlessScreen
import com.ledgergreen.terminal.ui.home.HomeScreen
import com.ledgergreen.terminal.ui.home.HomeViewModel
import com.ledgergreen.terminal.ui.home.register.RegisterCustomerScreen
import com.ledgergreen.terminal.ui.login.LoginScreen
import com.ledgergreen.terminal.ui.password.ChangePasswordScreen
import com.ledgergreen.terminal.ui.pin.PinScreen
import com.ledgergreen.terminal.ui.pinreset.ChangePinScreen
import com.ledgergreen.terminal.ui.receipt.ReceiptScreen
import com.ledgergreen.terminal.ui.splash.SplashScreen
import com.ledgergreen.terminal.ui.tips.TipsScreen
import com.ledgergreen.terminal.ui.transactions.RecentTransactionsScreen
import com.ledgergreen.terminal.ui.wallet.WalletScreen

@OptIn(ExperimentalTrackingApi::class)
@Composable
fun TerminalNavGraph(
    modifier: Modifier = Modifier,
) {
    val navController: NavHostController = rememberNavController().apply {
        NavigationViewTrackingEffect(
            navController = this,
            trackArguments = false,
            destinationPredicate = AcceptAllNavDestinations(),
        )
    }


    val appViewModel = hiltActivityViewModel<AppStateViewModel>()

    LaunchedEffect(key1 = "login_navigation") {
        appViewModel.appState.collect { appState ->
            when (appState.authenticationState) {
                AuthenticationState.LOGIN_REQUIRED -> NavRoute.Login.value
                AuthenticationState.PIN_REQUIRED -> NavRoute.Pin.value
                AuthenticationState.AUTHENTICATED -> NavRoute.HomeScreen.value
                AuthenticationState.PASSWORD_RESET_REQUIRED -> NavRoute.ChangePassword.value
                AuthenticationState.PIN_RESET_REQUIRED -> NavRoute.ChangePin.value
                AuthenticationState.INITIAL -> null
            }?.let { navController.navigate(it) { popUpTo(0) } }
        }
    }

    var showAutoLockDialog by remember { mutableStateOf(false) }
    if (showAutoLockDialog) {
        AutolockDialog(onClose = appViewModel::onIdleLockInterrupted)
    }

    LaunchedEffect("autolockDialog") {
        appViewModel.lockState.collect { lockState ->
            showAutoLockDialog = lockState.showAutolockDialog &&
                appViewModel.appState.value.authenticationState == AuthenticationState.AUTHENTICATED
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Splashscreen.value,
    ) {


        composable(NavRoute.Login.value) {
            LoginScreen()
        }

        composable(NavRoute.Pin.value) {
            PinScreen()
        }

        composable(NavRoute.RegisterCustomer.value) {
            RegisterCustomerScreen(
                onRegistered = {
                   customerId-> navController.navigate("${NavRoute.Wallet.value}/$customerId") {
                        popUpTo(NavRoute.RegisterCustomer.value) { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(NavRoute.HomeScreen.value) {
            HomeScreen(
                navigateToWallet = { customerId ->  navController.navigate("${NavRoute.Wallet.value}/$customerId") },
                navigateToAmount = { navController.navigate(NavRoute.Amount.value) },

                navigateToContactless = {
                    navController.navigate(NavRoute.Amount.contactlessAmountRoute)
                },
                navigateToRegisterCustomer = {
                    navController.navigate(
                        NavRoute.RegisterCustomer.value,
                    )
                },
                navigateToRecentTransactions = {
                    navController.navigate(NavRoute.RecentTransactions.value)
                },
            )
        }

        composable(NavRoute.Contactless.value) {
            ContactlessScreen(
                navigateToHome = {navController.navigate(NavRoute.HomeScreen.value){
                    popUpTo(0){inclusive = true}
                } },
                navigateToRecentTransactions = {
                    navController.navigate(NavRoute.RecentTransactions.value) {
                        popUpTo(NavRoute.Wallet.value)
                    }
                },
            )
        }

        composable(
            NavRoute.Amount.value,
            arguments = listOf(
                navArgument(NavRoute.Amount.contactlessArg) {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(NavRoute.Amount.loadFundsArgs) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
        ) {
            AmountScreen(
                contactless = it.arguments?.getBoolean(NavRoute.Amount.contactlessArg) ?: false,
                navigateToWallet = { customerId ->  navController.navigate("${NavRoute.Wallet.value}/$customerId") },
                loadfunds = it.arguments?.getBoolean(NavRoute.Amount.loadFundsArgs) ?: false,
                navigateTips = { navController.navigate(NavRoute.Tips.value) },
                navigateAgreement = { amount ->  navController.navigate("${NavRoute.Agreement.value}/$amount") },
                navigateContactless = { navController.navigate(NavRoute.Contactless.value) },
                navigateToHome = {
                    navController.navigate(NavRoute.HomeScreen.value) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable("${NavRoute.Agreement.value}/{amount}") {
            AgreementScreen(
                amount = (it.arguments?.getString("amount")?:"0"),
                navigateToCardReader = {amount ->
                    navController.navigate("${ NavRoute.CardReader.value }/$amount")
                },navigateToReceipt = { navController.navigate(NavRoute.Receipt.value) },
                navigateHome = {
                    navController.navigate(NavRoute.HomeScreen.value) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoute.Tips.value) {
            TipsScreen(
//                navigateToAgreement = {
//                        amount ->  navController.navigate("${NavRoute.Agreement.value}/$amount")
//                },
                navigateToCardReader = { amount ->  navController.navigate("${NavRoute.CardReader.value}/$amount")},
                navigateToWallet = { customerId ->  navController.navigate("${NavRoute.Wallet.value}/$customerId"){
                    popUpTo(NavRoute.Wallet.value){inclusive = true}
                } },
                navigateToHome = {
                    navController.navigate(NavRoute.HomeScreen.value) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable("${ NavRoute.CardReader.value }/{amount}") {
            CardReaderScreen(
                amount = (it.arguments?.getString("amount")?:"0"),
                navigateToCardDetails = {amount,cardToken ->
                    navController.navigate("${NavRoute.CardDetails.value}/$amount/$cardToken")
                },
            )
        }

        composable( "${NavRoute.CardDetails.value}/{amount}/{cardToken}") {
            CardDetailsScreen(
                amount = (it.arguments?.getString("amount")?:"0"),
                savedCardToken = (it.arguments?.getString("cardToken")?:null),
                navigateToWallet = { customerId ->
                    navController.navigate("${NavRoute.Wallet.value}/$customerId") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToReceipt = {
                    navController.navigate(NavRoute.Receipt.value) {
                        // remove backstack under
                        popUpTo(0)
                    }
                },
                navigateToHome = {
                    navController.navigate(NavRoute.HomeScreen.value) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateAgreement = { amount ->  navController.navigate("${NavRoute.Agreement.value}/$amount") },
                navigateToCardReader = {
                    navController.popBackStack()
                    AppState1.savedCardDetails = null
                },
            )
        }

        composable(NavRoute.Receipt.value) {
            ReceiptScreen(
                navigateToWallet = {
                        customerId ->  navController.navigate("${NavRoute.Wallet.value}/$customerId")
                    },
                navigateToStart = {
                    navController.navigate(NavRoute.HomeScreen.value){
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(NavRoute.HomeScreen.value) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoute.Splashscreen.value) {
            val context = LocalContext.current

            val appInfo = remember { GetVersionInfoUseCase(context).invoke() }
            SplashScreen(appInfo)
        }

        composable(NavRoute.ChangePassword.value) {
            ChangePasswordScreen()
        }

        composable(NavRoute.ChangePin.value) {
            ChangePinScreen()
        }

        composable(NavRoute.RecentTransactions.value) {
            RecentTransactionsScreen(
                navigateToHome = {
                    navController.navigate(NavRoute.HomeScreen.value) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(
            "${NavRoute.Wallet.value}/{customerId}"
        ) {
            WalletScreen(
                customerId = (it.arguments?.getString("customerId")?:""),
                navigateToAmount = { navController.navigate(NavRoute.Amount.value) },
                navigateToAmountWithoutTip = { navController.navigate(NavRoute.Amount.loadFundsAmountRoute) },
                navigateToCardReader = { navController.navigate(NavRoute.CardReader.value) },
                navigateToHome = { navController.navigate(NavRoute.HomeScreen.value){
                    popUpTo(0){inclusive = true}
                } },
            )
        }
    }
}

sealed class NavRoute(
    val value: String,
) {
    data object Splashscreen : NavRoute("splash")
    data object Login : NavRoute("login")
    data object ChangePassword : NavRoute("change_password")
    data object ChangePin : NavRoute("change_pin")
    data object Pin : NavRoute("pin")
    data object HomeScreen : NavRoute("home")
    data object RegisterCustomer : NavRoute("home/register_customer")
    data object Amount : NavRoute("amount?contactless={contactless}&loadfunds={loadfunds}") {
        const val contactlessArg = "contactless"
        const val contactlessAmountRoute = "amount?contactless=true"
        const val loadFundsArgs = "loadfunds"
        const val loadFundsAmountRoute = "amount?contactless=false&loadfunds=true"
    }

    data object Contactless : NavRoute("contactless")
//    data object Agreement : NavRoute("agreement")
    data object Agreement : NavRoute("agreement") {
        const val amountArgs = "amount"
    }
    data object Tips : NavRoute("tips")
    data object CardReader : NavRoute("card/reader")
    data object CardDetails : NavRoute("card/details")
    data object Receipt : NavRoute("receipt")
    data object Wallet : NavRoute("home/wallet")
    data object RecentTransactions : NavRoute("recent_transactions")
}
