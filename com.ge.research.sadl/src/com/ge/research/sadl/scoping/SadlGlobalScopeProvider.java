/************************************************************************
 * Copyright © 2007-2010 - General Electric Company, All Rights Reserved
 *
 * Project: SADL
 *
 * Description: The Semantic Application Design Language (SADL) is a
 * language for building semantic models and expressing rules that
 * capture additional domain knowledge. The SADL-IDE (integrated
 * development environment) is a set of Eclipse plug-ins that
 * support the editing and testing of semantic models using the
 * SADL language.
 *
 * This software is distributed "AS-IS" without ANY WARRANTIES
 * and licensed under the Eclipse Public License - v 1.0
 * which is available at http://www.eclipse.org/org/documents/epl-v10.php
 *
 ***********************************************************************/

/***********************************************************************
 * $Last revised by: crapo $
 * $Revision: 1.1 $ Last modified on   $Date: 2014/05/05 15:09:43 $
 ***********************************************************************/

package com.ge.research.sadl.scoping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;

import com.ge.research.sadl.builder.ConfigurationManagerForIDE;
import com.ge.research.sadl.builder.IConfigurationManagerForIDE;
import com.ge.research.sadl.builder.SadlModelManager;
import com.ge.research.sadl.reasoner.ConfigurationException;
import com.ge.research.sadl.sadl.Model;
import com.ge.research.sadl.sadl.SadlPackage;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class SadlGlobalScopeProvider extends ImportUriGlobalScopeProvider {
	@Inject
	private SadlModelManager visitor;

	@Inject
	IResourceDescription.Manager resourceDescriptionManager;

	/**
	 * For each imported owl resource, import also the accoring sadl resource to
	 * have the elements on the global scope
	 */
	@Override
	protected LinkedHashSet<URI> getImportedUris(final Resource resource) {
		// access ConfigurationManager to get all accessed URIs
		IConfigurationManagerForIDE cmgr = null;
		try {
			synchronized (resource) {
				cmgr = visitor.getConfigurationMgr(resource.getURI());
			}
			if (cmgr==null) {
				return super.getImportedUris(resource);
			}
			String publicUri = null;
			if (!resource.getContents().isEmpty()) {
				Model model = (Model) resource.getContents().get(0);
				publicUri = model.getModelName().getBaseUri();
			}
			
			LinkedHashSet<URI> uriSet = Sets.newLinkedHashSet();
			collectImportedURIs(resource, URI.createURI(publicUri), uriSet, cmgr);
			
			return uriSet;
		} catch (Exception e) {
			return super.getImportedUris(resource);
		}
	}
	

	/**
	 * Recursive method to resolve transitive imports. 
	 * @param resource The context resource
	 * @param publicURI The public URI of the imported resource
	 * @param uriSet The result set into which the URIs are collected
	 * @param cmgr The configuration manager for the context resource
	 * @throws ConfigurationException
	 * @throws MalformedURLException
	 */
	private void collectImportedURIs (Resource resource, URI publicURI, LinkedHashSet<URI> uriSet, IConfigurationManagerForIDE cmgr) throws ConfigurationException, MalformedURLException {
		URI altUrlFromPublicUri = URI.createURI(cmgr.getAltUrlFromPublicUri(publicURI.toString()));
		// For SADL derived OWL models, resolve the SADL resource URI from the index.
		if (cmgr.isSadlDerived(publicURI.toString())) {
			// TODO: Use ResourceManager#sadlFileNameOfOwlAltUrl
			IResourceDescriptions descriptions = getResourceDescriptions(resource);
			Iterable<IEObjectDescription> matchingModels = descriptions.getExportedObjects(SadlPackage.Literals.MODEL, QualifiedName.create(publicURI.toString()), false);
			Iterator<IEObjectDescription> it = matchingModels.iterator();
			if (it.hasNext()) {
				IEObjectDescription description = it.next();
				// This will be the URI of the SADL file
				altUrlFromPublicUri = description.getEObjectURI().trimFragment();
			}
		}
		if (uriSet.add(altUrlFromPublicUri)) {
			// This URI was not collected yet, thus collect its imports again
			try {
				Map<String, String> imports = cmgr.getImports(publicURI.toString());
				for (String s: imports.keySet()) {
					URI uri = URI.createURI(s);
					collectImportedURIs(resource, uri, uriSet, cmgr);
				}
			} catch (ConfigurationException e) {
				; // TODO: Handle exception
			} catch (IOException e) {
				; // TODO: Handle exception
			}
		}
	}
}
