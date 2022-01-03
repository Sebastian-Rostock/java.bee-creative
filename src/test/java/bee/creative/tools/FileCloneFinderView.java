package bee.creative.tools;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

class FileCloneFinderView {

	JFrame frame;

	JTextArea sourceText;

	JTextArea targetText;

	JTextField hashSizeText;

	JTextField testSizeText;

	JTextField txtDuplikat;

	JButton testButton;

	JButton moveButton;

	JLabel progressInfo;

	FileCloneFinderView() {
		this.initialize();
		this.frame.setVisible(true);
	}

	/** Initialize the contents of the frame. */
	private void initialize() {
		this.frame = new JFrame("Dateiduplikatfinder");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);

		final JPanel sourcePage = new JPanel();
		tabbedPane.addTab("Eingabe", null, sourcePage, null);
		final SpringLayout sl_sourcePage = new SpringLayout();
		sourcePage.setLayout(sl_sourcePage);

		final JLabel sourceInfo = new JLabel("<html>" //
			+ "Die abzugleichenden Dateien und Verzeichnisse können hier eingefügt bzw. fallengelassen werden. " //
			+ "Bei Verzeichnissen werden alle darin enthatenen und Verzeichnisse betrachtet." //
			+ "</html>");
		sl_sourcePage.putConstraint(SpringLayout.NORTH, sourceInfo, 5, SpringLayout.NORTH, sourcePage);
		sl_sourcePage.putConstraint(SpringLayout.WEST, sourceInfo, 5, SpringLayout.WEST, sourcePage);
		sl_sourcePage.putConstraint(SpringLayout.EAST, sourceInfo, -5, SpringLayout.EAST, sourcePage);
		sourcePage.add(sourceInfo);

		this.sourceText = new JTextArea();
		sourceText.setText("D:\\projects\\java\\bee-creative");
		
		JScrollPane sourcePane = new JScrollPane(sourceText); 
		sourcePage.add(sourcePane);
		
		sl_sourcePage.putConstraint(SpringLayout.NORTH, sourcePane, 5, SpringLayout.SOUTH, sourceInfo);
		sl_sourcePage.putConstraint(SpringLayout.WEST, sourcePane, 0, SpringLayout.WEST, sourceInfo);
		sl_sourcePage.putConstraint(SpringLayout.SOUTH, sourcePane, -5, SpringLayout.SOUTH, sourcePage);
		sl_sourcePage.putConstraint(SpringLayout.EAST, sourcePane, 0, SpringLayout.EAST, sourceInfo);

		final JPanel targetPage = new JPanel();
		tabbedPane.addTab("Ausgabe", null, targetPage, null);
		final SpringLayout sl_targetPage = new SpringLayout();
		targetPage.setLayout(sl_targetPage);

		final JLabel targetInfo = new JLabel("<html>" //
			+ "Hier werden zu jeder ersten duplikaten Datei aus der Eingabe die anderen dazu inhaltlich gleichen Dateien aufgelistet." //
			+ "</html>");
		sl_targetPage.putConstraint(SpringLayout.NORTH, targetInfo, 5, SpringLayout.NORTH, targetPage);
		sl_targetPage.putConstraint(SpringLayout.WEST, targetInfo, 5, SpringLayout.WEST, targetPage);
		sl_targetPage.putConstraint(SpringLayout.EAST, targetInfo, -5, SpringLayout.EAST, targetPage);
		targetPage.add(targetInfo);

		this.targetText = new JTextArea();
		JScrollPane targetPane = new JScrollPane(targetText); 
		targetPage.add(targetPane);
		
		sl_targetPage.putConstraint(SpringLayout.NORTH, targetPane, 5, SpringLayout.SOUTH, targetInfo);
		sl_targetPage.putConstraint(SpringLayout.WEST, targetPane, 0, SpringLayout.WEST, targetInfo);
		sl_targetPage.putConstraint(SpringLayout.SOUTH, targetPane, -5, SpringLayout.SOUTH, targetPage);
		sl_targetPage.putConstraint(SpringLayout.EAST, targetPane, 0, SpringLayout.EAST, targetInfo);

		final JPanel optionPage = new JPanel();
		tabbedPane.addTab("Optionen", null, optionPage, null);
		final SpringLayout sl_optionPage = new SpringLayout();
		optionPage.setLayout(sl_optionPage);

		final JLabel optionInfo = new JLabel("Die ");
		sl_optionPage.putConstraint(SpringLayout.NORTH, optionInfo, 6, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, optionInfo, 6, SpringLayout.WEST, optionPage);
		sl_optionPage.putConstraint(SpringLayout.EAST, optionInfo, 704, SpringLayout.WEST, optionPage);
		optionPage.add(optionInfo);

		final JLabel hashSizeInfo = new JLabel("Puffergröße für Streuwert (MB)");
		sl_optionPage.putConstraint(SpringLayout.NORTH, hashSizeInfo, 29, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, hashSizeInfo, 26, SpringLayout.WEST, optionPage);
		optionPage.add(hashSizeInfo);

		this.hashSizeText = new JTextField();
		hashSizeText.setText("5");
		sl_optionPage.putConstraint(SpringLayout.NORTH, this.hashSizeText, 26, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, this.hashSizeText, 181, SpringLayout.WEST, optionPage);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.hashSizeText, 704, SpringLayout.WEST, optionPage);
		this.hashSizeText.setColumns(10);

		final JLabel testSizeInfo = new JLabel("Puffergröße für Dateivergleich (MB)");
		sl_optionPage.putConstraint(SpringLayout.NORTH, testSizeInfo, 55, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, testSizeInfo, 6, SpringLayout.WEST, optionPage);
		optionPage.add(testSizeInfo);

		this.testSizeText = new JTextField();
		testSizeText.setText("200");
		sl_optionPage.putConstraint(SpringLayout.NORTH, this.testSizeText, 52, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, this.testSizeText, 181, SpringLayout.WEST, optionPage);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.testSizeText, 704, SpringLayout.WEST, optionPage);
		this.testSizeText.setColumns(10);

		optionPage.add(this.hashSizeText);

		optionPage.add(this.testSizeText);

		final JLabel movePathInfo = new JLabel("New label");
		sl_optionPage.putConstraint(SpringLayout.NORTH, movePathInfo, 81, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, movePathInfo, 131, SpringLayout.WEST, optionPage);
		optionPage.add(movePathInfo);

		this.txtDuplikat = new JTextField();
		txtDuplikat.setText("DUPLIKAT");
		sl_optionPage.putConstraint(SpringLayout.NORTH, this.txtDuplikat, 78, SpringLayout.NORTH, optionPage);
		sl_optionPage.putConstraint(SpringLayout.WEST, this.txtDuplikat, 181, SpringLayout.WEST, optionPage);
		sl_optionPage.putConstraint(SpringLayout.EAST, this.txtDuplikat, 704, SpringLayout.WEST, optionPage);
		optionPage.add(this.txtDuplikat);
		this.txtDuplikat.setColumns(10);

		this.testButton = new JButton("Ausgabe erzeugen (Test)");

		this.moveButton = new JButton("Ausgaben erzeugen und verschieben");

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
		this.frame.getContentPane().add(this.testButton);
		this.frame.getContentPane().add(this.moveButton);
		this.frame.getContentPane().add(this.progressInfo);

	}

}
