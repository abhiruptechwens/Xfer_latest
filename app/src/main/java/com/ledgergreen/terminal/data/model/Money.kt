package com.ledgergreen.terminal.data.model

import com.ledgergreen.terminal.data.model.Money.Companion.toMoney
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private val currencyFormat = NumberFormat.getCurrencyInstance()
private val doubleFormat = DecimalFormat("#.##")

@Serializable(MoneySerializer::class)
@JvmInline
value class Money(val value: BigDecimal) {

    init {
        value.setScale(2, RoundingMode.HALF_UP)
    }

    operator fun plus(other: Money) = Money(this.value + other.value)
    operator fun minus(other: Money) = Money(this.value - other.value)

    fun toCurrencyString(): String = currencyFormat.format(value)

    fun toDouble(): Double = doubleFormat.format(value).toDouble()

    companion object {
        fun BigDecimal.toMoney() = Money(this)
        fun String.toMoney() = this.toBigDecimalOrNull()?.let { Money(it) }
        fun Double.toMoney() = Money(BigDecimal.valueOf(this))
    }
}

object MoneySerializer : KSerializer<Money> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Money", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Money = decoder.decodeDouble().toMoney()

    override fun serialize(encoder: Encoder, value: Money) {
        encoder.encodeDouble(value.toDouble())
    }
}
