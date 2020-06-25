package kr.jadekim.common.apiserver.exception

import kr.jadekim.common.apiserver.protocol.ApiResponse
import kr.jadekim.common.util.exception.ExceptionLevel
import kr.jadekim.common.util.exception.FriendlyException
import kr.jadekim.logger.model.Level
import java.util.*

private val Level.exceptionLevel
    get() = when (this) {
        Level.ERROR -> ExceptionLevel.ERROR
        Level.WARNING -> ExceptionLevel.WARNING
        else -> ExceptionLevel.DEBUG
    }

private val ExceptionLevel.httpStatus
    get() = when (this) {
        ExceptionLevel.ERROR -> 500
        else -> 400
    }

private val ExceptionLevel.logLevel
    get() = when (this) {
        ExceptionLevel.ERROR -> Level.ERROR
        ExceptionLevel.WARNING -> Level.WARNING
        else -> Level.INFO
    }

open class ApiException(
        code: String,
        val httpStatus: Int = 400,
        data: Any? = null,
        cause: Throwable? = null,
        message: String? = cause?.message,
        val logLevel: Level = Level.WARNING
) : FriendlyException(code, data, cause, message, logLevel.exceptionLevel) {

    companion object {

        @JvmStatic
        fun of(exception: FriendlyException) = ApiException(
                exception.code,
                exception.level.httpStatus,
                exception.data,
                exception.cause,
                exception.message,
                exception.level.logLevel
        )
    }

    open fun toResponse(locale: Locale? = null) = ApiResponse(
            false,
            code,
            getFriendlyMessage(locale?.language),
            data
    )
}

class NotFoundException(
        cause: Throwable? = null
) : ApiException(
        code = "CAS-1",
        httpStatus = 404,
        cause = cause,
        logLevel = Level.ERROR
)

class UnauthorizedException(
        message: String,
        cause: Throwable? = null
) : ApiException(
        code = "CAS-2",
        httpStatus = 401,
        message = message,
        cause = cause
)

class MaintenanceException : ApiException(
        code = "CAS-3",
        logLevel = Level.DEBUG
)

class MissingParameterException(
        message: String,
        cause: Throwable? = null
) : ApiException(
        code = "CAS-4",
        message = message,
        cause = cause
)

class InvalidParameterException(
        message: String,
        cause: Throwable? = null
) : ApiException(
        code = "CAS-5",
        message = message,
        cause = cause
)