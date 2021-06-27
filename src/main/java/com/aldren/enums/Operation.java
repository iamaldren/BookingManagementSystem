package com.aldren.enums;

import java.util.HashMap;
import java.util.Map;

public enum Operation {

    BORROW("borrow"),
    RETURN("return"),
    DEFAULT("default");

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
        if(lookup.containsKey(operation)) {
            return lookup.get(operation);
        }

        return lookup.get("default");
    }

}
