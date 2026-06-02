package com.lox;

import java.util.HashMap;

public class Environment {

    Environment enclosing;
    private final HashMap<String, Object> values = new HashMap<>();

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Environment() {
        this.enclosing = null;
    }


    void define(String name, Object value) {
        values.put(name, value);
    }

    Object get(Token name){
        if (values.containsKey(name.lexeme)){
            if (values.get(name.lexeme) == null){
                throw new RuntimeError(name,"Unassigned Variable '" + name.lexeme + "'.");
            }
            return values.get(name.lexeme);
        }
        if (this.enclosing != null){
            return this.enclosing.get(name);
        }
        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)){
            values.put(name.lexeme, value);
            return;
        }
        if (this.enclosing != null){
            this.enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }

    public Object getAt(Integer distance, String name) {
        return ancestor(distance).values.get(name);
    }

    private Environment ancestor(Integer distance) {
        Environment environment = this;
        for (int i = 0 ; i< distance; i++){
            environment = environment.enclosing;
        }
        return environment;
    }

    public void assignAt(Integer distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
}