package kr.jadekim.common.apiserver.protocol

open class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
)