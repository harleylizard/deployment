package com.harleylizard.deployment

class ErrorResult(override val reason: String) : Result {
    override val ok = false

}