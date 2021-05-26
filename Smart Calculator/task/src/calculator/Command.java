package calculator;

public class Command extends Line {

    Command(String line) {
        super(line);
    }

    @Override
    public void execute() {
        switch (line) {
            case "/help":
                System.out.println("The program calculates the expression result with variables");
                break;
            case "/exit":
                System.out.println("Bye!");
                System.exit(0);
            default:
                System.out.println("Unknown command");
                break;
        }
    }
}
