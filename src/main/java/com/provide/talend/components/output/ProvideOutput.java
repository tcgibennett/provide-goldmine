package com.provide.talend.components.output;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.provide.talend.components.custom.ContractDetail;
import com.provide.talend.components.custom.IPFSFields;
import com.provide.talend.components.dataset.CustomDataset;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import com.provide.talend.components.service.ProvideGoldmineService;
import org.talend.sdk.component.api.record.Schema;
import services.provide.client.IPFSClient;
import services.provide.client.microservices.Goldmine;
import services.provide.client.microservices.Ident;
import services.provide.dao.Contract;
import services.provide.dao.Field;
import services.provide.dao.Function;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(Icon.IconType.STAR) // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding icons/filename_icon32.png in resources
@Processor(name = "Output")
@Documentation("TODO fill the documentation for this processor")
public class ProvideOutput implements Serializable {
    private final ProvideOutputConfiguration configuration;
    private final ProvideGoldmineService service;
    private Goldmine goldmine = null;
    private Ident ident = null;
    private IPFSClient ipfsClient = null;
    private Contract[] contracts;
    private Contract contract;
    private ContractDetail contractDetail;
    private String app_id;
    private String wallet;
    private String network_id;
    private ArrayList<String> ipfs_fields = null;
    private boolean doIPFS = false;
    private Function function;
    private String function_name;

    public ProvideOutput(@Option("configuration") final ProvideOutputConfiguration configuration,
                         final ProvideGoldmineService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String token = this.configuration.getDataset().getDatastore().getToken();
        String sel_contract = this.configuration.getDataset().getDatastore().getContract();
        this.function_name = sel_contract.split("::")[2];
        this.wallet = this.configuration.getDataset().getDatastore().getWallet();
        this.goldmine = (Goldmine) Goldmine.init(token);
        this.ident = (Ident) Ident.init(token);
        this.ipfsClient = (IPFSClient) IPFSClient.init(null, null, null);
        ArrayList<String> contracts_json = (ArrayList<String>)this.goldmine.fetchContracts(null);

        this.contracts = this.goldmine.getContracts(contracts_json.get(1));
        this.contract = this.fetchContract(sel_contract.split("::")[1]);
        this.app_id = (String) this.goldmine.getApplicationId(token);
        ContractDetail[] contractDetails = null;
        IPFSFields fields = null;

        try {
            contractDetails = mapper.readValue(contracts_json.get(1), ContractDetail[].class);

            ArrayList<String> detail = (ArrayList<String>) this.goldmine.fetchContractDetails(this.contract.getId());
            this.contractDetail = this.getContractDetail(contractDetails, this.contract.getId().toString());
            boolean doIPFS = this.hasIPFSFields(this.contract);


            if (doIPFS) {
                ArrayList<String> xfields = this.getIPFSFields(this.contractDetail.getNetwork_id(), this.app_id, this.wallet, this.contract.getAddress().toString());
                fields = mapper.readValue(xfields.get(1), IPFSFields.class);
                this.ipfs_fields = new ArrayList<>();
                for (String field : fields.getResponse())
                {
                    this.ipfs_fields.add(field);
                }
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        for (Function function : this.contract.getFunctions())
        {
            if (function.getName().equals(sel_contract.split("::")[2]))
            {
                this.function = function;
                break;
            }
        }
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input final Record defaultInput) {
        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).
        List<Schema.Entry> schemaEntry = defaultInput.getSchema().getEntries();
        ArrayList<String> params = new ArrayList<String>();
        for (Field field : this.function.getInputs())
        {
            if (this.ipfs_fields.contains(field.getName()))
                params.add(this.ipfsClient.add(UUID.randomUUID().toString(), defaultInput.getBytes(field.getName())).toString());
            else
                params.add(defaultInput.getString(field.getName()));
        }

        ArrayList<String> result = this.write(this.contractDetail.getNetwork_id(), this.app_id, this.wallet, this.contractDetail.getAddress(), function_name, params);


    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }

    private ArrayList<String> write(String network_id, String app_id, String wallet, String address, String func, ArrayList<String> params)
    {
        HashMap<String, Object> setup = new HashMap<String, Object>();
        setup.put("network_id", network_id);
        setup.put("app_id", app_id);
        setup.put("wallet_id", wallet);
        setup.put("to",address);
        setup.put("value",0);
        setup.put("data","");
        setup.put("params",params);
        setup.put("method",func);
        return (ArrayList<String>) this.goldmine.executeContract(address, setup);
    }


    private ArrayList<String> getIPFSFields(String network_id, String app_id, String wallet, String address)
    {
        HashMap<String, Object> setup = new HashMap<String, Object>();
        setup.put("network_id", network_id);
        setup.put("app_id", app_id);
        setup.put("wallet_id", wallet);
        setup.put("to",address);
        setup.put("value",0);
        setup.put("data","");
        setup.put("params",null);
        setup.put("method","ipfs_fields");
        return (ArrayList<String>) this.goldmine.executeContract(address, setup);
    }

    private Contract fetchContract(String id)
    {
        for (Contract contract : this.contracts)
        {
            if (contract.getId().equals(id))
                return contract;
        }

        return null;
    }

    private boolean hasIPFSFields(Contract contract)
    {
        boolean hasIPFSFields = false;
        for (Function function : contract.getFunctions())
        {
            if (function.getName().equals("ipfs_fields"))
            {
                hasIPFSFields = true;
                break;
            }
        }

        return hasIPFSFields;
    }

    private ContractDetail getContractDetail(ContractDetail[] contractDetails, String id)
    {
        for(ContractDetail contractDetail : contractDetails)
        {
            if (contractDetail.getId().equals(id))
                return contractDetail;
        }

        return null;
    }
}