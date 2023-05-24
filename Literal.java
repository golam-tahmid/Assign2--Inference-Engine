import java.util.Objects;

public class Literal {
    public String symbol;
    public boolean isPositive;

    public Literal(String symbol, boolean isPositive) {
        this.symbol = symbol;
        this.isPositive = isPositive;
    }

    // Override equals and hashCode to correctly differentiate positive and negative literals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal = (Literal) o;
        return isPositive == literal.isPositive && Objects.equals(symbol, literal.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, isPositive);
    }

    @Override
    public String toString() {
        return (isPositive ? "" : "~") + symbol;
    }
}