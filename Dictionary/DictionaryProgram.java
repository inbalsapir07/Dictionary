/**
 * The class DictionaryProgram displays the panel of a dictionary.
 * Question 2, maman 14.
 * 
 * @author (Inbal Sapir)
 * @version (December 25, 2020)
 */
import java.io.IOException;
import javax.swing.JFrame;
public class DictionaryProgram 
{
	/**
	 * The main method of the the dictionary program.
	 * The class DictionaryProgram displays the panel of a dictionary.
	 */
	public static void main(String[] args) 
	{
		try 
		{
			JFrame frame= new JFrame ("Dictionary");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(700,700);
			Dictionary dictionary= new Dictionary ();
			frame.add(dictionary);
			frame.setVisible(true);
		}
		catch (IOException e) 
		{
			System.out.println("Error in opening or reading the file");
		}
	}
}
