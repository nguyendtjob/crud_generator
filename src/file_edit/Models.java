package file_edit;

import java.io.File;
import java.util.ArrayList;

import object.ItemAttribute;

public class Models {
	public static void changeFile(String path, String item, ArrayList<ItemAttribute> params) {
		String source = path + "/copy/api/models/@item@.js";
		File srcFile = new File(source);
		Replace.replaceAttributes(srcFile, params);

		String destination = path + "/copy/api/models/" + item + ".js";
		Replace.renameFileorDirectory(srcFile, destination);
	}
}
