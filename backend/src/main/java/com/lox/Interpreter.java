package com.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {
    private final Logger consoleLogger;
    Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    public Interpreter(Logger consoleLogger) {
        this.consoleLogger = consoleLogger;
        globals.define("clock", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {

        Object right = evaluate(expr.right);
        Object left = evaluate(expr.left);

        switch (expr.operator.type) {
            case LESS:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left < (Double) right;

            case LESS_EQUAL:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left <= (Double) right;

            case GREATER:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left > (Double) right;

            case GREATER_EQUAL:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left >= (Double) right;

            case MINUS:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left - (Double) right;

            case SLASH:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left / (Double) right;

            case PERCENTAGE:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left % (Double) right;

            case STAR:
                checkNumberOperant(expr.operator, left, right);
                return (Double) left * (Double) right;

            case EQUAL_EQUAL:
                return isEqual(left, right);

            case BANG_EQUAL:
                return !isEqual(left, right);

            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    checkNumberOperant(expr.operator, left, right);
                    return (Double) left + (Double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return left + (String) right;
                }

                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left)+ String.valueOf(right);
                }


                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
        }
        return null;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }


    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {

        Object right = evaluate(expr.right);
        return switch (expr.operator.type) {
            case MINUS -> {
                checkNumberOperant(expr.operator, right);
                yield -(double) right;
            }
            case BANG -> !isTruthy(right);
            default -> null;
        };
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        LoxCallable function = (LoxCallable) callee;
        if (function.arity() != arguments.size()) {
            throw new RuntimeError(expr.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof LoxInstance) {
            return ((LoxInstance) object).get(expr.name);
        }

        throw new RuntimeError(expr.name,
                "Only instances have properties.");
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof LoxInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);

        ((LoxInstance) object).set(expr.name, value);
        return null;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        Integer distance = locals.get(expr);
        LoxClass superClass = (LoxClass) environment.getAt(distance, "super");
        LoxInstance object = (LoxInstance) environment.getAt(distance - 1, "this");

        LoxFunction method = superClass.findMethod(expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(expr.method,
                    "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);

    }


    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return true;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }


    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }


    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }

    private boolean isEqual(Object term1, Object term2) {
        if (term1 == null && term2 == null) return true;
        if (term1 == null) return false;
        return term1.equals(term2);
    }


    private void checkNumberOperant(Token operator, Object candidate) {
        if (candidate instanceof Double) return;
        throw new RuntimeError(operator, "Operator must be number");
    }

    private void checkNumberOperant(Token operator, Object candidate1, Object candidate2) {
        if (candidate1 instanceof Double && candidate2 instanceof Double) return;
        throw new RuntimeError(operator, "Operator must be number");
    }


    Object interpret(List<Stmt> stmts) {

        Object lastResult = null;

        try {
            for (Stmt stmt : stmts) {
                lastResult = execute(stmt);
            }
        } catch (RuntimeError error) {
            Lox.runTimeError(error);
        }
        return lastResult;
    }

    private Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                return text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression stmt) {
        return evaluate(stmt.expression);
    }

    @Override
    public Object visitClassStmt(Stmt.Class stmt) {
        LoxClass superClass = null;
        if (stmt.superclass != null) {
            Object evaluated = evaluate(stmt.superclass);
            if (!(evaluated instanceof LoxClass)) {
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
            superClass  = (LoxClass) evaluated;
        }

        environment.define(stmt.name.lexeme, null);
        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superClass);
        }

        HashMap<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            LoxFunction loxMethod = new LoxFunction(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, loxMethod);
        }

        LoxClass klass = new LoxClass(stmt.name.lexeme, superClass, methods);
        if (stmt.superclass != null) {
            environment = environment.enclosing;
        }
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Object visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, this.environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Object visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }


    @Override
    public Object visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        consoleLogger.println(stringify(value));
        return null;
    }

    @Override
    public Object visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }


    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {

        Integer depth = locals.get(expr);
        if (depth != null) {
            return environment.getAt(depth, name.lexeme);
        }
        return globals.get(name);
    }

    @Override
    public Object visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block stmt) {
        return executeBlock(stmt.statements, new Environment(environment));
    }

    Object executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
        return null;
    }



    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);

    }
}
