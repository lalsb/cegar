package com.app.model.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtomicFormulaScanner {
	
	/**
	 * Regex pattern for brackets
	 */
	public static Pattern brackets = Pattern.compile("\\([^()]*\\)"); 
	
	/**
	 * Regex pattern for junctions
	 */
	public static Pattern junctions = Pattern.compile("\\s*([&|∧∨])\\s*");


    /** Receives a <i>predicate</i> and returns all terms possibly matching an {@code AtomicFormula} as a {@Code List<String>}.
     * 
     *  A predicate is defined as any number of terms of type {@code AtomicFormula}, joined by <i>junctions</i>.
     *  An {@code AtomicFormula} is defined as 2 terms of type {@Code Expression}, joined by a <i>relation</i>.
     *  
     * @param Predicate
     * @return List of Strings that are atomic f
     * @see AtomicFormula
     * @see Expression
     */
    public static List<String> scan(String predicate) {	
    	
        List<String> atomicFormulaTerms = new ArrayList<>();
    	
    	predicate =  predicate.replaceAll("[¬~]", "");

        // Process brackets recursively
        Matcher bracketTerm = brackets.matcher(predicate);
        
        while (bracketTerm.find()) {
            String subTerm = bracketTerm.group();
            atomicFormulaTerms.addAll(scan(subTerm.substring(1, subTerm.length() - 1)));
        }

        String remainingTerm = bracketTerm.replaceAll("");
        
        Matcher junctionMatcher = junctions.matcher(remainingTerm);

        // Process {@code AtomicFormula} terms
        int i = 0;

        while (junctionMatcher.find()) {
            String atomicTerm = remainingTerm.substring(i, junctionMatcher.start()).trim();
            if (!atomicTerm.isEmpty()) {
                if (hasOneRelation(atomicTerm) && containsLiterals(atomicTerm)) {
                    atomicFormulaTerms.add(removeBrackets(atomicTerm));
                }
            }
            i = junctionMatcher.end();
        }

        // Process last {@code AtomicFormula} term
        String atomicTerm = remainingTerm.substring(i).trim();
        if (!atomicTerm.isEmpty() && hasOneRelation(atomicTerm) && containsLiterals(atomicTerm)) {
            atomicFormulaTerms.add(removeBrackets(atomicTerm));
        }

        return atomicFormulaTerms;
    }

    /**
     * Method to count the number of relations in a String
     * @param String with relation symbols
     * @return Number of relation symbols
     */
    private static boolean hasOneRelation(String substring) {
        int count = 0;
        for (char c : substring.toCharArray()) {
            if (c == '<' || c == '>' || c == '=') {
                count++;
            }
        }
        return count == 1;
    }

    /**
     * Method to check if a string contains only '0's or '1's
     * @param String
     * @return Boolean
     */
    private static boolean containsLiterals(String str) {
        for (char c : str.toCharArray()) {
            if (c != '0' && c != '1' && c != '=' && c != '<' && c != '>') {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to remove all brackets and whitespace from a string
     * @param String with brackets and whitespace
     * @return String without brackets and whitespace
     */
    private static String removeBrackets(String str) {
        return str.replaceAll("[()\\s]", "");
    }

}
