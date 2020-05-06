package kr.jadekim.common.apiserver

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kr.jadekim.common.apiserver.enumuration.Environment
import kr.jadekim.common.apiserver.enumuration.IEnvironment
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.time.Duration

abstract class BaseSimpleServer(
        serviceEnv: IEnvironment = Environment.LOCAL,
        port: Int = 8080,
        release: String = "not_set",
        serverName: String? = null
) : AbstractServer(serviceEnv, port, release, serverName) {

    private val server = HttpServer.create(InetSocketAddress(port), 0)

    override fun start() = inStart {
        server.start()
    }

    override fun stop(timeout: Duration) = inStop {
        server.stop(timeout.seconds.toInt())
    }

    protected fun route(path: String, block: HttpExchange.() -> Unit) {
        server.createContext(path, block)
    }

    protected fun HttpExchange.response(body: String, httpStatus: Int = 200, contentType: String = "text/plain") {
        responseHeaders.add("Content-Type", contentType)

        sendResponseHeaders(httpStatus, body.length.toLong())

        PrintWriter(responseBody).use {
            it.print(body)
        }
    }
}