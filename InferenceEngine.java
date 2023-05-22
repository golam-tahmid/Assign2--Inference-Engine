import java.util.*;

public class InferenceEngine {
    public static boolean forwardChaining(KnowledgeBase kb, Literal query) {
        // A set of literals (facts) inferred so far
        Set<Literal> inferred = new HashSet<>();

        // A queue of literals to be processed
        Queue<Literal> agenda = new LinkedList<>();

        // A counter for the number of literals in each clause
        Map<Clause, Integer> count = new HashMap<>();

        // Initialize data structures
        for (Clause c : kb.clauses) {
            count.put(c, c.literals.size());
            for (Literal l : c.literals) {
                if (l.isPositive) {
                    inferred.add(l);
                    agenda.add(l);
                }
            }
        }

        while (!agenda.isEmpty()) {
            Literal p = agenda.poll();

            // If the literal matches the query, we have found a solution
            if (p.equals(query)) {
                return true;
            }

            for (Clause c : kb.clauses) {
                if (c.literals.contains(p)) {
                    int numLiterals = count.get(c);
                    count.put(c, --numLiterals);

                    if (numLiterals == 0) {
                        for (Literal l : c.literals) {
                            if (!inferred.contains(l)) {
                                inferred.add(l);
                                agenda.add(l);
                            }
                        }
                    }
                }
            }
        }

        // If the agenda is empty and we have not returned, there is no solution
        return false;
    }
    public static boolean backwardChaining(KnowledgeBase kb, Literal query) {
        // A list of literals (facts) inferred so far
        List<Literal> inferred = new ArrayList<>();

        return bcRecursive(kb, query, inferred);
    }

    private static boolean bcRecursive(KnowledgeBase kb, Literal query, List<Literal> inferred) {
        if (kb.clauses.stream().anyMatch(c -> c.literals.contains(query))) {
            return true;
        }

        for (Clause c : kb.clauses) {
            if (c.literals.contains(query) && !inferred.contains(query)) {
                inferred.add(query);
                for (Literal l : c.literals) {
                    if (!l.equals(query)) {
                        if (!bcRecursive(kb, l, inferred)) {
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }
}
