import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {
    public List<Clause> clauses;

    public KnowledgeBase() {
        this.clauses = new ArrayList<>();
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }
}
