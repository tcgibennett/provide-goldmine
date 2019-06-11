package com.provide.talend.components.custom;

public class ContractParams {
    private ABI[] abi;

    public ContractParams() {
    }

    public ContractParams(ABI[] abi) {
        this.abi = abi;
    }

    public ABI[] getAbi() {
        return abi;
    }

    public void setAbi(ABI[] abi) {
        this.abi = abi;
    }
}
