package com.bt_remote_server

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ButtonConfigActivity : AppCompatActivity() {

    private lateinit var spinnerButtonIndex: Spinner
    private lateinit var editName: EditText
    private lateinit var editShortEntity: EditText
    private lateinit var spinnerShortAction: Spinner
    private lateinit var editLongEntity: EditText
    private lateinit var spinnerLongAction: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var editToken: EditText

    private lateinit var appConfig: AppConfig
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button_config)

        spinnerButtonIndex = findViewById(R.id.spinnerButtonIndex)
        editName = findViewById(R.id.editName)
        editShortEntity = findViewById(R.id.editShortEntity)
        spinnerShortAction = findViewById(R.id.spinnerShortAction)
        editLongEntity = findViewById(R.id.editLongEntity)
        spinnerLongAction = findViewById(R.id.spinnerLongAction)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        editToken = findViewById(R.id.editToken)

        loadConfig()

        val buttonLabels = listOf("Button 1", "Button 2", "Button 3", "Button 4")
        spinnerButtonIndex.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, buttonLabels)

        spinnerButtonIndex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedIndex = position
                showConfig(appConfig.buttonConfigs[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val actions = listOf("on", "off", "toggle")
        val actionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, actions)
        spinnerShortAction.adapter = actionAdapter
        spinnerLongAction.adapter = actionAdapter

        // Initialize token field
        editToken.setText(appConfig.haToken)

        btnSave.setOnClickListener {
            // Update current button config
            val config = ButtonConfig(
                name = editName.text.toString(),
                shortPressEntity = editShortEntity.text.toString(),
                shortPressAction = spinnerShortAction.selectedItem.toString(),
                longPressEntity = editLongEntity.text.toString(),
                longPressAction = spinnerLongAction.selectedItem.toString()
            )
            appConfig.buttonConfigs[selectedIndex] = config

            // Update token
            appConfig.haToken = editToken.text.toString()

            saveConfig()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showConfig(config: ButtonConfig) {
        editName.setText(config.name)
        editShortEntity.setText(config.shortPressEntity)
        spinnerShortAction.setSelection(getActionIndex(config.shortPressAction))
        editLongEntity.setText(config.longPressEntity)
        spinnerLongAction.setSelection(getActionIndex(config.longPressAction))
    }

    private fun getActionIndex(action: String): Int {
        return when (action.lowercase()) {
            "on" -> 0
            "off" -> 1
            "toggle" -> 2
            else -> 0
        }
    }

    private fun loadConfig() {
        val prefs = getSharedPreferences("button_config_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("appConfig", null)
        appConfig = if (json != null) {
            Gson().fromJson(json, AppConfig::class.java)
        } else {
            AppConfig()
        }
    }

    private fun saveConfig() {
        val prefs = getSharedPreferences("button_config_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(appConfig)
        editor.putString("appConfig", json)
        editor.apply()
    }
}
