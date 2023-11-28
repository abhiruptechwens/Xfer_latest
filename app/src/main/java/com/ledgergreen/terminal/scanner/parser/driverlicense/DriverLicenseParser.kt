package com.ledgergreen.terminal.scanner.parser.driverlicense

import com.ledgergreen.terminal.scanner.Document
import com.ledgergreen.terminal.scanner.DocumentParser
import com.ledgergreen.terminal.scanner.parser.driverlicense.models.License
import com.ledgergreen.terminal.scanner.parser.driverlicense.parsers.DLParser
import java.util.Date
import javax.inject.Inject
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DriverLicenseParser @Inject constructor() : DocumentParser {

    override fun isApplicable(raw: String): Boolean = raw.startsWith("@\n\u001E\rANSI")

    override fun parse(rawData: String): Result<Document> {
        val license = DLParser(rawData).parse()
        return if (license.isAcceptable) {
            Result.success(license.toDriverLicense())
        } else {
            Result.failure(DriverLicenseParserException("Driver License is not acceptable"))
        }
    }

    private fun License.toDriverLicense() = Document.DriverLicense(
        documentNumber = licenseNumber ?: parseError("Unable to read DL number"),
        firstName = firstName ?: parseError("Unable to read DL firstName"),
        middleName = middleNames.joinToString(" "),
        lastName = lastName ?: parseError("Unable to read DL lastName"),
        address1 = streetAddress ?: parseError("Unable to read DL streetAddress"),
        birthDate = birthdate?.toLocalDate() ?: parseError("Unable to read DL birthdate"),
        expiryDate = expirationDate?.toLocalDate()
            ?: parseError("Unable to read DL expirationDate"),
        issueDate = issuedDate?.toLocalDate() ?: parseError("Unable to read DL issuedDate"),
        city = city ?: parseError("Unable to read DL city"),
        state = state ?: parseError("Unable to read DL state"),
        country = country?.rawValue?: "USA", // USA, CAN
        sex = gender?.name ?: parseError("Unable to read DL gender"),
        postalCode = postalCode ?: parseError("Unable to read DL postalCode"),
    )

    private fun Date.toLocalDate(): LocalDate =
        Instant.fromEpochMilliseconds(time).toLocalDateTime(TimeZone.currentSystemDefault()).date

    @Suppress("NOTHING_TO_INLINE")
    private inline fun parseError(message: String): Nothing =
        throw DriverLicenseParserException(message)
}
