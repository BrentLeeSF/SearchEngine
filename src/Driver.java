import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * This software driver class provides a consistent entry point for the search
 * engine. Based on the arguments provided to {@link #main(String[])}, it
 * creates the necessary objects and calls the necessary methods to build an
 * inverted index, process search queries, configure multithreading, and launch
 * a web server (if appropriate).
 */
public class Driver {

	/**
	 * Flag used to indicate the following value is an INPUT DIRECTORY of text
	 * files to use when building the inverted index.
	 * 
	 * @see "Projects 1 to 5"
	 */
	public static final String INPUT_FLAG = "-input";

	//private static final Logger logger = LogManager.getLogger();

	/**
	 * Flag used to indicate the following value is the PATH to use when
	 * outputting the inverted index to a JSON file. If no value is provided,
	 * then {@link #INDEX_DEFAULT} should be used. If this flag is not provided,
	 * then the inverted index should not be output to a file.
	 * 
	 * @see "Projects 1 to 5"
	 */
	public static final String INDEX_FLAG = "-index";

	/**
	 * Flag used to indicate the following value is a text file of search
	 * queries.
	 * 
	 * @see "Projects 2 to 5"
	 */
	public static final String QUERIES_FLAG = "-query";

	/**
	 * Flag used to indicate the following value is the path to use when
	 * outputting the search results to a JSON file. If no value is provided,
	 * then {@link #RESULTS_DEFAULT} should be used. If this flag is not
	 * provided, then the search results should not be output to a file.
	 * 
	 * @see "Projects 2 to 5"
	 */
	public static final String RESULTS_FLAG = "-results";

	/**
	 * Flag used to indicate the following value is the number of threads to use
	 * when configuring multithreading. If no value is provided, then
	 * {@link #THREAD_DEFAULT} should be used. If this flag is not provided,
	 * then multithreading should NOT be used.
	 * 
	 * @see "Projects 3 to 5"
	 */
	public static final String THREAD_FLAG = "-threads";

	/**
	 * Flag used to indicate the following value is the seed URL to use when
	 * building the inverted index.
	 * 
	 * @see "Projects 4 to 5"
	 */
	public static final String SEED_FLAG = "-seed";

	/**
	 * Flag used to indicate the following value is the port number to use when
	 * starting a web server. If no value is provided, then
	 * {@link #PORT_DEFAULT} should be used. If this flag is not provided, then
	 * a web server should not be started.
	 */
	public static final String PORT_FLAG = "-port";

	/**
	 * Default to use when the value for the {@link #INDEX_FLAG} is missing.
	 */
	public static final String INDEX_DEFAULT = "index.json";

	/**
	 * Default to use when the value for the {@link #RESULTS_FLAG} is missing.
	 */
	public static final String RESULTS_DEFAULT = "results.json";

	/**
	 * Default to use when the value for the {@link #THREAD_FLAG} is missing.
	 */
	public static final int THREAD_DEFAULT = 5;

	/**
	 * Default to use when the value for the {@link #PORT_FLAG} is missing.
	 */
	public static final int PORT_DEFAULT = 8080;

	/**
	 * Parses the provided arguments and, if appropriate, will build an inverted
	 * index from a directory or seed URL, process search queries, configure
	 * multi-threading, and launch a web server.
	 * 
	 * @param args
	 *            set of flag and value pairs
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NullPointerException
	 */
	public static void main(String[] args) {

		CommandLineParser ca = new CommandLineParser(args);
		int threads = 0;

		try {
			
			String threadArgument = ca.getOrDefault(THREAD_FLAG, String.valueOf(THREAD_DEFAULT));
			
			if(threadArgument == null) {
				// if has seedFlag, set to threadDefault, else to 1
				threads = ca.hasFlag(SEED_FLAG) ? THREAD_DEFAULT : 1;
			}
			else {
				threads = Integer.parseInt(threadArgument);
				if(threads <= 0) {
					threads = THREAD_DEFAULT;
				}
			}


			if(threads > 1) {

				ConcurrentInvertedIndex concurrentIndex = new ConcurrentInvertedIndex();

				// if value of flag key, is not provided, do not read
				String inputValue = ca.getOrDefault(INPUT_FLAG, null);
				
				if(inputValue != null) {
					ArrayList<Path> returnedFile = DirectoryTraverser.traverseDirectory(Paths.get(inputValue));
					ConcurrentInvertedIndexFileBuilder concurentFileBuilder = new ConcurrentInvertedIndexFileBuilder(concurrentIndex, threads);
					concurentFileBuilder.getFiles(returnedFile);
					concurentFileBuilder.shutdown();
				}

				String seedValue = ca.getOrDefault(SEED_FLAG, null);
				
				if(seedValue != null) {
					WebCrawler webCrawler = new WebCrawler(concurrentIndex, seedValue, threads);
					webCrawler.parseSeedPage();
					webCrawler.shutdown();
				}

				String indexValue = ca.getOrDefault(INDEX_FLAG, INDEX_DEFAULT);
				
				if(indexValue != null) {
					concurrentIndex.toJSON(Paths.get(indexValue));
				}

				ConcurrentQueryFileParser concurrentQuery = new ConcurrentQueryFileParser(concurrentIndex, threads);

				// if flag not provided, then do not read
				String querieValue = ca.getOrDefault(QUERIES_FLAG, null);
				
				if(querieValue != null) {
					Path queriePath = Paths.get(querieValue);
					concurrentQuery.getQueryFiles(queriePath);
				}

				concurrentQuery.shutdown();
				
				String resultValue = ca.getOrDefault(RESULTS_FLAG, RESULTS_DEFAULT);
				
				if(resultValue != null) {
					concurrentQuery.toJSONQuery(Paths.get(resultValue));
				}
			}


			else {
				
				InvertedIndex index = new InvertedIndex();

				String inputValue = ca.getOrDefault(INPUT_FLAG, null);
				
				if(inputValue != null) {
					ArrayList<Path> returnedFile = DirectoryTraverser.traverseDirectory(Paths.get(inputValue));
					InvertedIndexFileBuilder.getFiles(returnedFile, index);
				}

				String indexPath = ca.getOrDefault(INDEX_FLAG, INDEX_DEFAULT);
				
				if (indexPath != null) {
					
					index.toJSON(Paths.get(indexPath));
				}
				
				QueryFileParser query = new QueryFileParser(index);

				String querieValue = ca.getOrDefault(QUERIES_FLAG, null);
				
				if(querieValue != null) {
					Path queriePath = Paths.get(querieValue);
					query.getQueryFiles(queriePath);
				}

				String resultsPath = ca.getOrDefault(RESULTS_FLAG, RESULTS_DEFAULT);
				
				if(resultsPath != null) {
					
					query.toJSONQuery(Paths.get(resultsPath));
				}
			}

		} catch(NumberFormatException nfe) {
			//logger.warn("Invalid value for threads");
		}
		catch (NullPointerException n) {
			System.err.println("No Input Flag Found"); }
		catch (FileNotFoundException e) {
			System.err.println("Missing File: " +Paths.get(INDEX_DEFAULT));
		} catch (IOException e) {
			System.err.println("Could not find file " + ca.getValue(INDEX_FLAG));
		}
	}
}
