package bee.creative.app.ft;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import bee.creative.lang.Strings;
import bee.creative.util.Iterables;

public class AA extends JFrame {

	private final JTextArea textPane;

	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new AA();
	}

	public AA() {
		this.setMinimumSize(new Dimension(400, 200));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(new Dimension(600, 400));
	

		final Panel panel = new Panel();
		this.getContentPane().add(panel, BorderLayout.CENTER);

		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0};
		gbl_panel.rowHeights = new int[]{0};
		gbl_panel.columnWeights = new double[]{1.0};
		gbl_panel.rowWeights = new double[]{0.0};
		panel.setLayout(gbl_panel);

		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel.add(scrollPane, gbc_scrollPane);

		final JMenuBar menuBar = new JMenuBar();
		scrollPane.setColumnHeaderView(menuBar);

		final JMenu mnNewMenu = new JMenu("New menu");
		menuBar.add(mnNewMenu);

		final JMenu mnNewMenu_1 = new JMenu("New menu");
		menuBar.add(mnNewMenu_1);

		final JPanel panel_1 = new JPanel();
		scrollPane.setViewportView(panel_1);
		final GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0};
		gbl_panel_1.rowHeights = new int[]{50, 0, 500};
		gbl_panel_1.columnWeights = new double[]{0.0};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);

		final JPanel panel_2 = new JPanel();
		final GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.weightx = 1.0;
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.anchor = GridBagConstraints.WEST;
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel_1.add(panel_2, gbc_panel_2);

		this.textPane = new JTextArea();
		final GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPane.weighty = 1.0;
		gbc_textPane.weightx = 1.0;
		gbc_textPane.anchor = GridBagConstraints.NORTHWEST;
		gbc_textPane.insets = new Insets(0, 0, 0, 5);
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 1;
		panel_1.add(this.textPane, gbc_textPane);

		final JPanel panel_3 = new JPanel();
		final GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.weightx = 1.0;
		gbc_panel_3.anchor = GridBagConstraints.WEST;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 2;
		panel_1.add(panel_3, gbc_panel_3);

		final JViewport viewport = scrollPane.getViewport();
		viewport.addChangeListener(e -> {
			
			
			var textHeightPixel = textPane.getHeight();
			var textHeightLindes = textPane.getLineCount();
			System.out.println(textHeightLindes+" "+textHeightPixel);
			
			var y = panel_1.getY();
			System.out.println(panel_1.getBounds());

		});

		sc(0);
		doLayout();
		this.setVisible(true);
	}

	void sc(final int i) {
		var t = Strings.join("\n", Iterables.fromCount(1000000).translate(a -> ("Zeile ist ganz lang und so weiter sdakjjf asdhf sladf sf jsdklf hsadjkf hjklsdafh ksda kjf " + a)));
		textPane.setText(t);

	}

}
