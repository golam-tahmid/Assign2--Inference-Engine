public class Literal {
    public String symbol;
    public boolean isPositive;

    public Literal(String symbol, boolean isPositive) {
        this.symbol = symbol;
        this.isPositive = isPositive;
    }

    @Override
    public String toString() {
        return (isPositive ? "" : "~") + symbol;
    }
}
