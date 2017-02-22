/**
 * Compares FilePath, Frequency, and Initial Index Position of each SearchResult and sorts accordingly
 */
public class SearchResult implements Comparable<SearchResult> {

	/**
	 * File path that contains word
	 */
	private final String filePath;
	
	/**
	 * Number of times word appears in query search
	 */
	private int frequency;
	
	/**
	 * First position word appears in file path
	 */
	private int initialPosition;
	
	/**
	 * Search Result creates filePath, frequency, and initial position
	 * @param filePath
	 * 		file path the query word appears in
	 * @param frequency
	 * 		Total number of times any query word appeared at this file path
	 * @param initialPosition
	 * 		first position of query word in file path
	 */
	public SearchResult(String filePath, int frequency, int initialPosition) {
		this.filePath = filePath;
		this.frequency = frequency;
		this.initialPosition = initialPosition;
	}

	/**
	 * Compares each frequency, initial position, and file path of each Search Result passed
	 * @param SearchResult
	 * 		Compares SearchResult with contains frequency, initialPosition and filePath
	 */
	@Override
	public int compareTo(SearchResult s) {
		if(Integer.compare(this.frequency, s.frequency) == 0) {
			if(Integer.compare(this.initialPosition, s.initialPosition) == 0) {
				if(this.filePath.compareTo(s.filePath) == 0) {
					return 0;
				}
				else {
					return this.filePath.compareTo(s.filePath);
				}
			}
			else  {
				return Integer.compare(this.initialPosition, s.initialPosition);
			}
		}
		else {
			return Integer.compare(s.frequency, this.frequency);
		}
	}
	
	/**
	 * Increases frequency and compares initial position
	 * 
	 * @param frequency
	 * 		how many times a word appears in a file
	 * @param initialPosition
	 * 		first position a word appears in that path
	 */
	public void update(int frequency, int initialPosition) {
		this.frequency += frequency;
		if(this.initialPosition > initialPosition) {
			this.initialPosition = initialPosition;
		}
	}

	@Override
	public String toString() {
		return "SearchResult [filePath=" + filePath + ", frequency=" + frequency + ", intitialPosition="
				+ initialPosition + "]";
	}

	/**
	 * @return filePath
	 * 		returns filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @return frequency
	 * 		returns frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @return initialPosition
	 * 		returns initialPosition
	 */
	public int getIntitialPosition() {
		return initialPosition;
	}
}

