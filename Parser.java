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
            this.hornClauses = new ArrayList<>();
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

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '(') {
                operators.push(ch);
            } else if (ch == '=') {
                if (i + 1 < input.length() && input.charAt(i + 1) == '>') {
                    operators.push('>');
                    i++; // skip the next character
                }
            } else if (ch == '<') {
                if (i + 2 < input.length() && input.charAt(i + 1) == '=' && input.charAt(i + 2) == '>') {
                    operators.push('<');
                    i += 2; // skip the next two characters
                }
            } else if (ch == '|') {
                if (i + 1 < input.length() && input.charAt(i + 1) == '|') {
                    operators.push('|');
                    i++; // skip the next character
                }
            } else if (ch == ')') {
                // process all operators until the opening parenthesis
                while (!operators.isEmpty() && operators.peek() != '(') {
                    createExpression(operators, operands);
                }
                operators.pop(); // remove the '('
            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && getPriority(operators.peek()) >= getPriority(ch)) {
                    createExpression(operators, operands);
                }
                operators.push(ch);
            } else if (Character.isLetterOrDigit(ch)) {
                StringBuilder operand = new StringBuilder();
                while (i < input.length() && Character.isLetterOrDigit(input.charAt(i))) {
                    operand.append(input.charAt(i++));
                }
                i--; // adjusting for extra increment
                operands.push(new Expression(operand.toString()));
            }
        }

        while (!operators.isEmpty()) {
            createExpression(operators, operands);
        }

        return operands.isEmpty() ? null : operands.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '~' || ch == '&' || ch == '|' || ch == '>' || ch == '<';
    }

    public List<Clause> transformToClauses(List<Expression> knowledgeBase) {
        List<Clause> clauses = new ArrayList<>();

        for (Expression expression : knowledgeBase) {
            List<Clause> transformedClauses = transformExpressionToClauses(expression);
            clauses.addAll(transformedClauses);
            transformedClauses.forEach(c -> System.out.println("Transformed clause: " + c));
        }

        return clauses;
    }


    private List<Clause> transformExpressionToClauses(Expression expression) {
        List<Clause> clauses = new ArrayList<>();

        if (expression.operator == null) {
            // This is a symbol
            Clause clause = new Clause();
            clause.addLiteral(new Literal(expression.symbol, true));
            clauses.add(clause);
        } else if (expression.operator.equals("~")) {
            // This is a negated symbol
            Clause clause = new Clause();
            clause.addLiteral(new Literal(expression.right.symbol, false));
            clauses.add(clause);
        } else if (expression.operator.equals("&")) {
            // This is a conjunction, so split it into separate literals
            clauses.addAll(transformExpressionToClauses(expression.left));
            clauses.addAll(transformExpressionToClauses(expression.right));
        } else if (expression.operator.equals("||")) {
            // This is a disjunction, so create a new clause for each operand
            Clause clause = new Clause();
            clause.getLiterals().addAll(transformExpressionToLiterals(expression));
            clauses.add(clause);
        } else if (expression.operator.equals("=>")) {
            // This is an implication, so transform it to a disjunction and negate the left operand
            Expression negatedLeft = new Expression("~", null, expression.left);
            Expression disjunction = new Expression("||", negatedLeft, expression.right);
            clauses.addAll(transformExpressionToClauses(disjunction));
        } else if (expression.operator.equals("<=>")) {
            // This is a biconditional, so split it into two implications and handle each one separately
            // Note that this assumes that the biconditional is the root of the expression
            clauses.addAll(transformExpressionToClauses(new Expression("=>", expression.left, expression.right)));
            clauses.addAll(transformExpressionToClauses(new Expression("=>", expression.right, expression.left)));
        }

        return clauses;
    }

    private List<Literal> transformExpressionToLiterals(Expression expression) {
        List<Literal> literals = new ArrayList<>();
        if (expression.operator == null) {
            literals.add(new Literal(expression.symbol, true));
        } else if (expression.operator.equals("~")) {
            literals.add(new Literal(expression.right.symbol, false));
        } else if (expression.operator.equals("||")) {
            literals.addAll(transformExpressionToLiterals(expression.left));
            literals.addAll(transformExpressionToLiterals(expression.right));
        }
        return literals;
    }


    private int getPriority(char ch) {
        switch (ch) {
            case '~':
                return 4;
            case '&':
                return 3;
            case '|':
                return 2;
            case '>': // for => operator
            case '<': // for <=> operator
                return 1;
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
        if (operators.isEmpty()) {
            throw new IllegalStateException("No more operators to process");
        }
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
            case '>':
                operator = "=>";
                break;
            case '<':
                operator = "<=>";
                break;
            default:
                throw new IllegalArgumentException("Unexpected operator: " + op);
        }

        Expression right = operands.pop();
        Expression left = (operator.equals("~")) ? null : operands.pop();
        operands.push(new Expression(operator, left, right));
    }

}
