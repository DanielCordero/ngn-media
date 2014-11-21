package net.sf.fmj.media;

import java.util.ArrayList;
import java.util.List;

import javax.media.Format;
import javax.media.protocol.ContentDescriptor;

import net.sf.fmj.utility.PlugInInfo;

/**
 * Defaults for the FMJ registry. Broken out into fmj, jmf, and third-party. fmj
 * ones are fmj-specific. jmf ones are to duplicate what is in jmf (useful if
 * jmf is in the classpath). third-party ones are those that are not included
 * with fmj but might be in the classpath (like fobs4jmf). The flags give us the
 * flexibility to make the registry the same as JMF's or JMF's + FMJ's, or just
 * FMJ's. Making it the same as JMF's is useful for unit testing.
 * 
 * @author Ken Larson
 * 
 */
public class RegistryDefaults {

	public static List<String> contentPrefixList() {
		final List<String> contentPrefixList = new ArrayList<String>();

		contentPrefixList.add("javax");
		contentPrefixList.add("com.sun");
		contentPrefixList.add("com.ibm");
		contentPrefixList.add("net.sf.fmj");
		return contentPrefixList;
	}

	/**
	 * List items are either classnames (String) or PlugInInfo.
	 */
	public static List<Object> plugInList() {
		final List<Object> result = new ArrayList<Object>();
		result.add(new PlugInInfo(net.sf.fmj.media.parser.RawPushBufferParser.class.getName(), new Format[] { new ContentDescriptor("raw"), }, new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER));
		result.add(net.sf.fmj.media.codec.audio.ulaw.Decoder.class.getName());
		result.add(net.sf.fmj.media.codec.audio.ulaw.Encoder.class.getName());
		result.add(net.sf.fmj.media.codec.audio.ulaw.DePacketizer.class.getName());
		result.add(net.sf.fmj.media.codec.audio.ulaw.Packetizer.class.getName());
		result.add(net.sf.fmj.media.codec.audio.RateConverter.class.getName());
		result.add(net.sf.fmj.media.codec.audio.alaw.Decoder.class.getName());
		result.add(net.sf.fmj.media.codec.audio.alaw.Encoder.class.getName());
		result.add(net.sf.fmj.media.codec.audio.alaw.DePacketizer.class.getName());
		result.add(net.sf.fmj.media.codec.audio.alaw.Packetizer.class.getName());
		result.add(net.sf.fmj.media.multiplexer.RTPSyncBufferMux.class.getName());
		return result;
	}

	public static List<String> protocolPrefixList() {
		final List<String> protocolPrefixList = new ArrayList<String>();
		protocolPrefixList.add("javax");
		protocolPrefixList.add("com.sun");
		protocolPrefixList.add("com.ibm");
		protocolPrefixList.add("net.sf.fmj.gst");
		protocolPrefixList.add("net.sf.fmj");
		return protocolPrefixList;
	}

}
