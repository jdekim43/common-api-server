package kr.jadekim.common.apiserver

import kr.jadekim.common.apiserver.enumuration.Environment
import kr.jadekim.common.apiserver.enumuration.IEnvironment
import kr.jadekim.logger.JLog
import kr.jadekim.logger.context.GlobalLogContext
import java.time.Duration

abstract class AbstractServer(
        val serviceEnv: IEnvironment = Environment.LOCAL,
        val port: Int = 8080,
        val release: String = "not_set",
        serverName: String? = null
) {

    val serverName = serverName ?: javaClass.simpleName

    protected val logger = JLog.get(javaClass)

    init {
        GlobalLogContext["serviceEnv"] = serviceEnv.name
        GlobalLogContext["servicePort"] = port
        GlobalLogContext["deployVersion"] = release
        GlobalLogContext["serverName"] = this.serverName
    }

    abstract fun start()

    abstract fun stop(timeout: Duration = Duration.ofSeconds(30))

    protected fun inStart(block: () -> Unit) {
        logger.info("Start $serverName : service_env=${serviceEnv.name}, service_port=$port")

        block()
    }

    protected fun inStop(block: () -> Unit) {
        logger.info("Request stop $serverName")

        block()

        logger.info("Stopped $serverName")
    }
}