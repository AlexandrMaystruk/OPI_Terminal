package com.hssoft.smartcheckout.opi_core.terminal.entity

import java.math.BigDecimal
import java.util.*

enum class PaymentType { SALE }

data class Payment(
    val total: BigDecimal,
    val currency: String,
    val transactionId: Long,
    val type: PaymentType,
    val lastReceiptNumber: Int
) {

    class Builder {
        private var total: BigDecimal = BigDecimal.ZERO
        private var currency: String = "EUR"
        private var transactionId: Long = Random().nextLong()
        private var type: PaymentType = PaymentType.SALE
        private var lastReceiptNumber: Int = -1

        fun total(total: BigDecimal) = apply { this.total = total }
        fun currency(currency: String) = apply { this.currency = currency }
        fun transactionId(transactionId: Long) = apply { this.transactionId = transactionId }
        fun type(type: PaymentType) = apply { this.type = type }
        fun lastReceiptNumber(number: Int) = apply { this.lastReceiptNumber = number }

        fun build(): Payment {
            return Payment(
                total,
                currency,
                transactionId,
                type,
                lastReceiptNumber
            )
        }
    }
}