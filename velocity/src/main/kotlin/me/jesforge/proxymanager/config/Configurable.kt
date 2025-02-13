package me.jesforge.proxymanager.config

interface Configurable {
    fun save()
    fun load() {}
    fun reset() {}
}