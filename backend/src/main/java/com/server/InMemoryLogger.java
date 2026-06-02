package com.server;

import com.lox.Logger;

import java.util.ArrayList;

public class InMemoryLogger implements Logger {
    public static final ArrayList<String> logs = new ArrayList<>();
    public static final ArrayList<String> errors = new ArrayList<>();

    @Override
    public void println(Object obj) {
        logs.add((String) obj);
        logs.add("\n");
    }

    @Override
    public void print(Object obj) {
        logs.add((String) obj);
    }

    @Override
    public void error(Object obj) {
        errors.add((String) obj);
    }

    public void reset() {
        logs.clear();
        errors.clear();
    }
    public Result getResult(){
        return new Result(logs, errors);
    }
}
