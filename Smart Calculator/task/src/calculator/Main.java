package calculator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, BigInteger> variables = new HashMap<>();
        while (true) {
            String input = scanner.nextLine();
            Line line;
            if (input.isEmpty()) {
                continue;
            } else if (input.charAt(0) == '/') {
                line = new Command(input);
            } else if (input.contains("=")) {
                line = new Assignment(input, variables);
            } else {
                line = new Expression(input, variables);
            }
            line.execute();
        }
    }
}