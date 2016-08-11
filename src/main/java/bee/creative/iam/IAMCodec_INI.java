package bee.creative.iam;

import java.io.IOException;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.iam.IAMBuilder.IAMMappingBuilder;
import bee.creative.iam.IAMCodec.IAMArrayFormat;
import bee.creative.iam.IAMCodec.IAMByteOrder;
import bee.creative.iam.IAMCodec.IAMFindMode;
import bee.creative.ini.INIReader;
import bee.creative.ini.INIToken;
import bee.creative.ini.INIWriter;

/** @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
final class IAMCodec_INI {

	INIReader _reader_;

	{}

	final INIToken _readContent_() throws IOException {
		while (true) {
			final INIToken result = this._reader_.read();
			if ((result == null) || !result.isComment()) return result;
		}
	}

	final INIToken _readProperty_(final String key) throws IOException, IllegalStateException {
		final INIToken result = this._readContent_();
		if (result == null) throw new IllegalStateException(key + " missing");
		if (!key.equals(result.key())) throw new IllegalStateException(key + " expected: " + result.key());
		return result;
	}

	final IllegalArgumentException _newIllegalProperty_(final INIToken token, final Throwable cause) throws IllegalArgumentException {
		return new IllegalArgumentException(token.key() + " invalid: " + token.value(), cause);
	}

	final int _readPropertyAsInt_(final String key, final int length) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			final int result = Integer.parseInt(token.value());
			if ((result < 0) || (result >= length)) throw new IllegalArgumentException();
			return result;
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	final IAMByteOrder _readPropertyAsOrder_(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			return IAMByteOrder.from(token.value());
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	final IAMFindMode _readPropertyAsMode_(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			return IAMFindMode.from(token.value());
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	final IAMArrayFormat _readPropertyAsFormat_(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			return IAMArrayFormat.from(token.value());
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	public final IAMIndexBuilder decode(final IAMCodec codec) throws IOException, IllegalStateException, IllegalArgumentException {
		INIToken token;
		IAMByteOrder indexOrder = null;
		IAMIndexBuilder indexBuilder = null;
		IAMMappingBuilder mappingBuilder = null;
		IAMMappingBuilder[] mappingBuilders = null;
		IAMFindMode mappingFindMode = null;
		IAMArrayFormat mappingKeyFormat = null;
		IAMArrayFormat mappingValueFormat = null;
		IAMListingBuilder listingBuilder = null;
		IAMListingBuilder[] listingBuilders = null;
		IAMArrayFormat listingItemFormat = null;
		try (INIReader reader = INIReader.from(codec.getSourceData())) {
			this._reader_ = reader;

			token = this._readContent_();
			if (token == null) throw new IllegalStateException("[IAM_INDEX] missing");

			if (!"IAM_INDEX".equals(token.section())) throw new IllegalArgumentException("property outside section: " + token.key());
			indexBuilder = new IAMIndexBuilder();
			indexOrder = this._readPropertyAsOrder_("byteOrder");
			codec.useByteOrder(indexOrder);

			final int mappingCount = this._readPropertyAsInt_("mappingCount", 1073741824);
			mappingBuilders = new IAMMappingBuilder[mappingCount];
			for (int i = 0; i < mappingCount; i++) {
				indexBuilder.putMapping(mappingBuilders[i] = new IAMMappingBuilder());
			}

			final int listingCount = this._readPropertyAsInt_("listingCount", 1073741824);
			listingBuilders = new IAMListingBuilder[listingCount];
			for (int i = 0; i < listingCount; i++) {
				indexBuilder.putListing(listingBuilders[i] = new IAMListingBuilder());
			}

			for (token = this._readContent_(); true;) {

				if (token == null) return indexBuilder;

				if ("IAM_MAPPING".equals(token.section())) {

					mappingBuilder = mappingBuilders[this._readPropertyAsInt_("index", mappingCount)];
					mappingFindMode = this._readPropertyAsMode_("findMode");
					mappingKeyFormat = this._readPropertyAsFormat_("keyFormat");
					mappingValueFormat = this._readPropertyAsFormat_("valueFormat");

					final int oldCount = mappingBuilder.entryCount();
					for (token = this._readContent_(); (token != null) && token.isProperty(); token = this._readContent_()) {

						final int[] key = mappingKeyFormat.parse(token.key());
						final int[] value = mappingValueFormat.parse(token.value());
						mappingBuilder.put(key, value);

					}
					final int newCount = mappingBuilder.entryCount();

					if (oldCount == newCount) throw new IllegalStateException("[IAM_MAPPING] incomplete");
					mappingBuilder.mode(mappingFindMode.toMode(newCount));

					continue;
				}

				if ("IAM_LISTING".equals(token.section())) {

					listingBuilder = listingBuilders[this._readPropertyAsInt_("index", listingCount)];

					listingItemFormat = this._readPropertyAsFormat_("itemFormat");

					final int count = listingBuilder.itemCount();
					for (token = this._readContent_(); (token != null) && token.isProperty(); token = this._readContent_()) {
						int key;

						try {
							key = Integer.parseInt(token.key());
						} catch (final Exception cause) {
							throw new IllegalArgumentException(cause);
						}
						final int[] item = listingItemFormat.parse(token.value());

						if (key != listingBuilder.itemCount()) throw new IllegalArgumentException("inconsistent key in [IAM_LISTING]");
						listingBuilder.put(item, false);

					}
					if (count == listingBuilder.itemCount()) throw new IllegalStateException("[IAM_LISTING] incomplete");
					continue;
				}

				if (token.isProperty()) throw new IllegalArgumentException("unknown property: " + token.key());

			}
		}
	}

	static void _writeSection_(final INIWriter writer, final String name) throws IOException {
		writer.writeSection(name);
	}

	static void _writeProperty_(final INIWriter writer, final String key, final String value) throws IOException {
		writer.writeProperty(key, value);
	}

	static void _writeListing_(final INIWriter writer, final IAMListing source, final int index) throws IOException, IllegalArgumentException {

		final int itemCount = source.itemCount();
		if (itemCount == 0) return;

		IAMCodec_INI._writeSection_(writer, "IAM_LISTING");
		IAMCodec_INI._writeProperty_(writer, "index", Integer.toString(index));
		IAMCodec_INI._writeProperty_(writer, "itemFormat", IAMArrayFormat.ARRAY.toString());

		for (int i = 0; i < itemCount; i++) {
			IAMCodec_INI._writeProperty_(writer, Integer.toString(i), IAMArrayFormat.ARRAY.format(source.item(i).toArray()));
		}

	}

	static void _writeMapping_(final INIWriter writer, final IAMMapping source, final int index) throws IOException, IllegalArgumentException {

		final int entryCount = source.entryCount();
		if (entryCount == 0) return;

		IAMCodec_INI._writeSection_(writer, "IAM_MAPPING");
		IAMCodec_INI._writeProperty_(writer, "index", Integer.toString(index));
		IAMCodec_INI._writeProperty_(writer, "findMode", IAMFindMode.from(source.mode()).toString());
		IAMCodec_INI._writeProperty_(writer, "keyFormat", IAMArrayFormat.ARRAY.toString());
		IAMCodec_INI._writeProperty_(writer, "valueFormat", IAMArrayFormat.ARRAY.toString());

		for (int i = 0; i < entryCount; i++) {
			IAMCodec_INI._writeProperty_(writer, IAMArrayFormat.ARRAY.format(source.key(i).toArray()), IAMArrayFormat.ARRAY.format(source.value(i).toArray()));
		}

	}

	public final void encode(final IAMCodec codec, final IAMIndex source) throws IOException, IllegalStateException, IllegalArgumentException {
		try (final INIWriter writer = INIWriter.from(codec.getTargetData())) {
			final int mappingCount = source.mappingCount();
			final int listingCount = source.listingCount();

			IAMCodec_INI._writeSection_(writer, "IAM_INDEX");
			IAMCodec_INI._writeProperty_(writer, "byteOrder", codec.getByteOrder().toString());
			IAMCodec_INI._writeProperty_(writer, "mappingCount", Integer.toString(mappingCount));
			IAMCodec_INI._writeProperty_(writer, "listingCount", Integer.toString(listingCount));

			for (int i = 0; i < mappingCount; i++) {
				IAMCodec_INI._writeMapping_(writer, source.mapping(i), i);
			}

			for (int i = 0; i < listingCount; i++) {
				IAMCodec_INI._writeListing_(writer, source.listing(i), i);
			}

		}
	}

}
