package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Utils {
	public static String formatString(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
	}
	
	public static void renameFileorDirectory(File file, String name) {
		File newName = new File(name);
		file.renameTo(newName);
	}

	public static void replaceString(File fileToBeModified, String oldString, String newString) {
		String oldContent = "";
		BufferedReader reader = null;
		FileWriter writer = null;

		try {
			reader = new BufferedReader(new FileReader(fileToBeModified));

			// Reading all the lines of input text file into oldContent
			String line = reader.readLine();

			while (line != null) {
				oldContent = oldContent + line + System.lineSeparator();
				line = reader.readLine();
			}

			// Replacing oldString with newString in the oldContent
			String newContent = oldContent.replaceAll(oldString, newString);

			// Rewriting the input text file with newContent
			writer = new FileWriter(fileToBeModified);
			writer.write(newContent);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Closing the resources
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void replaceString(File fileToBeModified, Map<String, String> strings) {
		String content = "";
		BufferedReader reader = null;
		FileWriter writer = null;

		try {
			reader = new BufferedReader(new FileReader(fileToBeModified));

			// Reading all the lines of input text file into oldContent
			String line = reader.readLine();

			while (line != null) {
				content = content + line + System.lineSeparator();
				line = reader.readLine();
			}

			// Replacing all oldstring by their new value
			for (String oldString : strings.keySet()) {
				content = content.replaceAll(oldString, strings.get(oldString));
			}

			// Rewriting the input text file with newContent
			writer = new FileWriter(fileToBeModified);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Closing the resources
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
