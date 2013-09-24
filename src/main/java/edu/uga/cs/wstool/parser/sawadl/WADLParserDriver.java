package edu.uga.cs.wstool.parser.sawadl;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

/*
 * generate url's and param names
 */

public class WADLParserDriver {

	// private List<List<String>> paramNames = new ArrayList<List<String>>();
	private List<Method> definedMethods = new ArrayList<Method>();

	private int count = 0;
	private List<Method> crossReferencedMethods = new ArrayList<Method>();
	private List<String> crossReferencedMethodURLS = new ArrayList<String>();

	private List<Method> completeMethodList = new ArrayList<Method>();
	private List<String> url = new ArrayList<String>();

	
	public List<Method> getCompleteMethodList() {
		return completeMethodList;
	}

	public List<String> getUrl() {
		return url;
	}

	public static void main(String[] args) throws Exception {

		WADLParserDriver mpw = new WADLParserDriver();
		//mpw.parse(new URL("http://cs.uga.edu/~ganjoo/galaxy/DDBJNCBIGenomeAnnotationGetGeneList.wadl"));
		mpw.parse(new URL("http://eupathdb.org/webservices/GeneQuestions/GenesByMolecularWeight.wadl"));
		//mpw.parse(new URL("http://nif-services.neuinfo.org/nif/services/application.wadl"));
		
		//mpw.displayInfo();

	}

	public List<Method> getMethods(Resource res) {
		return res.getMethods();
	}

	public void parse(URL fileURL) throws Exception {
		//int i = 0;
		// URL fileURL = new
		// URL("http://www.eupathdb.org/webservices/GeneQuestions/GenesByMolecularWeight.wadl");

		WADLParser wadlP = new WADLParser(fileURL);

		Application app = new Application();
		app = wadlP.getApplicationOfWADL();

		String temp = "";
		//String tempHref = "";

		for (Resources resources : app.getResources()) {
			temp = resources.getBase().toString();
			// traverseSubResources(temp, resources);
			// System.out.println(temp);
			for (Resource resource : resources.getResources()) {

				temp = temp + resource.getPath();
				traverseResource(temp, resource);
				// traverseSubResources(temp, resource);
				System.out.println("resource path = " + resource.getPath());
				if (resource.getParams() != null
						&& resource.getParams().size() != 0) {
					for (Param param : resource.getParams()) {
						System.out.println("resource = " + resource.getId() + ", params = " + param);
					}
				}

			}

		}

		traverseCrossReferencedMethods();
		displayInfo();

	}// end main

	public void traverseSubResources(String passedString, Resource resource) {

		for (Resource subResource : resource.getResources()) {

			String temp = passedString;

			if (temp.charAt(temp.length() - 1) != '/')
				temp += "/";
			temp += subResource.getPath();

			// if(subResource.getParams()!=null &&
			// subResource.getParams().size()!=0){
			// for(Param param: subResource.getParams()){
			// if(param.getStyle().equalsIgnoreCase("template")){
			// System.out.println("params "+ param.getName());
			// temp=temp+"/{"+param.getName()+"}";
			// }
			// }
			// }

			// start

			for (Method method : subResource.getMethods()) {
				if (method.getId() != null) {
					url.add(count, temp);
					definedMethods.add(method);

					completeMethodList.add(count, method);

					count++;
				} else if (method.getHref() != null) { // will work only for intra-document cross-reference currently

					crossReferencedMethods.add(method);
					crossReferencedMethodURLS.add(temp);

				} else {

					System.out.println("ERROR : Method " + method.getName()
							+ " does not have an id or a href !!");
				}
			}

			traverseSubResources(temp, subResource);

		}// end for

	}// end method

	public void traverseResource(String passedString, Resource resource) {

		String temp = passedString;

		// start

		for (Method method : resource.getMethods()) {
			if (method.getId() != null) {
				url.add(count, temp);
				definedMethods.add(method);

				completeMethodList.add(count, method);

				count++;
			} else if (method.getHref() != null) { // will work only for
													// intra-document
													// cross-reference currently

				crossReferencedMethods.add(method);
				crossReferencedMethodURLS.add(temp);

			} else {

				System.out.println("ERROR : Method " + method.getName()
						+ " does not have an id or a href !!");
			}
		}

		traverseSubResources(temp, resource);

	}// end method

	public void traverseCrossReferencedMethods() {
		int i = 0;
		String tempHref = "";

		for (Method method : crossReferencedMethods) {

			url.add(count, crossReferencedMethodURLS.get(i));

			tempHref = method.getHref().toString();
			if (tempHref.startsWith("#")) {
				tempHref = tempHref.substring(1);
			}

			for (Method m : definedMethods) {
				if (m.getId().equals(tempHref)) {

					Method tempMethod = new Method(m.getDocs(), m.getRequest(),
							m.getResponse(), m.getId(), m.getName(),
							method.getHref(), m.getElement());
					completeMethodList.add(count, tempMethod);

					break;

				}

			}

			count++;
			i++;
		}

	}// end of method

	// public void displayInfo(){
	// if(url.size()!=paramNames.size()){
	// System.out.println("Debug the code !!!! :( ");
	// System.exit(0);
	// }
	// for(int i=0;i<url.size();i++){
	// System.out.println(url.get(i));
	//
	// for(String p : paramNames.get(i)){
	// System.out.print(p + ", ");
	// }
	//
	// System.out.println();
	// System.out.println();
	// }
	//
	// }

	public void displayInfo() {

		if (url.size() != completeMethodList.size()) {
			System.out.println("Debug the code !!!! :( ");
			System.exit(0);
		}

		for (int i = 0; i < url.size(); i++) {
			System.out.println(url.get(i));

			for (Method m : completeMethodList) {
				System.out.println(m.getName() + " -> " + m.getId());
				if (m.getRequest() != null)
					for (Param param : m.getRequest().getParams()) {
						System.out.println("\t" + param.getName());
						System.out.println("\t" + param.isRequired());
						for (Doc doc : param.getDocs()) {
							if (doc != null
									&& doc.getTitle() != null
									&& doc.getTitle()
											.equalsIgnoreCase("prompt")) {
								System.out.println("Prompt: "
										+ doc.getInnerText());
							}

						}
						//List<Option> ops = param.getOptions();

						// if(ops.size()==0){
						// System.out.println("No options available");
						// }
						// else{
						// System.out.println("\t options:");
						//
						// for(Option option:param.getOptions()){
						// System.out.println("\t "+option.getName());
						// }
						// }
					}
			}

			System.out.println();
			System.out.println();
		}
	}
}
