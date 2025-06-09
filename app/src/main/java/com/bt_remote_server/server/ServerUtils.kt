package com.bt_remote_server.server

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.bluetooth.BluetoothAdapter
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

object ServerUtils {

    @SuppressLint("MissingPermission")
    private fun setBluetoothAdapterName(context: Context) {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val serverName = sharedPreferences.getString("server_name", "Default Server Name")

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.name = serverName
    }

    fun startServer(context: Context) {
        //setBluetoothAdapterName(context)
        val intent = Intent(context, BluetoothServerService::class.java).apply {
            action = BluetoothServerService.ACTION_START_SERVER
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopServer(context: Context) {
        val intent = Intent(context, BluetoothServerService::class.java)
        context.stopService(intent)
    }
}