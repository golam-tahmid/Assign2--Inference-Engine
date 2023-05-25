import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Parser
{

    // Nested class to hold parsed knowledge base and query identifier
    public class Result
    {
        public List<Expression> knowledgeBase;
        public List<Clause> Clauses;
        public String query;

        // Constructor for initialising Result nested class
        public Result(List<Expression> knowledgeBase, String query)
        {
            this.knowledgeBase = knowledgeBase;
            this.Clauses = new ArrayList<>();
            this.query = query;
        }
    }

    // Read the contents of the text file into a string builder
    public Result parse(String filename) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;

        // Read the text file line by line and append each line to the string
        while ((line = reader.readLine()) != null)
        {
            sb.append(line);
        }

        reader.close();
        return parseInput(sb.toString());  // Call parseInput with complete string
    }

    // Split the string into sections for TELL and ASK
    private Result parseInput(String input)
    {
        List<Expression> knowledgeBase = new ArrayList<>();
        String query = null;

        String[] sections = input.split("TELL|ASK");    // Split string into TELL and ASK sections
        // If the string has been split successfully
        if (sections.length == 3)
        {
            String[] expressions = sections[1].trim().split(";");   // Split kb section and store in expressions array

            // For each expression in the new expressions array
            for (String expression : expressions)
            {
                Expression parsedExpression = parseExpression(expression.trim());
                // REMOVE
                System.out.println("Parsed expression: " + parsedExpression);
                knowledgeBase.add(parsedExpression);
            }

            query = sections[2].trim();     // Trim any whitespace from query and add to query variable
        }

        return new Result(knowledgeBase, query);    // Return the object containing the kb and the query
    }

    private Expression parseExpression(String input)
    {
        Stack<Character> operators = new Stack<>();
        Stack<Expression> operands = new Stack<>();

        // For each character in the input
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            // Push to stack
            if (ch == '(')
            {
                operators.push(ch);
            }
            else if (ch == '=')
            {
                // check the next character
                if (i + 1 < input.length() && input.charAt(i + 1) == '>')
                {
                    operators.push('>');
                    i++; // skip the next character
                }
            }
            else if (ch == '<')
            {
                if (i + 2 < input.length() && input.charAt(i + 1) == '=' && input.charAt(i + 2) == '>')
                {
                    operators.push('<');
                    i += 2; // skip the next two characters
                }
            }
            else if (ch == '|')
            {
                if (i + 1 < input.length() && input.charAt(i + 1) == '|')
                {
                    operators.push('|');
                    i++; // skip the next character
                }
            }
            else if (ch == ')')
            {
                // process all operators until the opening parenthesis
                while (!operators.isEmpty() && operators.peek() != '(')
                {
                    createExpression(operators, operands);
                }
                operators.pop(); // remove the '('
            }
            else if (isOperator(ch))
            {
                while (!operators.isEmpty() && getPriority(operators.peek()) >= getPriority(ch))
                {
                    createExpression(operators, operands);
                }
                operators.push(ch);
            }
            else if (Character.isLetterOrDigit(ch))
            {
                StringBuilder operand = new StringBuilder();
                while (i < input.length() && Character.isLetterOrDigit(input.charAt(i)))
                {
                    operand.append(input.charAt(i++));
                }
                i--; // adjusting for extra increment
                operands.push(new Expression(operand.toString()));
            }
        }
        // create expressions from remaining operators
        while (!operators.isEmpty())
        {
            createExpression(operators, operands);
        }
        // If the operand stack is empty, return null; otherwise, return the last expression in the stack
        return operands.isEmpty() ? null : operands.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '~' || ch == '&' || ch == '|' || ch == '>' || ch == '<';
    }

    // for truth table checking
    public List<Clause> transformToClauses(List<Expression> knowledgeBase)
    {
        List<Clause> clauses = new ArrayList<>();

        // for every expression in the knowledge base
        for (Expression expression : knowledgeBase)
        {
            // REMOVE
            System.out.println("Transforming expression: " + expression);
            List<Clause> transformedClauses = transformExpressionToClauses(expression); // call the transform expression to clauses method
            clauses.addAll(transformedClauses); // add them to the list
            // REMOVE
            transformedClauses.forEach(c -> System.out.println("Transformed clause: " + c));
        }

        return clauses;
    }


    // as above but with horn clauses for use with fc and bc
    public List<HornClause> transformToHornClauses(List<Expression> knowledgeBase)
    {
        List<HornClause> HornClauses = new ArrayList<>();

        for (Expression expression : knowledgeBase)
        {
            List<HornClause> transformedClauses = transformExpressionToHornClauses(expression); // calls the expression to horn clauses method
            HornClauses.addAll(transformedClauses);
            // REMOVE
            transformedClauses.forEach(c -> System.out.println("Transformed clause: " + c));
        }

        return HornClauses;
    }

    //version for truth table checking, propositional logic, each clause is a disjunction of literals
    private List<Clause> transformExpressionToClauses(Expression expression)
    {
        // REMOVE
        System.out.println("Inside transformExpressionToClauses with expression: " + expression);

        if (expression.operator == null || expression.operator.equals("~"))
        {
            // This is a literal, so create a clause with a single literal
            List<Literal> literals = transformExpressionToLiterals(expression);
            Clause clause = new Clause(literals);
            return Collections.singletonList(clause);
        }
        else if (expression.operator.equals("||"))
        {
            // This is a disjunction of literals, so create a clause with multiple literals
            List<Literal> literals = transformExpressionToLiterals(expression);
            Clause clause = new Clause(literals);
            return Collections.singletonList(clause);
        }
        else if (expression.operator.equals("&"))
        {
            // This is a conjunction, so split it into separate clauses
            List<Clause> clauses = new ArrayList<>();
            clauses.addAll(transformExpressionToClauses(expression.left));
            clauses.addAll(transformExpressionToClauses(expression.right));
            return clauses;
        }
        else if (expression.operator.equals("=>"))
        {
            // This is an implication, so transform it to a disjunction with the left operand negated and then create clauses
            Expression negatedLeft = negateExpression(expression.left);
            Expression disjunction = new Expression("||", negatedLeft, expression.right);
            return transformExpressionToClauses(disjunction);
        }
        else if (expression.operator.equals("<=>"))
        {
            // This is a biconditional, so transform it into an equivalent conjunction and then create clauses
            Expression leftImplication = new Expression("=>", expression.left, expression.right);
            Expression rightImplication = new Expression("=>", expression.right, expression.left);
            Expression conjunction = new Expression("&", leftImplication, rightImplication);
            return transformExpressionToClauses(conjunction);
        }
        return Collections.emptyList();
    }

    // for use with fc and bc this transforms to horn clauses, so a bit more involved than above
    private List<HornClause> transformExpressionToHornClauses(Expression expression)
    {
        List<HornClause> hornClauses = new ArrayList<>();

        if (expression.operator == null)
        {
            // This is a symbol
            HornClause clause = new HornClause();
            clause.setHead(new Literal(expression.symbol, true));
            hornClauses.add(clause);
        }
        else if (expression.operator.equals("~"))
        {
            // Negated symbol, as above
            HornClause clause = new HornClause();
            clause.setHead(new Literal(expression.right.symbol, false));    //expression.right to reach the symbol after the negation
            hornClauses.add(clause);
        }
        else if (expression.operator.equals("&"))
        {
            // This is a conjunction. For a conjunction where the left side is a symbol
            // and the right side is an implication, create a single Horn clause instead of two
            // otherwise separate horn clauses are created for each part of the conjunction
            if (expression.right.operator != null && expression.right.operator.equals("=>"))
            {
                HornClause clause = new HornClause();
                clause.setHead(transformExpressionToLiteral(expression.right.right));
                clause.addBodyLiteral(transformExpressionToLiteral(expression.right.left));
                clause.addBodyLiteral(transformExpressionToLiteral(expression.left));
                hornClauses.add(clause);
            }
            else
            {
                // If the conjunction is not of the above form, create separate Horn clauses for each part
                // this is a recursive call back to this method to do so
                hornClauses.addAll(transformExpressionToHornClauses(expression.left));
                hornClauses.addAll(transformExpressionToHornClauses(expression.right));
            }
        }
        else if (expression.operator.equals("||"))
        {
            // This is a disjunction, which cannot be directly transformed into a Horn clause without disjunctive normal form or something like that
            throw new UnsupportedOperationException("Disjunctions are not supported in Horn clauses.");
        }
        else if (expression.operator.equals("=>"))
        {
            // This is an implication, create a new Horn clause with the right side as the head
            Literal head = transformExpressionToLiteral(expression.right);
            HornClause clause = new HornClause();
            clause.setHead(head);

            // If the left side is a conjunction, add each part of the conjunction to the body of the clause.
            if (expression.left.operator != null && expression.left.operator.equals("&"))
            {
                clause.addBodyLiteral(transformExpressionToLiteral(expression.left.left));
                clause.addBodyLiteral(transformExpressionToLiteral(expression.left.right));
            }
            else
            {
                // If the left side is not a conjunction, just add it to the body of the clause.
                clause.addBodyLiteral(transformExpressionToLiteral(expression.left));
            }

            hornClauses.add(clause);
        }
        else if (expression.operator.equals("<=>"))
        {
            // This is a biconditional, which cannot be directly transformed into a Horn clause
            // should not be reached unless testing a biconditional with fc or bc
            throw new UnsupportedOperationException("Biconditionals are not supported in Horn clauses.");
        }

        return hornClauses;
    }


    // transforms an expression into a list of literal objects
    private List<Literal> transformExpressionToLiterals(Expression expression)
    {
        List<Literal> literals = new ArrayList<>();

        // REMOVE
        System.out.println("Inside transformExpressionToLiterals with expression: " + expression);

        if (expression.operator == null) // no operator so symbol
        {
            literals.add(new Literal(expression.symbol, true));
        }
        else if (expression.operator.equals("~"))  // negated symbol
        {
            // If the right expression is a simple symbol (check for complex expressions)
            if (expression.right.operator == null)
            {
                literals.add(new Literal(expression.right.symbol, false));
            }
            else
            {  // if the right expression is more complex
                literals.addAll(transformExpressionToLiterals(expression.right));
            }
        }
        else if (expression.operator.equals("||") || expression.operator.equals("&"))
        {
            // If the expression is a conjunction or disjunction,
            // apply this method to both the left and right side of the expression
            literals.addAll(transformExpressionToLiterals(expression.left));
            literals.addAll(transformExpressionToLiterals(expression.right));
        }
        else if (expression.operator.equals("=>"))
        {
            // If the expression is an implication,
            // transform it into a disjunction with the left operand negated
            Expression negatedLeft = negateExpression(expression.left);
            Expression disjunction = new Expression("||", negatedLeft, expression.right);

            // apply this method to the new disjunction
            literals.addAll(transformExpressionToLiterals(disjunction));
        }
        else if (expression.operator.equals("<=>"))
        {
            // If the expression is a biconditional,
            // transform it into a conjunction of two implications
            Expression leftImplication = new Expression("=>", expression.left, expression.right);
            Expression rightImplication = new Expression("=>", expression.right, expression.left);
            Expression conjunction = new Expression("&", leftImplication, rightImplication);

            // apply this method to the new conjuction
            literals.addAll(transformExpressionToLiterals(conjunction));
        }
        else
        {
            // if something goes wrong
            System.out.println("Unhandled operator in transformExpressionToLiterals: " + expression.operator);
        }
        return literals;
    }



    // as above but for singular expressions, used in transform expression to horn clauses method
    // much simpler as doesn't need to deal with complex expressions
    private Literal transformExpressionToLiteral(Expression expression)
    {
        if (expression.operator == null) //symbol
        {
            return new Literal(expression.symbol, true);
        }
        else if (expression.operator.equals("~")) // negated symbol
        {
            return new Literal(expression.right.symbol, false);
        }
        else
        {
            // cant handle complex expressions, this should not be reached
            throw new IllegalArgumentException("Cannot transform complex expression to single literal");
        }
    }

    // returns the priority level for different operators
    private int getPriority(char ch) {
        switch (ch) {
            case '~':
                return 3;
            case '&':
                return 2;
            case '|':
                return 2;
            case '>':
                return 1;  // lowered precedence for '=>'
            case '<':
                return 1;  // lowered precedence for '<=>'
            default:
                return 0;
        }
    }


    // creates expressions using the operators and operands stacks
    private void createExpression(Stack<Character> operators, Stack<Expression> operands)
    {
        // this should not happen
        if (operators.isEmpty())
        {
            throw new IllegalStateException("No more operators to process");
        }
        char op = operators.pop(); // pop operator from the stack
        String operator = null;

        // set operator string value to operator popped
        switch (op)
        {
            case '~':
                operator = "~";
                break;
            case '&':
                operator = "&";
                break;
            case '|':
                operator = "||";
                break;
            case '>':
                operator = "=>";
                break;
            case '<':
                operator = "<=>";
                break;
            default:
                throw new IllegalArgumentException("Unexpected operator: " + op); // invalid input of some kind
        }

        Expression right = operands.pop();  // pop the operand from the top of the stack and set it to the right
        Expression left = (operator.equals("~")) ? null : operands.pop(); // if negation there is no left operand so set null, else pop operand and set to left of operator
        operands.push(new Expression(operator, left, right));
    }

    // negates expressions
    private Expression negateExpression(Expression expression)
    {
        if (expression.operator == null)
        {
            // This is a symbol, so negate it
            return new Expression("~", null, new Expression(expression.symbol));
        }
        else if (expression.operator.equals("~"))
        {
            // This is a negation, so return the original expression
            return expression.right;
        }
        else if (expression.operator.equals("&"))
        {
            // This is a conjunction, so apply De Morgan's law
            return new Expression("||", negateExpression(expression.left), negateExpression(expression.right));
        }
        else if (expression.operator.equals("||"))
        {
            // This is a disjunction, so apply De Morgan's law
            return new Expression("&", negateExpression(expression.left), negateExpression(expression.right));
        }
        else if (expression.operator.equals("=>"))
        {
            // This is an implication, so negate it to become a conjunction
            return new Expression("&", expression.left, negateExpression(expression.right));
        }
        else if (expression.operator.equals("<=>"))
        {
            // This is a biconditional, so negate it to become a disjunction
            Expression notLeft = negateExpression(expression.left);
            Expression notRight = negateExpression(expression.right);
            return new Expression("||", new Expression("&", expression.left, notRight), new Expression("&", notLeft, expression.right));
        }
        else
        {
            throw new IllegalArgumentException("Unknown operator: " + expression.operator);
        }
    }
}
