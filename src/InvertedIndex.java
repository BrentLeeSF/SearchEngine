import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * InvertedIndex creates data structure for word, path and positions to be searched through.
 * Adds word, path, positions to data structure. Sends data structure with BufferedWriter
 * to be send to be written out with JSON. Goes through each query line, gets word, path, 
 * frequency, and initial position of those words, adds to a list of SearchResults to be 
 * returned to QueryFileParsing.
 */
public class InvertedIndex {

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();
	
	/** 
	 * Maps a word to a path and the locations within that path the word appears.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * Adds a word and the path and position that word appeared.
	 * 
	 * @param word
	 * 		word to add to dataStructore
	 * @param path
	 * 		path that the word appeared in
	 * @param position
	 * 		position in the path that the word appeared in
	 * 
	 * @return true if the word, path, and position were added to the index
	 */ 
	public boolean add(String word, String path, int position) {

		boolean status = false;

		if (!index.containsKey(word)) { 
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
		}

		if (!index.get(word).containsKey(path)) {
			index.get(word).put(path, new TreeSet<Integer>());
		}

		status = index.get(word).get(path).add(position); 
		
		return status;
	}

	/** 
	 * Creates a new BufferedWriter (bw) and passes the bw with the DataStructure to be written out 
	 * to the path provided
	 * @param output
	 * 		path to be written to is passed
	 * @throws IOException
	 */
	public void toJSON(Path output) {

		try(BufferedWriter bufferedWriter = Files.newBufferedWriter(output,  StandardCharsets.UTF_8)) {
			JSONWriter.writeOuter(this.index, bufferedWriter);
		} catch (IOException e) {
			//logger.warn(e.toString());
		}
	}


	/**
	 * Receives query line. Goes through each query word in line and get path, positions and frequency from index.
	 * Adds path, positions and frequency to list. Returns list to GetQueryFiles
	 * 
	 * @param queries
	 * 		each query line in an array
	 * @return List<SearchResults>
	 * 		returns list of SearchResults from query to getQueryFiles
	 */
	public List<SearchResult> partialSearch(String[] queries) {

		List<SearchResult> searchList = new ArrayList<SearchResult>();

		HashMap<String, SearchResult> searchMap = new HashMap<>();

		for(String queryWord: queries) {

			for(String word: index.tailMap(queryWord).keySet()) { // word
				if(!word.startsWith(queryWord)) {
					break;
				}

				TreeMap<String, TreeSet<Integer>> pathKeySet = index.get(word); 
				
				for(String path : pathKeySet.keySet()) {
					TreeSet<Integer> positionSet = pathKeySet.get(path); 					

					if (!searchMap.containsKey(path)) {
						SearchResult sr = new SearchResult(path, positionSet.size(), positionSet.first());
						searchMap.put(path, sr);
						searchList.add(sr);
					}
					else {
						SearchResult sr = searchMap.get(path);					
						sr.update(positionSet.size(), positionSet.first());
					}
				}
			}
		} // end for loop

		Collections.sort(searchList);
		return searchList;
	}

	/**
	 * Add a local InvertedIndex to main InvertedIndex to reduce time. 
	 * @param local
	 * 		local InvertedIndex used to add to main InvertedIndex.
	 */		
	public void addAll(InvertedIndex local) {
		
		for (String word : local.index.keySet()) {
			if (!this.index.containsKey(word)) {
				this.index.put(word, local.index.get(word));
			} else {
				for (String path : local.index.get(word).keySet()) {
					if (!this.index.get(word).containsKey(path)) {
						this.index.get(word).put(path, local.index.get(word).get(path));
					} else {
						this.index.get(word).get(path).addAll(local.index.get(word).get(path));
					}
				}
			}
		}
	}
	
}
