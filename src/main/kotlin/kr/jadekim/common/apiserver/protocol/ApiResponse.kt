package kr.jadekim.common.apiserver.protocol

open class ApiResponse<T>(
    val isSuccess: Boolean = true,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
)