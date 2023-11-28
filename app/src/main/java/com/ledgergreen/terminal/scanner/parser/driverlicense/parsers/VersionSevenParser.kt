package com.ledgergreen.terminal.scanner.parser.driverlicense.parsers

import com.ledgergreen.terminal.scanner.parser.driverlicense.models.FieldKey

/**
 * Published 06-2012.
 */
internal class VersionSevenParser(data: String) : DLParser(data) {

    init {
        fields.remove(FieldKey.DRIVER_LICENSE_NAME)
        fields.remove(FieldKey.GIVEN_NAME)
    }
}
