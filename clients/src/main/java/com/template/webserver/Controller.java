package com.template.webserver;

import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.template.flows.AccountService;
import net.corda.core.contracts.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.internal.X500UtilsKt;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    /**
     * Tests that the Springboot Rest api is up and running.
     * Example request:
     * curl -X GET 'http://localhost:10050/api/test'
     */
    @GetMapping(value = "/test", produces = "text/plain")
    private String test() {
        return "Test is successful.";
    }

    /**
     * Gets all accounts using the Accounts SDK.
     * Example request:
     * curl -X GET 'http://localhost:10050/api/accounts'
     */
    @GetMapping(value = "/accounts", produces = {TEXT_PLAIN_VALUE})
    private ResponseEntity<String> accounts() {
        ResponseEntity<String> response;
        try {
            List<StateAndRef<AccountInfo>> allAccounts = this.proxy.startFlowDynamic(AccountService.FetchAllAccounts.class, new Object[]{}).getReturnValue().get();
            String output = "";
            for (StateAndRef<AccountInfo> account : allAccounts)
                output += account;
            response = ResponseEntity.status(HttpStatus.OK).body("AllAccounts: " + output);
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return response;
    }

    /**
     * Creates an account using the Accounts SDK.
     * Example request:
     * curl -X POST 'http://localhost:10050/api/accounts/create?accountName=MyNewAccountName'
     */
    @PostMapping(value = "/accounts/create", produces = {TEXT_PLAIN_VALUE})
    public final ResponseEntity<String> createAccount(@RequestParam("accountName") @NotNull String accountName) {
        ResponseEntity<String> response;
        try {
            this.proxy.startFlowDynamic(AccountService.CreateNewAccount.class, new Object[]{accountName}).getReturnValue().get();
            response = ResponseEntity.status(HttpStatus.CREATED).body("Account with name: " + accountName);
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return response;
    }
}