package file_edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import object.ItemAttribute;
import utils.Utils;

public class Views {

	public static void changeViews(String path, ArrayList<ItemAttribute> params, String item, String[] categories,
			String title, String author, String about) {
		String source = path + "/copy/views/list.ejs";
		File srcFile = new File(source);
		replaceTable(srcFile, params, item, categories);

		source = path + "/copy/views/adminlist.ejs";
		srcFile = new File(source);
		replaceAdminTable(srcFile, params, item, categories);

		source = path + "/copy/views/homepage.ejs";
		srcFile = new File(source);
		replaceHome(srcFile, item, title);

		source = path + "/copy/views/about.ejs";
		srcFile = new File(source);
		replaceAbout(srcFile, about, title);

		source = path + "/copy/views/layout.ejs";
		srcFile = new File(source);
		replaceLayout(srcFile, item, title, author);

		source = path + "/copy/views/add.ejs";
		srcFile = new File(source);
		replaceAdd(srcFile, item, params);

		source = path + "/copy/views/edit.ejs";
		srcFile = new File(source);
		replaceEdit(srcFile, item, params);
		
		source = path + "/copy/views/details.ejs";
		srcFile = new File(source);
		
		ItemAttribute media = null;
		for (ItemAttribute param : params) {
			if (param.getType().equals("pdf") || param.getType().equals("image")) {
				media = param;
				break;
			}
		}
		
		if (media == null) {
			srcFile.delete();
		} else {
			replaceDetails(srcFile, params.get(0), media);
		}
	}
	
	public static void replaceTable(File fileToBeModified, ArrayList<ItemAttribute> params, String item,
			String[] categories) {
		String thead = "\n";
		String tbody = "";
		String header = "";

		// Column index necessary for certain functions for the page
		int fullRefColumn = 0;
		int commentIconColumn = 0;

		// Columns of the table that must be hidden
		Set<Integer> hidden = new HashSet<Integer>();

		int extra = 0;
		int i;

		for (i = 0; i < params.size(); i++) {
			// Type column set at index 1 with categories converted to numbers
			if (i == 1 && categories.length > 0) {
				thead = thead + "\t\t<th>Type</th>\n";
				tbody = tbody + "\t\t<td>\n\t\t<% if (item.type == \"" + categories[0] + "\") { %>\n\t\t0\n";
				int cat;
				for (cat = 1; cat < categories.length - 1; cat++) {
					tbody = tbody + "\t\t<% } else if (item.type == \"" + categories[cat] + "\") { %>\n\t\t" + cat
							+ "\n";
				}
				tbody = tbody + "\t\t<% } else { %>\n\t\t" + cat + "\n\t\t<% } %>\n\t\t</td>\n";

			} else {
				header = Utils.formatString(params.get(i).getName());
				//If the parameter has an URL, the content of the column will have the URL embedded
				if (params.get(i).getType().equals("url")) {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">" + header + "<br><input class=\"filter "
							+ params.get(i).getFilter() + "\" type=\"text\" data-column=\"" + (i + extra)
							+ "\" /></th>\n";
					tbody = tbody + "\t\t<% if(item." + params.get(i).getName() + "Url) {%>\n"
							+ "\t\t<td><a href=\"<%= item." + params.get(i).getName()
							+ "Url %>\" target=\"_blank\"><%= item." + params.get(i).getName() + "? item."
							+ params.get(i).getName() + ":\"\" %></a></td>\n" + "\t\t<% } else {%>\n"
							+ "\t\t<td><%= item." + params.get(i).getName() + "? item." + params.get(i).getName()
							+ ":\"\" %></td>\n" + "\t\t<% } %>\n";

				//Ignoring full reference and urlValues	
				} else if (params.get(i).getType().equals("fullReference")
						|| params.get(i).getType().equals("urlValue")) {
					extra--;
					continue;
					
				//Create the URL and Full reference columns alongside reference	
				} else if (params.get(i).getType().equals("reference")) {
					thead = thead
							+ "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\"First author and year of the article<br/>*Click on the text to display its complete information at the bottom<br/>(Scrolling the table will clear the reference information)<br/>*Click on the arrow icon to access to the pubmed URL\">Reference<br><input class=\"filter "
							+ params.get(i).getFilter() + "\" type=\"text\" data-column=\"" + (i + 1 + extra)
							+ "\" /></th>\n";
					tbody = tbody
							+ "\t\t<td class=\"ref_text\"><u><%= item.reference %></u><a href=\"<%= item.url %>\" target=\"_blank\"><img src=\"/images/ext_link.png\"></a></td>\n";
					thead = thead + "\t\t<th>Full Reference</th>\n";
					tbody = tbody + "\t\t<td><%= item.fullReference ? item.fullReference :\"\" %></td>\n";
					hidden.add(i + 1 + extra);
					fullRefColumn = i + 1 + extra;
					extra += 1;
				//Create the comment column and another one that will display the comment icon	
				} else if (params.get(i).getType().equals("comment")) {
					thead = thead + "\t\t<th>Note</th>\n";
					tbody = tbody + "\t\t<td><%= item.comment ? item.comment :\"\" %></td>\n";
					hidden.add(i + extra);

					thead = thead
							+ "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" title=\"Hover over the icon to display the comments\">Note<br/><input class=\"filter "
							+ params.get(i).getFilter() + " com_filter\" type=\"text\" data-column=\"" + (i + extra)
							+ "\" /></th>\n";
					tbody = tbody + "\t\t<td class=\"comment-cell\"><% if(item.comment) {%>\n"
							+ "\t\t\t<button class=\"btn btn-outline-secondary btn-sm oi oi-copywriting\" data-toggle=\"tooltip\" data-placement=\"left\" title='<%= item.comment %>'></button>\n"
							+ "\t\t\t<% } %>\n" + "\t\t</td>\n";
					extra += 1;
					commentIconColumn = i + extra;
					
				//Create the pdf or image column	
				} else if (params.get(i).getType().equals("pdf")) {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">Pdf</th>\n";
					tbody = tbody + "\t\t<td class=\"comment-cell\"><% if(item.pdf) {%>\n"
							+ "\t\t\t<a href=\"details/<%= item.id %>\"><button class=\"btn btn-outline-secondary oi oi-document\" target=\"_blank\"></button></a>\n"
							+ "\t\t\t<% } %>\n" + "\t\t</td>\n";

				} else if (params.get(i).getType().equals("image")) {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">image</th>\n";
					tbody = tbody + "\t\t<td class=\"comment-cell\"><% if(item.image) {%>\n"
							+ "\t\t\t<a href=\"details/<%= item.id %>\"><button class=\"btn btn-outline-secondary oi oi-document\" target=\"_blank\"></button></a>\n"
							+ "\t\t\t<% } %>\n" + "\t\t</td>\n";

				//Any other type of metadata	
				} else {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">" + header + "<br><input class=\"filter "
							+ params.get(i).getFilter() + "\" type=\"text\" data-column=\"" + (i + extra)
							+ "\" /></th>\n";
					tbody = tbody + "\t\t<td><%= item." + params.get(i).getName() + "? item." + params.get(i).getName()
							+ ":\"\" %></td>\n";

				}
			}
		}

		String newString = "\t<thead>\n\t<tr>\n" + thead
				+ "\t</tr>\n\t</thead>\n\t<tbody>\n\t<% items.forEach(function(item){ %>\n\t<tr>\n" + tbody
				+ "\t</tr>\n\t<% })%>\n\t</tbody>";

		Map<String, String> strings = new HashMap<String, String>();

		// Code block if the user want categories for the item
		if (categories.length > 0) {
			String navigation = "";
			String orderData = "";
			String groupLabel = "";
			hidden.add(1);
			
			//Name of the different bootstrap buttons. Respectively red, yellow, green, teal, blue, black and grey
			String navigationtype[] = { "danger", "warning", "success", "info", "primary", "dark", "secondary" };
			String printReplace = "string";
			for (int h = 0; h < categories.length; h++) {
				navigation += "\t\t\t<li class=\"nav-item\">\r\n" + "\t\t\t<a id=\"section" + h
						+ "btn\" class=\"btn btn-sm btn-outline-" + navigationtype[h] + " sectionbtn\"><b>"
						+ Utils.formatString(categories[h]) + "</b></a>\r\n" + "\t\t\t</li>\n";
				groupLabel += '\"' + String.valueOf(h + 1) + ". " + Utils.formatString(categories[h]) + "\",";

				printReplace = printReplace + ".replace(" + h + ",\'" + Utils.formatString(categories[h]) + "\')";
			}

			for (int k = 0; k < i + extra; k++) {
				orderData += "\t\t{ \"orderData\": [ 1," + k + "],    \"targets\": " + k + " },\n";
			}

			groupLabel = groupLabel.substring(0, groupLabel.length() - 1);
			strings.put("<!--@navigation@-->", navigation);
			strings.put("\\/\\*@orderData@\\*\\/", orderData);
			strings.put("@grouplabel@", groupLabel);
			strings.put("@printReplace@", printReplace);

			strings.put("\\/\\*@category@", "");
			strings.put("@category@\\*\\/", "");
		}
		// Hidden columns will register columns that should be hidden by datatables.
		// Reset columns register every column for the reset button
		String hiddenColumns = "";
		String resetColumns = "";
		if (!hidden.isEmpty()) {
			for (int counter = 0; counter < params.size() + extra; counter++) {
				if (hidden.contains(counter)) {
					hiddenColumns += Integer.toString(counter) + ",";
				}

				resetColumns += Integer.toString(counter) + ",";

			}

		} else {
			for (int counter = 0; counter < i + extra; counter++) {
				resetColumns += Integer.toString(counter) + ",";
			}
		}

		strings.put("@item@", item);
		strings.put("@fullRefColumn@", Integer.toString(fullRefColumn));
		strings.put("@commentIconColumn@", Integer.toString(commentIconColumn));
		strings.put("@hiddenColumns@", hiddenColumns);
		strings.put("@resetColumns@", resetColumns);
		strings.put("@nbcolumns@", String.valueOf(params.size() + extra));
		strings.put("@itemtable@", newString);

		Utils.replaceString(fileToBeModified, strings);

	}

	public static void replaceAdminTable(File fileToBeModified, ArrayList<ItemAttribute> params, String item,
			String categories[]) {
		// Same as regular table method but with one extra column for the admin
		String thead = "\n";
		String tbody = "";
		String header = "";
		int fullRefColumn = 0;
		int commentIconColumn = 0;

		Set<Integer> hidden = new HashSet<Integer>();

		int extra = 0;
		int i;

		for (i = 0; i < params.size(); i++) {
			if (i == 1 && categories.length > 0) {
				thead = thead + "\t\t<th>Type</th>\n";
				tbody = tbody + "\t\t<td>\n\t\t<% if (item.type == \"" + categories[0] + "\") { %>\n\t\t0\n";
				int cat;
				for (cat = 1; cat < categories.length - 1; cat++) {
					tbody = tbody + "\t\t<% } else if (item.type == \"" + categories[cat] + "\") { %>\n\t\t" + cat
							+ "\n";
				}
				tbody = tbody + "\t\t<% } else { %>\n\t\t" + cat + "\n\t\t<% } %>\n\t\t</td>\n";

			} else {
				header = params.get(i).getName().substring(0, 1).toUpperCase() + params.get(i).getName().substring(1);

				if (params.get(i).getType().equals("url")) {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">" + header + "<br><input class=\"filter "
							+ params.get(i).getFilter() + "\" type=\"text\" data-column=\"" + (i + extra)
							+ "\" /></th>\n";
					tbody = tbody + "\t\t<% if(item." + params.get(i).getName() + "Url) {%>\n"
							+ "\t\t<td><a href=\"<%= item." + params.get(i).getName()
							+ "Url %>\" target=\"_blank\"><%= item." + params.get(i).getName() + "? item."
							+ params.get(i).getName() + ":\"\" %></a></td>\n" + "\t\t<% } else {%>\n"
							+ "\t\t<td><%= item." + params.get(i).getName() + "? item." + params.get(i).getName()
							+ ":\"\" %></td>\n" + "\t\t<% } %>\n";

				} else if (params.get(i).getType().equals("fullReference")
						|| params.get(i).getType().equals("urlValue")) {
					extra--;
					continue;
				} else if (params.get(i).getType().equals("reference")) {
					thead = thead
							+ "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\"First author and year of the article<br/>*Click on the text to display its complete information at the bottom<br/>(Scrolling the table will clear the reference information)<br/>*Click on the arrow icon to access to the pubmed URL\">Reference<br><input class=\"filter "
							+ params.get(i).getFilter() + "\" type=\"text\" data-column=\"" + (i + 1 + extra)
							+ "\" /></th>\n";
					tbody = tbody
							+ "\t\t<td class=\"ref_text\"><u><%= item.reference %></u><a href=\"<%= item.url %>\" target=\"_blank\"><img src=\"/images/ext_link.png\"></a></td>\n";
					thead = thead + "\t\t<th>Full Reference</th>\n";
					tbody = tbody + "\t\t<td><%= item.fullReference ? item.fullReference :\"\" %></td>\n";
					hidden.add(i + 1 + extra);
					fullRefColumn = i + 1 + extra;
					extra += 1;
				} else if (params.get(i).getType().equals("comment")) {
					thead = thead + "\t\t<th>Note</th>\n";
					tbody = tbody + "\t\t<td><%= item.comment ? item.comment :\"\" %></td>\n";
					hidden.add(i + extra);

					thead = thead
							+ "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" title=\"Hover on the icon to display the comments\">Note<br/><input class=\"filter "
							+ params.get(i).getFilter() + " com_filter\" type=\"text\" data-column=\"" + (i + extra)
							+ "\" /></th>\n";
					tbody = tbody + "\t\t<td class=\"comment-cell\"><% if(item.comment) {%>\n"
							+ "\t\t\t<button class=\"btn btn-outline-secondary btn-sm oi oi-copywriting\" data-toggle=\"tooltip\" data-placement=\"left\" title='<%= item.comment %>'></button>\n"
							+ "\t\t\t<% } %>\n" + "\t\t</td>\n";
					extra += 1;
					commentIconColumn = i + extra;
				} else if (params.get(i).getType().equals("pdf")) {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">Pdf</th>\n";
					tbody = tbody + "\t\t<td class=\"comment-cell\"><% if(item.pdf) {%>\n"
							+ "\t\t\t<a href=\"details/<%= item.id %>\"><button class=\"btn btn-outline-secondary oi oi-document\"></button></a>\n"
							+ "\t\t\t<% } %>\n" + "\t\t</td>\n";

				} else if (params.get(i).getType().equals("image")) {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">image</th>\n";
					tbody = tbody + "\t\t<td class=\"comment-cell\"><% if(item.image) {%>\n"
							+ "\t\t\t<a href=\"details/<%= item.id %>\"><button class=\"btn btn-outline-secondary oi oi-document\"></button></a>\n"
							+ "\t\t\t<% } %>\n" + "\t\t</td>\n";

				} else {
					thead = thead + "\t\t<th data-toggle=\"tooltip\" data-placement=\"top\" data-html=\"true\" title=\""
							+ params.get(i).getTooltip() + "\">" + header + "<br><input class=\"filter "
							+ params.get(i).getFilter() + "\" type=\"text\" data-column=\"" + (i + extra)
							+ "\" /></th>\n";
					tbody = tbody + "\t\t<td><%= item." + params.get(i).getName() + "? item." + params.get(i).getName()
							+ ":\"\" %></td>\n";

				}
			}
		}

		// Extra column for the admin: actions
		thead = thead + "\t\t<th>Actions</th>\n";
		tbody = tbody
				+ "\t\t<td>\r\n\t\t\t<a href=\"edit/<%= item.id %>\" class=\"btn btn-sm btn-primary\"><em class=\"fa fa-pencil\"></em></a>\r\n\t\t\t<a href=\"/"
				+ item
				+ "/delete/<%= item.id %>\" class=\"btn btn-sm btn-danger\" onclick=\"return confirm('Are you sure you want to delete this "
				+ item + "?');\"><em class=\"fa fa-trash\"></em></a>\r\n\t\t</td>\n";

		String newString = "\t<thead>\n\t<tr>\n" + thead
				+ "\t</tr>\n\t</thead>\n\t<tbody>\n\t<% items.forEach(function(item){ %>\n\t<tr>\n" + tbody
				+ "\t</tr>\n\t<% })%>\n\t</tbody>";

		Map<String, String> strings = new HashMap<String, String>();

		if (categories.length > 0) {
			String navigation = "";
			String orderData = "";
			String groupLabel = "";
			hidden.add(1);
			String navigationtype[] = { "danger", "warning", "success", "info", "primary", "dark", "secondary" };
			String printReplace = "string";
			for (int h = 0; h < categories.length; h++) {
				navigation += "\t\t\t<li class=\"nav-item\">\r\n" + "\t\t\t<a id=\"section" + h
						+ "btn\" class=\"btn btn-sm btn-outline-" + navigationtype[h] + " sectionbtn\"><b>"
						+ Utils.formatString(categories[h]) + "</b></a>\r\n" + "\t\t\t</li>\n";
				groupLabel += '\"' + String.valueOf(h + 1) + ". " + Utils.formatString(categories[h]) + "\",";

				printReplace = printReplace + ".replace(" + h + ",\'" + Utils.formatString(categories[h]) + "\')";
			}

			for (int k = 0; k < i + extra; k++) {
				orderData += "\t\t{ \"orderData\": [ 1," + k + "],    \"targets\": " + k + " },\n";
			}

			groupLabel = groupLabel.substring(0, groupLabel.length() - 1);
			strings.put("<!--@navigation@-->", navigation);
			strings.put("\\/\\*@orderData@\\*\\/", orderData);
			strings.put("@grouplabel@", groupLabel);
			strings.put("@printReplace@", printReplace);

			strings.put("\\/\\*@category@", "");
			strings.put("@category@\\*\\/", "");
		}

		String hiddenColumns = "";
		String resetColumns = "";
		if (!hidden.isEmpty()) {
			for (int counter = 0; counter < params.size() + extra; counter++) {
				if (hidden.contains(counter)) {
					hiddenColumns += Integer.toString(counter) + ",";
				}
				resetColumns += Integer.toString(counter) + ",";
			}

		} else {
			for (int counter = 0; counter < i + extra; counter++) {
				resetColumns += Integer.toString(counter) + ",";
			}
		}

		strings.put("@item@", item);
		strings.put("@fullRefColumn@", Integer.toString(fullRefColumn));
		strings.put("@commentIconColumn@", Integer.toString(commentIconColumn));
		strings.put("@hiddenColumns@", hiddenColumns);
		strings.put("@resetColumns@", resetColumns);
		strings.put("@nbcolumns@", String.valueOf(params.size() + extra));
		strings.put("@itemtable@", newString);

		Utils.replaceString(fileToBeModified, strings);

	}

	public static void replaceLayout(File fileToBeModified, String item, String title, String author) {
		Map<String, String> strings = new HashMap<String, String>();
		String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		strings.put("@item@", item);
		strings.put("@sitetitle@", title);
		strings.put("@author@", author);
		strings.put("@year@", year);
		Utils.replaceString(fileToBeModified, strings);
	}

	public static void replaceAdd(File fileToBeModified, String item, ArrayList<ItemAttribute> params) {
		String form = "";
		String fileInput = "";

		for (ItemAttribute param : params) {
			if (param.getType().equals("pdf") || param.getType().equals("image")) {
				fileInput = fileInput + "\t<div class=\"form-row\">\n\t\t<div class=\"col-md-4\">\n\t\t\t<label><b>"
						+ param.getName() + "</b></label><br/>\n\t\t\t<input type=\"file\" name=\"file\"/>\n\t\t</div>\n\t</div><br/>\n";
			}else {
				form = form + "\t<div class=\"form-row\">\n\t\t<div class=\"col-md-4\">\n\t\t\t<label><b>" + param.getName()
				+ "</b></label>\n\t\t\t<input type=\"text\" name=\"" + param.getName()
				+ "\" class=\"form-control\">\n\t\t</div>\n\t</div>\n";
			}
		
		}
		form = form + fileInput;

		Map<String, String> strings = new HashMap<String, String>();
		strings.put("@item@", item);
		strings.put("@form@", form);

		Utils.replaceString(fileToBeModified, strings);
	}

	public static void replaceEdit(File fileToBeModified, String item, ArrayList<ItemAttribute> params) {
		String form = "";
		String fileInput = "";

		for (ItemAttribute param : params) {
			if (param.getType().equals("pdf") || param.getType().equals("image")) {
				fileInput = fileInput +  "\t<div class=\"form-row\">\n\t\t<div class=\"col-md-4\">\n\t\t\t<label><b>"
						+ param.getName() + "</b></label><br/>\n\t\t\t<input type=\"hidden\" name=\"" + param.getType()
						+ "\" value=\"<%= item." + param.getType() + " ? item." + param.getType()
						+ ":''%>\"</>\n\t\t\t<input type=\"file\" name=\"file\"/>\n\t\t</div>\n\t</div><br/>\n";
			} else {
				form = form + "\t<div class=\"form-row\">\n\t\t<div class=\"col-md-4\">\n\t\t\t<label><b>"
						+ param.getName() + "</b></label>\n\t\t\t<input type=\"text\" name=\"" + param.getName()
						+ "\" class=\"form-control\" value=\"<%= item." + param.getName() + "? item." + param.getName()
						+ ":\"\" %>\">\n\t\t</div>\n\t</div>\n";
			}
		}
		
		form = form + fileInput;

		Map<String, String> strings = new HashMap<String, String>();
		strings.put("@item@", item);
		strings.put("@form@", form);

		Utils.replaceString(fileToBeModified, strings);
	}

	public static void replaceHome(File fileToBeModified, String item, String title) {
		Map<String, String> strings = new HashMap<String, String>();
		strings.put("@item@", item);
		strings.put("@sitetitle@", title);
		Utils.replaceString(fileToBeModified, strings);

	}

	public static void replaceAbout(File srcFile, String about, String title) {
		Map<String, String> strings = new HashMap<String, String>();
		if (about.equals("") || about.equals(null)) {
			strings.put("@about@", "<i>Under construction</i>");
		} else {
			strings.put("@about@", about);
		}
		strings.put("@sitetitle@", title);
		Utils.replaceString(srcFile, strings);
	}


	public static void replaceDetails(File srcFile, ItemAttribute item, ItemAttribute media) {
		String newString = "";
		
		if (media.getType().equals("pdf")) {
			newString = "<a id='blob' href=\"#\" onclick=\"dataURI_DL('<%= item.pdf %>','<%= item."+ item.getName() +" %>.pdf');\"><button class=\"btn btn-info\">Download PDF file</button></a>";
		}else {
			newString = "<img id=\"detail_image\" src=\"<%= item.image %>\"/>";
		}
		
		Map<String, String> strings = new HashMap<String, String>();
		strings.put("@data@", newString);
		strings.put("@itemname@", "<%= item." + item.getName() + " %>");
		
		Utils.replaceString(srcFile, strings);
		
	}

}
