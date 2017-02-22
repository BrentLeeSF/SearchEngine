import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** 
 * Traverse directory to paths. Adds path to arrayList to be returned
 */
public class DirectoryTraverser {
	
	/** 
	 * Path is passed, creates ArrayList of Paths and traverses directories
	 * @param directory
	 * 		directory of files of words to be added to InvertedIndex
	 * @return files
	 * 		list of files of words to be added to InvertedIndex
	 */
	public static ArrayList<Path> traverseDirectory(Path directory) throws IOException {
		ArrayList<Path> files = new ArrayList<>();
		traverseDirectory(directory, files);
		return files;
	}
	
	/** 
	 * Path and arrayList of files is passed, traverses directories to add textFiles
	 * @param directory
	 * 		directory of files to later be added to InvertedIndex
	 * @param files
	 * 		files to later be parsed and added to InvertedIndex
	 * @throws IOException, 
	 * @catch FileNotFoundException
	 */
	public static void traverseDirectory(Path directory, ArrayList<Path> files) throws IOException {
		
		assert Files.isDirectory(directory);
		if(Files.isDirectory(directory)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
				for(Path file : stream) {
					traverseDirectory(file, files);
				}
			}
			catch(FileNotFoundException f) {
				System.err.println("File Not Found");
			}
		}
		
		else { 
			String txtFile = directory.toString();
			
			if(txtFile.toLowerCase().endsWith("txt")) {
				files.add(directory);
			}
		}
	} // end traverseDirectory
} // end class
