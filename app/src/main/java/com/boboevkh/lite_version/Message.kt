package com.boboevkh.lite_version

data class Message(
    val download_speed: String,
    val upload_speed: String,
    val loaded_latency: String,
    val unloaded_latency: String
)
