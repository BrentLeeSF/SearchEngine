import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * Parses webpage from given seedURL, connects to server, gets html, locates links within webpage, cleans html, 
 * and build InvertedIndex using maximum of 50 links.
 */
public class WebCrawler {

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();
	
	/**
	 * InvertedIndex data structure of word, path and position
	 */
	private final ConcurrentInvertedIndex concurrentInvertedIndex;
	
	/**
	 * Starting url for web crawler to look for links and add text to InvIndex
	 */
	private final String seedURL;
	
	/**
	 * Set of unique urls, size less than 50
	 */
	private final Set<URL> urlSet;
	
	/**
	 * Declares work queue with the specified number of threads.
	 */
	private final WorkQueue minions;
	
	
	public WebCrawler(ConcurrentInvertedIndex concurrentInvertedIndex, String seedURL, int threads) {
		this.concurrentInvertedIndex = concurrentInvertedIndex;
		this.seedURL = seedURL;
		this.minions = new WorkQueue(threads);
		this.urlSet = new HashSet<>();
	}
	
	/**
	 * Starts building invertedIndex multi-threaded starting from a seedURL
	 * @throws MalformedURLException
	 * 		used to make sure url being read/created is valid
	 */
	public void parseSeedPage() {

		try {
			minions.execute(new WebCrawlerMinion(URI.create(seedURL).toURL()));
		} catch (MalformedURLException e) {
			//logger.warn(e.toString());
		}
	}
	
	/**
	 * Receives url with given html and creates a localInvertedIndex to be added to main InvertedIndex
	 * @param url
	 * 		link to be used as key
	 * @param html
	 * 		contains words to be added to InvertedIndex
	 */
	private void addToInvertedIndex(URL url, String html) {
		
		// create a local InvInd to make it multi-threaded, thread-safe, to add to main InvInd
		InvertedIndex localIndex = new InvertedIndex();
		
		int counter = 0;
		String[] words = InvertedIndexFileBuilder.split(html);

		for(String newWords : words) {
			String cleaned = InvertedIndexFileBuilder.clean(newWords);

			if(!cleaned.isEmpty()) {
				counter++;
				localIndex.add(cleaned, url.toString(), counter);
			}
		}
		concurrentInvertedIndex.addAll(localIndex);
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
	 * Read the html into memory, parses all links, recurses for every link found, create localInvertedIndex
	 * to be added to main InvteredIndex
	 */
	private class WebCrawlerMinion implements Runnable {
		
		/**
		 * URL to read webpage from
		 */
		private final URL url;

		public WebCrawlerMinion(URL url) {
			this.url = url;
		}

		/**
		 * Read the html into memory, parses all links, recurses for every link found, create localInvertedIndex
		 * to be added to main InvteredIndex
		 */
		@Override
		public void run() {
			String html = null;
			
			try {
				html = HTTPFetcher.fetchHTML(url);
			} catch (IOException e) {
				//logger.warn(e.toString());
				return;
			}

			html = HTMLCleaner.stripAllElements(html);

			Set<URL> localURLSet = null;
			
			try {
				localURLSet = LinkParser.listLinks(html, url);
			} catch (MalformedURLException e) {
				//logger.warn(e.toString());
				return;
			}
			
			synchronized(urlSet) {
				for(URL url : localURLSet) {

					if(urlSet.size() >= LinkParser.LINK_LIMIT) {
						break;
					}
					if(!urlSet.contains(url)) {
						urlSet.add(url);
						minions.execute(new WebCrawlerMinion(url));
					}
				}
			}

			html = HTMLCleaner.stripTagsEntities(html);
			addToInvertedIndex(url, html);
		}
	}
}
