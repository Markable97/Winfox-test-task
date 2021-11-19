package com.glushko.winfox_test_task.data_layer.datasource.response

import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData

data class ResponseServer(val isSuccess: Boolean, val user: LoginData)