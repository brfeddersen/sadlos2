/*
* generated by Xtext
*/
package com.ge.research.sadl.parser.antlr;

import java.io.InputStream;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;

public class SadlAntlrTokenFileProvider implements IAntlrTokenFileProvider {
	
	public InputStream getAntlrTokenFile() {
		ClassLoader classLoader = getClass().getClassLoader();
    	return classLoader.getResourceAsStream("com/ge/research/sadl/parser/antlr/internal/InternalSadl.tokens");
	}
}
