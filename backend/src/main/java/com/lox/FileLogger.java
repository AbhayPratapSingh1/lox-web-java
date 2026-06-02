package com.lox;

import java.io.IOException;
import java.io.PrintWriter;

public class FileLogger implements  Logger {
    private final PrintWriter errorWriter;
    private PrintWriter writer = null;

    public FileLogger(String name) throws IOException {
        String path = "./" + name + ".txt";
        String errorPath = "./" + name + "_error.txt";
        this.writer = new PrintWriter(path, "UTF-8");
        this.errorWriter = new PrintWriter(errorPath, "UTF-8");
    }

    @Override
    public  void println(Object obj){
        System.out.println(obj);
        this.writer.write(obj.toString()+"\n");

        this.writer.flush();
    }

    @Override
    public  void print(Object obj){
        System.out.println(obj);
        this.writer.write(obj.toString());
        this.writer.flush();
    }

    @Override
    public void error(Object obj){

        System.err.println(obj);
        this.errorWriter.write(obj.toString());
        this.errorWriter.flush();
    }
}
