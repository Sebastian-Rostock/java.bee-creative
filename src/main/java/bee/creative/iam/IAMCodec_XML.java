package bee.creative.iam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.iam.IAMBuilder.IAMMappingBuilder;
import bee.creative.iam.IAMCodec.IAMArrayFormat;
import bee.creative.iam.IAMCodec.IAMByteOrder;
import bee.creative.iam.IAMCodec.IAMFindMode;
import bee.creative.util.IO;

@SuppressWarnings ("javadoc")
final class IAMCodec_XML {

	@XmlType (name = "IAM_ITEM_TYPE")
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMITEMTYPE {

		@XmlAttribute (name = "data", required = true)
		public String data;

	}

	@XmlType (name = "IAM_ENTRY_TYPE")
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMENTRYTYPE {

		@XmlAttribute (name = "key", required = true)
		public String key;

		@XmlAttribute (name = "value", required = true)
		public String value;

	}

	@XmlType (name = "IAM_INDEX_TYPE", propOrder = {"mappingOrListing"})
	@XmlRootElement (name = "index")
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMINDEXTYPE {

		@XmlElements ({@XmlElement (name = "mapping", type = IAMMAPPINGTYPE.class), @XmlElement (name = "listing", type = IAMLISTINGTYPE.class)})
		public List<Object> mappingOrListing = new ArrayList<>();

		@XmlAttribute (name = "byteOrder", required = false)
		public String byteOrder;

		@XmlAttribute (name = "mappingCount", required = true)
		public String mappingCount;

		@XmlAttribute (name = "listingCount", required = true)
		public String listingCount;

	}

	@XmlType (name = "IAM_LISTING_TYPE", propOrder = {"item"})
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMLISTINGTYPE {

		@XmlElement (required = true)
		public List<IAMITEMTYPE> item = new ArrayList<>();

		@XmlAttribute (name = "index", required = true)
		public String index;

		@XmlAttribute (name = "itemFormat", required = false)
		public String itemFormat;

	}

	@XmlType (name = "IAM_MAPPING_TYPE", propOrder = {"entry"})
	@XmlAccessorType (XmlAccessType.FIELD)
	public static final class IAMMAPPINGTYPE {

		@XmlElement (required = true)
		public List<IAMENTRYTYPE> entry = new ArrayList<>();

		@XmlAttribute (name = "index", required = true)
		public String index;

		@XmlAttribute (name = "findMode", required = false)
		public String findMode;

		@XmlAttribute (name = "keyFormat", required = false)
		public String keyFormat;

		@XmlAttribute (name = "valueFormat", required = false)
		public String valueFormat;

	}

	{}

	public final IAMIndexBuilder decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
		final IAMINDEXTYPE xmlIndex = JAXB.unmarshal(IO.inputReaderFrom(codec.getSourceData()), IAMINDEXTYPE.class);

		final IAMIndexBuilder indexBuilder = new IAMIndexBuilder();
		codec.useByteOrder(IAMByteOrder.from(xmlIndex.byteOrder));
		final int mappingCount = IAMCodec._checkRange_(Integer.parseInt(xmlIndex.mappingCount), 1073741824);
		final int listingCount = IAMCodec._checkRange_(Integer.parseInt(xmlIndex.listingCount), 1073741824);

		final IAMMappingBuilder[] mappingBuilders = new IAMMappingBuilder[mappingCount];
		for (int i = 0; i < mappingCount; i++) {
			indexBuilder.putMapping(mappingBuilders[i] = new IAMMappingBuilder());
		}

		final IAMListingBuilder[] listingBuilders = new IAMListingBuilder[listingCount];
		for (int i = 0; i < listingCount; i++) {
			indexBuilder.putListing(listingBuilders[i] = new IAMListingBuilder());
		}

		for (final Object object: xmlIndex.mappingOrListing) {
			if (object instanceof IAMMAPPINGTYPE) {
				final IAMMAPPINGTYPE xmlMapping = (IAMMAPPINGTYPE)object;

				final IAMMappingBuilder mappingBuilder = mappingBuilders[IAMCodec._checkRange_(Integer.parseInt(xmlMapping.index), mappingCount)];
				final IAMFindMode mappingFindMode = IAMFindMode.from(xmlMapping.findMode);
				final IAMArrayFormat mappingKeyFormat = IAMArrayFormat.from(xmlMapping.keyFormat);
				final IAMArrayFormat mappingValueFormat = IAMArrayFormat.from(xmlMapping.valueFormat);

				final int oldCount = mappingBuilder.entryCount();
				for (final IAMENTRYTYPE xmlEntry: xmlMapping.entry) {
					final int[] key = mappingKeyFormat.parse(xmlEntry.key);
					final int[] value = mappingValueFormat.parse(xmlEntry.value);
					mappingBuilder.put(key, value);
				}
				final int newCount = mappingBuilder.entryCount();

				if (oldCount == newCount) throw new IllegalStateException("<mapping> incomplete");
				mappingBuilder.mode(mappingFindMode.toMode(newCount));

			} else {
				final IAMLISTINGTYPE xmlListing = (IAMLISTINGTYPE)object;

				final IAMListingBuilder listingBuilder = listingBuilders[IAMCodec._checkRange_(Integer.parseInt(xmlListing.index), listingCount)];
				final IAMArrayFormat listingItemFormat = IAMArrayFormat.from(xmlListing.itemFormat);

				final int oldCount = listingBuilder.itemCount();
				for (final IAMITEMTYPE xmlItem: xmlListing.item) {
					final int[] data = listingItemFormat.parse(xmlItem.data);
					listingBuilder.put(data, false);
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
					xmlEntry.key = IAMArrayFormat.ARRAY.format(mapping.key(i).toArray());
					xmlEntry.value = IAMArrayFormat.ARRAY.format(mapping.value(i).toArray());
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
					xmlItem.data = IAMArrayFormat.ARRAY.format(listing.item(i).toArray());
				}

			}
		}

		JAXB.marshal(xmlIndex, IO.outputWriterFrom(codec.getTargetData()));
	}

}
