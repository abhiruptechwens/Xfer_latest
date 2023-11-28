package com.ledgergreen.terminal.scanner.parser.driverlicense.categories

/**
 * The AAMVA gender types.
 */
enum class Gender {

    MALE,
    FEMALE;

    companion object {
        fun of(rawValue: String): Gender? = when (rawValue) {
            "1", "M" -> MALE
            "2", "F" -> FEMALE
            else -> null
        }
    }
}
