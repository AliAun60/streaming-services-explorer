package cse385;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class finalProject extends JFrame {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/streaming_services";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "1234";

	private JTable resultsTable, watchlistTable;
	private DefaultTableModel tableModel, watchlistTableModel;
	private JTextField searchField, recommendationsField, yearField;
	private JComboBox<String> platformDropdown, typeDropdown, genreDropdown;
	private JCheckBox countryCheckbox, castCheckbox, directorCheckbox;
	private JTextField startYearField, endYearField;
	private JComboBox<String> platformGenreDropdown;

	public finalProject() {
		setTitle("Streaming Services Explorer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1750, 1100);
		setLayout(new BorderLayout());
		setResizable(true);

		try {
			UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Main Panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(mainPanel);

		// Title Header
		JLabel titleLabel = new JLabel("Streaming Services Explorer");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		// Search Panel
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
		searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.add(searchPanel, BorderLayout.WEST);

		// Title Search Section
		JPanel titleSearchPanel = createTitleSearchPanel();
		searchPanel.add(titleSearchPanel);
		searchPanel.add(new JSeparator(SwingConstants.HORIZONTAL)); // Divider

		// Platform Search Section
		JPanel platformSearchPanel = createPlatformSearchPanel();
		searchPanel.add(platformSearchPanel);
		searchPanel.add(new JSeparator(SwingConstants.HORIZONTAL)); // Divider

		// Genre Search Section
		JPanel genreSearchPanel = createGenreSearchPanel();
		searchPanel.add(genreSearchPanel);
		searchPanel.add(new JSeparator(SwingConstants.HORIZONTAL)); // Divider

		// Genre Per Platform Search Section
		JPanel genrePerPlatformPanel = createGenrePerPlatformPanel();
		searchPanel.add(genrePerPlatformPanel);
		searchPanel.add(new JSeparator(SwingConstants.HORIZONTAL)); // Divider

		// Random Titles Section
		JPanel randomTitlesPanel = createRandomTitlesPanel();
		searchPanel.add(randomTitlesPanel);

		// Results Table
		tableModel = new DefaultTableModel();
		resultsTable = new JTable(tableModel);
		resultsTable.setFillsViewportHeight(true);
		resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
		resultsTable.getTableHeader()
				.setFont(new Font("Arial", Font.BOLD, 14));
		JScrollPane tableScrollPane = new JScrollPane(resultsTable);
		tableScrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

		// Watchlist Buttons
		JPanel watchlistPanel = new JPanel(new FlowLayout());
		JButton addToWatchlistButton = new JButton("Add to Watchlist");
		JButton viewWatchlistButton = new JButton("View Watchlist");

		addToWatchlistButton.addActionListener(e -> addToWatchlist());
		viewWatchlistButton.addActionListener(e -> viewWatchlist());

		watchlistPanel.add(addToWatchlistButton);
		watchlistPanel.add(viewWatchlistButton);

		// Add the table and the modified watchlistPanel to the mainPanel
		mainPanel.add(tableScrollPane, BorderLayout.CENTER);
		mainPanel.add(watchlistPanel, BorderLayout.SOUTH);

		setVisible(true);

	}

	private JPanel createTitleSearchPanel() {
		JPanel titleSearchPanel = new JPanel(new BorderLayout());
		JLabel titleSearchLabel = new JLabel("Search by Show Name");
		titleSearchLabel.setFont(new Font("Arial", Font.BOLD, 16));
		titleSearchLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
		titleSearchPanel.add(titleSearchLabel, BorderLayout.NORTH);

		JPanel titleSearchContent = new JPanel(new FlowLayout());
		searchField = new JTextField(20);
		recommendationsField = new JTextField(5);
		JButton searchButton = new JButton("Search");
		searchButton.setToolTipText("Search titles and descriptions");
		searchButton.addActionListener(e -> executeTitleSearchQuery());
		titleSearchContent.add(new JLabel("Enter Title:"));
		titleSearchContent.add(searchField);
		titleSearchContent.add(new JLabel("Recommendations:"));
		titleSearchContent.add(recommendationsField);
		titleSearchContent.add(searchButton);
		titleSearchPanel.add(titleSearchContent, BorderLayout.CENTER);

		return titleSearchPanel;
	}

	private JPanel createPlatformSearchPanel() {
		JPanel platformSearchPanel = new JPanel(new BorderLayout());
		JLabel platformSearchLabel = new JLabel(
				"Search by Platform, Type, and Year");
		platformSearchLabel.setFont(new Font("Arial", Font.BOLD, 16));
		platformSearchLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
		platformSearchPanel.add(platformSearchLabel, BorderLayout.NORTH);

		JPanel platformSearchContent = new JPanel(new FlowLayout());
		platformDropdown = new JComboBox<>(new String[] { "Select Platform",
				"Netflix", "Hulu", "Amazon Prime", "Disney+" });
		typeDropdown = new JComboBox<>(
				new String[] { "Select Type", "Movie", "TV Show" });
		yearField = new JTextField(5);
		JButton platformSearchButton = new JButton("Search by Platform");
		platformSearchButton
				.setToolTipText("Search by platform, type, and release year");
		platformSearchButton
				.addActionListener(e -> executePlatformYearSearchQuery());
		platformSearchContent.add(new JLabel("Platform:"));
		platformSearchContent.add(platformDropdown);
		platformSearchContent.add(new JLabel("Type:"));
		platformSearchContent.add(typeDropdown);
		platformSearchContent.add(new JLabel("Year:"));
		platformSearchContent.add(yearField);
		platformSearchContent.add(platformSearchButton);
		platformSearchPanel.add(platformSearchContent, BorderLayout.CENTER);

		return platformSearchPanel;
	}

	private JPanel createGenreSearchPanel() {
		JPanel genreSearchPanel = new JPanel(new BorderLayout());
		JLabel genreSearchLabel = new JLabel(
				"Search by Genre with Optional Details");
		genreSearchLabel.setFont(new Font("Arial", Font.BOLD, 16));
		genreSearchLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
		genreSearchPanel.add(genreSearchLabel, BorderLayout.NORTH);

		JPanel genreSearchContent = new JPanel(new FlowLayout());
		genreDropdown = new JComboBox<>(new String[] { "Select Genre" });
		populateGenres();
		countryCheckbox = new JCheckBox("Country");
		castCheckbox = new JCheckBox("Cast");
		directorCheckbox = new JCheckBox("Director");
		genreDropdown.addActionListener(e -> updateTableForGenre());
		countryCheckbox.addActionListener(e -> toggleCountryColumn());
		castCheckbox.addActionListener(e -> toggleCastColumn());
		directorCheckbox.addActionListener(e -> toggleDirectorColumn());
		genreSearchContent.add(new JLabel("Genre:"));
		genreSearchContent.add(genreDropdown);
		genreSearchContent.add(countryCheckbox);
		genreSearchContent.add(castCheckbox);
		genreSearchContent.add(directorCheckbox);
		genreSearchPanel.add(genreSearchContent, BorderLayout.CENTER);

		return genreSearchPanel;
	}

	private JPanel createGenrePerPlatformPanel() {
		JPanel genrePerPlatformPanel = new JPanel(new BorderLayout());
		JLabel genrePerPlatformLabel = new JLabel(
				"Find Genres Per Platform Over Time");
		genrePerPlatformLabel.setFont(new Font("Arial", Font.BOLD, 16));
		genrePerPlatformLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
		genrePerPlatformPanel.add(genrePerPlatformLabel, BorderLayout.NORTH);

		JPanel genrePerPlatformContent = new JPanel(new FlowLayout());
		platformGenreDropdown = new JComboBox<>(
				new String[] { "Select Platform", "Netflix", "Hulu",
						"Amazon Prime", "Disney+" });
		startYearField = new JTextField(5);
		endYearField = new JTextField(5);
		JButton genreSearchButton = new JButton("Find Genres");
		genreSearchButton
				.addActionListener(e -> executeGenrePerPlatformQuery());
		genrePerPlatformContent.add(new JLabel("Platform:"));
		genrePerPlatformContent.add(platformGenreDropdown);
		genrePerPlatformContent.add(new JLabel("Start Year:"));
		genrePerPlatformContent.add(startYearField);
		genrePerPlatformContent.add(new JLabel("End Year:"));
		genrePerPlatformContent.add(endYearField);
		genrePerPlatformContent.add(genreSearchButton);
		genrePerPlatformPanel.add(genrePerPlatformContent,
				BorderLayout.CENTER);

		return genrePerPlatformPanel;
	}

	private JPanel createRandomTitlesPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		JLabel label = new JLabel("Get Random Titles");
		label.setFont(new Font("Arial", Font.BOLD, 16));
		label.setBorder(new EmptyBorder(10, 0, 10, 0));
		panel.add(label, BorderLayout.NORTH);
		;

		JButton randomButton = new JButton("Surprise me!");
		randomButton.setToolTipText(
				"Display 5 random titles with genre and platform.");
		randomButton.addActionListener(e -> displayRandomTitles());

		panel.add(randomButton);
		return panel;
	}

	private void populateGenres() {
		String query = "SELECT name FROM genre";
		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				genreDropdown.addItem(resultSet.getString("name"));
			}

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Error fetching genres: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void executeTitleSearchQuery() {
		String title = searchField.getText().trim();
		int recommendations = 100000;

		try {
			if (!recommendationsField.getText().isEmpty()) {
				recommendations = Integer
						.parseInt(recommendationsField.getText().trim());
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Please enter a valid number for recommendations.",
					"Input Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String query = "SELECT title AS 'Show Title', description AS 'Short Description' FROM content WHERE title LIKE ? LIMIT ?";

		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query)) {

			preparedStatement.setString(1, "%" + title + "%");
			preparedStatement.setInt(2, recommendations);

			updateTable(preparedStatement);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void executePlatformYearSearchQuery() {
		String platform = platformDropdown.getSelectedItem().toString();
		String type = typeDropdown.getSelectedItem().toString();
		String year = yearField.getText().trim();

		if (platform.equals("Select Platform") || type.equals("Select Type")
				|| year.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Please select a platform, type, and enter a valid year.",
					"Input Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String query = """
				SELECT c.title AS 'Show Title'
				FROM content c
				JOIN content_platform cp ON c.content_id = cp.content_id
				JOIN platform p ON cp.platform_id = p.platform_id
				WHERE c.release_year = ? AND c.movie_or_series = ? AND p.name LIKE ?
				""";

		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query)) {

			preparedStatement.setInt(1, Integer.parseInt(year));
			preparedStatement.setString(2, type);
			preparedStatement.setString(3, "%" + platform + "%");

			updateTable(preparedStatement);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void updateTableForGenre() {
		String genre = genreDropdown.getSelectedItem().toString();
		if (genre.equals("Select Genre")) {
			tableModel.setRowCount(0);
			tableModel.setColumnCount(0);
			return;
		}

		String query = """
				SELECT c.title AS 'Show Title'
				FROM content c
				JOIN content_genre cg ON c.content_id = cg.content_id
				JOIN genre g ON cg.genre_id = g.genre_id
				WHERE g.name LIKE ?
				""";

		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query)) {

			preparedStatement.setString(1, "%" + genre + "%");
			ResultSet resultSet = preparedStatement.executeQuery();

			tableModel.setRowCount(0);
			tableModel.setColumnCount(0);
			tableModel.addColumn("Show Title");

			while (resultSet.next()) {
				tableModel.addRow(
						new Object[] { resultSet.getString("Show Title") });
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void toggleCountryColumn() {
		int countryColumnIndex = tableModel.findColumn("Filming Country");

		if (countryCheckbox.isSelected()) {

			if (countryColumnIndex == -1) {
				tableModel.addColumn("Filming Country");
			}

			StringBuilder titleList = new StringBuilder();
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String title = (String) tableModel.getValueAt(i,
						tableModel.findColumn("Show Title"));
				titleList.append("'").append(title.replace("'", "\\'"))
						.append("', ");
			}

			if (titleList.length() > 0) {
				titleList.setLength(titleList.length() - 2);
			}

			String query = """
					SELECT c.title AS 'Show Title', GROUP_CONCAT(co.country SEPARATOR ', ') AS 'Filming Country'
					FROM content c
					JOIN content_country cc ON c.content_id = cc.content_id
					JOIN country co ON cc.country_id = co.country_id
					WHERE c.title IN (%s)
					GROUP BY c.title
					"""
					.formatted(titleList);

			try (Connection connection = DriverManager.getConnection(JDBC_URL,
					USERNAME, PASSWORD);
					PreparedStatement preparedStatement = connection
							.prepareStatement(query)) {

				ResultSet resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					String title = resultSet.getString("Show Title");
					String country = resultSet.getString("Filming Country");

					for (int i = 0; i < tableModel.getRowCount(); i++) {
						if (tableModel
								.getValueAt(i,
										tableModel.findColumn("Show Title"))
								.equals(title)) {
							tableModel.setValueAt(country, i,
									tableModel.findColumn("Filming Country"));
							break;
						}
					}
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		} else {
			if (countryColumnIndex != -1) {
				tableModel.setColumnCount(tableModel.getColumnCount() - 1);
			}
		}
	}

	private void toggleCastColumn() {
		int castColumnIndex = tableModel.findColumn("Actor Cast");

		if (castCheckbox.isSelected()) {

			if (castColumnIndex == -1) {
				tableModel.addColumn("Actor Cast");
			}

			StringBuilder titleList = new StringBuilder();
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String title = (String) tableModel.getValueAt(i,
						tableModel.findColumn("Show Title"));
				titleList.append("'").append(title.replace("'", "\\'"))
						.append("', ");
			}

			if (titleList.length() > 0) {
				titleList.setLength(titleList.length() - 2);
			}

			String query = """
					SELECT c.title AS 'Show Title', GROUP_CONCAT(a.name SEPARATOR ', ') AS 'Actor Cast'
					FROM content c
					JOIN content_cast cc ON c.content_id = cc.content_id
					JOIN actors a ON cc.cast_id = a.cast_id
					WHERE c.title IN (%s)
					GROUP BY c.title
					"""
					.formatted(titleList);

			try (Connection connection = DriverManager.getConnection(JDBC_URL,
					USERNAME, PASSWORD);
					PreparedStatement preparedStatement = connection
							.prepareStatement(query)) {

				ResultSet resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					String title = resultSet.getString("Show Title");
					String cast = resultSet.getString("Actor Cast");

					for (int i = 0; i < tableModel.getRowCount(); i++) {
						if (tableModel
								.getValueAt(i,
										tableModel.findColumn("Show Title"))
								.equals(title)) {
							tableModel.setValueAt(cast, i,
									tableModel.findColumn("Actor Cast"));
							break;
						}
					}
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		} else {

			if (castColumnIndex != -1) {
				tableModel.setColumnCount(tableModel.getColumnCount() - 1);
			}
		}
	}

	private void toggleDirectorColumn() {
		int directorColumnIndex = tableModel.findColumn("Director");

		if (directorCheckbox.isSelected()) {

			if (directorColumnIndex == -1) {
				tableModel.addColumn("Director");
			}

			StringBuilder titleList = new StringBuilder();
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String title = (String) tableModel.getValueAt(i,
						tableModel.findColumn("Show Title"));
				titleList.append("'").append(title.replace("'", "\\'"))
						.append("', ");
			}

			if (titleList.length() > 0) {
				titleList.setLength(titleList.length() - 2);
			}

			String query = """
					SELECT c.title AS 'Show Title', GROUP_CONCAT(d.name SEPARATOR ', ') AS Director
					FROM content c
					JOIN content_director cd ON c.content_id = cd.content_id
					JOIN director d ON cd.director_id = d.director_id
					WHERE c.title IN (%s)
					GROUP BY c.title
					"""
					.formatted(titleList);

			try (Connection connection = DriverManager.getConnection(JDBC_URL,
					USERNAME, PASSWORD);
					PreparedStatement preparedStatement = connection
							.prepareStatement(query)) {

				ResultSet resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					String title = resultSet.getString("Show Title");
					String director = resultSet.getString("Director");

					for (int i = 0; i < tableModel.getRowCount(); i++) {
						if (tableModel
								.getValueAt(i,
										tableModel.findColumn("Show Title"))
								.equals(title)) {
							tableModel.setValueAt(director, i,
									tableModel.findColumn("Director"));
							break;
						}
					}
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		} else {

			if (directorColumnIndex != -1) {
				tableModel.setColumnCount(tableModel.getColumnCount() - 1);
			}
		}
	}

	private void executeGenrePerPlatformQuery() {
		String platform = platformGenreDropdown.getSelectedItem().toString();
		String startYear = startYearField.getText().trim();
		String endYear = endYearField.getText().trim();

		if (platform.equals("Select Platform") || startYear.isEmpty()
				|| endYear.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Please select a platform and enter a valid time range.",
					"Input Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {

			Integer.parseInt(startYear);
			Integer.parseInt(endYear);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Please enter valid numeric years for the time range.",
					"Input Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String query = """
				SELECT g.name AS Genre, COUNT(*) AS 'Number of shows over the specified Years'
				FROM content c
				JOIN content_genre cg ON c.content_id = cg.content_id
				JOIN genre g ON cg.genre_id = g.genre_id
				JOIN content_platform cp ON c.content_id = cp.content_id
				JOIN platform p ON cp.platform_id = p.platform_id
				WHERE p.name LIKE ? AND c.release_year BETWEEN ? AND ?
				GROUP BY g.name
				ORDER BY 'Number of shows over the specified Years' DESC;
				""";

		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query)) {

			// Set query parameters
			preparedStatement.setString(1, "%" + platform + "%");
			preparedStatement.setString(2, startYear);
			preparedStatement.setString(3, endYear);

			updateTable(preparedStatement);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Error executing query: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addToWatchlist() {
		int selectedRow = resultsTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this,
					"Please select a movie or TV show to add to the watchlist.");
			return;
		}

		String title = (String) resultsTable.getValueAt(selectedRow,
				resultsTable.getColumn("Show Title").getModelIndex());

		String query = "INSERT INTO watchlist (title) VALUES (?)";
		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query)) {

			preparedStatement.setString(1, title);
			preparedStatement.executeUpdate();

			JOptionPane.showMessageDialog(this,
					"Added to watchlist successfully!");
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Error adding to watchlist: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void viewWatchlist() {
		String query = "SELECT title AS 'Title', added_at AS 'Added At' FROM watchlist ORDER BY added_at DESC";
		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			if (watchlistTableModel == null) {
				watchlistTableModel = new DefaultTableModel();
				watchlistTable = new JTable(watchlistTableModel);
			}

			int columnCount = resultSet.getMetaData().getColumnCount();
			String[] columnNames = new String[columnCount + 1];
			for (int i = 1; i <= columnCount; i++) {
				columnNames[i - 1] = resultSet.getMetaData().getColumnLabel(i);
			}
			columnNames[columnCount] = "Remove";

			List<String[]> rows = new ArrayList<>();
			while (resultSet.next()) {
				String[] row = new String[columnCount + 1];
				for (int i = 1; i <= columnCount; i++) {
					row[i - 1] = resultSet.getString(i);
				}
				row[columnCount] = "Remove";
				rows.add(row);
			}

			watchlistTableModel.setDataVector(rows.toArray(new Object[0][0]),
					columnNames);

			watchlistTable.getColumn("Remove")
					.setCellRenderer(new ButtonRenderer());
			watchlistTable.getColumn("Remove")
					.setCellEditor(new ButtonEditor(new JCheckBox()));

			JScrollPane scrollPane = new JScrollPane(watchlistTable);
			JOptionPane.showMessageDialog(this, scrollPane, "Your Watchlist",
					JOptionPane.PLAIN_MESSAGE);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Error fetching watchlist: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value == null) {
				setText("");
			} else {
				setText("Remove");
			}
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;
		private String label;
		private int row;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(e -> fireEditingStopped());
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.row = row;
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			return button;
		}

		public Object getCellEditorValue() {
			if (row == -1) {

				JOptionPane.showMessageDialog(null, "No row selected.");
				return label;
			}

			String title = (String) watchlistTable.getValueAt(row, 0);
			removeFromWatchlist(title);
			return label;
		}
	}

	private void removeFromWatchlist(String title) {
		if (title == null || title.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Invalid selection. Cannot remove from watchlist.");
			return;
		}

		String query = "DELETE FROM watchlist WHERE title = ?";
		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query)) {

			preparedStatement.setString(1, title);
			preparedStatement.executeUpdate();
			JOptionPane.showMessageDialog(this,
					"Removed from watchlist successfully!");

			viewWatchlist();

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Error removing from watchlist: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void displayRandomTitles() {
		String query = """
				SELECT
				    c.title AS 'Show Title',
				    g.name AS 'Genre',
				    p.name AS 'Platform'
				FROM
				    content c
				JOIN
				    content_genre cg ON c.content_id = cg.content_id
				JOIN
				    genre g ON cg.genre_id = g.genre_id
				JOIN
				    content_platform cp ON c.content_id = cp.content_id
				JOIN
				    platform p ON cp.platform_id = p.platform_id
				ORDER BY
				    RAND()
				LIMIT 5;
				""";

		try (Connection connection = DriverManager.getConnection(JDBC_URL,
				USERNAME, PASSWORD);
				PreparedStatement preparedStatement = connection
						.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			int columnCount = resultSet.getMetaData().getColumnCount();
			String[] columnNames = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				columnNames[i - 1] = resultSet.getMetaData().getColumnLabel(i);
			}

			List<String[]> rows = new ArrayList<>();
			while (resultSet.next()) {
				String[] row = new String[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					row[i - 1] = resultSet.getString(i);
				}
				rows.add(row);
			}

			tableModel.setDataVector(rows.toArray(new Object[0][0]),
					columnNames);

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Error fetching random titles: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateTable(PreparedStatement preparedStatement) {
		try (ResultSet resultSet = preparedStatement.executeQuery()) {

			int columnCount = resultSet.getMetaData().getColumnCount();
			String[] columnNames = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				columnNames[i - 1] = resultSet.getMetaData().getColumnLabel(i);
			}

			List<String[]> rows = new ArrayList<>();
			while (resultSet.next()) {
				String[] row = new String[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					row[i - 1] = resultSet.getString(i);
				}
				rows.add(row);
			}

			tableModel.setDataVector(rows.toArray(new Object[0][0]),
					columnNames);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(finalProject::new);
	}
}
