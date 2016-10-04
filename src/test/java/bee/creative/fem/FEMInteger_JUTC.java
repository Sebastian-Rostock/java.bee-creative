package bee.creative.fem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings ("javadoc")
public class FEMInteger_JUTC {

	FEMInteger _Z, _N, _P;

	@Before
	public void setUp() throws Exception {
		this._Z = new FEMInteger(0L);
		this._N = new FEMInteger(Long.MIN_VALUE);
		this._P = new FEMInteger(9223372036854775807L);
	}

	@Test
	public void testToString() {
		Assert.assertEquals("-9223372036854775808", this._N.toString());
		Assert.assertEquals("0", this._Z.toString());
		Assert.assertEquals("9223372036854775807", this._P.toString());
	}

	@Test
	public void testFromLong() {
		Assert.assertEquals(this._Z, FEMInteger.from(0));
	}

	@Test
	public void testFromNumber() {
		Assert.assertEquals(this._Z, FEMInteger.from(new Byte((byte)0)));
	}

	@Test
	public void testFromString() {
		Assert.assertEquals(this._Z, FEMInteger.from("0"));
		Assert.assertEquals(this._P, FEMInteger.from("9223372036854775807"));
		Assert.assertEquals(this._N, FEMInteger.from("-9223372036854775808"));
	}

 
	@Test
	public void testValue() {
		Assert.assertEquals(0L, this._Z.value());
	}

	@Test
	public void testHash() {
		Assert.assertEquals(this._Z.hash(), this._Z.hash());
	}

	@Test
	public void testEqualsFEMInteger() {
		Assert.assertTrue(this._Z.equals(this._Z));
	}

	@Test
	public void testCompare() {
		Assert.assertTrue(this._N.compare(this._P) == -1);
		Assert.assertTrue(this._P.compare(this._N) == +1);
		Assert.assertTrue(this._Z.compare(this._Z) == 0);
	}

	@Test
	public void testToNumber() {
		Assert.assertEquals(new Long(0), this._Z.toNumber());
	}

	@Test
	public void testData() {
		Assert.assertEquals(this._Z, this._Z.data());
	}

	@Test
	public void testType() {
		Assert.assertEquals(FEMInteger.TYPE, this._Z.type());
	}

	@Test
	public void testResultBoolean() {
		Assert.assertEquals(this._Z, this._Z.result(false));
		Assert.assertEquals(this._Z, this._Z.result(true));
	}

}
