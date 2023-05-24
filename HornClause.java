import java.util.*;
import java.util.stream.Collectors;

public class HornClause {
    public Literal head;  // The head of the clause (positive or negative literal)
    public Set<Literal> body;  // The body of the clause (negative literals)

    public HornClause() {
        this.body = new HashSet<>();
    }

    public void addLiteral(Literal literal) {
        if (this.head != null) {
            throw new IllegalArgumentException("Horn Clause can have at most one head literal");
        }
        this.head = literal;
    }

    @Override
    public String toString() {
        String bodyString = this.body.stream()
                .map(Literal::toString)
                .collect(Collectors.joining(", "));
        return bodyString + (this.head != null ? ", " + this.head : "");
    }
}

