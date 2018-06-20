package file_edit;

import java.io.File;
import java.util.ArrayList;

import object.ItemAttribute;
import utils.Utils;

public class Models {
	public static void changeFile(String path, String item, ArrayList<ItemAttribute> params) {
		String source = path + "/copy/api/models/@item@.js";
		File srcFile = new File(source);
		replaceAttributes(srcFile, params);

		String destination = path + "/copy/api/models/" + item + ".js";
		Utils.renameFileorDirectory(srcFile, destination);
	}
	
	public static void replaceAttributes(File fileToBeModified, ArrayList<ItemAttribute> params) {
		String attributeString = "";

		String type = "";

		for (int i = 0; i < params.size(); i++) {
			type = params.get(i).getType();
			if (type.equals("reference") || type.equals("comment") || type.equals("url") || type.equals("urlValue")
					|| type.equals("pdf") || type.equals("image") || type.equals("fullReference")) {
				type = "string";
			}
			attributeString = attributeString + "\n\t" + params.get(i).getName() + " : { type: '" + type + "'},\n";
		}

		Utils.replaceString(fileToBeModified, "@itemparamssetup@", attributeString);

	}
}
