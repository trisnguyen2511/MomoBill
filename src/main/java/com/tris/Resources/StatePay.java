package com.tris.Resources;

public enum StatePay {
    PAID(1, "PAID"),
    NOT_PAID(2, "NOT_PAID");

    private final int id;
    private final String name;

    StatePay(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}