import java.util.*;

public class TruthTable
{
    private List<Clause> knowledgeBase;
    private Literal query;
    private Set<String> symbols;
    private int modelsEntailment;

    public TruthTable(List<Clause> knowledgeBase, Literal query)
    {
        // store kb and query
        this.knowledgeBase = knowledgeBase;
        this.query = query;

        // Extract all symbols from the knowledge base
        this.symbols = new HashSet<>();
        for (Clause clause : knowledgeBase) //each clause in kb
        {
            for (Literal literal : clause.literals) // each literal in clause
            {
                symbols.add(literal.symbol);    // add symbol to hash set
            }
        }
        // REMOVE
        System.out.println("Knowledge Base: " + this.knowledgeBase);
        System.out.println("Query: " + this.query);
    }

    public boolean check()
    {
        // Generate all possible models
        List<Map<String, Boolean>> models = generateModels(new ArrayList<>(symbols));
        boolean isEntailed = false;

        // Counters for the number of models where the KB and query are true
        int modelsKB = 0;

        // Check each model
        for (Map<String, Boolean> model : models)
        {
            if (evaluateModel(model))
            {
                // If the knowledge base is true, increment the modelsKB counter
                modelsKB++;
                // If the query is true in a model where the knowledge base is also true,
                // then the query is entailed by the knowledge base.
                if (model.getOrDefault(query.symbol, false) == query.isPositive)
                {
                    isEntailed = true;
                    modelsEntailment++;
                }
            }
            // REMOVE
            System.out.println("Evaluation of model " + model + ": " + (evaluateModel(model) ? "true" : "false"));
        }
        // REMOVE
        // Print the number of models where the KB and query are true
        System.out.println("Number of models where KB is true: " + modelsKB);
        System.out.println("Number of models where KB entails the query: " + modelsEntailment);
        // Return whether the query is entailed by the knowledge base
        return isEntailed;
    }

    private List<Map<String, Boolean>> generateModels(List<String> symbols)
    {
        if (symbols.isEmpty())
        {
            // Base case: no symbols left, return a model with no assignments
            return Collections.singletonList(new HashMap<>());
        }

        // Recursive case: choose a symbol and generate models for the rest
        String symbol = symbols.remove(symbols.size() - 1);
        List<Map<String, Boolean>> smallerModels = generateModels(symbols);

        // For each smaller model, create two larger models: one where the chosen symbol is true, and one where it's false
        List<Map<String, Boolean>> models = new ArrayList<>();
        for (Map<String, Boolean> smallerModel : smallerModels)
        {
            // create a new model where symbol is true
            Map<String, Boolean> trueModel = new HashMap<>(smallerModel);
            trueModel.put(symbol, true);

            // add model to list
            if (!models.contains(trueModel))
            {
                models.add(trueModel);
            }

            // as above but for false
            Map<String, Boolean> falseModel = new HashMap<>(smallerModel);
            falseModel.put(symbol, false);
            if (!models.contains(falseModel))
            {
                models.add(falseModel);
            }
            // REMOVE
            System.out.println("Model with " + symbol + ": " + trueModel);
            System.out.println("Model without " + symbol + ": " + falseModel);
        }

        return models;
    }

    public int getModelsEntailment()
    {
        return this.modelsEntailment;
    }

    private boolean evaluateModel(Map<String, Boolean> model)
    {
        for (Clause clause : knowledgeBase)
        {
            // Assume the clause is false until proven otherwise
            boolean clauseIsTrue = false;

            for (Literal literal : clause.literals)
            {
                Boolean valueInModel = model.get(literal.symbol);

                // Determine the truth value of the literal in the current model
                boolean literalIsTrue = literal.isPositive ? valueInModel : !valueInModel;

                // if the literal is true, the entire clause is true
                if (literalIsTrue)
                {
                    clauseIsTrue = true;
                    break; // No need to check the rest of the literals in this clause
                }
            }

            // If none of the literals in the clause are true, the clause is false
            if (!clauseIsTrue)
            {
                return false; // This clause is false, so the knowledge base is false
            }
        }
        // All clauses are true, so the knowledge base is true
        return true;
    }

}