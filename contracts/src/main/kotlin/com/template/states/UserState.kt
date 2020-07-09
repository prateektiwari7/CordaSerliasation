package com.template.states

import com.template.contracts.UserContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

@BelongsToContract(UserContract::class)
data class UserState(val name: String,
                     val UniqueID: String,
                     val Balance : String,
                     val party: Party,
                     val linearId: UniqueIdentifier,
                     val settlepay:List<SettlePay> = listOf()) : ContractState {

    override val participants: List<AbstractParty> = listOf(party);
    //To change initializer of created properties use File | Settings | File Templates.


}
