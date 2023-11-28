package com.ledgergreen.terminal.scanner.parser.driverlicense.parsers

import com.ledgergreen.terminal.scanner.parser.driverlicense.models.FieldKey

/**
 * Published 2000.
 */
internal class VersionOneParser(data: String) : DLParser(data) {

    init {
        fields[FieldKey.LAST_NAME] = "DAB"
        fields.remove(FieldKey.COUNTRY) // No documentation?
        fields.remove(FieldKey.LAST_NAME_TRUNCATION)
        fields.remove(FieldKey.FIRST_NAME_TRUNCATION)
        fields.remove(FieldKey.MIDDLE_NAME_TRUNCATION)
    }

    override val unitedStatesDateFormat = "yyyyMMdd"
}
