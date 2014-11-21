package net.sf.fmj.registry;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.utility.PlugInInfo;
import net.sf.fmj.utility.PlugInUtility;

/**
 * This is a registry of Plugins, Protocol prefixes, Content prefixes, and MIME
 * types. The registry may be serialized to an XML. The XML file is nominally
 * located in ${user.home}/.fmj.registry.xml
 * 
 * This object is used by the PackageManager and the PluginManager for
 * persisting data across sessions.
 * 
 * Currently the Registry does not store the supported input and output formats
 * for Plugins. This may be supported by adding CDATA sections that are
 * serialized Format objects. However, it would be good to be able to clear the
 * stored formats, and refresh the supported formats by introspecting the
 * Plugins. Sometimes the installed plugins may be updated, and the list of
 * supported formats may change for the same plugin class.
 * 
 * Nevertheless, the present situation is that the PluginManager will need to
 * determine supported formats upon loading informatin from this Registry.
 * 
 * TODO separate the persistence mechanism from this object, so that it may be
 * updated/plugged-in. TODO perhaps remove reliance on JDOM. Although JDOM makes
 * it easy to program, it is another jar to ship.
 * 
 * @author Warren Bloomer
 * @author Ken Larson
 * @author Lyubomir Marinov
 */
public class Registry {

	private static Registry registry = null;
	
	private static Object registryMutex = new Object();

	private final RegistryContents registryContents = new RegistryContents();

	public static final int NUM_PLUGIN_TYPES = 5;

	/**
	 * Get the singleton.
	 * 
	 * @return The singleton JmfRegistry object.
	 */
	public static Registry getInstance() {
		synchronized (registryMutex) {
			if (null == registry) {
				registry = new Registry();
			}
			return registry;
		}
	}

	// for unit tests, add
	// -Dnet.sf.fmj.utility.JmfRegistry.disableLoad=true
	// -Dnet.sf.fmj.utility.JmfRegistry.JMFDefaults=true

	/**
	 * Private constructor.
	 */
	private Registry() {
			setDefaults(); // this capability needed for unit tests or
	}

	public synchronized boolean addDevice(CaptureDeviceInfo newDevice) {
		return registryContents.captureDeviceInfoList.add(newDevice);

	}

	@SuppressWarnings("unchecked")
	public synchronized Vector<String> getContentPrefixList() {
		return (Vector<String>) registryContents.contentPrefixList.clone();
	}

	/* ---------------- for PluginManager ---------------------- */

	@SuppressWarnings("unchecked")
	public synchronized Vector<CaptureDeviceInfo> getDeviceList() {
		return (Vector<CaptureDeviceInfo>) registryContents.captureDeviceInfoList.clone();
	}

	/* --------- for PackageManager ----------------- */

	/**
	 * pluginType = [1..NUM_PLUGIN_TYPES]
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<String> getPluginList(int pluginType) {
		// get the list of plugins of the given type
		Vector<String> pluginList = registryContents.plugins[pluginType - 1];

		return (List<String>) pluginList.clone();
	}

	/* ---------------- for mime-type --------------------- */
	@SuppressWarnings("unchecked")
	public synchronized Vector<String> getProtocolPrefixList() {
		return (Vector<String>) registryContents.protocolPrefixList.clone();
	}

	/* ---------------- for CaptureDeviceManager --------------------- */

	public synchronized boolean removeDevice(CaptureDeviceInfo device) {
		return registryContents.captureDeviceInfoList.remove(device);
	}

	/**
	 * Prefices for determining Handlers for content of particular MIME types.
	 * 
	 * MIME types are converted to package names, e.g. text/html -> text.html
	 * 
	 * These package names are added to the prefices in this list to determine
	 * Handlers for them. i.e. "<i>prefix</i>.media.content.text.html.Handler"
	 */
	public synchronized void setContentPrefixList(List<String> list) {
		registryContents.contentPrefixList.clear();
		registryContents.contentPrefixList.addAll(list);
	}

	/* ------------------------- defaults ------------------------- */

	private void setDefaults() {

		registryContents.protocolPrefixList.addAll(RegistryDefaults.protocolPrefixList());
		registryContents.contentPrefixList.addAll(RegistryDefaults.contentPrefixList());
		final List<Object> list = RegistryDefaults.plugInList();
		for (Object o : list) {
			if (o instanceof PlugInInfo) {
				final PlugInInfo i = (PlugInInfo) o;
				registryContents.plugins[i.type - 1].add(i.className);
			} else {
				final PlugInInfo i = PlugInUtility.getPlugInInfo((String) o);
				if (i != null)
					registryContents.plugins[i.type - 1].add(i.className);
			}
		}
	}

	/**
	 * Plugin list of PluginInfo objects = { classname, inputFormats,
	 * outputFormats, pluginType};
	 * 
	 * @param pluginType
	 *            range of [1..NUM_PLUGIN_TYPES]
	 * @param plugins
	 */
	public synchronized void setPluginList(int pluginType, List<String> plugins) {
		// use the plugin vector for the given type
		Vector<String> pluginList = registryContents.plugins[pluginType - 1];
		pluginList.clear();
		pluginList.addAll(plugins);
	}

	/**
	 * Prefices for determining URL Handlers for content delivered via
	 * particular protocol.
	 * 
	 * Protocols are converted to package names, e.g. "http" -> "http" These
	 * package names are added to the prefices in this list to determine
	 * Handlers for them. i.e. "<i>prefix</i>.media.protocol.http.Handler"
	 * 
	 * TODO perhaps use URLStreamHandlers
	 * 
	 */
	public synchronized void setProtocolPrefixList(List<String> list) {
		registryContents.protocolPrefixList.clear();
		registryContents.protocolPrefixList.addAll(list);
	}
}
