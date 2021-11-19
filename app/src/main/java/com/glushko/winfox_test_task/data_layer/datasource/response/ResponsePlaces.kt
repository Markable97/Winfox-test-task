package com.glushko.winfox_test_task.data_layer.datasource.response

import com.glushko.winfox_test_task.business_logic_layer.domain.Place

data class ResponsePlaces(
    val isSuccess: Boolean,
    val places: List<Place> = listOf()
)
