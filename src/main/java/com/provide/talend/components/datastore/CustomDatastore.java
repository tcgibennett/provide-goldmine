package com.provide.talend.components.datastore;

import java.io.Serializable;
import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("CustomDatastore")
@Checkable
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "Token" }),
    @GridLayout.Row({ "Wallet" }),
        @GridLayout.Row({"Contract"})
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomDatastore implements Serializable {
    @Option
    @Required
    @Documentation("TODO fill the documentation for this parameter")
    private String Token;

    @Option
    @Required
    @Documentation("TODO fill the documentation for this parameter")
    private String Wallet;

    @Option
    @ActiveIf(target = "Token", value="0", negate = true, evaluationStrategy = ActiveIf.EvaluationStrategy.LENGTH)
    @Suggestable(value="loadContracts", parameters = {"Token"})
    @Documentation("TODO")
    private String Contract;


    /*
    @Option
    @Proposable("contractsList")
    @ActiveIfs(operator = ActiveIfs.Operator.AND, value= {
            @ActiveIf(target = "Token", value ="0", negate = true, evaluationStrategy = ActiveIf.EvaluationStrategy.LENGTH),
            @ActiveIf(target = "Wallet", value ="0", negate = true, evaluationStrategy = ActiveIf.EvaluationStrategy.LENGTH)
    })
    @Documentation("TODO fill the documentation for this parameter")
    private String Contract;
    */
    public String getToken() {
        return Token;
    }

    public CustomDatastore setToken(String Token) {
        this.Token = Token;
        return this;
    }

    public String getWallet() {
        return Wallet;
    }

    public CustomDatastore setWallet(String Wallet) {
        this.Wallet = Wallet;
        return this;
    }

    public String getContract() {
        return Contract;
    }

    public CustomDatastore setContract(String contract) {
        Contract = contract;
        return this;
    }

}