package com.bt_remote_server

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val saveButton: Button = findViewById(R.id.save_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        val serverNameEditText: EditText = findViewById(R.id.server_name_edit_text)
        val clientMacAddressEditText: EditText = findViewById(R.id.client_mac_address_edit_text)

        // Load the server name and MAC address from SharedPreferences
        val serverName = preferences.getString("server_name", "")
        val clientMacAddress = preferences.getString("client_mac_address", "00:00:00:00:00:00")

        // Set the loaded values to the EditText fields
        serverNameEditText.setText(serverName)
        clientMacAddressEditText.setText(clientMacAddress)

        // Set an OnClickListener on the save button
        saveButton.setOnClickListener {
            val newServerName = serverNameEditText.text.toString()
            val newClientMacAddress = clientMacAddressEditText.text.toString()

            // Validate the MAC address format
            if (isValidMacAddress(newClientMacAddress)) {
                // Save the new values to SharedPreferences
                preferences.edit().putString("server_name", newServerName).apply()
                preferences.edit().putString("client_mac_address", newClientMacAddress).apply()

                // Exit the settings activity
                finish()
            } else {
                // Show an error message
                Toast.makeText(this, "Invalid MAC address format", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    // Function to validate MAC address format
    private fun isValidMacAddress(macAddress: String): Boolean {
        val macRegex = Regex("^([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2})$")
        return macAddress.matches(macRegex)
    }
}
