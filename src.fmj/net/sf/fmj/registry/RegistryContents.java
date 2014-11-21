package net.sf.fmj.registry;

import java.util.Vector;

import javax.media.CaptureDeviceInfo;

/**
 * The contents of the registry.
 * 
 * @author Warren Bloomer
 * @author Ken Larson
 */
class RegistryContents {
	/** Lists of Plugin for each category */
	@SuppressWarnings("unchecked")
	Vector<String>[] plugins = new Vector[] { new Vector<String>(), new Vector<String>(), new Vector<String>(), new Vector<String>(), new Vector<String>(), };

	/** a List of protocol prefixes */
	Vector<String> protocolPrefixList = new Vector<String>();

	/** a list of content prefixes */
	Vector<String> contentPrefixList = new Vector<String>();

	/** a List of protocol prefixes */
	Vector<CaptureDeviceInfo> captureDeviceInfoList = new Vector<CaptureDeviceInfo>();
}
