import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Receives InvertedIndex and BufferedWriter and writes to JSON file at a specific path
 */
public class JSONWriter {

	/** 
	 * Used for formatting output for BufferedWriter
	 * @param times
	 * 		number of indents for formatted string
	 * @return formatted String
	 * 		formatted String
	 */
	public static String indent(int times) throws IOException {
		return times > 0 ? String.format("%" + (times * 2) + "s", " ") : "";
	}
	
	/** 
	 * Used for quoting output for BufferedWriter
	 * @param text
	 * 		text to be quoted
	 * @return quoted Text
	 * 		quoted Text
	 */
	public static String quote(String text) {
		return "\"" + text + "\"";
	} 

	/** 
	 * Writes nested map in JSON format using the provided writer.
	 * @throws IOException
	 * @param outerMap
	 * 		Word, Path and positions within that path
	 * @param bw
	 * 		BufferedWriter to be used for printing to JSON
	 * @return status
	 * 		return true if written properly
	 */
	public static boolean writeOuter(TreeMap<String, TreeMap<String, TreeSet<Integer>>> outerMap, BufferedWriter bw) throws IOException {
		
		boolean status = true;
		
		bw.write("{");
			
		if(!outerMap.isEmpty()) {
			Entry<String, TreeMap<String, TreeSet<Integer>>> firstEntry = outerMap.firstEntry();
			status = writeMiddle(firstEntry, bw, status);
				
			for(Entry<String, TreeMap<String, TreeSet<Integer>>> entry : outerMap.tailMap(firstEntry.getKey(), false).entrySet()) {
				bw.write(","); 
				status = writeMiddle(entry, bw, status);
			}
		}
			
		bw.newLine();
		bw.write("}");
		bw.newLine();
		
		return status;
	}
	
	/** 
	 * Gets the Second Entry path/text file and passes it to inner
	 * @throws IOException 
	 * @param outer
	 * 		Word, Path and positions within that path
	 * @param bw
	 * 		BufferedWriter to be used for printing to JSON
	 * @param status
	 * 		return true if written properly
	 */
	public static boolean writeMiddle(Entry<String, TreeMap<String, TreeSet<Integer>>> outer, BufferedWriter bw, boolean status) throws IOException {
		
		status = true;

		bw.newLine();
		bw.write(indent(1) + quote(outer.getKey()));
		bw.write(": ");
		bw.write("{");
			
		if(!outer.getKey().isEmpty()) {
			Entry<String, TreeSet<Integer>> midEntry = outer.getValue().firstEntry();
			status = writeInner(midEntry, bw, status);
				
			for(Entry<String, TreeSet<Integer>> entry : outer.getValue().tailMap(midEntry.getKey(), false).entrySet()) {
	        	bw.write(","); 
	        	status = writeInner(entry, bw, status);
	        }
		}
			
		bw.newLine();
		bw.write(indent(1) +"}");

		return status;
	}
	
	/** 
	 * Writes out the word, textfile and position that the word was found. 
	 * @param mid
	 * 		Path and positions within that path
	 * @param bw
	 * 		BufferedWriter to be used for printing to JSON
	 * @param status
	 * 		return true if written properly
	 * @throws IOException 
	 */
	public static boolean writeInner(Entry<String, TreeSet<Integer>> mid, BufferedWriter bw, boolean status) throws IOException {
	
		bw.newLine();
    	bw.write(indent(2)); 
    	bw.write(quote(mid.getKey()));
    	bw.write(": ");
    		
    	TreeSet<Integer> datNewSet = mid.getValue();
    	bw.write("[");

    		if(!datNewSet.isEmpty()) { 
    			Integer firstOne = datNewSet.first();
    	        bw.newLine();
    	        bw.write(indent(3) + firstOne);
    	        	
    	        for(Integer datValue : datNewSet.tailSet(firstOne, false)) {
    	        	bw.write(",");  
    	        	bw.newLine(); 
    	        	bw.write(indent(3) + datValue);
    	        }
    		}
    			
    		bw.newLine();
    		bw.write(indent(2) + "]");	        
		
		return status;	
	}
}
