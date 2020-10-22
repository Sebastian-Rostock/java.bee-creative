package bee.creative.iam.bind;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMIndex;
import bee.creative.iam.IAMIndexBuilder;
import bee.creative.iam.IAMListing;
import bee.creative.iam.IAMListingBuilder;
import bee.creative.iam.IAMMapping;
import bee.creative.iam.IAMMappingBuilder;
import bee.creative.iam.bind.IAMCodec.IAMArrayFormat;
import bee.creative.iam.bind.IAMCodec.IAMByteOrder;
import bee.creative.iam.bind.IAMCodec.IAMFindMode;
import bee.creative.io.IO;

/** @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
final class IAMCodec_XML {

	@XmlType
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMITEMTYPE {

		@XmlAttribute (required = true)
		public String data;

	}

	@XmlType
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMENTRYTYPE {

		@XmlAttribute (required = true)
		public String key;

		@XmlAttribute (required = true)
		public String value;

	}

	@XmlType
	@XmlRootElement (name = "index")
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMINDEXTYPE {

		@XmlElements ({@XmlElement (name = "mapping", type = IAMMAPPINGTYPE.class), @XmlElement (name = "listing", type = IAMLISTINGTYPE.class)})
		public ArrayList<Object> mappingOrListing = new ArrayList<>();

		@XmlAttribute
		public String byteOrder;

		@XmlAttribute (required = true)
		public String mappingCount;

		@XmlAttribute (required = true)
		public String listingCount;

	}

	@XmlType
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMLISTINGTYPE {

		@XmlElement (required = true)
		public ArrayList<IAMITEMTYPE> item = new ArrayList<>();

		@XmlAttribute (required = true)
		public String index;

		@XmlAttribute
		public String itemFormat;

	}

	@XmlType
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMMAPPINGTYPE {

		@XmlElement (required = true)
		public ArrayList<IAMENTRYTYPE> entry = new ArrayList<>();

		@XmlAttribute (required = true)
		public String index;

		@XmlAttribute
		public String findMode;

		@XmlAttribute
		public String keyFormat;

		@XmlAttribute
		public String valueFormat;

	}

	public final IAMIndexBuilder decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
		final IAMINDEXTYPE xmlIndex = JAXB.unmarshal(IO.inputReaderFrom(codec.getSourceData()), IAMINDEXTYPE.class);

		final IAMIndexBuilder indexBuilder = new IAMIndexBuilder();
		codec.useByteOrder(IAMByteOrder.from(xmlIndex.byteOrder));
		final int mappingCount = IAMCodec.checkRange(Integer.parseInt(xmlIndex.mappingCount), 1073741824);
		final int listingCount = IAMCodec.checkRange(Integer.parseInt(xmlIndex.listingCount), 1073741824);

		final IAMMappingBuilder[] mappingBuilders = new IAMMappingBuilder[mappingCount];
		for (int i = 0; i < mappingCount; i++) {
			indexBuilder.put(-1, mappingBuilders[i] = new IAMMappingBuilder());
		}

		final IAMListingBuilder[] listingBuilders = new IAMListingBuilder[listingCount];
		for (int i = 0; i < listingCount; i++) {
			indexBuilder.put(-1, listingBuilders[i] = new IAMListingBuilder());
		}

		for (final Object object: xmlIndex.mappingOrListing) {
			if (object instanceof IAMMAPPINGTYPE) {
				final IAMMAPPINGTYPE xmlMapping = (IAMMAPPINGTYPE)object;

				final IAMMappingBuilder mappingBuilder = mappingBuilders[IAMCodec.checkRange(Integer.parseInt(xmlMapping.index), mappingCount)];
				final IAMFindMode mappingFindMode = IAMFindMode.from(xmlMapping.findMode);
				final IAMArrayFormat mappingKeyFormat = IAMArrayFormat.from(xmlMapping.keyFormat);
				final IAMArrayFormat mappingValueFormat = IAMArrayFormat.from(xmlMapping.valueFormat);

				final int oldCount = mappingBuilder.entryCount();
				for (final IAMENTRYTYPE xmlEntry: xmlMapping.entry) {
					final IAMArray key = mappingKeyFormat.parse(xmlEntry.key);
					final IAMArray value = mappingValueFormat.parse(xmlEntry.value);
					mappingBuilder.put(key, value);
				}
				final int newCount = mappingBuilder.entryCount();

				if (oldCount == newCount) throw new IllegalStateException("<mapping> incomplete");
				mappingBuilder.mode(mappingFindMode.toMode(newCount));

			} else {
				final IAMLISTINGTYPE xmlListing = (IAMLISTINGTYPE)object;

				final IAMListingBuilder listingBuilder = listingBuilders[IAMCodec.checkRange(Integer.parseInt(xmlListing.index), listingCount)];
				final IAMArrayFormat listingItemFormat = IAMArrayFormat.from(xmlListing.itemFormat);

				final int oldCount = listingBuilder.itemCount();
				for (final IAMITEMTYPE xmlItem: xmlListing.item) {
					final IAMArray data = listingItemFormat.parse(xmlItem.data);
					listingBuilder.put(-1, data);
				}
				final int newCount = listingBuilder.itemCount();

				if (oldCount == newCount) throw new IllegalStateException("<listing> incomplete");

			}
		}

		return indexBuilder;
	}

	public final void encode(final IAMCodec codec, final IAMIndex source) throws IOException, IllegalArgumentException {
		final int mappingCount = source.mappingCount();
		final int listingCount = source.listingCount();

		final IAMINDEXTYPE xmlIndex = new IAMINDEXTYPE();
		xmlIndex.byteOrder = codec.getByteOrder().toString();
		xmlIndex.mappingCount = Integer.toString(mappingCount);
		xmlIndex.listingCount = Integer.toString(listingCount);

		for (int index = 0; index < mappingCount; index++) {
			final IAMMapping mapping = source.mapping(index);
			final int entryCount = mapping.entryCount();
			if (entryCount != 0) {
				final IAMMAPPINGTYPE xmlMapping = new IAMMAPPINGTYPE();
				xmlIndex.mappingOrListing.add(xmlMapping);
				xmlMapping.index = Integer.toString(index);

				for (int i = 0; i < entryCount; i++) {
					final IAMENTRYTYPE xmlEntry = new IAMENTRYTYPE();
					xmlMapping.entry.add(xmlEntry);
					xmlEntry.key = IAMArrayFormat.ARRAY.print(mapping.key(i));
					xmlEntry.value = IAMArrayFormat.ARRAY.print(mapping.value(i));
				}

			}
		}

		for (int index = 0; index < listingCount; index++) {
			final IAMListing listing = source.listing(index);

			final int itemCount = listing.itemCount();
			if (itemCount != 0) {
				final IAMLISTINGTYPE xmlListing = new IAMLISTINGTYPE();
				xmlIndex.mappingOrListing.add(xmlListing);
				xmlListing.index = Integer.toString(index);

				for (int i = 0; i < itemCount; i++) {
					final IAMITEMTYPE xmlItem = new IAMITEMTYPE();
					xmlListing.item.add(xmlItem);
					xmlItem.data = IAMArrayFormat.ARRAY.print(listing.item(i));
				}

			}
		}

		JAXB.marshal(xmlIndex, IO.outputWriterFrom(codec.getTargetData()));
	}

}
