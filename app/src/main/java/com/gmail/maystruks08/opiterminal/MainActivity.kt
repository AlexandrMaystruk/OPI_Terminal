package com.gmail.maystruks08.opiterminal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gmail.maystruks08.opiterminal.terminal.Payment
import com.gmail.maystruks08.opiterminal.terminal.PaymentType
import com.gmail.maystruks08.opiterminal.terminal.Terminal
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*

private const val CVV_PORT_IN = 20007
private const val CVV_PORT_OUT = 20002

private const val INGENIKO_PORT_IN = 5577
private const val INGENIKO_PORT_OUT = 5578

private const val TIMEOUT = 10000

private const val TERMINAL_IP = "192.168.0.114"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val terminal = Terminal.Builder()
            .ipAddress(TERMINAL_IP)
            .inputPort(INGENIKO_PORT_IN)
            .outputPort(INGENIKO_PORT_OUT)
            .timeout(TIMEOUT)
            .build()

        btnLogin.setOnClickListener {
            Thread(Runnable {
                terminal.login()
                this.runOnUiThread {
                    Toast.makeText(this, "Login finished", Toast.LENGTH_LONG).show()
                }
            }).start()
        }

        btnTransaction.setOnClickListener {
            Thread(Runnable {
                val transactionData = Payment.Builder()
                    .total(BigDecimal.TEN)
                    .currency("EUR")
                    .transactionId(UUID.randomUUID().toString())
                    .type(PaymentType.SALE)
                    .build()

                terminal.transaction(transactionData)
                this.runOnUiThread {
                    Toast.makeText(this, "Transaction finished", Toast.LENGTH_LONG).show()
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
