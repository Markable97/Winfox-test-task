package com.glushko.winfox_test_task.data_layer.datasource.response

import com.glushko.winfox_test_task.business_logic_layer.domain.Menu
import com.glushko.winfox_test_task.data_layer.datasource.ApiService

data class ResponseMenu(
    val isSuccess: Boolean,
    val menu: List<Menu> = listOf()
){
    companion object{
        fun createMap(idPlace: String):Map<String, String>{
            val map = HashMap<String, String>()
            map[ApiService.PARAM_PLACE_ID] = idPlace
            return map
        }
    }
}
