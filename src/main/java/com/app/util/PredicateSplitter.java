package com.app.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PredicateSplitter {


    public static List<String> splitPredicate(String booleanExpression) {
    	// Remove all occurrences of "¬" and "~"
        String cleanedExpression = booleanExpression.replaceAll("[¬~]", "");

        List<String> substrings = new ArrayList<>();

        // Regular expression to match expressions within parentheses
        String subExpressionRegex = "\\([^()]*\\)";
        Pattern subExpressionPattern = Pattern.compile(subExpressionRegex);

        // Process subexpressions recursively
        Matcher subExpressionMatcher = subExpressionPattern.matcher(cleanedExpression);
        while (subExpressionMatcher.find()) {
            String subExpression = subExpressionMatcher.group();
            substrings.addAll(splitPredicate(subExpression.substring(1, subExpression.length() - 1)));
        }

        // Remove processed subexpressions from the original expression
        String expressionWithoutSubexpressions = subExpressionMatcher.replaceAll("");

        // Regular expression to split the remaining expression based on junctions (&, |, ∧, ∨)
        String junctionRegex = "\\s*([&|∧∨])\\s*";
        Pattern junctionPattern = Pattern.compile(junctionRegex);
        Matcher junctionMatcher = junctionPattern.matcher(expressionWithoutSubexpressions);

        // Index to keep track of the start of the next substring
        int startIndex = 0;

        while (junctionMatcher.find()) {
            // Extract the substring between the current match and the previous one
            String substring = expressionWithoutSubexpressions.substring(startIndex, junctionMatcher.start()).trim();
            if (!substring.isEmpty()) {
                // If the substring contains exactly one relation and doesn't contain only 0s or 1s, add it to the list
                if (countRelations(substring) == 1 && !containsOnlyZeroesOrOnes(substring)) {
                    substrings.add(removeBrackets(substring));
                }
            }
            // Update the start index for the next substring
            startIndex = junctionMatcher.end();
        }

        // Add the last substring after the last match
        String lastSubstring = expressionWithoutSubexpressions.substring(startIndex).trim();
        if (!lastSubstring.isEmpty() && countRelations(lastSubstring) == 1 && !containsOnlyZeroesOrOnes(lastSubstring)) {
            substrings.add(removeBrackets(lastSubstring));
        }

        return substrings;
    }

    // Method to count the number of relations in a substring
    private static int countRelations(String substring) {
        int count = 0;
        for (char c : substring.toCharArray()) {
            if (c == '<' || c == '>' || c == '=') {
                count++;
            }
        }
        return count;
    }

    // Method to check if a string contains only '0's or '1's
    private static boolean containsOnlyZeroesOrOnes(String str) {
        for (char c : str.toCharArray()) {
            if (c != '0' && c != '1' && c != '=' && c != '<' && c != '>') {
                return false;
            }
        }
        return true;
    }

    // Method to remove all brackets and whitespace from a string
    private static String removeBrackets(String str) {
        return str.replaceAll("[()\\s]", "");
    }

}
