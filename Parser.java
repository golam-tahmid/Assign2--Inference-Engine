import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    // Nested class to hold parsed knowledge base and query identifier
    public class Result {
        public List<Expression> knowledgeBase;
        public List<Clause> hornClauses;
        public String query;

        public Result(List<Expression> knowledgeBase, String query) {
            this.knowledgeBase = knowledgeBase;
            this.hornClauses = hornClauses;
            this.query = query;
        }
    }

    // Read the contents of the text file into a string builder
    public Result parse(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;

        // Read the text file line by line and append each line to the string
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        reader.close();
        return parseInput(sb.toString());  // Call parseInput with complete string
    }

    // Split the string into sections for TELL and ASK
    private Result parseInput(String input) {
        List<Expression> knowledgeBase = new ArrayList<>();
        String query = null;

        String[] sections = input.split("TELL|ASK");    // Split string into TELL and ASK sections
        // If the string has been split successfully
        if (sections.length == 3) {
            String[] expressions = sections[1].trim().split(";");   // Split kb section and store in expressions array

            // For each expression in the new expressions array
            for (String expression : expressions) {
                Expression parsedExpression = parseExpression(expression.trim());
                System.out.println("Parsed expression: " + parsedExpression);
                knowledgeBase.add(parsedExpression);
            }

            query = sections[2].trim();     // Trim any whitespace from query and add to query variable
        }

        return new Result(knowledgeBase, query);    // Return the object containing the kb and the query
    }

    private Expression parseExpression(String input) {
        Stack<Character> operators = new Stack<>();
        Stack<Expression> operands = new Stack<>();

        // Loops through each character in the input string
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // Check if character is an operator or parenthesis
            if (ch == '(' || ch == '&' || ch == '|' || ch == '~' || ch == '=' || ch == '<') {
                if (ch == '|') {
                    i++; // Skip the next character for the || operator
                    processOperators(operators, operands, 1);
                    operators.push('|');
                } else {
                    if (ch == '=') {
                        if (i + 1 < input.length() && input.charAt(i + 1) == '>') {
                            i++; // Skip the '>' character for the => operator
                        }
                    } else if (ch == '<') {
                        if (i + 2 < input.length() && input.charAt(i + 1) == '=' && input.charAt(i + 2) == '>') {
                            i += 2; // Skip the '=>' characters for the <=> operator
                        }
                    }
                    processOperators(operators, operands, getPriority(ch));
                    operators.push(ch);
                }
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    createExpression(operators, operands);
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Remove the opening parenthesis
                }
            } else if (ch != ' ') {
                StringBuilder symbol = new StringBuilder();
                while (i < input.length() && Character.isLetterOrDigit(input.charAt(i))) {
                    symbol.append(input.charAt(i));
                    i++;
                }
                i--; // Adjust for the extra increment
                operands.push(new Expression(symbol.toString()));
            }
        }

        while (!operators.isEmpty()) {
            createExpression(operators, operands);
        }

        return operands.pop();
    }

    public List<Clause> transformToClauses(List<Expression> knowledgeBase) {
        List<Clause> clauses = new ArrayList<>();

        for (Expression expression : knowledgeBase) {
            Clause clause = new Clause();
            transformExpressionToClause(expression, clause);
            clauses.add(clause);
            System.out.println("Transformed clause: " + clause);
        }

        return clauses;
    }

    private void transformExpressionToClause(Expression expression, Clause clause) {
        if (expression.operator == null) {
            // This is a symbol
            clause.addLiteral(new Literal(expression.symbol, true)); // Use the symbol here, not the operator
        } else if (expression.operator.equals("~")) {
            // This is a negated symbol
            clause.addLiteral(new Literal(expression.right.symbol, false)); // Use the symbol here, not the operator
        } else if (expression.operator.equals("&")) {
            // This is a conjunction, so split it into separate literals
            transformExpressionToClause(expression.left, clause);
            transformExpressionToClause(expression.right, clause);
        } else if (expression.operator.equals("||")) {
            // This is a disjunction, so create a new clause for each operand
            Clause newClause = new Clause();
            transformExpressionToClause(expression.left, newClause);
            clause.addLiteral(new Literal(newClause.literals.get(0).symbol, newClause.literals.get(0).isPositive));
            transformExpressionToClause(expression.right, clause);
        } else if (expression.operator.equals("=>")) {
            // This is an implication, so transform it to a disjunction and negate the left operand
            transformExpressionToClause(new Expression("~", null, expression.left), clause);
            transformExpressionToClause(expression.right, clause);
        } else if (expression.operator.equals("<=>")) {
            // This is a biconditional, so split it into two implications and handle each one separately
            // Note that this assumes that the biconditional is the root of the expression
            transformExpressionToClause(new Expression("=>", expression.left, expression.right), clause);
            transformExpressionToClause(new Expression("=>", expression.right, expression.left), clause);
        }
    }

    private int getPriority(char op) {
        switch (op) {
            case '~':
                return 4;
            case '&':
                return 3;
            case '|':
                return 1;
            case '=':
                return 2;
            case '<':
                return 2;
            default:
                return 0;
        }
    }

    private void processOperators(Stack<Character> operators, Stack<Expression> operands, int priority) {
        while (!operators.isEmpty() && operators.peek() != '(' && getPriority(operators.peek()) >= priority) {
            createExpression(operators, operands);
        }
    }



    private void createExpression(Stack<Character> operators, Stack<Expression> operands) {
        char op = operators.pop();
        String operator = null;

        switch (op) {
            case '~':
                operator = "~";
                break;
            case '&':
                operator = "&";
                break;
            case '|':
                operator = "||";
                break;
            case '=':
                operator = "=>";
                break;
            case '<':
                operator = "<=>";
                break;
        }

        Expression right = operands.pop();
        Expression left = (operator.equals("~")) ? null : operands.pop();
        operands.push(new Expression(operator, left, right));
    }

}
