package com.ledgergreen.terminal.ui.common.topbar

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.R
import com.ledgergreen.terminal.app.AppStateViewModel
import com.ledgergreen.terminal.ui.common.AppIcon
import com.ledgergreen.terminal.ui.common.SupportButton
import com.ledgergreen.terminal.ui.common.hiltActivityViewModel
import com.ledgergreen.terminal.ui.common.stringresources.StringResources

@Composable
fun DefaultAppBar(
    buttonText: String,
    logoUrl: String,
    onLogout: () -> Unit,
    onSwitch: () -> Unit,
    onNavigateToHome: () ->Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .background(Color(0xFFFFFF))
            .then(modifier),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                logoUrl,
                Modifier
                    .size(50.dp),
                onNavigateToHome,
                onLogout,
            )
            Spacer(Modifier.weight(1f))
            actions()
            TextButton(
                modifier = modifier,
                colors = ButtonDefaults.buttonColors(Color(0xFF0043A5)),
                content = {
                    Row{

                        Image(painter = if (buttonText != "Switch") painterResource(R.drawable.support_btn) else painterResource(R.drawable.swap_icon), contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(buttonText, color = Color.White)
                    }

                },
                onClick = {
                    onSwitch()
                },
            )
        }
    }
}

data class DefaultAppBarConfig(
    val logoUrl: String,
    val onLogout: () -> Unit,
    val onSwitch: () -> Unit,
) {
    companion object {
        val preview = DefaultAppBarConfig("", { }, { })
    }
}

@Composable
fun defaultAppBarConfig(
    appStateViewModel: AppStateViewModel = hiltActivityViewModel(),
): DefaultAppBarConfig = DefaultAppBarConfig(
//    logoUrl = R.drawable.xfericon.toString(),
    logoUrl = appStateViewModel.appState.value.brand?.logoUrl ?: "",
    onLogout = appStateViewModel::logout,
    onSwitch = appStateViewModel::dropPin,
)

@Composable
@Preview
fun PinAppBar(
    logoUrl: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .then(modifier),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                logoUrl,
                Modifier
                    .size(50.dp),
                navigateToHome = {},
                logout = onLogout,
            )
            Spacer(Modifier.weight(1f))
            SupportButton()
        }
    }
}

@Composable
@Preview
fun SwitchAppBar(
    config: DefaultAppBarConfig,
    onNavigateToHome: () -> Unit,
    logout: () -> Unit,
) {
    DefaultAppBar(

        buttonText = stringResource(R.string.switch_txt),
        logoUrl = config.logoUrl,
//        onLogout = config.onLogout,
        onLogout = logout,
        onSwitch = config.onSwitch,
        onNavigateToHome = onNavigateToHome
    )
}
