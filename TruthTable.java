import java.util.*;

public class TruthTable {
    private List<Clause> knowledgeBase;
    private Literal query;
    private Set<String> symbols;

    public TruthTable(List<Clause> knowledgeBase, Literal query) {
        this.knowledgeBase = knowledgeBase;
        this.query = query;

        // Extract all symbols from the knowledge base
        this.symbols = new HashSet<>();
        for (Clause clause : knowledgeBase) {
            for (Literal literal : clause.literals) {
                symbols.add(literal.symbol);
            }
        }
        System.out.println("Knowledge Base: " + this.knowledgeBase);
        System.out.println("Query: " + this.query);
    }

    public boolean check() {
        // Generate all possible models
        List<Map<String, Boolean>> models = generateModels(new ArrayList<>(symbols));

        // Check each model
        for (Map<String, Boolean> model : models) {
            if (evaluateModel(model)) {
                // If the query is true in a model where the knowledge base is also true,
                // then the query is entailed by the knowledge base.
                if (model.getOrDefault(query.symbol, false) == query.isPositive) {
                    return true;
                }
            }
        }

        // If no such model is found, the query is not entailed by the knowledge base.
        return false;
    }

    private List<Map<String, Boolean>> generateModels(List<String> symbols) {
        if (symbols.isEmpty()) {
            // Base case: no symbols left, return a model with no assignments
            return Collections.singletonList(new HashMap<>());
        }

        // Recursive case: choose a symbol and generate models for the rest
        String symbol = symbols.remove(symbols.size() - 1);
        List<Map<String, Boolean>> smallerModels = generateModels(symbols);

        // For each smaller model, create two larger models: one where the chosen symbol is true, and one where it's false
        List<Map<String, Boolean>> models = new ArrayList<>();
        for (Map<String, Boolean> smallerModel : smallerModels) {
            Map<String, Boolean> trueModel = new HashMap<>(smallerModel);
            trueModel.put(symbol, true);
            models.add(trueModel);

            Map<String, Boolean> falseModel = new HashMap<>(smallerModel);
            falseModel.put(symbol, false);
            models.add(falseModel);

            System.out.println("Model with " + symbol + ": " + trueModel);
            System.out.println("Model without " + symbol + ": " + falseModel);
        }

        return models;
    }


    private boolean evaluateModel(Map<String, Boolean> model) {
        for (Clause clause : knowledgeBase) {
            boolean clauseIsTrue = false;
            for (Literal literal : clause.literals) {
                Boolean value = model.get(literal.symbol);
                if (value != null && value == literal.isPositive) {
                    // This literal is true, so the clause is true
                    clauseIsTrue = true;
                    break;
                }
            }
            if (!clauseIsTrue) {
                // This clause is false, so the knowledge base is false
                return false;
            }
            System.out.println("Evaluation of model " + model + ": " + (clauseIsTrue ? "true" : "false"));
        }

        // All clauses are true, so the knowledge base is true
        return true;
    }

}
