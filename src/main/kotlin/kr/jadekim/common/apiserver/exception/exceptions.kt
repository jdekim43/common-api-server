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
    messages.forEach { (messageName, message) ->
        val languageMap = messageMap.getOrPut(messageName) { mutableMapOf() }
        languageMap[locale] = message
    }
}

fun setMessageResources(directory: File) {
    directory.readMessageResources().forEach {
        it.loadMessagesTo(messageMap)
    }
}

fun MessageMap.getMessage(name: String, locale: Locale? = null): String {
    val localeMap = messageMap.getOrPut(name) {
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
    val code: Int,
    val httpStatus: Int = 500,
    val data: Any? = null,
    cause: Throwable? = null,
    message: String? = cause?.message,
    val logLevel: Level = Level.WARNING
) : RuntimeException("API-EXCEPTION($code) : $message", cause) {

    open fun toResponse(locale: Locale? = null) = ApiResponse(code, getAlertMessage(locale), data)

    fun getAlertMessage(locale: Locale? = null) = messageMap.getMessage(javaClass.simpleName, locale)
}

// 1 ~ 99 : 공통 오류
class UnknownException(
    cause: Throwable,
    message: String? = null,
    data: Any? = null
) : ApiException(
    code = 1,
    data = data,
    message = message ?: cause.message ?: "Unknown Exception",
    cause = cause,
    logLevel = Level.ERROR
)

class ServerException(
    cause: Throwable,
    message: String,
    data: Any? = null
) : ApiException(
    code = 2,
    data = data,
    message = message,
    cause = cause,
    logLevel = Level.ERROR
)

class NotFoundException(
    cause: Throwable? = null
) : ApiException(
    code = 4,
    cause = cause,
    logLevel = Level.ERROR
)

class UnauthorizedException(
    val token: String,
    cause: Throwable? = null
) : ApiException(
    code = 5,
    httpStatus = 401,
    cause = cause
)

class MaintenanceException : ApiException(
    code = 6,
    httpStatus = 400,
    logLevel = Level.DEBUG
)

class MissingParameterException(message: String, cause: Throwable? = null) : ApiException(
    code = 7,
    message = message,
    httpStatus = 400,
    cause = cause
)

class InvalidParameterException(message: String, cause: Throwable? = null) : ApiException(
    code = 8,
    message = message,
    httpStatus = 400,
    cause = cause
)