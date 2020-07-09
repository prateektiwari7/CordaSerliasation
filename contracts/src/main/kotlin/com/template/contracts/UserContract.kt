package com.template.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.requireSingleCommand

class UserContract : Contract {

    companion object{
        const val ID = "com.template.contracts.UserContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        requireThat {

        }
    }

    interface Commands : CommandData {
        class Create : UserContract.Commands;
    }
}