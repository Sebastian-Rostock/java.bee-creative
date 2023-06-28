package bee.creative.app.ft;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import bee.creative.emu.EMU;
import bee.creative.lang.Integers;
import bee.creative.util.Consumer;

public class FTEditor extends JPanel {

	private JTextArea textArea;

	Consumer<DropTargetDropEvent> onDrop;

	Consumer<ActionEvent> onPaste;

	public FTEditor() {
		setLayout(new BorderLayout(0, 0));

		textArea = make();
		add(textArea, BorderLayout.CENTER);

	}

	public void setOnDrop(Consumer<DropTargetDropEvent> onDrop) {
		this.onDrop = onDrop;
	}

	public void setOnPaste(Consumer<ActionEvent> onPaste) {
		this.onPaste = onPaste;
	}

	private JTextArea make() {
		var res = new JTextArea();

		new DropTarget(res, new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetDropEvent event) {
				onDrop.set(event);
			}

		});
		res.getActionMap().put("paste-from-clipboard", new AbstractAction() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				onPaste.set(event);
			}

		});

		return res;
	}

	public String getText() {
		return textArea.getText();
	}

	public void setText(String value) {
		textArea.setText(value);
	}

	public void setTextLater(String value) {
		EventQueue.invokeLater(() -> {
			setText(value);
		});
	}

	public void paste() {
		 textArea.paste();
//		ta.paste();
	}

}
