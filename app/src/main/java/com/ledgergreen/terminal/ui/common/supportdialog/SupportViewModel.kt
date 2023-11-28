package com.ledgergreen.terminal.ui.common.supportdialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgergreen.terminal.domain.support.GetUserSupportInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SupportViewModel @Inject constructor(
    getUserSupportInfo: GetUserSupportInfoUseCase,
) : ViewModel() {
    val state = getUserSupportInfo().map {
        SupportDialogState(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        SupportDialogState.initial,
    )
}
