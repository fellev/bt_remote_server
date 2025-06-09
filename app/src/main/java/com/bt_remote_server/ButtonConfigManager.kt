package com.bt_remote_server
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ButtonConfig(
    var name: String = "Button",
    var shortPressEntity: String = "",
    var shortPressAction: String = "on",   // "on" or "off"
    var longPressEntity: String = "",
    var longPressAction: String = "off"    // "on" or "off"
)

class ButtonConfigManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("button_configs", Context.MODE_PRIVATE)

    var configs: MutableList<ButtonConfig> = MutableList(4) { ButtonConfig(name = "Button ${it + 1}") }

    init {
        loadConfigs()
    }

    private fun loadConfigs() {
        val json = prefs.getString("configs", null) ?: return
        val array = JSONArray(json)
        configs.clear()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            configs.add(ButtonConfig(
                name = obj.optString("name"),
                shortPressEntity = obj.optString("shortPressEntity"),
                shortPressAction = obj.optString("shortPressAction"),
                longPressEntity = obj.optString("longPressEntity"),
                longPressAction = obj.optString("longPressAction")
            ))
        }
    }

    fun saveConfigs() {
        val array = JSONArray()
        configs.forEach {
            array.put(JSONObject().apply {
                put("name", it.name)
                put("shortPressEntity", it.shortPressEntity)
                put("shortPressAction", it.shortPressAction)
                put("longPressEntity", it.longPressEntity)
                put("longPressAction", it.longPressAction)
            })
        }
        prefs.edit().putString("configs", array.toString()).apply()
    }

    fun getAction(type: String, buttonIndex: Int): Pair<String, String>? {
        if (buttonIndex !in 0..3) return null
        val cfg = configs[buttonIndex]
        return when (type) {
            "short" -> Pair(cfg.shortPressEntity, cfg.shortPressAction)
            "long" -> Pair(cfg.longPressEntity, cfg.longPressAction)
            else -> null
        }
    }
}
