package com.bt_remote_server

data class AppConfig(
    var haToken: String = "",
    var buttonConfigs: MutableList<ButtonConfig> = MutableList(4) { ButtonConfig(name = "Button ${it + 1}") }
)