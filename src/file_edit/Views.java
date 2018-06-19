package file_edit;

import java.io.File;
import java.util.ArrayList;

import object.ItemAttribute;

public class Views {

	public static void changeViews(String path, ArrayList<ItemAttribute> params, String item, String[] categories,
			String title, String author, String about) {
		String source = path + "/copy/views/list.ejs";
		File srcFile = new File(source);
		Replace.replaceTable(srcFile, params, item, categories);

		source = path + "/copy/views/adminlist.ejs";
		srcFile = new File(source);
		Replace.replaceAdminTable(srcFile, params, item, categories);

		source = path + "/copy/views/homepage.ejs";
		srcFile = new File(source);
		Replace.replaceHome(srcFile, item, title);

		source = path + "/copy/views/about.ejs";
		srcFile = new File(source);
		Replace.replaceAbout(srcFile, about, title);

		source = path + "/copy/views/layout.ejs";
		srcFile = new File(source);
		Replace.replaceLayout(srcFile, item, title, author);

		source = path + "/copy/views/add.ejs";
		srcFile = new File(source);
		Replace.replaceAdd(srcFile, item, params);

		source = path + "/copy/views/edit.ejs";
		srcFile = new File(source);
		Replace.replaceEdit(srcFile, item, params);
		
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
			Replace.replaceDetails(srcFile, params.get(0), media);
		}
	}

}
