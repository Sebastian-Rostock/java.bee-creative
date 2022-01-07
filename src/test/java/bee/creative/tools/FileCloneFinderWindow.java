package bee.creative.tools;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import javax.swing.UIManager;

class FileCloneFinderWindow {

	JFrame frame;

	JTextArea sourceList;

	JTextArea targetList;

	JSpinner hashSize;

	JSpinner testSize;

	JTextField pathLabel;

	JSpinner pathIndex;

	JButton scanButton;

	JButton testButton;

	JButton moveButton;

	JLabel progressInfo;

	FileCloneFinderWindow() {
		this.initialize();
		this.frame.setVisible(true);
	}

	void initialize() {
		this.frame = new JFrame("Dateiduplikatfinder");

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);

		final JPanel sourcePage = new JPanel();
		sourcePage.setBackground(UIManager.getColor("TabbedPane.highlight"));
		tabbedPane.addTab("Eingabe", null, sourcePage, null);
		final SpringLayout sl_sourcePage = new SpringLayout();
		sourcePage.setLayout(sl_sourcePage);

		final JLabel sourceInfo = new JLabel("<html>" //
			+ "Die abzugleichenden Dateien und Verzeichnisse können hier eingefügt bzw. fallengelassen werden. " //
			+ "Beim Aufbereiten der Eingabe werden Verzeichnisse durch alle darin enthatenen Dateien ersetzt. " //
			+ "Beim Aufbereiten der Ausgabe werden Verzeichnisse hingegen ignoriert. " //
			+ "Relative Pfadangaben werden ebenfalls ignoriert." //
			+ "</html>");
		sl_sourcePage.putConstraint(SpringLayout.NORTH, sourceInfo, 5, SpringLayout.NORTH, sourcePage);
		sl_sourcePage.putConstraint(SpringLayout.WEST, sourceInfo, 5, SpringLayout.WEST, sourcePage);
		sl_sourcePage.putConstraint(SpringLayout.EAST, sourceInfo, -5, SpringLayout.EAST, sourcePage);
		sourcePage.add(sourceInfo);

		this.sourceList = new JTextArea();

		final JScrollPane sourcePane = new JScrollPane(this.sourceList);
		sourcePage.add(sourcePane);

		sl_sourcePage.putConstraint(SpringLayout.NORTH, sourcePane, 5, SpringLayout.SOUTH, sourceInfo);
		sl_sourcePage.putConstraint(SpringLayout.WEST, sourcePane, 0, SpringLayout.WEST, sourceInfo);
		sl_sourcePage.putConstraint(SpringLayout.SOUTH, sourcePane, -5, SpringLayout.SOUTH, sourcePage);
		sl_sourcePage.putConstraint(SpringLayout.EAST, sourcePane, 0, SpringLayout.EAST, sourceInfo);

		final JPanel targetPage = new JPanel();
		targetPage.setBackground(UIManager.getColor("TabbedPane.highlight"));
		tabbedPane.addTab("Ausgabe", null, targetPage, null);
		final SpringLayout sl_targetPage = new SpringLayout();
		targetPage.setLayout(sl_targetPage);

		final JLabel targetInfo = new JLabel("<html>" //
			+ "Hier werden zu jeder ersten duplikaten Datei aus der Eingabe die anderen dazu inhaltlich gleichen Dateien aufgelistet. " //
			+ "Beim Verscheiben der Ausgaben werden nur die Zeilen betrachtet, die aus einem absoluten Quelldateipfad und einem absoluten Zieldateipfad bestehen. "
			+ "Beide Pfade müssen mit einem Tabulator oder einer schließenden spitzen Klammer voneinander getrennt sein. " //
			+ "Sie dürfen zudem in beliebig viel Leerraum eingeschlossen sein." //
			+ "</html>");
		sl_targetPage.putConstraint(SpringLayout.NORTH, targetInfo, 5, SpringLayout.NORTH, targetPage);
		sl_targetPage.putConstraint(SpringLayout.WEST, targetInfo, 5, SpringLayout.WEST, targetPage);
		sl_targetPage.putConstraint(SpringLayout.EAST, targetInfo, -5, SpringLayout.EAST, targetPage);
		targetPage.add(targetInfo);

		this.targetList = new JTextArea();
		final JScrollPane targetPane = new JScrollPane(this.targetList);
		targetPage.add(targetPane);

		sl_targetPage.putConstraint(SpringLayout.NORTH, targetPane, 5, SpringLayout.SOUTH, targetInfo);
		sl_targetPage.putConstraint(SpringLayout.WEST, targetPane, 0, SpringLayout.WEST, targetInfo);
		sl_targetPage.putConstraint(SpringLayout.SOUTH, targetPane, -5, SpringLayout.SOUTH, targetPage);
		sl_targetPage.putConstraint(SpringLayout.EAST, targetPane, 0, SpringLayout.EAST, targetInfo);

		final JPanel optionPage = new JPanel();
		optionPage.setBackground(UIManager.getColor("TabbedPane.highlight"));
		tabbedPane.addTab("Optionen", null, optionPage, null);
		final SpringLayout sl_optionPage = new SpringLayout();
		optionPage.setLayout(sl_optionPage);

		final JLabel optionInfo = new JLabel("<html>" //
			+ "Die Dateien aus der Eingabe werden zunächst bezüglich ihrer Dateigröße partitioniert. " //
			+ "Die Dateien innerhalb einer Dateigrößenpartition werden dann bezüglich ihres SHA-256-Streuwerts partitioniert. " //
			+ "Dieser Streuwert wird aus höchstens der unten angegebenen Anzanl an Bytes ab dem Dateibeginn berechnet. " //
			+ "Schließlich werden die Dateien innerhalb einer Streuwertpartition nach ihrem Dateiinhalt partitioniert. " //
			+ "Dabei wird höchstens die unten angegebene Anzanl an Bytes ab dem Dateibeginn betrachtet. " //
			+ "Die erste Datei einer Dateiinhaltspartition ... " //
			+ "</html>");
		sl_optionPage.putConstraint(SpringLayout.NORTH, optionInfo, 5, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, optionInfo, 5, SpringLayout.WEST, optionPage);
		sl_optionPage.putConstraint(SpringLayout.EAST, optionInfo, -5, SpringLayout.EAST, optionPage);
		optionPage.add(optionInfo);

		final JLabel hashSizeInfo = new JLabel("Puffergröße für Streuwert");
		sl_optionPage.putConstraint(SpringLayout.NORTH, hashSizeInfo, 5, SpringLayout.SOUTH, optionInfo);
		sl_optionPage.putConstraint(SpringLayout.WEST, hashSizeInfo, 0, SpringLayout.WEST, optionInfo);
		optionPage.add(hashSizeInfo);

		this.hashSize = new JSpinner();
		this.hashSize.setModel(new SpinnerNumberModel(new Long(1048576), new Long(0), null, new Long(1048576)));

		sl_optionPage.putConstraint(SpringLayout.NORTH, this.hashSize, 5, SpringLayout.SOUTH, optionInfo);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.hashSize, 0, SpringLayout.EAST, optionInfo);
		optionPage.add(this.hashSize);

		final JLabel testSizeInfo = new JLabel("Puffergröße für Dateivergleich");
		sl_optionPage.putConstraint(SpringLayout.WEST, this.hashSize, 5, SpringLayout.EAST, testSizeInfo);
		sl_optionPage.putConstraint(SpringLayout.WEST, testSizeInfo, 0, SpringLayout.WEST, optionInfo);
		optionPage.add(testSizeInfo);

		this.testSize = new JSpinner();
		this.testSize.setModel(new SpinnerNumberModel(new Long(20971520), new Long(0), null, new Long(1048576)));
		sl_optionPage.putConstraint(SpringLayout.NORTH, testSizeInfo, 0, SpringLayout.NORTH, this.testSize);
		sl_optionPage.putConstraint(SpringLayout.NORTH, this.testSize, 5, SpringLayout.SOUTH, this.hashSize);
		sl_optionPage.putConstraint(SpringLayout.WEST, this.testSize, 0, SpringLayout.WEST, this.hashSize);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.testSize, 0, SpringLayout.EAST, this.hashSize);

		optionPage.add(this.testSize);

		final JLabel pathLabelInfo = new JLabel("Verzeichnis zur Pfadergänzung");
		sl_optionPage.putConstraint(SpringLayout.WEST, pathLabelInfo, 0, SpringLayout.WEST, optionInfo);
		optionPage.add(pathLabelInfo);

		this.pathLabel = new JTextField();
		sl_optionPage.putConstraint(SpringLayout.NORTH, pathLabelInfo, 0, SpringLayout.NORTH, this.pathLabel);
		sl_optionPage.putConstraint(SpringLayout.NORTH, this.pathLabel, 5, SpringLayout.SOUTH, this.testSize);
		sl_optionPage.putConstraint(SpringLayout.WEST, this.pathLabel, 0, SpringLayout.WEST, this.hashSize);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.pathLabel, 0, SpringLayout.EAST, optionInfo);
		this.pathLabel.setText("DUPLIKAT");
		optionPage.add(this.pathLabel);
		this.pathLabel.setColumns(10);

		final JLabel pathIndexInfo = new JLabel("Position zur Pfadergänzung");
		sl_optionPage.putConstraint(SpringLayout.WEST, pathIndexInfo, 0, SpringLayout.WEST, optionInfo);
		optionPage.add(pathIndexInfo);

		this.pathIndex = new JSpinner();
		this.pathIndex.setModel(new SpinnerNumberModel(-1, -10, 10, 1));
		sl_optionPage.putConstraint(SpringLayout.NORTH, pathIndexInfo, 0, SpringLayout.NORTH, this.pathIndex);
		sl_optionPage.putConstraint(SpringLayout.NORTH, this.pathIndex, 5, SpringLayout.SOUTH, this.pathLabel);
		sl_optionPage.putConstraint(SpringLayout.WEST, this.pathIndex, 0, SpringLayout.WEST, this.hashSize);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.pathIndex, 0, SpringLayout.EAST, optionInfo);
		optionPage.add(this.pathIndex);

		this.testButton = new JButton("Ausgabe aufbereiten");

		this.moveButton = new JButton("Ausgaben verschieben");

		this.progressInfo = new JLabel(" ");
		final SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 5, SpringLayout.NORTH, this.frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, this.progressInfo);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -5, SpringLayout.NORTH, this.testButton);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, this.progressInfo);
		springLayout.putConstraint(SpringLayout.NORTH, this.testButton, 0, SpringLayout.NORTH, this.moveButton);
		springLayout.putConstraint(SpringLayout.EAST, this.testButton, -5, SpringLayout.WEST, this.moveButton);
		springLayout.putConstraint(SpringLayout.SOUTH, this.moveButton, -5, SpringLayout.NORTH, this.progressInfo);
		springLayout.putConstraint(SpringLayout.EAST, this.moveButton, 0, SpringLayout.EAST, this.progressInfo);
		springLayout.putConstraint(SpringLayout.WEST, this.progressInfo, 5, SpringLayout.WEST, this.frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, this.progressInfo, -5, SpringLayout.SOUTH, this.frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, this.progressInfo, -5, SpringLayout.EAST, this.frame.getContentPane());
		this.frame.getContentPane().setLayout(springLayout);
		this.frame.getContentPane().add(tabbedPane);

		this.scanButton = new JButton("Eingabe aufbereiten");
		springLayout.putConstraint(SpringLayout.NORTH, this.scanButton, 0, SpringLayout.NORTH, this.moveButton);
		springLayout.putConstraint(SpringLayout.EAST, this.scanButton, -5, SpringLayout.WEST, this.testButton);
		this.frame.getContentPane().add(this.scanButton);
		this.frame.getContentPane().add(this.testButton);
		this.frame.getContentPane().add(this.moveButton);
		this.frame.getContentPane().add(this.progressInfo);

	}
}
