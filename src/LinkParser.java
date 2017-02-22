import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses links from HTML. Assumes the HTML is valid, and all attributes are
 * properly quoted and URL encoded.
 *
 * <p>
 * See the following link for details on the HTML Anchor tag:
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a"> https:
 * //developer.mozilla.org/en-US/docs/Web/HTML/Element/a </a>
 *
 * @see LinkTester
 */
public class LinkParser {

	// http://docs.oracle.com/javase/7/docs/api/?java/util/regex/Pattern.html

	/**
	 * Largest size of URLs is 50, seed url makes it start at 1
	 */
	public static final int LINK_LIMIT = 49;

	/**
	 * The regular expression used to PARSE the HTML for LINKS.
	 */
	public static final String REGEX = "(?i)<a[^>]*href\\s*=\\s*\"(\\S*)\"[^>]*>";

	/**
	 * The group in the regular expression that CAPTURES THE RAW LINK.
	 */
	public static final int GROUP = 1; 
	
	/**
	 * Parses links from html web page, checks webpage for other links. resolves relative links and adds them
	 * to the URL site. Returns URL set
	 * @param text
	 * 		html webpage text
	 * @param baseURL
	 * 		URL used to resolve relative links
	 * @return
	 * 		URL set
	 * @throws MalformedURLException
	 */
	public static Set<URL> listLinks(String text, URL baseURL) throws MalformedURLException {

		// list to store links
		Set<URL> links = new HashSet<>();

		Pattern p = Pattern.compile(REGEX); 
		Matcher m = p.matcher(text);

		while(m.find() && links.size() < LINK_LIMIT) {

			String link = m.group(GROUP);

			if(link.startsWith("#") || link.startsWith("mailto:")) {
				continue;
			}

			URI uri = URI.create(link);
			URI baseURI = URI.create(baseURL.toString());
			uri = baseURI.resolve(uri.getPath());

			links.add(uri.toURL());
		}

		return links;
	}
}