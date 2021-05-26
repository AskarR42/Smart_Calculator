package calculator;

import java.math.BigInteger;
import java.util.Map;

public class Assignment extends Line {

    private final Map<String, BigInteger> variables;

    Assignment(String line, Map<String, BigInteger> variables) {
        super(line);
        this.variables = variables;
    }

    private void validate() throws InvalidSyntax {
        String[] lineElements = line.split("=");

        if (lineElements.length != 2 || !(lineElements[1].matches("[+-]?\\d+") || lineElements[1].matches("[+-]?[a-zA-Z]+"))) {
            throw new InvalidSyntax("Invalid assignment");
        }
        if (!lineElements[0].matches("[a-zA-Z]+")) {
            throw new InvalidSyntax("Invalid identifier");
        }
        if (lineElements[1].matches("[a-zA-Z]+") && !variables.containsKey(lineElements[1])) {
            throw new InvalidSyntax("Unknown variable");
        }
    }

    @Override
    public void execute() {
        try {
            validate();
            String[] lineElements = line.split("=");
            variables.put(lineElements[0], lineElements[1].matches("[+-]?\\d+") ? new BigInteger(lineElements[1]) : variables.get(lineElements[1]));
        } catch (InvalidSyntax e) {
            System.out.println(e.getMessage());
        }
    }
}

