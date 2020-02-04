package kr.jadekim.common.apiserver.enumuration

enum class Environment {
    LOCAL,
    DEVELOPMENT,
    QA,
    STAGE,
    PRODUCTION;

    companion object {
        @JvmStatic
        fun from(name: String?): Environment? = when (name?.toLowerCase()) {
            "local" -> LOCAL
            "dev", "development" -> DEVELOPMENT
            "qa" -> QA
            "stage", "staging" -> STAGE
            "prd", "prod", "production", "real", "live" -> PRODUCTION
            else -> null
        }
    }
}

fun Number.isTrue(serviceEnv: Environment) = when {
    this == 1 -> true
    this == 2 && serviceEnv == Environment.STAGE -> true
    else -> false
}