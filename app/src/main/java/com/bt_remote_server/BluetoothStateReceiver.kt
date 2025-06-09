package com.bt_remote_server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.bluetooth.BluetoothAdapter
import android.util.Log
import com.bt_remote_server.server.ServerUtils

class BluetoothStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BluetoothStateReceiver", "Broadcast Receiver")
        if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    Log.d("BluetoothStateReceiver", "Bluetooth is off")
                    stopGATTServer(context)
                }
                BluetoothAdapter.STATE_ON -> {
                    Log.d("BluetoothStateReceiver", "Bluetooth is on")
                    startGATTServer(context)
                }
            }
        }
    }

    private fun startGATTServer(context: Context) {
        ServerUtils.startServer(context)
    }

    private fun stopGATTServer(context: Context) {
        ServerUtils.stopServer(context)
    }
}