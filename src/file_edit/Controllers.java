package file_edit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import object.ItemAttribute;
import utils.Utils;

public class Controllers {
	public static void changeFile(String path, String item, ArrayList<ItemAttribute> params) {
		String source = path + "/copy/api/controllers/@item@Controller.js";
		File srcFile = new File(source);

		replaceParams(srcFile, params, item);

		String destination = path + "/copy/api/controllers/" + item + "Controller.js";
		Utils.renameFileorDirectory(srcFile, destination);

		source = path + "/copy/api/controllers/UserController.js";
		srcFile = new File(source);

		Utils.replaceString(srcFile, "@item@", item);
	}
	
	public static void replaceParams(File fileToBeModified, ArrayList<ItemAttribute> params, String item) {

		String paramString = "\n";
		String paramsQueryString = "\n";
		String conversion = "";

		for (int i = 0; i < params.size(); i++) {
			paramString = paramString + "\t\t\tvar " + params.get(i).getName() + " = req.body."
					+ params.get(i).getName() + ";\n";
			paramsQueryString = paramsQueryString + "\t\t\t" + params.get(i).getName() + ": " + params.get(i).getName()
					+ ",\n";
			if (params.get(i).getType().equals("pdf")) {
				conversion = params.get(i).getName()
						+ " = \"data:application/pdf;base64,\"+ bitmap.toString('base64');";
			} else if (params.get(i).getType().equals("image")) {
				conversion = params.get(i).getName() + " = \"data:image/jpeg;base64,\"+ bitmap.toString('base64');";
			}
		}

		Map<String, String> strings = new HashMap<String, String>();
		strings.put("@item@", item);
		strings.put("@itemparams@", paramString);
		strings.put("@itemparamsquery@", paramsQueryString);
		strings.put("@conversion@", conversion);

		Utils.replaceString(fileToBeModified, strings);
	}

}
