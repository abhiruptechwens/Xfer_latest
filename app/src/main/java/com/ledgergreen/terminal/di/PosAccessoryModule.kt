package com.ledgergreen.terminal.di

import com.ledgergreen.terminal.pos.PosAccessory
import com.ledgergreen.terminal.pos.PosAccessoryImpl
import com.ledgergreen.terminal.pos.card.CardReader
import com.ledgergreen.terminal.pos.card.DecoratedCardReader
import com.ledgergreen.terminal.pos.scanner.ScannerImpl
import com.ledgergreen.terminal.scanner.IdScanner
import com.ledgergreen.terminal.scanner.IdScannerImpl
import com.ledgergreen.terminal.scanner.Scanner
import com.ledgergreen.terminal.scanner.parser.driverlicense.DriverLicenseParser
import com.ledgergreen.terminal.scanner.parser.mrz.MrzParser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class PosAccessoryModule {
    @Binds
    abstract fun posAccessory(impl: PosAccessoryImpl): PosAccessory

    @Binds
    abstract fun scanner(impl: ScannerImpl): Scanner

    @Binds
    abstract fun idScanner(impl: IdScannerImpl): IdScanner
}

@Module
@InstallIn(SingletonComponent::class)
class PosAccessoryModuleProvides {
    @Provides
    fun cardDetailsReader(posAccessory: PosAccessory): CardReader =
        DecoratedCardReader(
            posAccessory.cardDetailsReader(),
            posAccessory.beeper(),
            posAccessory.led(),
        )

    @Provides
    @ElementsIntoSet
    fun documentParsers(dlParser: DriverLicenseParser, mrzParser: MrzParser) =
        setOf(
            dlParser,
            mrzParser,
        )
}
