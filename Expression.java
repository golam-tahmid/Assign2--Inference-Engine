public class Expression {
    public String operator;
    public Expression left;
    public Expression right;

    public Expression(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    // Testing purposes, for printing parsed file
    @Override
    public String toString() {
        if (left == null && right == null) {
            return operator;
        }
        if (operator.equals("~")) {
            return operator + right.toString();
        }
        String leftString = (left.operator != null && (left.operator.equals("=>") || left.operator.equals("<=>"))) ? "(" + left.toString() + ")" : left.toString();
        String rightString = (right.operator != null && (right.operator.equals("=>") || right.operator.equals("<=>"))) ? "(" + right.toString() + ")" : right.toString();
        return leftString + " " + operator + " " + rightString;
    }
}
