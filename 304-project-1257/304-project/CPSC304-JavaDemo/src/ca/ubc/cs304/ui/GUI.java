package ca.ubc.cs304.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.*;
import java.util.List;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.GenreAvgLengthModel;
import ca.ubc.cs304.model.GenreCountModel;
import ca.ubc.cs304.model.PictureModel;
import ca.ubc.cs304.model.UserModel;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * The class is only responsible for handling terminal text inputs. 
 */
public class GUI extends JFrame implements ActionListener, ItemListener, FocusListener {
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	private static final int INVALID_INPUT = Integer.MIN_VALUE;
	private static final int EMPTY_INPUT = 0;
	private static final Date INVALID_DATE = null;
	private static final String INVALID_STRING = "";
	
	private BufferedReader bufferedReader = null;
	private TerminalTransactionsDelegate delegate = null;

	/**
	public List<PictureModel> pictures;
	public List<AccountModel> accounts;
	public List<GenreModel> genres;
	public List<SeriesModel> series;
	public List<PlanTypeModel> planTypes;
	 **/

	static final int GAP = 10;

	//fields for gui
	JMenuBar menuBar; //do we need this
	JTextArea output;
	JScrollPane scrollPane;

	String newline = "\n";
	Font regularFont;
	Font italicFont;

	//Users that watched a specific picture
	JLabel userDisplay = new JLabel();
	JTextField watchedTitle = new JTextField();
	JTextField watchRelDate = new JTextField();
	boolean userSet = false;

	//users with fav genre fields
	JLabel usersWithFavGenreDisplay = new JLabel();
	JTextField category = new JTextField();
	boolean genreSet = false;

	//Picture creator fields
	JLabel pictureDisplay = new JLabel();
	JTextField pictureTitle = new JTextField();
	JTextField releaseDate = new JTextField();
	JTextField length = new JTextField();
	JTextField director = new JTextField();
	JTextField seriesID = new JTextField();
	JTextField picGenre = new JTextField();
	boolean pictureSet = false;

	//Director update functionality
	JLabel directorUpdateDisplay = new JLabel();
	boolean directorSet = false;
	JTextField newDirTitle = new JTextField();
	JTextField newDirDate = new JTextField();
	JTextField newDir = new JTextField();

	//genres with enough entries to be printed
	JLabel numPics = new JLabel();
	boolean numSet = false;
	JTextField number = new JTextField();

	//Delete display
	JLabel deleteDisplay = new JLabel();
	boolean deleted = false;
	JTextField delTitle = new JTextField();
	JTextField delDate = new JTextField();

	//printButtons
	JLabel printButtonsDisplay = new JLabel();



	/*

	 TODO: !!!
	 	- Create GUI with options to:
	 		- Create a Picture
	 			- Different if it's a movie vs TV show
	 			- director, genre, and series should all be selectable from those which already exist
	 		- Create an account !
	 		- Add a user to an account !
	 		- Create a series !
	 		- Create a genre !
	 		- Add a picture to a user's watchlist
	 		- Submit a picture that a user has watched
	 			- Add it to their history
	 		- Create a director

	*/

	public GUI() {
	}

	//TODO: get default movies, users, genres, etc into gui
	//TODO: startup/shutdown sounds? lol

	public static void createAndShowGUI() {
		JFrame frame = new JFrame("CPSC304 Milestone 3");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GUI gui = new GUI();
		frame.setContentPane(gui.createContentPane());

		frame.setSize(450, 260);
		frame.setVisible(true);
	}

	public JPanel createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(true);
		output = new JTextArea(20, 30);
		output.setEditable(false);
		scrollPane = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		contentPane.add(createXtoYPanels());
		return contentPane;
	}

	public JPanel createXtoYPanels() {
		JPanel creatorPanels = new JPanel(new BorderLayout());
		creatorPanels.setLayout(new BoxLayout(creatorPanels, BoxLayout.X_AXIS));
		creatorPanels.add(StackTextEntryPanels());
		creatorPanels.add(printButtonsPanel());
		return creatorPanels;
		}


	public JPanel StackTextEntryPanels() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(pictureCreationPanel());
		panel.add(deletePicturePanel());
		panel.add(usersThatWatchedPanel());
		panel.add(usersWithFavGenrePanel());
		panel.add(numPicsOverDisplay());
		panel.add(directorUpdatePanel());
		return panel;
	}


	//generate panel to craete an picture
	public JPanel pictureCreationPanel() {
		JPanel UI = new JPanel();
		UI.setLayout(new BoxLayout(UI, BoxLayout.X_AXIS));

		String title = "Add a Picture to DB";
		Border border = BorderFactory.createTitledBorder(title);
		UI.setBorder(border);

		JPanel leftHalf = new JPanel() {
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE, pref.height);
			}
		};
		leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.PAGE_AXIS));
		List<String> textLabels =
				new ArrayList<>(Arrays.asList("Title: ", "Release Date: ", "Length: ", "Director: ", "Series ID: "));
		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Create Picture"));
		leftHalf.add(createEntryFields(textLabels));
		leftHalf.add(createButtons(buttonLabels));

		UI.add(leftHalf);
		//left half complete

		JComponent panel = new JPanel(new BorderLayout());
		pictureDisplay = new JLabel();
		pictureDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = pictureDisplay.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updatePictureDisplays();
		
		panel.setBorder(BorderFactory.createEmptyBorder(
				GAP / 2, 0, GAP / 3, 0));
		panel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		panel.add(pictureDisplay, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200, 150));
		
		UI.add(panel);
		
		return UI;
	}

	/**
	 * public void showPicture();
	 * Prints out all the tuples in picture
	 *
	 *public void projectPictureSeriesID();
	 * Projects picture table, showing only series IDs
	 *
	 * public void avgLengthByGenre();
	 * Find the average length, grouped by genre
	 *
	 * public void usersThatWatchedAllPictures();
	 * Find users that watched all pictures
	 *
	 *
	 * public void genreWithLowestAvgLength();
	 * Find the genre with the lowest avg length across all genres
	 */

	public JPanel printButtonsPanel() {
		JPanel UI = new JPanel();
		UI.setLayout(new BoxLayout(UI, BoxLayout.X_AXIS));

		String title = "Print Functions";
		Border userBorder = BorderFactory.createTitledBorder(title);
		UI.setBorder(userBorder);

		JPanel buttonPanel = new JPanel();
		printButtonsDisplay = new JLabel();
		printButtonsDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = printButtonsDisplay.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);

		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Print All Picture Tuples", "Print All User Tuples", "Print Series IDs",
						"Find Average Picture Length by Genre", "Find Users Who Watched All Pictures",
						"Find the Genre with the Lowest Average Picture Length"));
		buttonPanel.add(createButtons(buttonLabels));

		UI.add(buttonPanel);

		return UI;
	}

	public JPanel deletePicturePanel() {
		JPanel UI = new JPanel();
		UI.setLayout(new BoxLayout(UI, BoxLayout.X_AXIS));

		String title = "Delete a Picture";
		Border userBorder = BorderFactory.createTitledBorder(title);
		UI.setBorder(userBorder);

		JPanel lh = new JPanel() {
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE, pref.height);
			}
		};

		lh.setLayout(new BoxLayout(lh, BoxLayout.PAGE_AXIS));
		List<String> textLabels =
				new ArrayList<>(Arrays.asList("Title: ", "Release Date"));
		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Delete"));
		lh.add(createEntryFields(textLabels));
		lh.add(createButtons(buttonLabels));

		UI.add(lh);

		JComponent panel = new JPanel(new BorderLayout());
		deleteDisplay = new JLabel();
		deleteDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = deleteDisplay.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updateDelete();

		panel.setBorder(BorderFactory.createEmptyBorder(
				GAP / 2, 0, GAP / 3, 0));
		panel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		panel.add(deleteDisplay, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200, 150));

		UI.add(panel);

		return UI;
	}

	public JPanel usersThatWatchedPanel() {
		JPanel userUI = new JPanel();
		userUI.setLayout(new BoxLayout(userUI, BoxLayout.X_AXIS));

		String title = "Print Users That Watched a Certain Picture";
		Border userBorder = BorderFactory.createTitledBorder(title);
		userUI.setBorder(userBorder);

		JPanel lh = new JPanel() {
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE, pref.height);
			}
		};

		lh.setLayout(new BoxLayout(lh, BoxLayout.PAGE_AXIS));
		List<String> textLabels =
				new ArrayList<>(Arrays.asList("Title: ", "Release Date"));
		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Print Users"));
		lh.add(createEntryFields(textLabels));
		lh.add(createButtons(buttonLabels));

		userUI.add(lh);

		JComponent panel = new JPanel(new BorderLayout());
		userDisplay = new JLabel();
		userDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = userDisplay.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updateUsersThatWatched();

		panel.setBorder(BorderFactory.createEmptyBorder(
				GAP / 2, 0, GAP / 3, 0));
		panel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		panel.add(userDisplay, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200, 150));

		userUI.add(panel);

		return userUI;
	}

	public JPanel usersWithFavGenrePanel() {
		JPanel genreUI = new JPanel();
		genreUI.setLayout(new BoxLayout(genreUI, BoxLayout.X_AXIS));

		String genreTitle = "Print Users with Favourite Genre";
		Border genreBorder = BorderFactory.createTitledBorder(genreTitle);
		genreUI.setBorder(genreBorder);

		JPanel leftHalf = new JPanel() {
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE, pref.height);
			}
		};
		leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.PAGE_AXIS));
		List<String> textLabels =
				new ArrayList<>(Arrays.asList("Genre"));
		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Print"));
		leftHalf.add(createEntryFields(textLabels));
		leftHalf.add(createButtons(buttonLabels));

		genreUI.add(leftHalf);

		JComponent panel = new JPanel(new BorderLayout());
		usersWithFavGenreDisplay = new JLabel();
		usersWithFavGenreDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = usersWithFavGenreDisplay.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updateGenreDisplays();

		panel.setBorder(BorderFactory.createEmptyBorder(
				GAP / 2, 0, GAP / 3, 0));
		panel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		panel.add(usersWithFavGenreDisplay, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200, 150));

		genreUI.add(panel);

		return genreUI;
	}

	public JPanel numPicsOverDisplay() {
		JPanel UI = new JPanel();
		UI.setLayout(new BoxLayout(UI, BoxLayout.X_AXIS));

		String title = "Print Pictures by Genre, if the Genre Has More Entries Than the Input Number";
		Border border = BorderFactory.createTitledBorder(title);
		UI.setBorder(border);

		JPanel leftHalf = new JPanel() {
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE, pref.height);
			}
		};
		leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.PAGE_AXIS));
		List<String> textLabels =
				new ArrayList<>(Arrays.asList("Number of Pictures"));
		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Print Pictures"));
		leftHalf.add(createEntryFields(textLabels));
		leftHalf.add(createButtons(buttonLabels));

		UI.add(leftHalf);

		JComponent panel = new JPanel(new BorderLayout());
		numPics = new JLabel();
		numPics.setHorizontalAlignment(JLabel.CENTER);
		regularFont = numPics.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updateNunPicsDisplays();

		panel.setBorder(BorderFactory.createEmptyBorder(
				GAP / 2, 0, GAP / 3, 0));
		panel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		panel.add(numPics, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200, 150));

		UI.add(panel);

		return UI;
	}

	public JPanel directorUpdatePanel() {
		JPanel panel = new JPanel(new SpringLayout());
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		String title = "Update a Picture's Director";
		Border border = BorderFactory.createTitledBorder(title);
		panel.setBorder(border);

		JPanel lh = new JPanel() {
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE, pref.height);
			}
		};
		lh.setLayout(new BoxLayout(lh, BoxLayout.PAGE_AXIS));
		List<String> textLabels =
				new ArrayList<>(Arrays.asList("Title ", "Release Date ", "New Director: "));
		lh.add(createEntryFields(textLabels));
		List<String> buttonLabels =
				new ArrayList<>(Arrays.asList("Update"));
		lh.add(createButtons(buttonLabels));

		panel.add(lh);

		JComponent panel2 = new JPanel(new BorderLayout());
		directorUpdateDisplay = new JLabel();
		directorUpdateDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = directorUpdateDisplay.getFont().deriveFont(Font.PLAIN, 16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updateDirectorDisplays();

		panel2.setBorder(BorderFactory.createEmptyBorder(
				GAP / 2, 0, GAP / 3, 0));
		panel2.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
		panel2.add(directorUpdateDisplay, BorderLayout.CENTER);
		panel2.setPreferredSize(new Dimension(200, 150));

		panel.add(panel2);

		return panel;

	}

	public JComponent createEntryFields(List<String> labels) {

		JPanel panel = new JPanel(new SpringLayout());

		String[] labelStrings = labels.toArray(new String[0]);


		JLabel[] JL = new JLabel[labelStrings.length];
		JComponent[] fields = new JComponent[labelStrings.length];
		JTextField t = new JTextField();
		t.setColumns(3000);

		int i=0;

		for (String s: labels) {
			JLabel J = new JLabel(s);
			J.setPreferredSize(new Dimension(s.length(), 2));
			t.setPreferredSize(new Dimension(3000, 500));
			panel.add(J);
			panel.add(t);
		}
//		for (int k=0; k<JL.length; k++) {
//
//			panel.add(JL[k]);
//			panel.add(fields[k]);
//					//new JLabel(labelStrings[i], JLabel.TRAILING);
////			JL[i].setLabelFor(fields[i]);
////			panel.add(JL[i]);
////			panel.add(fields[i]);
//		}
		makeCompactGrid(panel, labelStrings.length, 0, GAP, GAP, GAP, GAP / 2);

		return panel;
	}

	public JComponent createButtons(List<String> labels) {

		JPanel panel = new JPanel(new SpringLayout());
		panel.setPreferredSize(new Dimension(labels.toArray().length, labels.toArray().length));

		String[] labelStrings = labels.toArray(new String[0]);

		for (int i=0; i<labelStrings.length; i++) {
			JButton button = new JButton(labelStrings[i]);
			button.addActionListener(this);
			button.setPreferredSize(new Dimension(25, 25));
			panel.add(button);
		}

		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, GAP - 5, GAP - 5));
		return panel;

	}

	//EFFECTS updates created account display if account is created
	protected void updatePictureDisplays() {
		pictureDisplay.setText(formatPicture());
		if (pictureSet) {
			pictureDisplay.setFont(regularFont);
		} else {
			pictureDisplay.setFont(italicFont);
		}
	}

	public String formatPicture() {
		if (!pictureSet) {
			pictureTitle.resetKeyboardActions();
			director.resetKeyboardActions();
			seriesID.resetKeyboardActions();
			length.resetKeyboardActions();
			releaseDate.resetKeyboardActions();
			picGenre.resetKeyboardActions();
			return "No picture set";
		}
		String title = pictureTitle.getText();
		Date date = Date.valueOf(releaseDate.getText());
		String dir = director.getText();
		String gen = picGenre.getText();
		int sid = Integer.parseInt(seriesID.getText());
		int len = Integer.parseInt(length.getText());
		String empty = "";
		if ((title == null) || empty.equals(title)) {
			title = "<em>(no title specified)</em>";
		}
		if ((gen == null) || empty.equals(gen)) {
			title = "<em>(no genre specified)</em>";
		}
		if ((date == null) || empty.equals(date)) {
			date = new Date(0, 0, 0);
		}
		if ((dir == null) || empty.equals(dir)) {
			dir = "<em>(no director specified)</em>";
		}

		StringBuffer sb = new StringBuffer();
		doPictureStringBufferThings(sb, title, date, len, dir, sid, gen);
		if (title != null && date != null && !empty.equals(date) && !empty.equals(title)
			&& dir != null && !empty.equals(dir)) {
			pictureSet = true;
		}

		if (pictureSet && title != "<em>(no title specified)</em>"
				&& date != null
				&& dir != "<em>(no director specified)</em>") {
			PictureModel p = new PictureModel(title, date, len, dir, sid, gen);
			delegate.insertPicture(p);
		}
		return sb.toString();
	}

	public StringBuffer doPictureStringBufferThings(StringBuffer sb, String title, Date date, int len,
													String dir, int sid, String gen) {
		sb.append("<html><p align=center>");
		sb.append("Title: ");
		sb.append(title);
		sb.append("<br>");
		sb.append("Release Date: ");
		sb.append(date);
		sb.append("<br>");
		sb.append("Length: ");
		sb.append(len);
		sb.append("<br>");
		sb.append("Director: ");
		sb.append(dir);
		sb.append("<br>");
		sb.append("Series ID: ");
		sb.append(sid);
		sb.append("<br>");
		sb.append("Genre: ");
		sb.append(gen);
		sb.append("</p></html>");
		return sb;
	}

	protected void updateNunPicsDisplays() {
		numPics.setText(formatNumPics());
		if (numSet) {
			numPics.setFont(regularFont);
		} else {
			numPics.setFont(italicFont);
		}
	}

	public String formatNumPics() {
		if (!numSet) {
			number.resetKeyboardActions();
			return "No number selected";
		}
		String text = number.getText();
		String empty = "";
		if (text == null || empty.equals(text)) {
			text = "<em>(no number specified)</em>";
		}

		StringBuffer sb = new StringBuffer();
		doNumPicsSBThings(sb, text);
		if (text != null && !empty.equals(text)) {
			numSet = true;
		}

		if (numSet && text != "<em>(no genre specified)</em>") {
			try {
				int n = Integer.valueOf(text);
				delegate.numPicturesByGenreOver(n);
				//TODO this outputs a list of genreCountModel, should probably do something with it

			} catch (NumberFormatException e) {
				return "The input was not a string!";
			}
		}
		return sb.toString();
	}

	public StringBuffer doNumPicsSBThings(StringBuffer sb, String text) {
		sb.append("<html><p align=center>");
		sb.append("Minimum Number of Pictures per Genre: ");
		sb.append(text);
		sb.append("</p></html>");
		return sb;
	}

	protected void updateGenreDisplays() {
		usersWithFavGenreDisplay.setText(formatGenre());
		if (genreSet) {
			usersWithFavGenreDisplay.setFont(regularFont);
		} else {
			usersWithFavGenreDisplay.setFont(italicFont);
		}
	}

	public String formatGenre() {
		if (!genreSet) {
			category.resetKeyboardActions();
			return "No genre set";
		}
		String categoryText = category.getText();
		String empty = "";
		if ((categoryText == null) || empty.equals(categoryText)) {
			categoryText = "<em>(no genre specified)</em>";
		}

		StringBuffer sb = new StringBuffer();
		doGenreStringBufferThings(sb, categoryText);
		if (categoryText != null && !empty.equals(categoryText)) {
			genreSet = true;
		}

		if (genreSet && categoryText != "<em>(no genre specified)</em>") {
			delegate.selectUsersFavGenre(categoryText);
			//TODO this outputs a list of users, should probably do something with it
			//favGenre.add(categoryText);
			//updateDataBase(genre);
		}
		return sb.toString();
	}

	public StringBuffer doGenreStringBufferThings(StringBuffer sb, String g) {
		sb.append("<html><p align=center>");
		sb.append("Genre: ");
		sb.append(g);
		sb.append("</p></html>");
		return sb;
	}

	public void updateDelete() {
		userDisplay.setText(formatDelete());
		if (deleted) {
			deleteDisplay.setFont(regularFont);
		} else {
			deleteDisplay.setFont(italicFont);
		}
	}

	public String formatDelete() {
		if (!deleted) {
			delTitle.resetKeyboardActions();
			return "No title set";
		}
		String title = delTitle.getText();
		Date date = Date.valueOf(delDate.getText());
		String empty = "";

		if ((title == null) || empty.equals(title)) {
			title = "<em>(no title specified)</em>";
		}
		if ((date == null) || empty.equals(date)) {
			date = new Date(0, 0, 0);
		}

		StringBuffer sb = new StringBuffer();
		doDeleteStringBufferThings(sb, title, date);

		if (title != null && !empty.equals(title) && date != null && !empty.equals(date)) {
			deleted = true;
		}

		if (deleted && title != "<em>(no title specified)</em>"
				&& date != null) {
			delegate.deletePicture(title, date);
			//TODO this outputs a list of users, should probably do something with it

		}

		return sb.toString();
	}

	public StringBuffer doDeleteStringBufferThings(StringBuffer sb, String title, Date date) {
		sb.append("<html><p align=center>");
		sb.append("Title: ");
		sb.append(title);
		sb.append("<br>");
		sb.append("Release Date: ");
		sb.append(date);
		sb.append("</p></html>");
		return sb;
	}


	public void updateUsersThatWatched() {
		userDisplay.setText(formatWatchers());
		if (userSet) {
			userDisplay.setFont(regularFont);
		} else {
			userDisplay.setFont(italicFont);
		}
	}

	public String formatWatchers() {
		if (!userSet) {
			watchedTitle.resetKeyboardActions();
			return "No title set";
		}
		String title = watchedTitle.getText();
		Date date = Date.valueOf(watchRelDate.getText());
		String empty = "";

		if ((title == null) || empty.equals(title)) {
			title = "<em>(no title specified)</em>";
		}
		if ((date == null) || empty.equals(date)) {
			date = new Date(0, 0, 0);
		}

		StringBuffer sb = new StringBuffer();
		doWatcherStringBufferThings(sb, title, date);

		if (title != null && !empty.equals(title) && date != null && !empty.equals(date)) {
			userSet = true;
		}

		if (userSet && title != "<em>(no title specified)</em>"
				&& date != null) {
			delegate.allUsersThatWatchedAPicture(title, date);
			//TODO this outputs a list of users, should probably do something with it

		}

		return sb.toString();
	}

	public StringBuffer doWatcherStringBufferThings(StringBuffer sb, String title, Date date) {
		sb.append("<html><p align=center>");
		sb.append("Title: ");
		sb.append(title);
		sb.append("<br>");
		sb.append("Release Date: ");
		sb.append(date);
		sb.append("</p></html>");
		return sb;
	}

	public void updateDirectorDisplays() {
		directorUpdateDisplay.setText(formatDirectorDisplay());
		if (directorSet) {
			directorUpdateDisplay.setFont(regularFont);
		} else {
			directorUpdateDisplay.setFont(italicFont);
		}
	}

	public String formatDirectorDisplay() {
		if (!directorSet) {
			newDirTitle.resetKeyboardActions();
			newDirDate.resetKeyboardActions();
			newDir.resetKeyboardActions();
			return "No director set";
		}
		String title = newDirTitle.getText();
		Date date = Date.valueOf(newDirDate.getText());
		String dir = newDir.getText();
		String empty = "";

		if ((title == null) || empty.equals(title)) {
			title = "<em>(no title specified)</em>";
		}
		if ((date == null) || empty.equals(date)) {
			date = new Date(0, 0, 0);
		}
		if ((dir == null) || empty.equals(dir)) {
			dir = "<em>(no director specified)</em>";
		}

		StringBuffer sb = new StringBuffer();
		doDirStringBufferThings(sb, title, date, dir);
		if (title != null && date != null && !empty.equals(date) && !empty.equals(title)
				&& dir != null && !empty.equals(dir)) {
			directorSet = true;
		}
		if (directorSet && title != "<em>(no title specified)</em>"
				&& date != null
				&& dir != "<em>(no director specified)</em>") {
			delegate.updatePictureDirector(title, date, dir);
		}
		return sb.toString();
	}

	public StringBuffer doDirStringBufferThings(StringBuffer sb, String title, Date date, String dir) {
		sb.append("<html><p align=center>");
		sb.append("Title: ");
		sb.append(title);
		sb.append("<br>");
		sb.append("Release Date: ");
		sb.append(date);
		sb.append("<br>");
		sb.append("New Director: ");
		sb.append(dir);
		sb.append("</p></html>");
		return sb;
	}

	/**
	 * Sets up the database to have a branch table with two tuples so we can insert/update/delete from it.
	 * Refer to the databaseSetup.sql file to determine what tuples are going to be in the table.
	 */
	public void setupDatabase(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;
		
		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;
		
		while(choice != 1 && choice != 2) {
			System.out.println("If you have a table called Branch in your database (capitialization of the name does not matter), it will be dropped and a new Branch table will be created.\nIf you want to proceed, enter 1; if you want to quit, enter 2.");
			
			choice = readInteger(false);
			
			if (choice != INVALID_INPUT) {
				switch (choice) {
				case 1:  
					delegate.databaseSetup(); 
					break;
				case 2:  
					handleQuitOption();
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.\n");
					break;
				}
			}
		}
	}

	public static void makeCompactGrid(Container parent,
									   int rows, int cols,
									   int initialX, int initialY,
									   int xpad, int ypad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout) parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		x = doThingsToX(x, parent, rows, cols, xpad);

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		y = doThingsToY(y, parent, rows, cols, ypad);
		//Set the parent's size.
		SpringLayout.Constraints parentConstraints = layout.getConstraints(parent);
		parentConstraints.setConstraint(SpringLayout.SOUTH, y);
		parentConstraints.setConstraint(SpringLayout.EAST, x);
	}

	//EFFECTS: used by makecompactgrid
	public static Spring doThingsToX(Spring x, Container parent, int rows, int cols, int xpad) {
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
						getConstraintsForCell(r, c, parent, cols).getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xpad)));
		}
		return x;
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			buttonCommand(e);
		}
	}


	//EFFECTS: processes buttons being pressed
	public void buttonCommand(ActionEvent e) {
		JButton buttonSource = (JButton) e.getSource();
		String buttonCommand = buttonSource.getText();
		if (buttonCommand == "Print All Picture Tuples") {
			printPictures(delegate.showPicture());
		}
		if (buttonCommand == "Print All User Tuples")
			printUsers(delegate.showUser());
		if (buttonCommand == "Print Series IDs") {
			printStrings(delegate.projectPictureSeriesID());
		}
		if (buttonCommand == "Find Average Picture Length by Genre") {
			printLengths(delegate.avgLengthByGenre());
		}
		if (buttonCommand == "Find Users Who Watched All Pictures") {
			printUsers(delegate.usersThatWatchedAllPictures());
		}
		if (buttonCommand == "Find the Genre with the Lowest Average Picture Length") {
			printStrings(delegate.genreWithLowestAvgLength());
		}
		if (buttonCommand == "Create Picture") {
			pictureSet = true;
			updatePictureDisplays();
		}
		if (buttonCommand == "Delete") {
			deleted = true;
			updateDelete();
		}
		if (buttonCommand == "Print Users") {
			userSet = true;
			updateUsersThatWatched();
		}
		if (buttonCommand == "Print Pictures") {
			numSet = true;
			updateNunPicsDisplays();
		}
		if (buttonCommand == "Update") {
			directorSet = true;
			updateDirectorDisplays();
		}
	}

	public void printPictures(PictureModel[] p) {
		for (int i = 0; i < p.length; i++) {
			PictureModel model = p[i];

			// simplified output formatting; truncation may occur
			output.append("%-10.10s");
			output.append(model.getTitle());
			output.append("%-20.20s");
			output.append(model.getDirector());
			output.append("%-20.20s");
			output.append(String.valueOf(model.getLength()));
			output.append("%-15.15s");
			output.append(String.valueOf(model.getReleaseDate()));
			if (model.getSeriesID() == 0) {
				output.append("%-15.15s");
				output.append(" ");
			} else {
				output.append("%-15.15s");
				output.append(String.valueOf(model.getSeriesID()));
			}

			output.append(newline);
		}
	}

	public void printUsers(UserModel[] u) {
		for (int i = 0; i < u.length; i++) {
			UserModel model = u[i];

			output.append("%-10.10s");
			output.append(model.getUsername());
			output.append("%-20.20s");
			output.append(model.getEmail());
			if (model.getFavGenreCategory() == null) {
				output.append("%-20.20s");
				output.append(" ");
			} else {
				output.append("%-20.20s");
				output.append(model.getFavGenreCategory());
			}
			output.append("%-15.15s");
			output.append(String.valueOf(model.getWatchlistID()));
			if (model.getHistoryID() == 0) {
				output.append("%-15.15s");
				output.append(" ");
			} else {
				output.append("%-15.15s");
				output.append(String.valueOf(model.getHistoryID()));
			}

			output.append(newline);
		}
	}

	public void printStrings(String[] s) {
		for (int i = 0; i < s.length; i++) {
			output.append(s[i] + ", ");
		}

	}

	public void printLengths(GenreAvgLengthModel[] g) {
		for (int i = 0; i < g.length; i++) {
			GenreAvgLengthModel model = g[i];

			output.append("%-10.10s");
			output.append(model.getGenre());
			output.append("%-20.20s");
			output.append(String.valueOf(model.getAvgLength()));

		}
		output.append(newline);
	}

	@Override
	public void focusGained(FocusEvent e) {
		Component c = e.getComponent();
		if (c instanceof JFormattedTextField) {
			selectItLater(c);
		} else if (c instanceof JTextField) {
			((JTextField) c).selectAll();
		}
	}

	public void focusLost(FocusEvent e) {
	} //ignore


	public void selectItLater(Component c) {
		if (c instanceof JFormattedTextField) {
			final JFormattedTextField ftf = (JFormattedTextField) c;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ftf.selectAll();
				}
			});
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		String s = ("Command detected!"
				+ newline
				+ "  Source: " + source.getText()
				+ "  Command: " + getClassName(source)
				+ newline
				+ "  New State: "
				+ ((e.getStateChange() == ItemEvent.SELECTED)
				? "selected" : "unselected"));
		output.append(s + newline);
		output.setCaretPosition(output.getDocument().getLength());
	}

	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex + 1);
	}


	//EFFECTS: used by makecompactgrid
	public static Spring doThingsToY(Spring y, Container parent, int rows, int cols, int ypad) {
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
						getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(ypad)));
		}
		return y;
	}

	private static SpringLayout.Constraints getConstraintsForCell(
			int row, int col,
			Container parent,
			int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}



	// * Displays simple text interface

	public void showMainMenu(TerminalTransactionsDelegate delegate) { //todo
		this.delegate = delegate;
		
	    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;
		
		while (choice != 5) {
			System.out.println();
			System.out.println("1. Insert branch");
			System.out.println("2. Delete branch");
			System.out.println("3. Update branch name");
			System.out.println("4. Show branch");
			System.out.println("5. Quit");
			System.out.print("Please choose one of the above 5 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
				case 1:  
					handleInsertOption(); 
					break;
				case 2:  
					handleDeleteOption(); 
					break;
				case 3: 
					handleUpdateOption();
					break;
				case 4:  
					delegate.showPicture();
					break;
				case 5:
					handleQuitOption();
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
					break;
				}
			}
		}		
	}
	
	private void handleDeleteOption() {
		String branchId = INVALID_STRING;
        Date date = INVALID_DATE;
		while (branchId.equals(INVALID_STRING)) {
			System.out.print("Please enter the branch ID you wish to delete: ");
			branchId = readLine();
			if (!branchId.equals(INVALID_STRING)) {
			    while (date == INVALID_DATE) {
                    System.out.println("Please enter year");
                    int year = readInteger(false);
                    System.out.println("Please enter Month");
                    int month = readInteger(false);
                    System.out.println("Please enter day");
                    int day = readInteger(false);
                    date = new Date(year,month,day);

                    delegate.deletePicture(branchId, date);
                }

			}
		}
	}
	
	private void handleInsertOption() {
        String title = null;
        while (title == null || title.length() <= 0) {
            System.out.print("Please enter the branch title you wish to insert: ");
            title = readLine().trim();
        }
		
		Date date = null;
		while (date == null) {
            System.out.println("Please enter year");
            int year = readInteger(false);
            System.out.println("Please enter Month");
            int month = readInteger(false);
            System.out.println("Please enter day");
            int day = readInteger(false);
            date = new Date(year,month,day);
		}
		
		// branch address is allowed to be null so we don't need to repeatedly ask for the address
		int length = INVALID_INPUT;
        while (length == INVALID_INPUT) {
            System.out.print("Please enter the branch length you wish to insert: ");
            length = readInteger(true);
        }
		
		String director = null;
        while (director == null || director.length() <= 0) {
            System.out.print("Please enter the branch director you wish to insert: ");
            director = readLine().trim();
        }
		
		int seriesID = INVALID_INPUT;
		while (seriesID == INVALID_INPUT) {
			System.out.print("Please enter the branch seriesid you wish to insert: ");
			seriesID = readInteger(true);
		}
		
		PictureModel model = new PictureModel(title,
											date,
											length,
											director,
											seriesID,
		"");
		delegate.insertPicture(model);
	}
	
	private void handleQuitOption() {
		System.out.println("Good Bye!");
		
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				System.out.println("IOException!");
			}
		}
		
		delegate.terminalTransactionsFinished();
	}
	
	private void handleUpdateOption() {

        String title = null;
        while (title == null || title.length() <= 0) {
            System.out.print("Please enter the branch title you wish to update: ");
            title = readLine().trim();
        }

		Date date = INVALID_DATE;
		while (date == INVALID_DATE) {
            System.out.println("Please enter year");
            int year = readInteger(false);
            System.out.println("Please enter Month");
            int month = readInteger(false);
            System.out.println("Please enter day");
            int day = readInteger(false);
            date = new Date(year,month,day);
		}

        String director = null;
        while (director == null || director.length() <= 0) {
            System.out.print("Please enter the branch director you wish to update: ");
            director = readLine().trim();
        }
		


		delegate.updatePictureDirector(title, date, director);
	}
	
	private int readInteger(boolean allowEmpty) {
		String line = null;
		int input = INVALID_INPUT;
		try {
			line = bufferedReader.readLine();
			input = Integer.parseInt(line);
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		} catch (NumberFormatException e) {
			if (allowEmpty && line.length() == 0) {
				input = EMPTY_INPUT;
			} else {
				System.out.println(WARNING_TAG + " Your input was not an integer");
			}
		}
		return input;
	}
	
	private String readLine() {
		String result = null;
		try {
			result = bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return result;
	}
}
