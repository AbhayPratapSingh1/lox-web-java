package com.server;
import com.lox.Lox;
import static com.lox.Lox.*;


public class StringLoxRunner {

    static void main() {
        Result result = runProgram("var a = 0;for (var b = 1; b < 5; b =  b + 1) {    print a ;    a  = a+ b;}\n");
        System.out.println("\n\n");
        System.out.println(result.getErrors());
        System.out.println(result.getLogs());
    }

    static Result runProgram(String program) {
        InMemoryLogger memLogger = new InMemoryLogger();
        memLogger.reset();
        try{
            Lox.setLogger(memLogger);
            Lox.reset();
            System.out.println(memLogger);
            run(program);
        } catch (Exception e) {
            memLogger.println("error");
            memLogger.println(e);
            throw new RuntimeException(e);
        }
        return memLogger.getResult();
    }
}
