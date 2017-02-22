import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** 
 * Takes in each file and parses into words, word position, and file. 
 */
public class InvertedIndexFileBuilder {

	/** 
	 * Regular expression for removing special characters. 
	 */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/** 
	 * Regular expression for splitting text into words by whitespace.
	 */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	/**
	 * ArrayList of Paths (text files) and DataStructure are passed. 
	 * Goes through each text file, passes file to parseFile
	 * 
	 * @param files
	 * 		files of words to be added to InvertedIndex
	 * @param InvertedIndex
	 * 		Data Structure to add words, paths and locations words appear in file
	 * @throws IOException
	 */
	public static void getFiles(ArrayList<Path> files, InvertedIndex index) throws IOException {
		for(Path file : files) {
			parseFile(file, index);
		}
	}

	/**
	 * Receives path and InvertedIndex, creates BufferedWriter, goes through each line and 
	 * each word in line, cleans word and adds word, file and count of words to be added to InvertedIndex
	 * @param file
	 * 		file of words to be added to InvertedIndex
	 * @param index
	 * 		Data Structure to add words, paths and locations words appear in file
	 * @throws IOException
	 */
	public static void parseFile(Path file, InvertedIndex index) throws IOException {

		try (BufferedReader in = Files.newBufferedReader(file, Charset.forName("UTF8"))) {

			String line;
			int counter = 0;
			
			while((line = in.readLine()) != null) {

				String[] words = line.split(SPLIT_REGEX);

				for(String newWords : words) {
					String cleaned = clean(newWords);

					if(!cleaned.isEmpty()) {
						counter++;
						index.add(cleaned, file.toString(), counter);
					}
				}
			}
		} 
	}

	/** 
	 * Word is passed, removes special characters, word is cleaned and returned 
	 * @param text
	 * 		word is passed to be cleaned
	 * @return newText
	 * 		cleaned word
	 */
	public static String clean(String text) {
		String newText = text.toLowerCase();
		newText = newText.replaceAll(CLEAN_REGEX,"");
		newText = newText.trim();
		return newText;
	}

	/** 
	 * Line is passed, cleans each word and takes out any whitespace, returns array of words
	 * @param text
	 * 		Regex for splitting words by whitespace
	 * @return newText
	 * 		array of cleaned words
	 */
	public static String[] split(String text) {
		String newText = clean(text);

		if(!newText.isEmpty()) {
			return newText.split(SPLIT_REGEX);
		}
		else {
			return new String[] {};
		}
	}

} // end class
