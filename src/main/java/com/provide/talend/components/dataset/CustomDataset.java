package com.provide.talend.components.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.provide.talend.components.datastore.CustomDatastore;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@DataSet("CustomDataset")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "datastore" })
        , @GridLayout.Row({ "inputFields" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomDataset implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDatastore datastore;


    @Option()
    @Structure(discoverSchema = "CustomDataset", type = Structure.Type.IN)
    @Documentation("TODO")
    private List<String> inputFields = new ArrayList<>();


    public CustomDatastore getDatastore() {
        return datastore;
    }

    public CustomDataset setDatastore(CustomDatastore datastore) {
        this.datastore = datastore;
        return this;
    }


    public List<String> getInputFields() {
        return inputFields;
    }

    public CustomDataset setInputFields(List<String> inputFields) {
        this.inputFields = inputFields;
        return this;
    }




}