package kr.jadekim.common.apiserver.protocol

import kotlinx.serialization.Serializable

@Serializable
open class ApiResponse<T>(
        val isSuccess: Boolean = true,
        val errorCode: String? = null,
        val errorMessage: String? = null,
        val data: T? = null,
        val timestamp: Long = System.currentTimeMillis()
)

@Suppress("FunctionName")
fun <T> SuccessResponse(
        data: T
) = ApiResponse(
        isSuccess = true,
        data = data
)

@Suppress("FunctionName")
fun <T> ErrorResponse(
        errorCode: String,
        errorMessage: String,
        data: T? = null
) = ApiResponse(
        isSuccess = false,
        errorCode = errorCode,
        errorMessage = errorMessage,
        data = data
)