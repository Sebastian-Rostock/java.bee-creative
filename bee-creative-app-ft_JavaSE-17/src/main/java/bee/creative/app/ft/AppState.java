package bee.creative.app.ft;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;
import bee.creative.io.IO;

class AppState {

	private Point selection;

	private byte[] data;

	public AppState(Text widget) {
		try (var bd = new ByteArrayOutputStream(); var cd = new OutputStreamWriter(new GZIPOutputStream(bd), StandardCharsets.UTF_8)) {
			IO.writeChars(cd, widget.getText());
			this.data = bd.toByteArray();
		} catch (IOException ignore) {}
		this.selection = widget.getSelection();
	}

	public void apply(Text widget) {
		try (var bd = new GZIPInputStream(new ByteArrayInputStream(this.data)); var cd = new InputStreamReader(bd, StandardCharsets.UTF_8)) {
			widget.setText(IO.readChars(cd));
		} catch (IOException ignore) {}
		widget.setSelection(this.selection);
	}

}