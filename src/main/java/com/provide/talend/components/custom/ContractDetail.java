package com.provide.talend.components.custom;

public class ContractDetail {
    private String id;
    private String created_at;
    private String application_id;
    private String network_id;
    private String transaction_id;
    private String name;
    private String address;
    private ContractParams params;
    private String accessed_at;

    public ContractDetail() {
    }

    public ContractDetail(String id, String created_at, String application_id, String network_id, String transaction_id, String name, String address, ContractParams params, String accessed_at) {
        this.id = id;
        this.created_at = created_at;
        this.application_id = application_id;
        this.network_id = network_id;
        this.transaction_id = transaction_id;
        this.name = name;
        this.address = address;
        this.params = params;
        this.accessed_at = accessed_at;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    public String getNetwork_id() {
        return network_id;
    }

    public void setNetwork_id(String network_id) {
        this.network_id = network_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ContractParams getParams() {
        return params;
    }

    public void setParams(ContractParams params) {
        this.params = params;
    }

    public String getAccessed_at() {
        return accessed_at;
    }

    public void setAccessed_at(String accessed_at) {
        this.accessed_at = accessed_at;
    }
}
