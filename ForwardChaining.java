import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ForwardChaining {
    private List<Clause> clauses;
    private Literal query;
    private Map<String, Boolean> inferred;
    private List<String> provenSymbols;

    public ForwardChaining(List<Clause> clauses, Literal query) {
        this.clauses = clauses;
        this.query = query;
        this.inferred = new HashMap<>();
        this.provenSymbols = new ArrayList<>();
        for (Clause c : clauses) {
            for (Literal l : c.getLiterals()) {
                inferred.put(l.symbol, false);
            }
        }
    }

    public boolean inference() {
        List<Literal> agenda = new ArrayList<>();
        agenda.add(new Literal(query.symbol, true));
        while (!agenda.isEmpty()) {
            Literal p = agenda.remove(agenda.size() - 1);
            if (!inferred.get(p.symbol)) {
                inferred.put(p.symbol, true);
                provenSymbols.add(p.symbol);
                for (Clause c : clauses) {
                    if (c.getLiterals().contains(p)) {
                        agenda.add(c.getLiterals().get(0));
                    }
                }
                // Iterate over provenSymbols
                for (String symbol : provenSymbols) {
                    // Do something with each proven symbol
                    // For example, print each proven symbol
                    System.out.println(symbol);
                }
            }
        }
        return inferred.get(query.symbol);
    }


    // Method to print proven symbols
    public void printProvenSymbols() {
        System.out.println("Proven Symbols: ");
        for (String symbol : provenSymbols) {
            System.out.println(symbol);
        }
    }
}
