package calculator;

import java.math.BigInteger;
import java.util.*;

public class Expression extends Line {

    private final Map<String, BigInteger> variables;
    private final Deque<BigInteger> operandStack = new ArrayDeque<>();
    private final Deque<String> operatorStack = new ArrayDeque<>();
    private final Map<String, Integer> OPERATOR_PRECEDENCE = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2
    );

    public Expression(String line, Map<String, BigInteger> variables) {
        super(line);
        removeUnnecessaryOperators();
        this.variables = variables;
    }

    private void removeUnnecessaryOperators() {
        while (line.contains("---")) {
            line = line.replaceAll("---", "-");
        }
        while (line.contains("--")) {
            line = line.replaceAll("--", "+");
        }
        line = line.replaceAll("[+]+", "+");
    }

    private char addElement(List<String> lineElements, StringBuilder sb, char unaryOp) {
        if (unaryOp != 0) {
            lineElements.addAll(List.of("(", "0", String.valueOf(unaryOp), sb.toString(), ")"));
            unaryOp = 0;
        } else {
            lineElements.add(sb.toString());
        }
        sb.delete(0, sb.length());
        return unaryOp;
    }

    // this is tokenize()
    private List<String> getLineElements() {
        List<String> lineElements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        char unaryOp = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            boolean isNumOrVar = (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
            if (isNumOrVar) {
                sb.append(c);
                if (i == line.length() - 1) {
                    unaryOp = addElement(lineElements, sb, unaryOp);
                }
            } else if ((c == '-' || c == '+') && (i == 0 || OPERATOR_PRECEDENCE.containsKey(String.valueOf(line.charAt(i - 1))))) {
                if (sb.length() != 0) {
                    unaryOp = addElement(lineElements, sb, unaryOp);
                }
                unaryOp = c;
            } else {
                if (sb.length() != 0) {
                    unaryOp = addElement(lineElements, sb, unaryOp);
                }
                lineElements.add(String.valueOf(c));
            }
        }
        return lineElements;
    }

    private void validate() throws InvalidSyntax {
        int parenthesesBalance = 0;
        int operandCounter = 0;
        int operatorCounter = 0;
        for (String element : getLineElements()) {
            if (element.matches("\\d+") || element.matches("[a-zA-Z]+")) {
                operandCounter++;
                if (element.matches("[a-zA-Z]+") && !variables.containsKey(element)) {
                    throw new InvalidSyntax("Unknown variable");
                }
            } else if (OPERATOR_PRECEDENCE.containsKey(element)) {
                operatorCounter++;
            } else if ("(".equals(element)) {
                parenthesesBalance++;
            } else if (")".equals(element)) {
                parenthesesBalance--;
            } else {
                throw new InvalidSyntax("Invalid expression");
            }
            if (parenthesesBalance < 0) {
                throw new InvalidSyntax("Invalid expression");
            }
        }
        if (parenthesesBalance != 0 || operandCounter - 1 != operatorCounter) {
            throw new InvalidSyntax("Invalid expression");
        }
    }

    private void performOperator(String operator) {
        BigInteger a = operandStack.removeLast();
        BigInteger b = operandStack.removeLast();
        BigInteger result;
        switch (operator) {
            case "+":
                result = a.add(b);
                break;
            case "-":
                result = b.subtract(a);
                break;
            case "*":
                result = a.multiply(b);
                break;
            case "/":
                result = b.divide(a);
                break;
            default:
                result = BigInteger.ZERO;
                break;
        }
        operandStack.addLast(result);
    }

    private void calculateExpression() {
        for (String element : getLineElements()) {
            if (element.matches("\\d+")) {
                operandStack.addLast(new BigInteger(element));
            } else if (element.matches("[a-zA-Z]")) {
                operandStack.addLast(variables.get(element));
            } else if ("(".equals(element)) {
                operatorStack.addLast(element);
            } else if (")".equals(element)) {
                while (true) {
                    String peek = operatorStack.removeLast();
                    if ("(".equals(peek)) {
                        break;
                    } else {
                        performOperator(peek);
                    }
                }
            } else if (operatorStack.isEmpty() || "(".equals(operatorStack.getLast()) || OPERATOR_PRECEDENCE.get(element) > OPERATOR_PRECEDENCE.get(operatorStack.getLast())) {
                operatorStack.add(element);
            } else {
                while (true) {
                    performOperator(operatorStack.removeLast());
                    if (operatorStack.isEmpty() || "(".equals(operatorStack.getLast())) {
                        operatorStack.addLast(element);
                        break;
                    }
                }
            }
        }
        while (!operatorStack.isEmpty()) {
            performOperator(operatorStack.removeLast());
        }
    }

        @Override
        public void execute () {
            try {
                validate();
                calculateExpression();
                System.out.println(operandStack.removeLast());
            } catch (InvalidSyntax e) {
                System.out.println(e.getMessage());
            }
        }
    }

