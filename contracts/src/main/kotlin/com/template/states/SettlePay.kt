package com.template.states

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class SettlePay (val UserUUID: String, val TokenID:String)