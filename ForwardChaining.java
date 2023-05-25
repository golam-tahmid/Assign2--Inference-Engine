import java.util.*;
import java.util.stream.Collectors;

public class ForwardChaining {
    private List<HornClause> clauses;
    private Literal query;
    private LinkedHashSet<Literal> inferred; // Using LinkedHashSet to keep the order and disallow duplicates

    public ForwardChaining(List<HornClause> clauses, Literal query) {
        this.clauses = clauses;
        this.query = query;
        this.inferred = new LinkedHashSet<>();

        // Add initial facts to the inferred set
        for (HornClause clause : clauses) {
            if (clause.body.isEmpty()) {
                this.inferred.add(clause.head);
            }
        }
    }

    public boolean inference() {
        while (true) {
            boolean newInferenceMade = false;

            for (HornClause clause : clauses) {
                if (inferred.containsAll(clause.body) && (clause.head != null && !inferred.contains(clause.head))) {
                    inferred.add(clause.head);
                    newInferenceMade = true;

                    if (clause.head.equals(query)) {
                        return true;
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
        List<Literal> literals = new ArrayList<>(inferred);
        String inferredString = literals.stream()
                .map(literal -> literal.symbol)
                .collect(Collectors.joining(", "));
        System.out.println(inferredString);
    }

}



