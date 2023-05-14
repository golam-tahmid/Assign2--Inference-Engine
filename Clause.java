import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Clause {
    public List<Literal> literals;

    public Clause() {
        this.literals = new ArrayList<>();
    }

    public void addLiteral(Literal literal) {
        literals.add(literal);
    }

    @Override
    public String toString() {
        return literals.stream()
                .map(Literal::toString)
                .collect(Collectors.joining(" & "));
    }
}
