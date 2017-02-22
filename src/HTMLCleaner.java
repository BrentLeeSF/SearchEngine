import java.util.ArrayList;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * A helper class with several static methods that will help fetch a webpage,
 * strip out all of the HTML, and parse the resulting plain text into words.
 * Meant to be used for the web crawler project.
 *
 * @see HTMLCleaner
 * @see HTMLCleanerTest
 */
public class HTMLCleaner {
	
	
	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();
	
	  /** Regular expression for removing special characters. */
    public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

    /** Regular expression for splitting text into words by whitespace. */
    public static final String SPLIT_REGEX = "(?U)\\p{Space}+";



    /**
     * Parses the provided plain text (already cleaned of HTML tags) into
     * individual words.
     *
     * @param text
     *            plain text without html tags
     * @return list of parsed words
     */
    public static ArrayList<String> parseWords(String text) {
        ArrayList<String> words = new ArrayList<String>();
        text = text.replaceAll(CLEAN_REGEX, "").toLowerCase();

        for (String word : text.split(SPLIT_REGEX)) {
            word = word.trim();

            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }

    /**
     * Removes all style and script tags (and any text in between those tags),
     * all HTML tags, and all special characters/entities.
     *
     * @param html
     *            html code to parse
     * @return plain text
     */
    public static String cleanHTML(String html) {
        String text = html;
        text = stripAllElements(html);
        text = stripTagsEntities(html);
        return text;
    }
    

    
    /**
     * Removes all style and script tags (and any text in between those tags),
     * all HTML tags, and all special characters/entities.
     *
     * @param html
     *            html code to parse
     * @return plain text
     */
    public static String stripAllElements(String html) {
        String text = html;
        text = stripElement("script", text);
        text = stripElement("style", text);
        return text;
    }
  

    /**
     * Removes everything between the element tags, and the element tags
     * themselves. For example, consider the html code:
     *
     * <pre>
     * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
     * </pre>
     * 
     * If removing the "style" element, all of the above code will be removed,
     * and replaced with the empty string.
     *
     * @param name
     *            name of the element to strip, like "style" or "script"
     * @param html
     *            html code to parse
     * @return html code without the element specified
     */
    public static String stripElement(String name, String html) {

    	html = html.replaceAll("(?i)(?s)<" + name + ".*?[^<]*<.*?" + name+ ".*?>", "");
        return html;
    }

    

    /**
     * Replaces all HTML entities in the text with an empty string. For example,
     * "2010&ndash;2012" will become "20102012".
     *
     * @param html
     *            the text with html code being checked
     * @return text with HTML entities replaced by an empty string
     */
    public static String stripEntities(String html) {

    	html = html.replaceAll("(?is)&\\S*?;", "");
        return html;
    }

    
    /**
     * Removes all HTML tags, which is essentially anything between the "<" and
     * ">" symbols. The tag will be replaced by the empty string.
     *
     * @param html
     *            html code to parse
     * @return text without any html tags
     */
    public static String stripTags(String html) {

        html = html.replaceAll("(?i)(?s)<[^>]*>", "");
        return html;
    }
    
   /**
    * Removes html tags and entities
    * @param html
    * 		html
    * @return
    * 		cleaned html without tags or entities
    */
    public static String stripTagsEntities(String html) {
        String text = html;
        text = stripTags(text);
        text = stripEntities(text);
        return text;
    }
}
