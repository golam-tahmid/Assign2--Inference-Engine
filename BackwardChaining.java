import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BackwardChaining {
    private List<Clause> clauses;
    private Literal query;
    private Map<String, Boolean> proven;
    private List<String> provenSymbols;

    public BackwardChaining(List<Clause> clauses, Literal query) {
        this.clauses = clauses;
        this.query = query;
        this.proven = new HashMap<>();
        this.provenSymbols = new ArrayList<>();
    }

    public boolean inference() {
        // If the query symbol has already been proven or disproven, return the result
        if (proven.containsKey(query.symbol)) {
            return proven.get(query.symbol);
        }

        // If the query symbol is in the list of literals in a clause, it's proven
        for (Clause clause : clauses) {
            for (Literal literal : clause.getLiterals()) {
                if (literal.symbol.equals(query.symbol) && literal.isPositive) {
                    proven.put(query.symbol, true);
                    provenSymbols.add(query.symbol);
                    System.out.println("Proven symbol: " + query.symbol);  // logging the proven symbol
                    return true;
                }
            }
        }

        // Try to prove the query symbol by proving all literals in clauses containing the negated query symbol
        for (Clause clause : clauses) {
            for (Literal literal : clause.getLiterals()) {
                if (literal.symbol.equals(query.symbol) && !literal.isPositive) {
                    boolean result = true;
                    for (Literal l : clause.getLiterals()) {
                        if (!l.symbol.equals(query.symbol)) {
                            Literal newQuery = new Literal(l.symbol, !l.isPositive);
                            BackwardChaining bc = new BackwardChaining(clauses, newQuery);
                            result = result && bc.inference();
                            if(result) {
                                provenSymbols.add(l.symbol);
                                System.out.println("Proven symbol: " + l.symbol);  // logging the proven symbol
                            }
                        }
                    }
                    proven.put(query.symbol, result);
                    if(result) {
                        provenSymbols.add(query.symbol);
                        System.out.println("Proven symbol: " + query.symbol);  // logging the proven symbol
                    }
                    return result;
                }
            }
        }

        // If the query symbol cannot be proven or disproven, it is assumed false
        proven.put(query.symbol, false);
        return false;
    }


    // Method to print proven symbols
    public void printProvenSymbols() {
        System.out.println("Proven Symbols: ");
        for (String symbol : provenSymbols) {
            System.out.println(symbol);
        }
    }
}
