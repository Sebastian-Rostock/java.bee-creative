package bee.creative.app.ft;

import bee.creative.lang.Runnable2;

class AppProcess implements Runnable2 {

	/** Dieses Feld speichert den Title des Prozesses, der in Statusanzeigen und Fehlermeldungen verwendet wird. */
	public String title;

	/** Dieses Feld speichert die Anzahl der verbleibenden Rechenschritten und wird in Statusanzeigen verwendet. */
	public int steps;

	/** Dieses Feld speichert den akteull verarbeiteten Datensatz und wird in Statusanzeigen verwendet. */
	public Object object;

	/** Dieses Feld speichert {@code true}, wenn die Berechnung abgebrochen werden soll. */
	public boolean isCanceled;

	public AppProcess(AppTask task, String title) {
		this.task = task;
		this.title = title;
	}

	@Override
	public void run() throws Exception {
		this.task.run(this);
	}

	private AppTask task;

}