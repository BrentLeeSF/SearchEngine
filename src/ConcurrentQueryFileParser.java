import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * MultiThreaded version of QueryFileParsing. 
 * Takes in query, passes query to InvertedIndex.partialSearch. Receives list of SearchResults.
 * Creates LinkedHashMap data structure to adds each search result with query. 
 * Passes data structure and BufferedWriter to JSONSearchWriter to write all query results
 */
public class ConcurrentQueryFileParser extends AbstractQueryFileParser {

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();

	/**
	 * Declares work queue with the specified number of threads.
	 */
	private final WorkQueue minions;

	/**
	 * ReadWriteLock variable to use for readWrite lock
	 */
	private final ReadWriteLock readWriteLock;

	/**
	 * Maps a Query to SearchResults 
	 */
	private final LinkedHashMap<String, List<SearchResult>> searchResultMap;

	/**
	 * MultiThreaded InvertedIndex to search
	 */
	private final ConcurrentInvertedIndex concurrentIndex;

	/**
	 * Calls parent QueryFileParsing with InvertedIndex. Creates new WorkQueue with specified number of threads.
	 * Implements pending work, ReadWriteLock, and number of threads.
	 * 
	 * @param index
	 * 		Data structure of word, path, and position of word 
	 * @param threads
	 * 		number of threads to use to complete work
	 */
	public ConcurrentQueryFileParser(ConcurrentInvertedIndex concurrentIndex, int threads) {
		this.minions = new WorkQueue(threads);
		this.readWriteLock = new ReadWriteLock();
		this.concurrentIndex = concurrentIndex;
		this.searchResultMap = new LinkedHashMap<>();
	}

	/**
	 * Concurrently searches from an InvertedIndex with each thread handling one query
	 */
	@Override
	public void parseLine(String line) {

		if(!line.isEmpty()) {
			try {
				readWriteLock.lockReadWrite();
				// we add null because we want to print out search results in the correct order
				searchResultMap.put(line, null);
			}
			finally {
				readWriteLock.unlockReadWrite();
			}
			minions.execute(new QueryFileMinion(line));
		}
	}

	/**
	 * After every Search Result is found and added to LinkedHashMap, BufferedWriter is created.
	 * LinkedHashMap and BufferedWriter is passed to JSONSearchWriter to be written out. Is thread safe
	 * @param querieOutput
	 * 		receives path to be written to
	 * @throws IOException
	 */
	@Override
	public void toJSONQuery(Path querieOutput) {

		try(BufferedWriter bufferedWriter = Files.newBufferedWriter(querieOutput,  StandardCharsets.UTF_8)) {
			readWriteLock.lockReadOnly();
			JSONSearchWriter.writeOuterQuery(searchResultMap, bufferedWriter);
		} catch (IOException e) {
			//logger.warn("Could not find file " + querieOutput.toString());
		}	
		finally {
			readWriteLock.unlockReadOnly();
		}
	}

	
	/**
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	// TODO Removed the synchronized keyword from method
	public void shutdown() { 
		//logger.debug("Shutting down");
		minions.finish();
		minions.shutdown();
	}


	/**
	 * Receives query line, cleans and splits query into words. 
	 * Calls PartialSearch of InvertedIndex to get a list of SearchResults for each query line.
	 * Locks it, and puts query and SearchResults into map to be written out.
	 */
	private class QueryFileMinion implements Runnable {

		/**
		 * Query to search for in InvertedIndex
		 */
		private final String query;

		public QueryFileMinion(String query) {
			this.query = query;
		}

		/**
		 * Receives query line, cleans and splits query into words. 
		 * Calls PartialSearch of InvertedIndex to get a list of SearchResults for each query line.
		 * Locks it, and puts query and SearchResults into map to be written out.
		 */
		@Override
		public void run() {

			String[] words = InvertedIndexFileBuilder.split(query);

			List<SearchResult> returnedList = concurrentIndex.partialSearch(words);	

			try {
				readWriteLock.lockReadWrite();
				searchResultMap.put(query, returnedList);
			}
			finally {
				readWriteLock.unlockReadWrite();
			}
		}
	}
}
