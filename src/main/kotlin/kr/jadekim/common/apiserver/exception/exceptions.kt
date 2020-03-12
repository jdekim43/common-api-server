package kr.jadekim.common.apiserver.exception

import kr.jadekim.common.apiserver.protocol.ApiResponse
import kr.jadekim.logger.model.Level
import java.io.File
import java.util.*

private const val defaultMessageResourcesPath = "kr.jadekim.common.apiserver.exception.message"
private val defaultMessageResourcesUri = ApiException::class.java.getResource(defaultMessageResourcesPath).toURI()
private val defaultMessages = File(defaultMessageResourcesUri).readMessageResources()

typealias MessageMap = MutableMap<String, MutableMap<Locale, String>>

var messageMap: MessageMap = defaultMessages.fold(mutableMapOf()) { acc, messages ->
    messages.loadMessagesTo(acc)
}

fun setMessages(locale: Locale, messages: Map<String, String>) {
    messages.forEach { (errorCode, message) ->
        val languageMap = messageMap.getOrPut(errorCode) { mutableMapOf() }
        languageMap[locale] = message
    }
}

fun loadErrorMessage(directory: File) {
    directory.readMessageResources().forEach {
        it.loadMessagesTo(messageMap)
    }
}

fun loadErrorMessage(locale: Locale, file: File) {
    file.inputStream()
        .use { Properties().apply { load(it) } }
        .let { Pair(locale, it) }
        .loadMessagesTo(messageMap)
}

fun MessageMap.getErrorMessage(errorCode: String, locale: Locale? = null): String {
    val localeMap = messageMap.getOrPut(errorCode) {
        messageMap["default"] ?: mutableMapOf(Locale.KOREA to "기타오류가 발생했습니다.")
    }
    val language = locale ?: Locale.getDefault() ?: Locale.KOREA

    return localeMap[language]
        ?: localeMap[Locale.getDefault()]
        ?: localeMap[Locale.KOREA]
        ?: localeMap[Locale.ENGLISH]
        ?: localeMap.values.firstOrNull()
        ?: "기타오류가 발생했습니다."
}

private fun File.readMessageResources() = listFiles { file -> file.extension == "properties" }
    ?.map {
        it.name.toLocale() to it.inputStream().use { Properties().apply { load(it) } }
    }
    ?.filter { it.first != null }
    ?.map { it.first!! to it.second }
    ?: emptyList()

private fun String.toLocale(): Locale? {
    val localeName = substringBefore(".", "")

    return Locale.getAvailableLocales().firstOrNull { it.language == localeName }
}

private fun Pair<Locale, Properties>.loadMessagesTo(messageMap: MessageMap): MessageMap {
    second.forEach { messageName, message ->
        val languageMap = messageMap.getOrPut(messageName.toString()) { mutableMapOf() }
        languageMap[first] = message.toString()
    }

    return messageMap
}

open class ApiException(
    val code: String,
    val httpStatus: Int = 400,
    val data: Any? = null,
    cause: Throwable? = null,
    message: String? = cause?.message,
    val logLevel: Level = Level.WARNING
) : RuntimeException("API-EXCEPTION($code) : $message", cause) {

    open fun toResponse(locale: Locale? = null) = ApiResponse(false, code, getErrorMessage(locale), data)

    open fun getErrorMessage(locale: Locale? = null) = messageMap.getErrorMessage(code, locale)
}

class UnknownException(
    cause: Throwable,
    message: String? = null
) : ApiException(
    code = "COM-1",
    message = message ?: cause.message ?: "Unknown Exception",
    cause = cause,
    httpStatus = 500,
    logLevel = Level.ERROR
)

class ServerException(
    cause: Throwable,
    message: String
) : ApiException(
    code = "COM-2",
    message = message,
    cause = cause,
    httpStatus = 500,
    logLevel = Level.ERROR
)

class AssertException(
    message: String
) : ApiException(
    code = "COM-3",
    message = message,
    logLevel = Level.ERROR
)

class NotFoundException(
    cause: Throwable? = null
) : ApiException(
    code = "COM-4",
    httpStatus = 404,
    cause = cause,
    logLevel = Level.ERROR
)

class UnauthorizedException(
    message: String,
    cause: Throwable? = null
) : ApiException(
    code = "COM-5",
    httpStatus = 401,
    message = message,
    cause = cause
)

class MaintenanceException : ApiException(
    code = "COM-6",
    logLevel = Level.DEBUG
)

class MissingParameterException(
    message: String,
    cause: Throwable? = null
) : ApiException(
    code = "COM-7",
    message = message,
    cause = cause
)

class InvalidParameterException(
    message: String,
    cause: Throwable? = null
) : ApiException(
    code = "COM-8",
    message = message,
    cause = cause
)