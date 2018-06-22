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

	INIReader reader;

	INIWriter writer;

	{}

	INIToken readContent() throws IOException {
		while (true) {
			final INIToken result = this.reader.read();
			if ((result == null) || !result.isComment()) return result;
		}
	}

	INIToken readProperty(final String key) throws IOException, IllegalStateException {
		final INIToken result = this.readContent();
		if (result == null) throw new IllegalStateException(key + " missing");
		if (!key.equals(result.key())) throw new IllegalStateException(key + " expected: " + result.key());
		return result;
	}

	IllegalArgumentException newIllegalProperty(final INIToken token, final Throwable cause) throws IllegalArgumentException {
		return new IllegalArgumentException(token.key() + " invalid: " + token.value(), cause);
	}

	int readPropertyAsInt(final String key, final int length) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this.readProperty(key);
		try {
			final int result = Integer.parseInt(token.value());
			if ((result < 0) || (result >= length)) throw new IllegalArgumentException();
			return result;
		} catch (final Exception cause) {
			throw this.newIllegalProperty(token, cause);
		}
	}

	IAMByteOrder readPropertyAsOrder(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this.readProperty(key);
		try {
			return IAMByteOrder.from(token.value());
		} catch (final Exception cause) {
			throw this.newIllegalProperty(token, cause);
		}
	}

	IAMFindMode readPropertyAsMode(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this.readProperty(key);
		try {
			return IAMFindMode.from(token.value());
		} catch (final Exception cause) {
			throw this.newIllegalProperty(token, cause);
		}
	}

	IAMArrayFormat readPropertyAsFormat(final String key) throws IOException, IllegalStateException, IllegalArgumentException {
		final INIToken token = this.readProperty(key);
		try {
			return IAMArrayFormat.from(token.value());
		} catch (final Exception cause) {
			throw this.newIllegalProperty(token, cause);
		}
	}

	public IAMIndexBuilder decode(final IAMCodec codec) throws IOException, IllegalStateException, IllegalArgumentException {
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
			this.reader = reader;

			token = this.readContent();
			if (token == null) throw new IllegalStateException("[IAM_INDEX] missing");

			if (!"IAM_INDEX".equals(token.section())) throw new IllegalArgumentException("property outside section: " + token.key());
			indexBuilder = new IAMIndexBuilder();
			indexOrder = this.readPropertyAsOrder("byteOrder");
			codec.useByteOrder(indexOrder);

			final int mappingCount = this.readPropertyAsInt("mappingCount", 1073741824);
			mappingBuilders = new IAMMappingBuilder[mappingCount];
			for (int i = 0; i < mappingCount; i++) {
				indexBuilder.putMapping(mappingBuilders[i] = new IAMMappingBuilder());
			}

			final int listingCount = this.readPropertyAsInt("listingCount", 1073741824);
			listingBuilders = new IAMListingBuilder[listingCount];
			for (int i = 0; i < listingCount; i++) {
				indexBuilder.putListing(listingBuilders[i] = new IAMListingBuilder());
			}

			for (token = this.readContent(); true;) {

				if (token == null) return indexBuilder;

				if ("IAM_MAPPING".equals(token.section())) {

					mappingBuilder = mappingBuilders[this.readPropertyAsInt("index", mappingCount)];
					mappingFindMode = this.readPropertyAsMode("findMode");
					mappingKeyFormat = this.readPropertyAsFormat("keyFormat");
					mappingValueFormat = this.readPropertyAsFormat("valueFormat");

					final int oldCount = mappingBuilder.entryCount();
					for (token = this.readContent(); (token != null) && token.isProperty(); token = this.readContent()) {

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

					listingBuilder = listingBuilders[this.readPropertyAsInt("index", listingCount)];

					listingItemFormat = this.readPropertyAsFormat("itemFormat");

					final int count = listingBuilder.itemCount();
					for (token = this.readContent(); (token != null) && token.isProperty(); token = this.readContent()) {
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

	void writeSection(final String name) throws IOException {
		this.writer.writeSection(name);
	}

	void writeProperty(final Object key, final Object value) throws IOException {
		this.writer.writeProperty(key.toString(), value.toString());
	}

	void writeListing(final IAMListing source, final int index) throws IOException, IllegalArgumentException {

		final int itemCount = source.itemCount();
		if (itemCount == 0) return;

		this.writeSection("IAM_LISTING");
		this.writeProperty("index", index);
		this.writeProperty("itemFormat", IAMArrayFormat.ARRAY);

		for (int i = 0; i < itemCount; i++) {
			this.writeProperty(i, IAMArrayFormat.ARRAY.format(source.item(i).toArray()));
		}

	}

	void writeMapping(final IAMMapping source, final int index) throws IOException, IllegalArgumentException {

		final int entryCount = source.entryCount();
		if (entryCount == 0) return;

		this.writeSection("IAM_MAPPING");
		this.writeProperty("index", index);
		this.writeProperty("findMode", IAMFindMode.from(source.mode()));
		this.writeProperty("keyFormat", IAMArrayFormat.ARRAY);
		this.writeProperty("valueFormat", IAMArrayFormat.ARRAY);

		for (int i = 0; i < entryCount; i++) {
			this.writeProperty(IAMArrayFormat.ARRAY.format(source.key(i).toArray()), IAMArrayFormat.ARRAY.format(source.value(i).toArray()));
		}

	}

	public void encode(final IAMCodec codec, final IAMIndex source) throws IOException, IllegalStateException, IllegalArgumentException {
		try (final INIWriter writer = INIWriter.from(codec.getTargetData())) {
			this.writer = writer;

			final int mappingCount = source.mappingCount();
			final int listingCount = source.listingCount();

			this.writeSection("IAM_INDEX");
			this.writeProperty("byteOrder", codec.getByteOrder());
			this.writeProperty("mappingCount", mappingCount);
			this.writeProperty("listingCount", listingCount);

			for (int i = 0; i < mappingCount; i++) {
				this.writeMapping(source.mapping(i), i);
			}

			for (int i = 0; i < listingCount; i++) {
				this.writeListing(source.listing(i), i);
			}

		}
	}

}
