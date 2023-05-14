import java.util.ArrayList;
import java.util.List;
public class Clause {
    public List<Literal> literals;

    public Clause() {
        this.literals = new ArrayList<>();
    }

    public void addLiteral(Literal literal) {
        literals.add(literal);
    }

}
