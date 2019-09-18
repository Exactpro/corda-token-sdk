package com.r3.corda.lib.tokens.workflows.internal.flows.finality

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.node.StatesToRecord
import net.corda.core.utilities.unwrap

class ObserverAwareFinalityFlowHandler(val otherSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        logger.info("zZT1-OAFF ObserverAwareFinalityFlowHandler start")
        val role = otherSession.receive<TransactionRole>().unwrap { it }
        val statesToRecord = when (role) {
            TransactionRole.PARTICIPANT -> StatesToRecord.ONLY_RELEVANT
            TransactionRole.OBSERVER -> StatesToRecord.ALL_VISIBLE
        }
        // If states are issued to self, then ReceiveFinalityFlow does not need to be invoked.
        if (!serviceHub.myInfo.isLegalIdentity(otherSession.counterparty)) {
            subFlow(ReceiveFinalityFlow(otherSideSession = otherSession, statesToRecord = statesToRecord))
            logger.info("zZT1-OAFF ObserverAwareFinalityFlowHandler finish")
        }
    }
}

