/*
* generated by Xtext
*/
package com.ge.research.sadl.testsuite.ui.contentassist;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.ge.research.sadl.builder.ConfigurationManagerForIDE;
import com.ge.research.sadl.builder.ResourceManager;
import com.ge.research.sadl.builder.SadlModelManager;
import com.ge.research.sadl.reasoner.ConfigurationException;
import com.ge.research.sadl.reasoner.ConfigurationManager;
import com.ge.research.sadl.testsuite.testSuite.Model;
import com.ge.research.sadl.testsuite.testSuite.Test;
import com.ge.research.sadl.testsuite.ui.contentassist.AbstractTestSuiteProposalProvider;
import com.google.inject.Inject;
/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class TestSuiteProposalProvider extends AbstractTestSuiteProposalProvider {
	@Inject
    private SadlModelManager visitor;

	//	public void complete_Model(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
	//	System.out.println("call to complete_Model");
	//	super.complete_Model(model, ruleCall, context, acceptor);
	//}
	
//	public void complete_Test(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		System.out.println("call to complete_Test");
//		
//	}
	
	public void completeTest_Name(Test test, Assignment assignment, 
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) throws URISyntaxException, IOException {
		IPreferencesService service = Platform.getPreferencesService();
		String value = service.getString("com.ge.research.sadl.Sadl", "importBy", "fn", null);
		boolean useSadlFN = value.equals("fn");
		
	//	super.completeTest_Name(test, assignment, context, acceptor); 
		URI prjUri = ResourceManager.getProjectUri(context.getResource().getURI());
		List<String> possibleFiles = getPossibleFileList(test.eContainer(), useSadlFN, prjUri);
		if (possibleFiles != null) {
			for (int i = 0; i < possibleFiles.size(); i++) {
				String file = possibleFiles.get(i);
				String proposalText = getValueConverter().toString(file, "STRING");
				String displayText = proposalText + " - Uri of Model to test";
				Image image = getImage(test.eContainer());
				ICompletionProposal proposal = createCompletionProposal(proposalText, displayText, image, context);
				acceptor.accept(proposal);			
			}
		}
	}
	
	public void completeModel_Tests(Model model, Assignment assignment, 
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) throws URISyntaxException, IOException {
		IPreferencesService service = Platform.getPreferencesService();
		String value = service.getString("com.ge.research.sadl.Sadl", "importBy", "fn", null);
		boolean useSadlFN = value.equals("fn");
		
	//	super.completeTest_Name(test, assignment, context, acceptor); 
		URI prjUri = ResourceManager.getProjectUri(context.getResource().getURI());
		List<String> possibleFiles = getPossibleFileList(model, useSadlFN, prjUri);
		if (possibleFiles != null && possibleFiles.size() > 0) {
			for (int i = 0; i < possibleFiles.size(); i++) {
				String file = possibleFiles.get(i);
				String proposalText = "Test: " + getValueConverter().toString(file, "STRING") + ".\n";
				String displayText = proposalText + " - Test this Model";
				Image image = getImage(model.eContainer());
				ICompletionProposal proposal = createCompletionProposal(proposalText, displayText, image, context);
				acceptor.accept(proposal);			
			}
		}
	}
	
	private List<String> getPossibleFileList(EObject model, boolean useSadlFN, URI prjUri) throws URISyntaxException, IOException {
		File prjFolder = new File(prjUri.toFileString());
		if (prjFolder.exists() && prjFolder.isDirectory()) {
			// get all files with a .sadl ending
			List<File> possibleFiles = ResourceManager.findFilesInDirWithExtension(prjFolder, ResourceManager.SADLEXTWITHPREFIX);
			// get all files with a .test ending
			possibleFiles = ResourceManager.findFilesInDirWithExtension(possibleFiles, prjFolder, ".test");
			if (possibleFiles != null && possibleFiles.size() > 0) {
				List<String> results = new ArrayList<String>();
				// find out whether to show file URLs or http URLs for SADL files
				List<String> alreadyAdded = new ArrayList<String>();
				// add this one to those to exclude
				alreadyAdded.add(model.eResource().getURI().lastSegment());
				ConfigurationManager cmgr = null;
				try {
					if (visitor != null) {
						cmgr = visitor.getConfigurationMgr(ResourceManager.getOwlModelsFolder(model.eResource().getURI()));
					}
					else {
						cmgr = new ConfigurationManager(ResourceManager.getOwlModelsFolder(model.eResource().getURI()), ConfigurationManagerForIDE.getOWLFormat());
					}
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				// now get all of the files already imported into this test suite
				if (model instanceof Model) {
					EList<Test> tests = ((Model)model).getTests();
					if (tests != null && tests.size() > 0) {
						for (int i = 0; i < tests.size(); i++) {
							String aTest = tests.get(i).getName();
							if (aTest.startsWith("http:") && cmgr != null) {
								try {
									aTest = cmgr.getAltUrlFromPublicUri(aTest);
									if (aTest.endsWith(".owl")) {
										aTest = aTest.substring(0, aTest.length() - 3) + ResourceManager.SADLEXT;
									}
								} catch (ConfigurationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (aTest.indexOf('/') >= 0) {
								aTest = aTest.substring(aTest.lastIndexOf('/') + 1);
							}
							alreadyAdded.add(aTest);
						}
					}
				}
				if (alreadyAdded.size() > 0) {
					for (int i = possibleFiles.size() - 1; i >= 0; i--) {
						String file = possibleFiles.get(i).getName();
						if (!alreadyAdded.contains(file)) {
							if (!useSadlFN && cmgr != null) {
								file = cmgr.findPublicUriOfOwlFile(file);
							}
							if (file == null) {
								throw new MalformedURLException("Unable to find a URI in the mappings file for SADL file '" + possibleFiles.get(i).getCanonicalPath() + "'; try cleaning and rebuilding the project.");
							}
							results.add(file);
						}
					}
				}
				return results;
			}
		}
		return null;
	}

}
