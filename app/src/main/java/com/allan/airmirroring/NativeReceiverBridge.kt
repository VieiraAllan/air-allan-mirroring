package com.allan.airmirroring

class NativeReceiverBridge {

    external fun receiverVersion(): String
    external fun startReceiver()
    external fun stopReceiver()
    external fun isRunning(): Boolean
    external fun statusText(): String

    companion object {
        init {
            System.loadLibrary("airallan_native")
        }
    }
}