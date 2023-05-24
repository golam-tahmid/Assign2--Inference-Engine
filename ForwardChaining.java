import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ForwardChaining {
    private List<HornClause> clauses;
    private Literal query;
    private List<Literal> inferredLiterals; // List to keep track of inferred literals

    public ForwardChaining(List<HornClause> clauses, Literal query) {
        this.clauses = clauses;
        this.query = query;
        this.inferredLiterals = new ArrayList<>();
    }

    public boolean inference() {
        Set<Literal> inferred = new HashSet<>();

        while (true) {
            boolean newInferenceMade = false;

            for (HornClause clause : clauses) {
                if (inferred.containsAll(clause.body)) {
                    if (clause.head != null && !inferred.contains(clause.head)) {
                        inferred.add(clause.head);
                        inferredLiterals.add(clause.head); // Add the literal to the inferred literals list
                        newInferenceMade = true;

                        if (clause.head.equals(query)) {
                            return true;
                        }
                    }
                }
            }

            if (!newInferenceMade) {
                break;
            }
        }

        return false;
    }

    public void printInferredLiterals() {
        for (Literal literal : inferredLiterals) {
            System.out.print(literal.symbol + ", ");
        }
        System.out.println();
    }
}

