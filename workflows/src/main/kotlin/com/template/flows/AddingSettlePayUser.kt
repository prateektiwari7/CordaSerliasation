package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.UserContract
import com.template.states.SettlePay
import com.template.states.UserState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

@InitiatingFlow
@StartableByRPC
class AddingSettlePayUser(val EditUUID : String, val SettleUUID : String,val Authtoken : String) : FlowLogic<SignedTransaction>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() :SignedTransaction {
        val UserStateref = serviceHub.vaultService.queryBy<UserState>().states
        val inputStateAndRef = UserStateref.filter { it.state.data.UniqueID.equals(EditUUID)}[0]
        val Transactionsdata = SettlePay(SettleUUID,Authtoken)
        val input = inputStateAndRef.state.data
        var TransactionList = ArrayList<SettlePay>()
        val command = Command(UserContract.Commands.Create(), listOf(ourIdentity).map { it.owningKey } )
        TransactionList.add(Transactionsdata)
        for (item in input.settlepay){
            TransactionList.add(item)
        }

        val output = input.copy(settlepay = TransactionList)

        val txBuilder = TransactionBuilder(inputStateAndRef.state.notary)
                .addInputState(inputStateAndRef)
                .addOutputState(output,UserContract.ID)
                .addCommand(command)

        txBuilder.verify(serviceHub)

        // Sign the transaction
        val stx = serviceHub.signInitialTransaction(txBuilder)


        var txResult =  subFlow(FinalityFlow(stx))

        return txResult

    }
}

@InitiatedBy(AddingSettlePayUser::class)
class AddingSettlePayUser_responder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call():SignedTransaction {
        subFlow(object : SignTransactionFlow(counterpartySession) {
            @Throws(FlowException::class)
            override fun checkTransaction(stx: SignedTransaction) {
            }
        })
        return subFlow(ReceiveFinalityFlow(counterpartySession))
    }
}
