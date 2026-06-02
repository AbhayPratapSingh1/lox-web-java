package com.lox;

public class ConsoleLogger implements Logger {
    @Override
    public void println(Object obj){
        System.out.println(obj);
    }
    @Override
    public void print(Object obj){
        System.out.print(obj);
    }

    @Override
    public  void error(Object obj){
        System.err.println(obj);
    }
}
