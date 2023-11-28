package com.ledgergreen.terminal.ui.agreement.domain

import com.ledgergreen.terminal.data.brand.BrandManager
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class GetAgreementUseCase @Inject constructor(
    private val brandManager: BrandManager,
) {
    operator fun invoke(): Flow<String> = brandManager.getBrand()
        .filterNotNull()
        .map { it.agreementText }
}
