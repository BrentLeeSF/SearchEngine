
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

/**
 * Uses a workQueue to build an InvertedIndex from a list of files
 */
public class ConcurrentInvertedIndexFileBuilder {
	

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();
	
	/**
	 * InvertedIndex data structure of word, path and position
	 */
	private final ConcurrentInvertedIndex concurrentIndex; 
	
	/**
	 * Declares work queue with the specified number of threads.
	 */
	private final WorkQueue minions;
	
	
	public ConcurrentInvertedIndexFileBuilder(ConcurrentInvertedIndex concurrentIndex, int threads) {
		this.minions = new WorkQueue(threads);
		this.concurrentIndex = concurrentIndex;
	}
	

	/**
	 * ArrayList of Paths (text files) are passed. Goes through each text file, and 
	 * executes WorkQueue with new FileReaderMinion to create new worker for every file found
	 * 
	 * @param files
	 * 		files of words to be added to InvertedIndex
	 * @param ids
	 * 		data structure (InvertedIndex) containing word, path, and count of words
	 * @throws IOException
	 */
	public void getFiles(ArrayList<Path> files) {	
 		for(Path file : files) {
			minions.execute(new FileReaderMinion(file));
		}
	}
	
	
	/**
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	public void shutdown() {
		//logger.debug("Shutting down");
		minions.finish();
		minions.shutdown();
	}
	
	/**
	 * Builds localInvertedIndex from a given path and adds all of local InvertedIndex to the main InvertedIndex
	 */
	private class FileReaderMinion implements Runnable {
		
		/**
		 * Paths of text files to go through split into words, paths, and word count, and add to InvertedIndex
		 */
		private final Path path;
		
		public FileReaderMinion(Path path) {
			this.path = path;
		}

		/**
		 * Parses file to build local InvertedIndex from a given path and adds all of local InvertedIndex to the main InvertedIndex.
		 * 
		 */
		@Override
		public void run() {
			
			InvertedIndex localIndex = new InvertedIndex();
			
			try {
				// add to localInvInd in the InvIndClass
				InvertedIndexFileBuilder.parseFile(path, localIndex);
				// add localInvInd to InvInd
				concurrentIndex.addAll(localIndex);
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
		}
	}	
}
