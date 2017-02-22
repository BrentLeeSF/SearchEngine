import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Takes in query, passes query to InvertedIndex.partialSearch. Receives list of SearchResults.
 * Creates LinkedHashMap data structure to adds each search result. 
 * Passes data structure and BufferedWriter to JSONSearchWriter to write all query results
 * @author brentrucker
 *
 */
public class QueryFileParser extends AbstractQueryFileParser {

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();
	
	/**
	 * Maps a Query to SearchResults 
	 */
	private final LinkedHashMap<String, List<SearchResult>> searchResultMap;
	
	/**
	 * Data structure containing word, path, and count of words
	 */
	private final InvertedIndex index;
	

	public QueryFileParser(InvertedIndex index) { 
		this.index = index;
		this.searchResultMap = new LinkedHashMap<String, List<SearchResult>>();
	}

	/**
	 * Helper Method for splitting query line into words, passing query words to partialSearch,
	 * which returns a list of searchResults, which is added to searchResultMap to later be printed out
	 * @param line
	 * 		query line
	 */
	@Override
	public void parseLine(String line) {
		String[] words = InvertedIndexFileBuilder.split(line);
		List<SearchResult> returnedList = index.partialSearch(words);	
		searchResultMap.put(line, returnedList);
	}

	/**
	 * After every Search Result is found and added to LinkedHashMap, BufferedWriter is created.
	 * LinkedHashMap and BufferedWriter is passed to JSONSearchWriter to be written out
	 * @param querieOutput
	 * 		receives path to be written to
	 * @throws IOException
	 */
	@Override
	public void toJSONQuery(Path querieOutput) {
		try(BufferedWriter bufferedWriter = Files.newBufferedWriter(querieOutput,  StandardCharsets.UTF_8)) {
			JSONSearchWriter.writeOuterQuery(searchResultMap, bufferedWriter);
		} catch (IOException e) {
			//logger.warn("Could not find file " + querieOutput.toString());
		}
	}
}					
