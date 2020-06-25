package kr.jadekim.common.apiserver.protocol

open class ApiResponse<T>(
        val isSuccess: Boolean = true,
        val errorCode: String? = null,
        val errorMessage: String? = null,
        val data: T? = null,
        val timestamp: Long = System.currentTimeMillis()
)

open class SuccessResponse<T>(
        data: T
) : ApiResponse<T>(
        isSuccess = true,
        data = data
)

open class ErrorResponse<T>(
        errorCode: String,
        errorMessage: String,
        data: T? = null
) : ApiResponse<T>(
        isSuccess = false,
        errorCode = errorCode,
        errorMessage = errorMessage,
        data = data
)