
import java.nio.file.Path;
import java.util.List;

/**
 * Multi-Threaded version of InvertedIndex. Extends InvertedIndex
 */
public class ConcurrentInvertedIndex extends InvertedIndex {
	
	
	/**
	 * ReadWrite variable to use readWrite lock
	 */
	private final ReadWriteLock readWriteLock;
	
	/**
	 * Includes parent, InvertedIndex. Creates new ReadWriteLock
	 */
	public ConcurrentInvertedIndex() {
		super();
		this.readWriteLock = new ReadWriteLock();
	}
	
	/**
	 * Multi-Threaded version of add to InvertedIndex. 
	 * Adds a word and the path and position that word appeared.
	 * 
	 * @param word
	 * 		word to be added to InvertedIndex
	 * @param path
	 * 		file path of where word is found
	 * @param position
	 * 		position of where word is found in file
	 * @return status
	 * 		returns if information was added properly
	 */
	@Override
	public boolean add(String word, String path, int position) {
		try {
			readWriteLock.lockReadWrite();
			return super.add(word, path, position);
		} finally {
			readWriteLock.unlockReadWrite();
		}
	}
	
	/**
	 * Multithreaded version of toJSON, to send InvertedIndex to be written out
	 * 
	 * @param output
	 * 		path to be written to
	 */
	@Override
	public void toJSON(Path output) {
		try {
			readWriteLock.lockReadOnly();
			super.toJSON(output);
		}
		finally {
			readWriteLock.unlockReadOnly();
		}
	}
	/**
	 * Multithreaded version of partialSearch. Receives query line. Goes through each query word 
	 * in line and gets path, positions and frequency from index. Adds path, positions and frequency 
	 * to list. Returns list to GetQueryFiles
	 * 
	 * @param queries
	 * 		line of query to be searched
	 * @return List<SearchResults>
	 * 		returns list of SearchResults from query to getQueryFiles
	 */
	@Override
	public List<SearchResult> partialSearch(String[] queries) {
		try {
			readWriteLock.lockReadOnly();	
			return super.partialSearch(queries);
		} finally {
			readWriteLock.unlockReadOnly();
		}
	}
	

	/**
	 * Add a local InvertedIndex to main InvertedIndex to reduce time. Main InvertedIndex is locked
	 * each time we add a local InvertedIndex.
	 * 
	 * @param local
	 * 		local InvertedIndex used to add to main InvertedIndex.
	 */	
	@Override
	public void addAll(InvertedIndex local) {
		try {
			readWriteLock.lockReadWrite();
			super.addAll(local);
		} finally {
			readWriteLock.unlockReadWrite();
		}
		
	}
}
