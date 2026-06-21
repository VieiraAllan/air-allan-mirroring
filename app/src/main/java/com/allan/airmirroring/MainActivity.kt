package com.allan.airmirroring

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var statusText: TextView
    private lateinit var deviceNameText: TextView
    private lateinit var nativeStatusText: TextView
    private lateinit var btnStartAirPlay: Button
    private lateinit var btnStopAirPlay: Button
    private lateinit var btnAbout: Button

    private val nativeBridge = NativeReceiverBridge()

    private val uiHandler = Handler(Looper.getMainLooper())
    private val statusUpdater = object : Runnable {
        override fun run() {
            updateUiState()
            uiHandler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleText = findViewById(R.id.titleText)
        statusText = findViewById(R.id.statusText)
        deviceNameText = findViewById(R.id.deviceNameText)
        nativeStatusText = findViewById(R.id.nativeStatusText)
        btnStartAirPlay = findViewById(R.id.btnStartAirPlay)
        btnStopAirPlay = findViewById(R.id.btnStopAirPlay)
        btnAbout = findViewById(R.id.btnAbout)

        deviceNameText.text = "Nome do receptor: Air Állan Mirroring"

        btnStartAirPlay.setOnClickListener {
            nativeBridge.startReceiver()

            val intent = Intent(this, AirPlayReceiverService::class.java).apply {
                action = AirPlayReceiverService.ACTION_START
            }
            ContextCompat.startForegroundService(this, intent)

            updateUiState()
            Toast.makeText(this, "Receptor iniciado", Toast.LENGTH_SHORT).show()
        }

        btnStopAirPlay.setOnClickListener {
            nativeBridge.stopReceiver()

            val intent = Intent(this, AirPlayReceiverService::class.java).apply {
                action = AirPlayReceiverService.ACTION_STOP
            }
            startService(intent)

            updateUiState()
            Toast.makeText(this, "Receptor parado", Toast.LENGTH_SHORT).show()
        }

        btnAbout.setOnClickListener {
            val msg = """
                Air Állan Mirroring
                Versão 0.6.0
                
                Fase atual:
                Core nativo AirPlay-ready
            """.trimIndent()

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        updateUiState()
        btnStartAirPlay.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        uiHandler.post(statusUpdater)
    }

    override fun onPause() {
        super.onPause()
        uiHandler.removeCallbacks(statusUpdater)
    }

    private fun updateUiState() {
        val running = nativeBridge.isRunning()

        statusText.text = if (running) {
            "Status: receptor AirPlay iniciado"
        } else {
            "Status: aguardando início"
        }

        nativeStatusText.text =
            "${nativeBridge.receiverVersion()}\n${nativeBridge.statusText()}"

        if (running) {
            btnStartAirPlay.text = "Receptor iniciado"
            btnStartAirPlay.isEnabled = false

            btnStopAirPlay.text = "Parar receptor"
            btnStopAirPlay.isEnabled = true
        } else {
            btnStartAirPlay.text = "Iniciar receptor AirPlay"
            btnStartAirPlay.isEnabled = true

            btnStopAirPlay.text = "Parar receptor"
            btnStopAirPlay.isEnabled = false
        }
    }
}