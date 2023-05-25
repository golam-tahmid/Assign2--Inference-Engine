import java.util.*;
import java.util.stream.Collectors;

public class HornClause
{
    public Literal head;  // The head of the clause (positive or negative literal)
    public Set<Literal> body;  // The body of the clause (negative literals)

    public HornClause()
    {
        this.body = new HashSet<>();
    }   // constructor

    public void setHead(Literal literal)
    {
        // check head not already occupied, should not be reached
        if (this.head != null)
        {
            throw new IllegalArgumentException("Horn Clause can have at most one head literal");
        }
        this.head = literal;    // set head
    }

    public void addBodyLiteral(Literal literal)
    {
        this.body.add(literal);
    }   // add to body method

    // toString for printing statements for debug purposes
    @Override
    public String toString()
    {
        if (this.body.isEmpty())
        {
            return this.head != null ? this.head.toString() : "";
        }
        else
        {
            String bodyString = this.body.stream()
                    .map(Literal::toString)
                    .collect(Collectors.joining(", "));
            return bodyString + (this.head != null ? " => " + this.head : "");
        }
    }
}



