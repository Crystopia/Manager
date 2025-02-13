package me.jesforge.servermanager.config

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    var template: String?
)

