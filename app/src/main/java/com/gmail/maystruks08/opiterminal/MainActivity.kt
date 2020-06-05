package com.gmail.maystruks08.opiterminal

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hssoft.smartcheckout.opi_core.terminal.entity.Payment
import com.hssoft.smartcheckout.opi_core.terminal.entity.PaymentType
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*

private const val CVV_PORT_SEND = 20002
private const val CVV_PORT_RECEIVE = 20007
private const val SOCKET_CONNECT_TIMEOUT = 4000

private const val TERMINAL_IP = "192.168.0.125"

private const val WORK_STATION_ID = "Elo C1242435235"
private const val APPLICATION_SENDER = "SmartCheckout"
private const val CURRENCY = "EUR"


class MainActivity : AppCompatActivity() {

    private lateinit var lastTransactionId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler = Handler {
            when (it.what) {
                0 -> {
                    val messageData = it.data.getString("0") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                1 -> {
                    val messageData = it.data.getString("1") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                2 -> {
                    val messageData = it.data.getString("2") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                3 -> {
                    val messageData = it.data.getString("3") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                4 -> {
                    val messageData = it.data.getString("4") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                5 -> {
                    val messageData = it.data.getString("5") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                6 -> {
                    val messageData = it.data.getString("6") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                7 -> {
                    val messageData = it.data.getString("7") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }

                10 -> {
                    val messageData = it.data.getString("10") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }
                11 -> {
                    val messageData = it.data.getString("11") ?: ""
                    textView.text = textView.text.toString() + "\n" + "\n" + messageData
                }

            }
            return@Handler true
        }

        val terminal = Terminal.Builder()
            .ipAddress(TERMINAL_IP)
            .inputPort(CVV_PORT_SEND)
            .outputPort(CVV_PORT_RECEIVE)
            .timeout(SOCKET_CONNECT_TIMEOUT)
            .applicationSender(APPLICATION_SENDER)
            .workstationID(WORK_STATION_ID)
            .build()

        //TODO remove
        terminal.handler = handler

        btnLogin.setOnClickListener {
            Thread(Runnable {
                terminal.initialization()
                this.runOnUiThread {
                    Toast.makeText(this, "Login finished", Toast.LENGTH_LONG).show()
                }
            }).start()
        }

        btnStatus.setOnClickListener {
            Thread(Runnable {
                terminal.status()
                this.runOnUiThread {
                    Toast.makeText(this, "Status finished", Toast.LENGTH_LONG).show()
                }
            }).start()
        }

        btnTransaction.setOnClickListener {

            lastTransactionId = UUID.randomUUID().toString()

            Thread(Runnable {
                val transactionData = Payment.Builder()
                    .total(BigDecimal.TEN)
                    .currency(CURRENCY)
                    .transactionId(lastTransactionId)
                    .type(PaymentType.SALE)
                    .lastReceiptNumber(5)
                    .build()

                terminal.transaction(transactionData)
                this.runOnUiThread {
                    Toast.makeText(this, "Transaction finished", Toast.LENGTH_LONG).show()
                }
            }).start()
        }

        btnCancelTransaction.setOnClickListener {
            Thread(Runnable {
                val transactionData = Payment.Builder()
                    .total(BigDecimal.TEN)
                    .currency(CURRENCY)
                    .transactionId(lastTransactionId)
                    .type(PaymentType.SALE)
                    .lastReceiptNumber(6)
                    .build()

                terminal.cancelTransaction(transactionData)
                this.runOnUiThread {
                    Toast.makeText(this, "Cancel transaction finished", Toast.LENGTH_LONG).show()
                }
            }).start()
        }

        btnDisconnect.setOnClickListener {
            Thread(Runnable {
                terminal.logout()
                this.runOnUiThread {
                    Toast.makeText(this, "Logout finished", Toast.LENGTH_LONG).show()
                }
            }).start()
        }
    }

}
