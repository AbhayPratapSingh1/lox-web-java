package com.server;

import java.util.ArrayList;

public class Result {
    private final ArrayList<String> logs;
    private final ArrayList<String> errors;

    public Result(ArrayList<String> logs, ArrayList<String> errors) {
        this.logs = logs;
        this.errors = errors;
    }


    public ArrayList<String> getErrors() {
        return errors;
    }

    public ArrayList<String> getLogs() {
        return logs;
    }


}
