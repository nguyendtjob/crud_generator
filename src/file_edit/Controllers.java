package file_edit;

import java.io.File;
import java.util.ArrayList;

import object.ItemAttribute;

public class Controllers {
	public static void changeFile(String path, String item, ArrayList<ItemAttribute> params) {
		String source = path + "/copy/api/controllers/@item@Controller.js";
		File srcFile = new File(source);

		Replace.replaceParams(srcFile, params, item);

		String destination = path + "/copy/api/controllers/" + item + "Controller.js";
		Replace.renameFileorDirectory(srcFile, destination);

		source = path + "/copy/api/controllers/UserController.js";
		srcFile = new File(source);

		Replace.replaceString(srcFile, "@item@", item);
	}

}
