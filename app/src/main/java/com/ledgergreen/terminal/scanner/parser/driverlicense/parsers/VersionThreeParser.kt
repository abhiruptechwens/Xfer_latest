package com.ledgergreen.terminal.scanner.parser.driverlicense.parsers

import com.ledgergreen.terminal.scanner.parser.driverlicense.models.FieldKey

/**
 * Published 03-2005.
 */
internal class VersionThreeParser(data: String) : DLParser(data) {

    init {
        fields.remove(FieldKey.FIRST_NAME)
        fields.remove(FieldKey.MIDDLE_NAME)
        fields.remove(FieldKey.LAST_NAME_TRUNCATION)
        fields.remove(FieldKey.FIRST_NAME_TRUNCATION)
        fields.remove(FieldKey.MIDDLE_NAME_TRUNCATION)
        fields.remove(FieldKey.DRIVER_LICENSE_NAME)
    }
}
