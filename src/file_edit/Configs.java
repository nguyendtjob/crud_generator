package file_edit;

import java.io.File;

public class Configs {
	public static void changeConfigs(String path, String database, String item) {
		String source = path + "/copy/config/connections.js";
		File srcFile = new File(source);

		Replace.replaceString(srcFile, "@database@", database);

		source = path + "/copy/config/policies.js";
		srcFile = new File(source);

		Replace.replaceString(srcFile, "@item@", item);
	}

}
