import java.util.*;

public class BackwardChaining {
    private List<Clause> clauses;
    private Literal query;

    public BackwardChaining(List<Clause> clauses, Literal query) {
        this.clauses = clauses;
        this.query = query;
    }

    public boolean inference() {
        return backwardChain(Collections.singleton(query));
    }

    private boolean backwardChain(Set<Literal> goals) {
        while (!goals.isEmpty()) {
            Literal literal = goals.iterator().next();
            goals.remove(literal);

            // if the literal is negative and is in the KB, remove it from goals
            if (!literal.isPositive && inKnowledgeBase(new Literal(literal.symbol, true))) {
                goals.remove(literal);
                continue;
            }
            // if the literal is positive and is in the KB, remove it from goals
            if (literal.isPositive && inKnowledgeBase(literal)) {
                goals.remove(literal);
                continue;
            }

            boolean newGoalAdded = false;
            for (Clause clause : clauses) {
                if (clause.getLiterals().contains(literal)) {
                    for (Literal lit : clause.getLiterals()) {
                        if (!lit.equals(literal)) {
                            goals.add(lit);
                            newGoalAdded = true;
                        }
                    }
                }
            }
            if (!newGoalAdded) {
                return false;
            }
        }
        return true;
    }

    private boolean inKnowledgeBase(Literal literal) {
        for (Clause clause : clauses) {
            if (clause.getLiterals().contains(literal)) {
                return true;
            }
        }
        return false;
    }
}
