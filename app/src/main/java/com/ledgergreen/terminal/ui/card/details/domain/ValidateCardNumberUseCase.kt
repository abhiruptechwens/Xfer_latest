package com.ledgergreen.terminal.ui.card.details.domain

import javax.inject.Inject

class ValidateCardNumberUseCase @Inject constructor() {

    operator fun invoke(cardNumber: String, cardNumberLength: Int): Boolean {
        if (cardNumber.length != cardNumberLength) {
            return false
        }

        return validateCardWithLuhnAlgorithm(cardNumber, cardNumberLength)
    }

    /* https://en.wikipedia.org/wiki/Luhn_algorithm */
    private fun validateCardWithLuhnAlgorithm(cardNumber: String, cardNumberLength: Int): Boolean {

        try {
            val listOfNumbers: MutableList<Int> =
                cardNumber.toCharArray().map { it.digitToInt() }.toMutableList()
            for (index in (cardNumberLength - 2) downTo 0 step 2) {
                val multiplied: Int = listOfNumbers[index] * 2
                listOfNumbers[index] = when (multiplied <= 9) {
                    true -> multiplied
                    false -> multiplied - 9
                }
            }

            return listOfNumbers.sum() % 10 == 0
        } catch (_: Exception) {
            return false
        }

    }
}
