package structures;

public final class BracketChecker {
    private BracketChecker() { }

    public static boolean check(String expression, StringBuilder log) {
        CustomStack stack = new CustomStack(expression.length() + 1);
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (isOpening(c)) {
                stack.push(c);
                log.append("Read '").append(c).append("' -> opening bracket, push.   Stack: ")
                   .append(show(stack)).append('\n');
            } else if (isClosing(c)) {
                if (stack.isEmpty()) {
                    log.append("Read '").append(c).append("' -> closing bracket but the stack is empty, nothing to match.\n");
                    log.append("\nRESULT: NOT balanced (extra closing bracket '").append(c).append("').\n");
                    return false;
                }
                char open = (char) stack.pop();
                if (!matches(open, c)) {
                    log.append("Read '").append(c).append("' -> popped '").append(open)
                       .append("', which does not match.   Stack: ").append(show(stack)).append('\n');
                    log.append("\nRESULT: NOT balanced ('").append(open).append("' cannot be closed by '").append(c).append("').\n");
                    return false;
                }
                log.append("Read '").append(c).append("' -> matches '").append(open)
                   .append("', pop.      Stack: ").append(show(stack)).append('\n');
            } else {
                log.append("Read '").append(c).append("' -> not a bracket, ignored.\n");
            }
        }
        if (!stack.isEmpty()) {
            log.append("\nEnd of expression, but the stack still holds ").append(stack.size())
               .append(" unclosed bracket(s): ").append(show(stack)).append('\n');
            log.append("RESULT: NOT balanced.\n");
            return false;
        }
        log.append("\nEnd of expression and the stack is empty.\nRESULT: balanced.\n");
        return true;
    }

    private static boolean isOpening(char c) { return c == '(' || c == '[' || c == '{'; }
    private static boolean isClosing(char c) { return c == ')' || c == ']' || c == '}'; }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')') || (open == '[' && close == ']') || (open == '{' && close == '}');
    }

    private static String show(CustomStack stack) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < stack.size(); i++) {
            sb.append((char) stack.get(i));
            if (i < stack.size() - 1) sb.append(' ');
        }
        return sb.append("]  (bottom -> top)").toString();
    }
}
