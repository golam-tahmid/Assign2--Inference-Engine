import java.util.*;
import java.util.stream.Collectors;

public class BackwardChaining
{
    private List<HornClause> clauses;
    private Literal query;
    private LinkedHashSet<Literal> inferred;

    public BackwardChaining(List<HornClause> clauses, Literal query)
    {
        this.clauses = clauses;
        this.query = query;
        this.inferred = new LinkedHashSet<>();
    }

    public boolean inference()
    {
        return backwardChain(new LinkedHashSet<>(Collections.singleton(query)));
    }

    private boolean backwardChain(Set<Literal> goals)
    {
        // while there are still goals
        while (!goals.isEmpty())
        {
            // get and remove first literal in goal set
            Literal literal = goals.iterator().next();
            goals.remove(literal);

            // if the literal is already inferred, skip
            if (alreadyInferred(literal))
            {
                continue;
            }

            boolean newGoalAdded = false;
            for (HornClause clause : clauses)
            {
                // if head of clause is current goal
                if (clause.head != null && clause.head.equals(literal))
                {
                    // add goal to inferred set
                    inferred.add(literal);
                    // add the literals in body of clause to goals set
                    goals.addAll(clause.body);
                    newGoalAdded = true;
                }
            }
            if (!newGoalAdded)
            {
                // if no goals were added, goal unachievable
                return false;
            }
            inferred.add(literal);  // Add the current goal to the inferred set
        }
        // reached only if all goals achieved
        return true;
    }

    private boolean alreadyInferred(Literal literal)
    {
        return inferred.contains(literal);
    }

    // for debugging
    public void printInferredLiterals()
    {
        List<Literal> literals = new ArrayList<>(inferred);
        String inferredString = literals.stream()
                .map(literal -> literal.symbol)
                .collect(Collectors.joining(", "));
        System.out.println(inferredString);
    }

}
