import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Check if command-line arguments are valid
        if (args.length != 2) {
            System.out.println("Usage: java iengine <method> <filename>");
            return;
        }

        String method = args[0];
        String filename = args[1];

        // Create parser object
        Parser parser = new Parser();
        try {
            // Parse the text file
            Parser.Result result = parser.parse(filename);

            // Printing results of parsing kb for testing purposes
            System.out.println("Parsed knowledge base:");
            // For each expression parsed print it
            for (Expression expression : result.knowledgeBase) {
                System.out.println(expression.toString());
            }
            // Print the query identifier
            System.out.println("Parsed query: " + result.query);

            // Transform the knowledge base into a list of clauses
            List<Clause> clauses = parser.transformToClauses(result.knowledgeBase);

            // Convert the query to a Literal
            Literal queryLiteral = new Literal(result.query, true);

            // Check which method to use
            if (method.equals("TT")) {
                // Use truth table checking
                TruthTable tt = new TruthTable(clauses, queryLiteral);
                boolean entails = tt.check();
                if (entails) {
                    System.out.println("YES: " + tt.getModelsEntailment());
                } else {
                    System.out.println("NO");
                }

            }
            else if (method.equals("FC")) {
                ForwardChaining fc = new ForwardChaining(clauses, queryLiteral);
               boolean entails = fc.inference();
                if (entails) {
                    System.out.println("YES");
                } else {
                    System.out.println("NO");
                }
            }
            else if (method.equals("BC")) {
                BackwardChaining bc = new BackwardChaining(clauses, queryLiteral);
                boolean entails = bc.inference();
                if (entails) {
                    System.out.println("YES");
                } else {
                    System.out.println("NO");
                }
            }

            else {
                System.out.println("Invalid method. Please choose TT.");
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());    // If error print this
        }
    }
}
