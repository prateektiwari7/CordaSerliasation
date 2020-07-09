package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.UserContract
import com.template.states.UserState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.contracts.requireThat
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

// *********
// * Flows *
// *********

@InitiatingFlow
@StartableByRPC
class CreateUserState constructor( var name:String, val UniqueID: String,
                            var Balance : String): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call() : SignedTransaction {

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val command = Command(UserContract.Commands.Create(), listOf(ourIdentity).map { it.owningKey } )
        val userstate = UserState(name,UniqueID,Balance,ourIdentity, UniqueIdentifier())

        val txBuilder = TransactionBuilder(notary)
                .addOutputState(userstate, UserContract.ID)
                .addCommand(command)
        txBuilder.verify(serviceHub)

        val stx = serviceHub.signInitialTransaction(txBuilder)


        var txResult =  subFlow(FinalityFlow(stx))

        return txResult
    }

}

@InitiatedBy(CreateUserState::class)
class CreateUserState_Responder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call() : SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data

            }
        }
        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(counterpartySession, txWeJustSignedId.id))
    }
}