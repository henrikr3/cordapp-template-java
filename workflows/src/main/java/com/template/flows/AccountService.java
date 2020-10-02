package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AllAccounts;
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;

import java.util.List;

/// For all Accounts SDK flows, see: https://github.com/corda/accounts/tree/master/workflows/src/main/kotlin/com/r3/corda/lib/accounts/workflows/flows
public abstract class AccountService {
    @InitiatingFlow
    @StartableByRPC
    public static class FetchAllAccounts extends FlowLogic<List<StateAndRef<AccountInfo>>> {
        @Suspendable
        @Override
        public List<StateAndRef<AccountInfo>>  call() throws FlowException {
            return (List<StateAndRef<AccountInfo>>) subFlow(new AllAccounts());
        }
    }

    @InitiatingFlow
    @StartableByRPC
    public static class CreateNewAccount extends FlowLogic<StateAndRef<AccountInfo>> {
        private String AccountName = "";
        public CreateNewAccount(String name) {
            this.AccountName = name;
        }

        @Suspendable
        @Override
        public StateAndRef<AccountInfo>  call() throws FlowException {
            return (StateAndRef<AccountInfo>) subFlow(new CreateAccount(this.AccountName));
        }
    }
}
