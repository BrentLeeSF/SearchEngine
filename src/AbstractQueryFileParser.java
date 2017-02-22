
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Abstract class that generates searchResults based on a given query file and InvertedIndex, and searchResults can be printed out.
 * 
 */
public abstract class AbstractQueryFileParser {

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();
	

	/**
	 * Receives query. Creates BufferedReader and goes through each line and passes line to parseLine
	 * @param files
	 * 		file of queries
	 */
	public void getQueryFiles(Path files) { 
		try (BufferedReader in = Files.newBufferedReader(files, Charset.forName("UTF8"))) {
			String line = null;
			while((line = in.readLine()) != null) {
				parseLine(line);
			}
		} catch (IOException e) {
			//logger.warn("File cannot be found");
		}
	}
	

	/**
	 * A method that searches the InvertedIndex based on a given query and adds to the SearchResult map
	 * @param line
	 * 		query line to use for searching
	 */
	public abstract void parseLine(String line);
	
	/**
	 * Prints out the SearchResult map to a given path
	 * @param querieOutput
	 * 		output path for JSON file
	 */
	public abstract void toJSONQuery(Path querieOutput);
	
}