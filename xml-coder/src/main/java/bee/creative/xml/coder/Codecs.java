package bee.creative.xml.coder;

import java.nio.charset.Charset;

public class Codecs {

	public static class UTF8Codec implements Codec {

		/**
		 * Dieses Feld speichert das in {@link #encode(String)} und {@link #decode(byte[])} verwendete {@link Charset}.
		 */
		static final Charset CHARSET = Charset.forName("UTF-8");

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] encode(String value) throws NullPointerException {
			return value.getBytes(Coder.CHARSET);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String decode(byte[] value) throws NullPointerException {
			return new String(value, Coder.CHARSET);
		}

	}
	
	public static class IntCodec implements Codec {
		
		
		
		@Override
		public byte[] encode(String value) throws NullPointerException {
		
			
			return null;
		}
		
		@Override
		public String decode(byte[] value) throws NullPointerException {
			//ArrayCopy.get2(value, 0)
			return null;
		}
		
	}
	
	public static class CodecPool {
		
		
		
		
	}

}
