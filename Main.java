import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        // Create parser object
        Parser parser = new Parser();
        try {
            // Parse the text file
            Parser.Result result = parser.parse("test_HornKB.txt");
            // Printing results of parsing kb for testing purposes
            System.out.println("Parsed knowledge base:");
            // For each expression parsed print it
            for (Expression expression : result.knowledgeBase) {
                System.out.println(expression.toString());
            }
            // Print the query identifier
            System.out.println("Parsed query: " + result.query);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());    // If error print this
        }
    }
}
