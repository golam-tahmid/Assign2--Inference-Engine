import java.io.IOException;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        // Check if command-line arguments are valid
        if (args.length != 2)
        {
            System.out.println("Usage: java iengine <method> <filename>");
            return;
        }

        String method = args[0];
        String filename = args[1];

        // Create parser object
        Parser parser = new Parser();
        try
        {
            // Parse the text file
            Parser.Result result = parser.parse(filename);

            // Printing results of parsing kb for testing purposes
            System.out.println("Parsed knowledge base:");
            for (Expression expression : result.knowledgeBase)
            {
                System.out.println(expression.toString());
            }
            System.out.println("Parsed query: " + result.query);

            // Convert the query to a Literal
            Literal queryLiteral = new Literal(result.query, true);

            // Check which method to use
            if (method.equals("TT"))
            {
                // Transform the knowledge base into a list of clauses
                List<Clause> clauses = parser.transformToClauses(result.knowledgeBase);
                // Use truth table checking
                TruthTable tt = new TruthTable(clauses, queryLiteral);
                boolean entails = tt.check();
                if (entails)
                {
                    System.out.println("YES: " + tt.getModelsEntailment());
                }
                else
                {
                    System.out.println("NO");
                }

            }
            else if (method.equals("FC"))
            {
                // Transform the knowledge base into a list of horn clauses
                List<HornClause> hornClauses = parser.transformToHornClauses(result.knowledgeBase);
                // Use forward chaining
                ForwardChaining fc = new ForwardChaining(hornClauses, queryLiteral);
                boolean entails = fc.inference();
                if (entails)
                {
                    System.out.print("YES: ");
                    fc.printInferredLiterals();
                }
                else
                {
                    System.out.println("NO");
                }

            }
            else if (method.equals("BC"))
            {
                // Transform the knowledge base into a list of clauses
                List<HornClause> clauses = parser.transformToHornClauses(result.knowledgeBase);
                // Use backward chaining
                BackwardChaining bc = new BackwardChaining(clauses, queryLiteral);
                boolean entails = bc.inference();
                if (entails)
                {
                    System.out.print("YES: ");
                    bc.printInferredLiterals();
                }
                else
                {
                    System.out.println("NO");
                }

            }
            else
            {
                System.out.println("Invalid method. Please choose TT, FC or BC.");
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading the file: " + e.getMessage());    // If error print this
        }
    }
}
