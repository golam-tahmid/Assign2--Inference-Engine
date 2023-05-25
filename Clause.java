import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Clause {
    public List<Literal> literals;

    // no longer using this constructor in current version of code
    public Clause() {
        this.literals = new ArrayList<>();
    }

    // alternate constructor for list
    public Clause(List<Literal> literals) {
        this.literals = literals;
    }


    // no longer using these two methods
    public void addLiteral(Literal literal) {
        literals.add(literal);
    }

    public List<Literal> getLiterals() {
        return literals;
    }

    // for debug printing
    @Override
    public String toString() {
        return literals.stream()
                .map(Literal::toString)
                .collect(Collectors.joining(" & "));
    }
}
