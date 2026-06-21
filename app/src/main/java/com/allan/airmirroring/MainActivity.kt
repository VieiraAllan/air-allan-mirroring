package com.allan.airmirroring

import android.content.Intent
import android.os.Bundle
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

    private var receiverRunning = false

    external fun nativeReceiverVersion(): String

    companion object {
        init {
            System.loadLibrary("airallan_native")
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
        nativeStatusText.text = nativeReceiverVersion()

        updateUiState()

        btnStartAirPlay.setOnClickListener {
            val intent = Intent(this, AirPlayReceiverService::class.java).apply {
                action = AirPlayReceiverService.ACTION_START
            }
            ContextCompat.startForegroundService(this, intent)

            receiverRunning = true
            updateUiState()

            Toast.makeText(this, "Receptor iniciado", Toast.LENGTH_SHORT).show()
        }

        btnStopAirPlay.setOnClickListener {
            val intent = Intent(this, AirPlayReceiverService::class.java).apply {
                action = AirPlayReceiverService.ACTION_STOP
            }
            startService(intent)

            receiverRunning = false
            updateUiState()

            Toast.makeText(this, "Receptor parado", Toast.LENGTH_SHORT).show()
        }

        btnAbout.setOnClickListener {
            val msg = """
                Air Állan Mirroring
                Versão 0.4.0
                
                Fase atual:
                Pipeline nativo JNI/CMake preparado
            """.trimIndent()

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        btnStartAirPlay.requestFocus()
    }

    private fun updateUiState() {
        if (receiverRunning) {
            statusText.text = "Status: receptor AirPlay iniciado"
            btnStartAirPlay.text = "Receptor iniciado"
            btnStartAirPlay.isEnabled = false

            btnStopAirPlay.text = "Parar receptor"
            btnStopAirPlay.isEnabled = true
        } else {
            statusText.text = "Status: aguardando início"
            btnStartAirPlay.text = "Iniciar receptor AirPlay"
            btnStartAirPlay.isEnabled = true

            btnStopAirPlay.text = "Parar receptor"
            btnStopAirPlay.isEnabled = false
        }
    }
}
