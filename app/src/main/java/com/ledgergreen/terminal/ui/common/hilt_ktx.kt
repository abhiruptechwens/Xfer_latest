package com.ledgergreen.terminal.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@Composable
inline fun <reified VM : ViewModel> hiltActivityViewModel(): VM {
    return hiltViewModel(LocalContext.current as ComponentActivity)
}
