package me.jesforge.servermanager.config

interface Configurable {
    fun save()
    fun load() {}
    fun reset() {}
}