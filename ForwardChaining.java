import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ForwardChaining {
    private List<Clause> clauses;
    private Literal query;

    public ForwardChaining(List<Clause> clauses, Literal query) {
        this.clauses = clauses;
        this.query = query;
    }

    public boolean inference() {
        // A set to hold all the literals inferred to be true
        Set<Literal> inferred = new HashSet<>();
        // A list to hold the count of unknown literals in each clause
        int[] count = new int[clauses.size()];

        // Count the number of literals in each clause and add known literals to inferred set
        for (int i = 0; i < clauses.size(); i++) {
            Clause clause = clauses.get(i);
            count[i] = clause.getLiterals().size();
            for (Literal literal : clause.getLiterals()) {
                if (!literal.isPositive && inferred.contains(new Literal(literal.symbol, true))) {
                    count[i]--;
                }
                if (literal.isPositive && inferred.contains(literal)) {
                    count[i]--;
                }
            }
        }

        while (true) {
            boolean newInferred = false;

            for (int i = 0; i < clauses.size(); i++) {
                // If all literals in a clause are known to be true
                if (count[i] == 0) {
                    Clause clause = clauses.get(i);
                    for (Literal literal : clause.getLiterals()) {
                        // Add new inferred literals to inferred set
                        if (!inferred.contains(literal)) {
                            inferred.add(literal);
                            newInferred = true;
                            if (literal.equals(query)) {
                                return true;
                            }
                        }
                    }
                }
            }

            if (!newInferred) {
                // If no new literals were inferred in the last loop, stop the inference
                break;
            }
        }

        return false;
    }
}
