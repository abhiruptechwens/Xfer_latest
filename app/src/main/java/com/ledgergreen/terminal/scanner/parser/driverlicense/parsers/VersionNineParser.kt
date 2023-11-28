package com.ledgergreen.terminal.scanner.parser.driverlicense.parsers

import com.ledgergreen.terminal.scanner.parser.driverlicense.models.FieldKey

/**
 * Published 2016.
 */
internal class VersionNineParser(data: String) : DLParser(data) {

    init {
        fields.remove(FieldKey.DRIVER_LICENSE_NAME)
        fields.remove(FieldKey.GIVEN_NAME)
    }
}
