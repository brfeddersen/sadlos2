/*
* generated by Xtext
*/
package com.ge.research.sadl.testsuite.parser.antlr;

import java.io.InputStream;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;

public class TestSuiteAntlrTokenFileProvider implements IAntlrTokenFileProvider {
	
	public InputStream getAntlrTokenFile() {
		ClassLoader classLoader = getClass().getClassLoader();
    	return classLoader.getResourceAsStream("com/ge/research/sadl/testsuite/parser/antlr/internal/InternalTestSuite.tokens");
	}
}
