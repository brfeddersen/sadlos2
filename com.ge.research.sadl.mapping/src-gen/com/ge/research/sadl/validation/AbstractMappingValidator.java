/*
 * generated by Xtext
 */
package com.ge.research.sadl.validation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;

public class AbstractMappingValidator extends org.eclipse.xtext.validation.AbstractDeclarativeValidator {

	@Override
	protected List<EPackage> getEPackages() {
	    List<EPackage> result = new ArrayList<EPackage>();
	    result.add(com.ge.research.sadl.mapping.MappingPackage.eINSTANCE);
		return result;
	}
}