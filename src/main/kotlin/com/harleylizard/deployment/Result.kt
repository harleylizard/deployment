package com.harleylizard.deployment

sealed interface Result {
    val ok: Boolean

    val reason: String?

    companion object {

        fun ok(): Result = OkResult()

        fun error(message: String): Result = ErrorResult(message)
    }

}