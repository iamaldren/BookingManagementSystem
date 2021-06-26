package com.aldren.enums;

import java.util.HashMap;
import java.util.Map;

public enum Operation {

    BORROW("borrow"),
    RETURN("return");

    private String operation;

    private static final Map<String, Operation> lookup = new HashMap<>();

    Operation(String operation) {
        this.operation = operation;
    }

    static {
        for(Operation operation : Operation.values()) {
            lookup.put(operation.getOperation(), operation);
        }
    }

    public String getOperation() {
        return operation;
    }

    public static Operation get(String operation) {
        return lookup.get(operation);
    }

}
