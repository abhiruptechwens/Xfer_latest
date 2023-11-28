package com.ledgergreen.terminal.scanner.parser.driverlicense.parsers

import com.ledgergreen.terminal.scanner.parser.driverlicense.models.FieldKey

/**
 * Published 07-2010.
 */
internal class VersionFiveParser(data: String) : DLParser(data) {

    init {
        fields.remove(FieldKey.DRIVER_LICENSE_NAME)
        fields.remove(FieldKey.GIVEN_NAME)
    }
}
