package com.glushko.winfox_test_task.business_logic_layer.domain

data class Place(
    val id: String,
    val name: String,
    val desc: String,
    val image: String,
    val latitide: Double,
    val longitude: Double
)
