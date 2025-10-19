package bee.creative.kb;

import static bee.creative.util.Tester.testCall;
import static bee.creative.util.Tester.testRun;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import bee.creative.csv.CSVReader;
import bee.creative.csv.CSVWriter;
import bee.creative.fem.FEMString;
import bee.creative.io.DZIPInputStream;
import bee.creative.io.DZIPOutputStream;
import bee.creative.lang.Integers;
import bee.creative.util.HashMapOI;

public class TEST_RES_SETUP {

	public static void main(String[] args) throws Exception {

		new TEST_RES_SETUP();
	}

	HashMapOI<String> nodeRef = new HashMapOI<>();

	KBBuffer buffer = new KBBuffer();

	CSVWriter textFile;

	public TEST_RES_SETUP() throws Exception {
		var root = new File("res\\DT");
		try (var csv = CSVReader.csvReaderFrom(new File(root, "SRC.csv"))) {
			try (var edge = CSVWriter.csvWriterFrom(new File(root, "EDGE.csv"))) {
				try (var textFile = this.textFile = CSVWriter.csvWriterFrom(new File(root, "TEXT.csv"))) {
					for (var e = csv.readEntry(); e != null; e = csv.readEntry()) {
						var sourceRef = this.putNode(e[0]);
						var relationRef = this.putNode(e[1]);
						var targetRef = this.putNode(e[2]);
						this.buffer.putEdge(sourceRef, targetRef, relationRef);
						edge.writeEntry(sourceRef, relationRef, targetRef);
					}
					this.printState(this.buffer);
					System.err.print("persist: ");
					testRun(() -> {
						try (var zipdos = new DZIPOutputStream(new FileOutputStream(new File(root, "RES.kbf")))) {
							KBCodec.persistState(zipdos,buffer);
						}
					});
					System.err.print("restore: ");
					var r = testCall(() -> {
						try (var zipdis = new DZIPInputStream(new FileInputStream(new File(root, "RES.kbf")))) {
							return KBCodec.restoreState(zipdis);
						}
					});
					this.buffer.putEdge(-1, -1, -1);
					this.buffer.putValue(FEMString.EMPTY);
					this.buffer.commit();
					this.printState(r);
					System.out.println(r.getSourceCount());
					System.out.println(r.getTargetCount());
					System.out.println(r.getValueCount());
					// System.out.println(KBState.from(buffer, r));
					// System.out.println(KBState.from(r,buffer));
				}
			}
		}
	}

	private void printState(KBState state) {
		System.err.print("memory: ");
		System.err.println(Integers.printSize(state.emu()));
	}

	int putNode(String nodeVal) throws Exception {
		var nodeRef = this.nodeRef.get(nodeVal);
		if (nodeRef != null) return nodeRef;
		if (nodeVal.charAt(0) == '<') {
			nodeRef = this.nodeRef.size() + 1;
		} else {
			nodeRef = -this.nodeRef.size() - 1;
			this.textFile.writeEntry(nodeRef, nodeVal);
			this.buffer.putValue(nodeRef, FEMString.from(nodeVal));
		}
		this.nodeRef.put(nodeVal, nodeRef);
		return nodeRef;
	}

}
