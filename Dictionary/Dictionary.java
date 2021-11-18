/**
 * The class Dictionary represents a dictionary.
 * 
 * @author (Inbal Sapir)
 * @version (December 25, 2020)
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.*;
public class Dictionary extends JPanel
{
	// variables
	private File file; // the dictionary file
	private TreeMap <String, String> dictionary; // a collection that represents the dictionary
	private MListener mouseListener = new MListener(); // listener for mouse events
	private AListener actionListener = new AListener(); // listener for action events
	private JPanel searchPanel; // the north panel, to search a word
	private JTextField searchField; // search field
	private JButton searchButton; // search button
	private JButton clearButton; // button for clearing the last search
	private JPanel centerPanel; // the center panel, shows the words and the meanings
	private JList <String> wordsList; // a list of the words to display that fits the user's last search
	private DefaultListModel <String> list; // saves the list of the words to display that fits the user's last search
	private JTextArea meanings; // the area where the meaning of a word is displayed
	private String lastSearch; // saving the last expression that was searched by user 
	private String lastMeaning; // saving the word which its meaning was displayed last
	private JPopupMenu pop; // popup menu for right clicking on a word
	private JMenuItem showMeaning; // show meaning menu item
	private JMenuItem updateMeaning; // update meaning menu item
	private JMenuItem removeWord; // remove word menu item
	private JPopupMenu addMenu; //  popup menu for right clicking on a an empty space of the words list
	private JMenuItem addWord; // add word menu item
	private JPanel buttonsPanel; // the south panel, shows the remove, add and update buttons
	private JButton updateButton; // button to update the meaning of a word in dictionary
	private JButton removeButton; // button to remove a word from dictionary
	private JButton addButton; // button to add a word to dictionary
	private JButton saveButton; // button to save the dictionary to file
	// constructor
	/**
	 * An empty constructor. Constructs a new Dictionary object by reading a file.
	 * the dictionary panel displays the words in the dictionary and allows the
	 * user to search, add, remove and update a word, in addition to 
	 * saving the changes to file.
	 * @throws IOException if an error occurred while reading or opening the file
	 */
	public Dictionary () throws IOException
	{	
		// search panel
		searchPanel= new JPanel();
		searchField= new JTextField (20);
		searchField.addActionListener(actionListener);
		searchButton= new JButton("search");
		searchButton.addActionListener(actionListener);
		clearButton= new JButton ("clear");
		clearButton.addActionListener(actionListener);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		searchPanel.add(clearButton);
		// words and meanings panel
		centerPanel= new JPanel();
		centerPanel.setLayout (new GridLayout (1, 2));
		// remove, add and update buttons panel
		buttonsPanel=new JPanel();
		updateButton= new JButton("update");
		updateButton.addActionListener(actionListener);
		updateButton.setEnabled(false);
		removeButton= new JButton("remove");
		removeButton.addActionListener(actionListener);
		removeButton.setEnabled(false);
		addButton= new JButton("add");
		addButton.addActionListener(actionListener);
		saveButton= new JButton("save");
		saveButton.addActionListener(actionListener);
		saveButton.setEnabled(false);
		buttonsPanel.add(updateButton);
		buttonsPanel.add(removeButton);
		buttonsPanel.add(addButton);
		buttonsPanel.add(Box.createHorizontalStrut(30));
		buttonsPanel.add(saveButton);
		// organizing panels in the dictionary panel
		setLayout (new BorderLayout());
		add (searchPanel, BorderLayout.NORTH);
		add (centerPanel, BorderLayout.CENTER);
		add (buttonsPanel, BorderLayout.SOUTH);
		// reading from file and creating the dictionary
		readingFromFile ();
		// displaying dictionary
		list= new DefaultListModel <String> ();
		wordsList= new JList <String> (list);
		wordsList.addMouseListener(mouseListener);
		wordsList.setVisibleRowCount(30);
		wordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		meanings= new JTextArea ();
		meanings.setLineWrap(true);
		meanings.setWrapStyleWord(true);
		lastSearch="";
		lastMeaning="";
		displayingWords(lastSearch);
		// constructing two popup menus for right clicking on the words list
		pop= new JPopupMenu();
		showMeaning= new JMenuItem ("show meaning");
		showMeaning.addActionListener(actionListener);
		updateMeaning= new JMenuItem ("update meaning");
		updateMeaning.addActionListener(actionListener);
		removeWord=	new JMenuItem ("remove word");
		removeWord.addActionListener(actionListener);
		pop.add(showMeaning);
		pop.add(updateMeaning);
		pop.add(removeWord);
		addMenu= new JPopupMenu();
		addWord= new JMenuItem("add word");
		addWord.addActionListener(actionListener);
		addMenu.add(addWord);
	}
	// methods
	/**
	 * Allows the user to choose a file to load the dictionary from.
	 * @throws IOException if an error occurred while reading or opening the file
	 */
	public void readingFromFile () throws IOException
	{
		JFileChooser fileChooser= new JFileChooser(); // allows to choose a file
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle ("Choose a file");
		int result= fileChooser.showOpenDialog(this);
		if (result==JFileChooser.CANCEL_OPTION)
			System.exit(1);
		if (result==JFileChooser.ERROR_OPTION)
			throw new IOException ();
		file=fileChooser.getSelectedFile();
		dictionary= new TreeMap <String, String> (); // the dictionary
		FileReader fileReader= new FileReader (file);
		BufferedReader bufferedReader= new BufferedReader (fileReader);
		while (bufferedReader.ready()) // reading file and creating dictionary
		{
			String word=bufferedReader.readLine();
			String meaning=bufferedReader.readLine();
			dictionary.put (word, meaning);
		}
		bufferedReader.close();
	}
	/**
	 * Saves changes in dictionary made by user to file.
	 * @throws IOException if an error occurred while writing to file
	 */
	public void writingToFile () throws IOException
	{
		FileWriter fileWriter= new FileWriter (file);
		BufferedWriter bufferedWriter= new BufferedWriter (fileWriter);
		Set <Entry<String, String>> keysAndValues= dictionary.entrySet(); // a set of all the words and meanings in the dictionary
		Iterator <Entry <String, String>> iterator= keysAndValues.iterator();
		while (iterator.hasNext()) // saving dictionary by writing to file
		{
			Entry <String, String> entry= iterator.next();
			String key= entry.getKey();
			String value= entry.getValue();
			if (iterator.hasNext())
				bufferedWriter.write(key+"\n"+value+"\n");
			else
				bufferedWriter.write(key+"\n"+value);
		}
		bufferedWriter.close();
	}
	/**
	 * Displaying words that user can choose from.
	 * If clear button was clicked or at the start of the program, 
	 * displays all the words in the dictionary.
	 * If user searched a certain string, displays all the words
	 * that start with that string.
	 * @param word the string that was searched
	 */
	public void displayingWords (String word)
	{
		Set <String> keys = dictionary.keySet(); // a set of all the words in the dictionary
		Iterator <String> iterator= keys.iterator();
		list.clear();
		while (iterator.hasNext()) // scanning all the words in the dictionary
		{
			String key= iterator.next();
			if (key.startsWith(word)) // if a word in the dictionary fits the user's search
				list.addElement(key); // adds word to the list of the words to display
		}
		displaying (wordsList, meanings); // updates display
	}
	/**
	 * Displaying the meaning of a word that the user chose.
	 * @param key the word that the user chose
	 */
	public void displayingMeanings (String key)
	{
		meanings.setText(dictionary.get(key));
		displaying (wordsList, meanings); // updates display
	}
	/**
	 * Updates the words and meanings to display to user.
	 * @param wordsList the words list to display to user
	 * @param meanings the meaning to display to user
	 */
	public void displaying (JList <String> wordsList, JTextArea meanings)
	{
		centerPanel.removeAll(); // clears the words and meanings panels
		JScrollPane wordsScroll= new JScrollPane (wordsList);
		centerPanel.add(wordsScroll); // displays words list
		JScrollPane meaningScroll= new JScrollPane (meanings);
		centerPanel.add(meaningScroll); // displays meaning
		revalidate();
	}
	/**
	 * Updates the meaning of a word.
	 * @param word the word which its meaning the method updates
	 */
	public void update (String word)
	{
		String newMeaning= JOptionPane.showInputDialog(null, "Word: \n"+word+"\nNew meaning: ", "Update Meaning", JOptionPane.INFORMATION_MESSAGE);
		if (newMeaning != null && !newMeaning.equals("")) // if the meaning of the word is valid
		{
			int approve= JOptionPane.showConfirmDialog(null, "Word: "+word+"\nMeaning: "+newMeaning+"\nDo you want to update?", "Update Meaning", JOptionPane.YES_NO_OPTION);
			if (approve==JOptionPane.YES_OPTION) // if the meaning of a word was updated
			{
				dictionary.replace(word, newMeaning); // updates the meaning
				if (lastMeaning.equals(word)) // if the meaning of the word is displayed to the user currently 
					displayingMeanings (word); // updates the meaning displayed
				saveButton.setEnabled(true); // enables the option to save file
			}
		}
	}
	/**
	 * Removes a word.
	 * @param word the word to be removed
	 */
	public void remove (String word)
	{
		int approve= JOptionPane.showConfirmDialog(null, "word: "+word+"\nDo you want to remove?", "Remove Word", JOptionPane.YES_NO_OPTION);
		if (approve==JOptionPane.YES_OPTION) // if a word was removed
		{
			dictionary.remove(word); // removes the word from dictionary
			displayingWords (lastSearch); // updates the list of words displayed
			saveButton.setEnabled(true); // enables the option to save file
		}
	}
	/**
	 * Adds a word.
	 */
	public void add ()
	{
		String newWord= JOptionPane.showInputDialog(null, "Add word: ", "Add Word", JOptionPane.INFORMATION_MESSAGE);
		if (newWord != null && !newWord.equals("")) // if the word is valid
		{
			String newMeaning= JOptionPane.showInputDialog(null, "Add meaning: ", "Add Word", JOptionPane.INFORMATION_MESSAGE);
			if (newMeaning != null && !newMeaning.equals("")) // if the meaning of the word is valid
			{
				int approve= JOptionPane.showConfirmDialog(null, "Word: "+newWord+"\nMeaning: "+newMeaning+"\nDo you want to add?", "Add Word", JOptionPane.YES_NO_OPTION);
				if (approve==JOptionPane.YES_OPTION) // if a word was added
				{
					dictionary.put(newWord, newMeaning); // adds the word
					if (newWord.startsWith(lastSearch)) // if the new word fits the user's last search
						displayingWords (lastSearch); // updates the list of words displayed
					saveButton.setEnabled(true); // enables the option to save file
				}
			}
		}
	}
	/**
	 * Displays all the words in the dictionary 
	 * and clears the search field and the meanings that are displayed to user
	 */
	public void clear ()
	{
		lastSearch="";
		lastMeaning="";
		searchField.setText("");
		meanings.setText("");
		displayingWords(lastSearch);
	}
	/**
	 * The class MListener handles relevant mouse events.
	 */
	private class MListener extends MouseAdapter
	{
		/**
		 * Handles the event which was invoked by user clicking on a the words list.
		 * If user right clicked on a word in the word list, 
		 * pops up a menu with show meaning, update meaning and remove word options.
		 * If user right clicked on an empty space in the words list, 
		 * pops up a menu with an add word option.
		 * If user left clicked twice on a word in the word list, displays the meaning of the word.
		 * If a word in the words list is selected, enables update and remove buttons.
		 * @override mouseClicked in class MouseAdapter
		 * @param e the event
		 */
		public void mouseClicked (MouseEvent e) // if user clicked on the words list
		{
			Point p= e.getPoint(); // the point of the mouse click
			int index= wordsList.locationToIndex(p); // the index of the selected word from the words list
			Rectangle rec= wordsList.getCellBounds(index, index); // the rectangle where the selected word from the words list is located
			if (e.getButton() == MouseEvent.BUTTON3) // if user right clicked on the words list
			{
				if (rec!=null && rec.contains(p)) // if user right clicked on a word in the words list
				{
					wordsList.setSelectedIndex (index);
					pop.show(e.getComponent(), e.getX(), e.getY()); //displays show meaning, update meaning, remove word popup menu
				}
				else // if user right clicked on an empty space in the words list
					addMenu.show(e.getComponent(), e.getX(), e.getY()); // displays add word popup menu
			}
			else // if user left clicked on a the words list
				if (e.getClickCount() == 2 && rec.contains(p)) // if user double clicked && clicked on a word
				{
					lastMeaning= wordsList.getSelectedValue(); // saves the last meaning that was displayed to user
					displayingMeanings(lastMeaning); // displays the meaning of the word
				}
			if (!wordsList.isSelectionEmpty()) // enables relevant buttons if a word is selected
			{
				updateButton.setEnabled(true);
				removeButton.setEnabled(true);
			}
		}
	}
	/**
	 * The class AListener handles relevant action events.
	 */
	private class AListener implements ActionListener
	{
		/**
		 * Handles the event which was invoked by user clicking on a menu item,
		 * clicking on a button, or using the enter key for searching.
	     * If a word in the words list is unselected, disables update and remove buttons.
	     * @override actionPerformed in interface ActionListener
		 * @param e the event
		 */
		public void actionPerformed (ActionEvent e)
		{
			if (e.getSource() instanceof JMenuItem) // if user clicked on one of the menu items
				jMenuItem(e);
			if (e.getSource() instanceof JButton) // if user clicked on one of the buttons
				jButton(e);
			if (e.getSource() instanceof JTextField) // if user search a word with the enter key
				jTextField(e);
			if (wordsList.isSelectionEmpty()) // disables irrelevant buttons if a word is unselected
			{
				updateButton.setEnabled(false);
				removeButton.setEnabled(false);
			}
		}
		/**
		 * Handles the event which was invoked by user clicking on a menu item.
		 * @param e the event
		 */
		public void jMenuItem (ActionEvent e)
		{
			if (((JMenuItem)e.getSource()).getText().equals ("show meaning")) // if user clicked on show meaning popup menu item
				displayingMeanings(wordsList.getSelectedValue());
			if (((JMenuItem)e.getSource()).getText().equals ("update meaning")) // if user clicked on update meaning popup menu item
				update(wordsList.getSelectedValue());
			if (((JMenuItem)e.getSource()).getText().equals ("remove word")) // if user clicked on remove word popup menu item
				remove(wordsList.getSelectedValue());
			if (((JMenuItem)e.getSource()).getText().equals ("add word")) // if user clicked on add word popup menu item
				add ();
		}
		/**
		 * Handles the event which was invoked by user clicking on a button.
		 * @param e the event
		 */
		public void jButton (ActionEvent e)
		{
			if (((JButton)e.getSource()).getText().equals("search")) // if user clicked the search button
			{
				lastSearch= searchField.getText(); // saves the last expression that was searched by user
				displayingWords(lastSearch);
			}
			if (((JButton)e.getSource()).getText().equals("clear")) // if user clicked the clear button
				clear();
			if (((JButton)e.getSource()).getText().equals("update")) // if user clicked the update button
				update(wordsList.getSelectedValue());
			if (((JButton)e.getSource()).getText().equals("remove")) // if user clicked the remove button
				remove(wordsList.getSelectedValue());
			if (((JButton)e.getSource()).getText().equals("add")) // if user clicked the add button
				add ();
			if (((JButton)e.getSource()).getText().equals("save")) // if user clicked the save button
			{
				try
				{
					writingToFile ();
					saveButton.setEnabled(false);
				}
				catch (IOException exception)
				{
					System.out.println("Error in writing to file");
				}
			}
		}
		/**
		 * Handles the event which was invoked by user using the enter key for searching.
		 * @param e the event
		 */
		public void jTextField (ActionEvent e)
		{
			lastSearch= ((JTextField)e.getSource()).getText(); // saves the last expression that was searched by user
			displayingWords(lastSearch);
		}
	}
}
