import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Writes search results for query
 */
public class JSONSearchWriter {

	/**
	 * Receives LinkedHashMap of Query and SearchResults, and BufferedWriter bw. 
	 * Writes out Query, passes query information to writeMiddleQuery
	 * 
	 * @param linkedMap
	 * 		Receives LinkedHashMap of query and SearchResults
	 * @param bw
	 * 		Receives BufferedWriter
	 * @return status
	 * 		Status is true if line is written
	 * @throws IOException
	 */
	public static boolean writeOuterQuery(LinkedHashMap<String, List<SearchResult>> linkedMap, BufferedWriter bw) throws IOException {

		boolean status = true;
		bw.write("{");

		if(!linkedMap.isEmpty()) {

			int lastIndex = linkedMap.size() - 1;
			int i = 0;

			for(String query : linkedMap.keySet()) {

				bw.newLine();
				bw.write(JSONWriter.indent(1) + JSONWriter.quote(query));
				bw.write(": [");
				bw.newLine();
				writeMiddleQuery(linkedMap.get(query), bw);
				bw.write(JSONWriter.indent(1) + "]");

				if(i < lastIndex) {
					bw.write(",");
				}
				i++;
			}
		}		
		bw.newLine();
		bw.write("}");
		bw.newLine();

		return status;
	}

	/**
	 * Receives ArrayList<SearchResult> and BufferedWriter. Writes path, frequency, and initial position
	 * @param srList
	 * 		List of SearchResults (path, frequency, initialPosition)
	 * @param bw
	 * 		BufferedWriter
	 * @return status
	 * 		returns true if SearchResult path, frequency, and initialPosition is written
	 * @throws IOException
	 */
	public static boolean writeMiddleQuery(List<SearchResult> srList, BufferedWriter bw) throws IOException {

		boolean status = true;

		for(int i = 0; i < srList.size(); i++) {

			bw.write(JSONWriter.indent(2) +"{");
			bw.newLine();
			bw.write(JSONWriter.indent(3) + "\"where\": " +JSONWriter.quote(srList.get(i).getFilePath())+ ",");
			bw.newLine();
			bw.write(JSONWriter.indent(3) + "\"count\": " + srList.get(i).getFrequency()+ ",");
			bw.newLine();
			bw.write(JSONWriter.indent(3) + "\"index\": " + srList.get(i).getIntitialPosition());
			bw.newLine();
			bw.write(JSONWriter.indent(2) +"}");
			if(i < srList.size() - 1) {
				bw.write(",");
			}
			bw.newLine();
		}
		return status;
	}
}
