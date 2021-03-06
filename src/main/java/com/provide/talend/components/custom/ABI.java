package com.provide.talend.components.custom;

public class ABI {
    private boolean constant;
    private ABIInputs[] inputs;
    private String name;
    private ABIOutputs[] outputs;
    private boolean payable;
    private String stateMutability;
    private String type;

    public ABI() {
    }

    public ABI(boolean constant, ABIInputs[] inputs, String name, ABIOutputs[] outputs, boolean payable, String stateMutability, String type) {
        this.constant = constant;
        this.inputs = inputs;
        this.name = name;
        this.outputs = outputs;
        this.payable = payable;
        this.stateMutability = stateMutability;
        this.type = type;
    }


    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public ABIInputs[] getInputs() {
        return inputs;
    }

    public void setInputs(ABIInputs[] inputs) {
        this.inputs = inputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ABIOutputs[] getOutputs() {
        return outputs;
    }

    public void setOutputs(ABIOutputs[] outputs) {
        this.outputs = outputs;
    }

    public boolean isPayable() {
        return payable;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }

    public String getStateMutability() {
        return stateMutability;
    }

    public void setStateMutability(String stateMutability) {
        this.stateMutability = stateMutability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
