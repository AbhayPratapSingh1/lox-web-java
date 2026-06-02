package com.lox;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static boolean hasError = false;
    private static boolean hadRuntimeError = false;
    public static Logger logger = new ConsoleLogger();

    public static void setLogger(Logger newLogger){
        logger = newLogger;
    }
    static void main(String[] args) throws IOException {
        try{
            if (args.length > 1) {
                logger.println("Usage: jlox [script]");
                System.exit(64);
            } else if (args.length == 1) {
                byte[] fileContent = Files.readAllBytes(Paths.get(args[0]));
                runFile(fileContent);
            } else {
                logger.println(" REPL");
                runInteractive();
            }
        } catch (Exception e) {
            logger.println("erorr");
            logger.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void runFile(byte[] fileContent) throws IOException {
        run(new String(fileContent, Charset.defaultCharset()));

        if (hasError) {
            System.exit(65);
        }
        if (hadRuntimeError) {
            System.exit(70);
        }
    }

    public static void runInteractive() throws IOException {
        InputStreamReader inputStream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStream);

        while (true) {
            String line = reader.readLine();
            logger.println("INTERPRETER MODE : " + line);
            if (line.equals("exit")) {
                break;
            }
            Object result = run(line);
            logger.println(result);
            hasError = false;
            hadRuntimeError = false;
        }
    }

    public static Object run(String source) throws IOException {
        Scanner scanner = new Scanner(source);
        Interpreter interpreter = new Interpreter(logger);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();


        if (hasError) return null;
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        if (hasError) return null;
        return interpreter.interpret(statements);
    }


    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        logger.error("[line " + line + "] Error " + where + ": " + message);
        hasError = true;
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, "at '" + token.lexeme + "'", message);
        }
    }

    public static void runTimeError(RuntimeError error) {
        logger.error(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    public static void reset() {
        hasError = false;
        hadRuntimeError = false;
    }
}
