import java.util.*;
import java.util.stream.Collectors;

public class ForwardChaining
{
    private List<HornClause> clauses;
    private Literal query;
    private LinkedHashSet<Literal> inferred; // Using LinkedHashSet to keep the order and disallow duplicates

    public ForwardChaining(List<HornClause> clauses, Literal query)
    {
        // store clause list and query
        this.clauses = clauses;
        this.query = query;
        this.inferred = new LinkedHashSet<>();

        // Add initial facts to the inferred set
        // if body is empty, head is a fact
        for (HornClause clause : clauses)
        {
            if (clause.body.isEmpty())
            {
                this.inferred.add(clause.head);
            }
        }
    }

    public boolean inference()
    {
        while (true)    // loop until break
        {
            boolean newInferenceMade = false; // keeps track of if inference is made

            for (HornClause clause : clauses) // for each clause in kb
            {
                // If all the literals in the body of the clause have been inferred
                // and the head hasn't been inferred
                if (inferred.containsAll(clause.body) && (clause.head != null && !inferred.contains(clause.head)))
                {
                    // add head to inferred set
                    inferred.add(clause.head);
                    // set inference made flag to true
                    newInferenceMade = true;

                    // if inferred the query
                    if (clause.head.equals(query))
                    {
                        return true;
                    }
                }
            }

            // checked all clauses and no inference made
            if (!newInferenceMade)
            {
                break;
            }
        }
        return false;
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



