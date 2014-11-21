/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.service.libjitsi;

import org.jitsi.impl.configuration.ConfigurationServiceImpl;
import org.jitsi.impl.fileaccess.FileAccessServiceImpl;
import org.jitsi.impl.neomedia.MediaServiceImpl;
import org.jitsi.impl.neomedia.notify.AudioNotifierServiceImpl;
import org.jitsi.service.audionotifier.AudioNotifierService;
import org.jitsi.service.configuration.ConfigurationService;
import org.jitsi.service.fileaccess.FileAccessService;
import org.jitsi.service.neomedia.MediaService;
import org.jitsi.service.packetlogging.PacketLoggingService;
import org.jitsi.service.resources.ResourceManagementService;
import org.jitsi.util.Logger;

public class LibJitsi {
	/**
	 * The <tt>Logger</tt> used by the <tt>LibJitsi</tt> class for logging
	 * output.
	 */
	private static final Logger logger = Logger.getLogger(LibJitsi.class);

	private static ConfigurationService cs = null;

	private static FileAccessService fas = null;

	private static MediaService ms = null;

	private static AudioNotifierService ans = null;

	private static PacketLoggingService pls = null;

	private static ResourceManagementService rms = null;

	/**
	 * Gets the <tt>AudioNotifierService</tt> instance. If no existing
	 * <tt>AudioNotifierService</tt> instance is known to the library, tries to
	 * initialize a new one. (Such a try to initialize a new instance is
	 * performed just once while the library is initialized.)
	 * 
	 * @return the <tt>AudioNotifierService</tt> instance known to the library
	 *         or <tt>null</tt> if no <tt>AudioNotifierService</tt> instance is
	 *         known to the library
	 */
	public static AudioNotifierService getAudioNotifierService() {
		return ans;
	}

	/**
	 * Gets the <tt>ConfigurationService</tt> instance. If no existing
	 * <tt>ConfigurationService</tt> instance is known to the library, tries to
	 * initialize a new one. (Such a try to initialize a new instance is
	 * performed just once while the library is initialized.)
	 * 
	 * @return the <tt>ConfigurationService</tt> instance known to the library
	 *         or <tt>null</tt> if no <tt>ConfigurationService</tt> instance is
	 *         known to the library
	 */
	public static ConfigurationService getConfigurationService() {
		return cs;
	}

	/**
	 * Gets the <tt>FileAccessService</tt> instance. If no existing
	 * <tt>FileAccessService</tt> instance is known to the library, tries to
	 * initialize a new one. (Such a try to initialize a new instance is
	 * performed just once while the library is initialized.)
	 * 
	 * @return the <tt>FileAccessService</tt> instance known to the library or
	 *         <tt>null</tt> if no <tt>FileAccessService</tt> instance is known
	 *         to the library
	 */
	public static FileAccessService getFileAccessService() {
		return fas;
	}

	/**
	 * Gets the <tt>MediaService</tt> instance. If no existing
	 * <tt>MediaService</tt> instance is known to the library, tries to
	 * initialize a new one. (Such a try to initialize a new instance is
	 * performed just once while the library is initialized.)
	 * 
	 * @return the <tt>MediaService</tt> instance known to the library or
	 *         <tt>null</tt> if no <tt>MediaService</tt> instance is known to
	 *         the library
	 */
	public static MediaService getMediaService() {
		return ms;
	}

	/**
	 * Gets the <tt>PacketLoggingService</tt> instance. If no existing
	 * <tt>PacketLoggingService</tt> instance is known to the library, tries to
	 * initialize a new one. (Such a try to initialize a new instance is
	 * performed just once while the library is initialized.)
	 * 
	 * @return the <tt>PacketLoggingService</tt> instance known to the library
	 *         or <tt>null</tt> if no <tt>PacketLoggingService</tt> instance is
	 *         known to the library
	 */
	public static PacketLoggingService getPacketLoggingService() {
		return pls;
	}

	/**
	 * Gets the <tt>ResourceManagementService</tt> instance. If no existing
	 * <tt>ResourceManagementService</tt> instance is known to the library,
	 * tries to initialize a new one. (Such a try to initialize a new instance
	 * is performed just once while the library is initialized.)
	 * 
	 * @return the <tt>ResourceManagementService</tt> instance known to the
	 *         library or <tt>null</tt> if no <tt>ResourceManagementService</tt>
	 *         instance is known to the library
	 */
	public static ResourceManagementService getResourceManagementService() {
		return rms;
	}

	/**
	 * Starts/initializes the use of the <tt>libjitsi</tt> library.
	 */
	public static void start() {
		cs = new ConfigurationServiceImpl();

		fas = new FileAccessServiceImpl();

		ms = new MediaServiceImpl();

		ans = new AudioNotifierServiceImpl();

		pls = null;
		rms = null;
	}

	/**
	 * Stops/uninitializes the use of the <tt>libjitsi</tt> library.
	 */
	public static void stop() {
	}

	/**
	 * Initializes a new <tt>LibJitsi</tt> instance.
	 */
	protected LibJitsi() {
	}

}
