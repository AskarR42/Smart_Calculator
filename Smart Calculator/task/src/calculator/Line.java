package calculator;

public abstract class Line {
    String line;

    public Line(String line) {
        this.line = line.replaceAll(" ", "");
    }

    public abstract void execute();
}