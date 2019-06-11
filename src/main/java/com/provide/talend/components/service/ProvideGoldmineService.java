package com.provide.talend.components.service;

import com.provide.talend.components.dataset.CustomDataset;
import com.provide.talend.components.datastore.CustomDatastore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;
import org.talend.sdk.component.api.service.configuration.Configuration;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;
import services.provide.client.IPFSClient;
import services.provide.client.microservices.Goldmine;
import services.provide.client.microservices.Ident;
import services.provide.dao.Contract;
import services.provide.dao.Field;
import services.provide.dao.Function;


import java.util.ArrayList;
import java.util.List;

@Service
public class ProvideGoldmineService {
    private Goldmine goldmine = null;
    private Ident ident = null;
    private IPFSClient ipfs = null;
    // you can put logic here you can reuse in components
    @HealthCheck()
    public HealthCheckStatus testConnection(@Option final CustomDatastore datastore) {

        System.out.println("testConnection");
        if (datastore == null || datastore.getToken() == null || datastore.getToken().equals("")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Connection not ok, datastore can't be null");
        }
        if (Boolean.parseBoolean(Goldmine.validateToken(datastore.getToken()).toString())) {

            return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection Ok");
        }
        else
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Token Invalid");

    }


    @Suggestions("loadContracts")
    public static SuggestionValues loadContracts(String token)
    {
        SuggestionValues values = new SuggestionValues();
        List<SuggestionValues.Item> items = new ArrayList<SuggestionValues.Item>();
        Goldmine prvd = (Goldmine)Goldmine.init(token);
        Object contracts_json = ((ArrayList<Object>) prvd.fetchContracts(null)).get(1);
        Contract[] contracts = prvd.getContracts(contracts_json);
        for (Contract contract : contracts) {
            for (Function function : contract.getFunctions()) {
                if (!function.getName().equals("ipfs_fields"))
                    items.add(new SuggestionValues.Item(contract.getName() + "::" + contract.getId()+"::"+function.getName(), contract.getName() + "::" + contract.getId()+"::"+function.getName()));
            }
        }
        values.setItems(items);

        return values;
    }

/*
    @DynamicValues("contractsList")
    public Values actions() {
        this.init(datastore.getToken());
        Values values = new Values();
        List<Values.Item> items = new ArrayList<Values.Item>();
        try {
            items.add(new Values.Item("AAA", "Test"));
            items.add(new Values.Item("AAB", datastore.getToken()));
            Object contracts_json = ((ArrayList<Object>) goldmine.fetchContracts(null)).get(1);
            Contract[] contracts = goldmine.getContracts(contracts_json);
            for (Contract contract : contracts) {
                items.add(new Values.Item(contract.getAddress().toString(), contract.getName() + "::" + contract.getAddress()));
            }
        } catch(Exception e) {
            items.add(new Values.Item("111", e.getMessage()));
        }

        values.setItems(items);
        return values;
    }*/

    @DiscoverSchema(value = "CustomDataset")
    public Schema guessSchema(@Option final CustomDataset dataset, final RecordBuilderFactory recordBuilderFactory) {
        System.out.println(
                "[" + this + "] using record builder " + recordBuilderFactory);
        Schema.Builder schemaBuilder = recordBuilderFactory.newSchemaBuilder(Schema.Type.RECORD);

        Schema.Entry.Builder entryBuilder = recordBuilderFactory.newEntryBuilder();
        Goldmine prvd = (Goldmine)Goldmine.init(dataset.getDatastore().getToken());
        String[] contractParts = dataset.getDatastore().getContract().split("::");
        Object contracts_json = ((ArrayList<Object>) prvd.fetchContracts(null)).get(1);
        Contract[] contracts = prvd.getContracts(contracts_json);
        for (Contract contract : contracts) {
            if (contract.getId().equals(contractParts[1])) {
                for (Function function : contract.getFunctions()) {
                    if (function.getName().equals(contractParts[2]))
                    {
                        if (function.getOutputs() != null) {
                            for (Field output : function.getOutputs()) {
                                schemaBuilder.withEntry(_doBuildEntry(output.getName(), Schema.Type.valueOf(output.getType()), "Output Field", entryBuilder));
                            }
                        }

                        if (function.getInputs() != null) {
                            for (Field input : function.getInputs()) {
                                schemaBuilder.withEntry(_doBuildEntry(input.getName(), Schema.Type.valueOf(input.getType().toUpperCase()), "Input Field", entryBuilder));
                            }
                        }
                    }
                }
            }
        }
        //schemaBuilder.withEntry(_doBuildEntry("alfaColumn", Schema.Type.STRING, entryBuilder));

        return schemaBuilder.build();
    }

    private Schema.Entry _doBuildEntry(
            String name, Schema.Type type, String comment, Schema.Entry.Builder entryBuilder) {

        return entryBuilder.withName(
                name
        ).withType(
                type
        ).withNullable(
                false
        ).withComment(
                comment
        ).build();
    }
}