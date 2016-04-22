package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.iam.IAMBuilder.IAMMappingBuilder;
import bee.creative.iam.IAMCodec.IAMArrayFormat;
import bee.creative.iam.IAMCodec.IAMFindMode;
import bee.creative.ini.INIReader;
import bee.creative.ini.INIToken;
import bee.creative.ini.INIWriter;

@SuppressWarnings ("javadoc")
class IAMCodec_INI {

	ByteOrder _order_;

	INIReader _reader_;

	INIWriter _writer_;

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

	final ByteOrder _readPropertyAsOrder_(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			return IAMCodec.parseByteOrder(token.value());
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	final IAMFindMode _readPropertyAsMode_(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			return IAMCodec.parseFindMode(token.value());
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	final IAMArrayFormat _readPropertyAsFormat_(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this._readProperty_(key);
		try {
			return IAMCodec.parseArrayFormat(token.value());
		} catch (final Exception cause) {
			throw this._newIllegalProperty_(token, cause);
		}
	}

	public final IAMIndexBuilder decode(final IAMCodec codec) throws IOException, IllegalStateException, IllegalArgumentException {
		INIToken token;
		ByteOrder indexOrder = null;
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
					mappingBuilder.mode(mappingFindMode.mode(newCount));

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

	final void _writeSection_(final String name) throws IOException {
		this._writer_.writeSection(name);
	}

	final void _writeProperty_(final String key, final String value) throws IOException {
		this._writer_.writeProperty(key, value);
	}

	final void _writeIndex_(final IAMIndex source) throws IOException, IllegalArgumentException {

		this._writeSection_("IAM_INDEX");
		this._writeProperty_("byteOrder", IAMCodec.formatByteOrder(this._order_));
		final int mappingCount = source.mappingCount();
		this._writeProperty_("mappingCount", Integer.toString(mappingCount));
		final int listingCount = source.listingCount();
		this._writeProperty_("listingCount", Integer.toString(listingCount));

		for (int i = 0; i < mappingCount; i++) {
			this._writeMapping_(source.mapping(i), i);
		}

		for (int i = 0; i < listingCount; i++) {
			this._writeListing_(source.listing(i), i);
		}

	}

	final void _writeListing_(final IAMListing source, final int index) throws IOException, IllegalArgumentException {
		final int itemCount = source.itemCount();
		if (itemCount == 0) return;

		this._writeSection_("IAM_LISTING");
		this._writeProperty_("index", Integer.toString(index));
		this._writeProperty_("itemFormat", IAMCodec.formatArrayFormat(IAMArrayFormat.ARRAY));

		for (int i = 0; i < itemCount; i++) {
			this._writeProperty_(Integer.toString(i), IAMArrayFormat.ARRAY.format(source.item(i).toArray()));
		}

	}

	final void _writeMapping_(final IAMMapping source, final int index) throws IOException, IllegalArgumentException {
		final int entryCount = source.entryCount();
		if (entryCount == 0) return;

		this._writeSection_("IAM_MAPPING");
		this._writeProperty_("index", Integer.toString(index));
		this._writeProperty_("findMode", IAMCodec.formatFindMode(source.mode() ? IAMFindMode.HASHED : IAMFindMode.SORTED));
		this._writeProperty_("keyFormat", IAMCodec.formatArrayFormat(IAMArrayFormat.ARRAY));
		this._writeProperty_("valueFormat", IAMCodec.formatArrayFormat(IAMArrayFormat.ARRAY));

		for (int i = 0; i < entryCount; i++) {
			this._writeProperty_(IAMArrayFormat.ARRAY.format(source.key(i).toArray()), IAMArrayFormat.ARRAY.format(source.value(i).toArray()));
		}

	}

	public final void encode(final IAMCodec codec, final IAMIndex source) throws IOException, IllegalStateException, IllegalArgumentException {
		try (final INIWriter writer = INIWriter.from(codec.getTargetData())) {
			this._order_ = codec.getByteOrder();
			this._writer_ = writer;
			this._writeIndex_(source);
		}
	}

}
