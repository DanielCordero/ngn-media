/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.impl.neomedia.codec;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import javax.media.Codec;
import javax.media.Multiplexer;
import javax.media.PackageManager;
import javax.media.PlugInManager;

import org.jitsi.util.Logger;
import org.jitsi.util.OSUtils;

/**
 * Utility class that handles registration of FMJ packages and plugins.
 * 
 * @author Damian Minkov
 * @author Lyubomir Marinov
 * @author Boris Grozev
 */
public class FMJPlugInConfiguration {

	/**
	 * The additional custom JMF codecs.
	 */
	private static final String[] CUSTOM_CODECS = {
			// OSUtils.IS_ANDROID
			// ?
			// org.jitsi.impl.neomedia.codec.video.AndroidEncoder.class.getName()
			// : null,
			// OSUtils.IS_ANDROID
			// ?
			// org.jitsi.impl.neomedia.codec.video.AndroidDecoder.class.getName()
			// : null,
			org.jitsi.impl.neomedia.codec.audio.alaw.DePacketizer.class.getName(), org.jitsi.impl.neomedia.codec.audio.alaw.JavaEncoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.alaw.Packetizer.class.getName(), org.jitsi.impl.neomedia.codec.audio.ulaw.JavaDecoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.ulaw.JavaEncoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.ulaw.Packetizer.class.getName(), org.jitsi.impl.neomedia.codec.audio.opus.JNIDecoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.opus.JNIEncoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.speex.JNIDecoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.speex.JNIEncoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.speex.SpeexResampler.class.getName(),
			// The MP3 encoder is not built for Android yet.
			org.jitsi.impl.neomedia.codec.audio.ilbc.JavaDecoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.ilbc.JavaEncoder.class.getName(), EncodingConfigurationImpl.G729 ? org.jitsi.impl.neomedia.codec.audio.g729.JavaDecoder.class.getName() : null, EncodingConfigurationImpl.G729 ? org.jitsi.impl.neomedia.codec.audio.g729.JavaEncoder.class.getName() : null, org.jitsi.impl.neomedia.codec.audio.g722.JNIDecoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.g722.JNIEncoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.silk.JavaDecoder.class.getName(), org.jitsi.impl.neomedia.codec.audio.silk.JavaEncoder.class.getName(),
			org.jitsi.impl.neomedia.codec.video.h263p.DePacketizer.class.getName(), org.jitsi.impl.neomedia.codec.video.h263p.JNIDecoder.class.getName(), org.jitsi.impl.neomedia.codec.video.h263p.JNIEncoder.class.getName(), org.jitsi.impl.neomedia.codec.video.h263p.Packetizer.class.getName(), org.jitsi.impl.neomedia.codec.video.h264.DePacketizer.class.getName(), org.jitsi.impl.neomedia.codec.video.h264.JNIDecoder.class.getName(), org.jitsi.impl.neomedia.codec.video.h264.JNIEncoder.class.getName(), org.jitsi.impl.neomedia.codec.video.h264.Packetizer.class.getName(), org.jitsi.impl.neomedia.codec.video.SwScale.class.getName(), org.jitsi.impl.neomedia.codec.video.vp8.Packetizer.class.getName(), org.jitsi.impl.neomedia.codec.video.vp8.DePacketizer.class.getName(),
			// TODO The VP8 codec is not built for Android yet.
			OSUtils.IS_ANDROID ? null : org.jitsi.impl.neomedia.codec.video.vp8.VPXEncoder.class.getName(), OSUtils.IS_ANDROID ? null : org.jitsi.impl.neomedia.codec.video.vp8.VPXDecoder.class.getName() };

	/**
	 * The package prefixes of the additional JMF <tt>DataSource</tt>s (e.g. low
	 * latency PortAudio and ALSA <tt>CaptureDevice</tt>s).
	 */
	private static final String[] CUSTOM_PACKAGES = { "org.jitsi.impl.neomedia.jmfext", "net.java.sip.communicator.impl.neomedia.jmfext", "net.sf.fmj" };

	/**
	 * The list of class names to register as FMJ plugins with type
	 * <tt>PlugInManager.MULTIPLEXER</tt>.
	 */
	private static final String[] CUSTOM_MULTIPLEXERS = {
	// org.jitsi.impl.neomedia.recording.BasicWavMux.class.getName()
	};

	/**
	 * The <tt>Logger</tt> used by the <tt>FMJPlugInConfiguration</tt> class for
	 * logging output.
	 */
	private static final Logger logger = Logger.getLogger(FMJPlugInConfiguration.class);

	/**
	 * Register in JMF the custom codecs we provide
	 */
	public static void registerCustomCodecs() {
		// Register the custom codecs which haven't already been registered.
		Collection<String> registeredPlugins = new HashSet<String>(PlugInManager.getPlugInList(null, null, PlugInManager.CODEC));

		for (String className : CUSTOM_CODECS) {
			if (className == null)
				continue;
			if (registeredPlugins.contains(className)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Codec " + className + " is already registered");
				}
			} else {
				boolean registered;
				Throwable exception = null;
				try {
					Codec codec = (Codec) Class.forName(className).newInstance();
					registered = PlugInManager.addPlugIn(className, codec.getSupportedInputFormats(), codec.getSupportedOutputFormats(null), PlugInManager.CODEC);
				} catch (Throwable ex) {
					registered = false;
					exception = ex;
				}
				if (registered) {
					if (logger.isTraceEnabled()) {
						logger.trace("Codec " + className + " is successfully registered");
					}
				} else {
					logger.warn("Codec " + className + " is NOT successfully registered", exception);
				}
			}
		}

		/*
		 * If Jitsi provides a codec which is also provided by FMJ and/or JMF,
		 * use Jitsi's version.
		 */
		@SuppressWarnings("unchecked")
		Vector<String> codecs = PlugInManager.getPlugInList(null, null, PlugInManager.CODEC);

		if (codecs != null) {
			boolean setPlugInList = false;

			for (int i = CUSTOM_CODECS.length - 1; i >= 0; i--) {
				String className = CUSTOM_CODECS[i];

				if (className != null) {
					int classNameIndex = codecs.indexOf(className);

					if (classNameIndex != -1) {
						codecs.remove(classNameIndex);
						codecs.add(0, className);
						setPlugInList = true;
					}
				}
			}

			if (setPlugInList)
				PlugInManager.setPlugInList(codecs, PlugInManager.CODEC);
		}

	}

	/**
	 * Register in JMF the custom packages we provide
	 */
	public static void registerCustomPackages() {

		@SuppressWarnings("unchecked")
		Vector<String> packages = PackageManager.getProtocolPrefixList();
		boolean loggerIsDebugEnabled = logger.isDebugEnabled();

		// We prefer our custom packages/protocol prefixes over FMJ's.
		for (int i = CUSTOM_PACKAGES.length - 1; i >= 0; i--) {
			String customPackage = CUSTOM_PACKAGES[i];

			/*
			 * Linear search in a loop but it doesn't have to scale since the
			 * list is always short.
			 */
			if (!packages.contains(customPackage)) {
				packages.add(0, customPackage);
				if (loggerIsDebugEnabled)
					logger.debug("Adding package  : " + customPackage);
			}
		}

		PackageManager.setProtocolPrefixList(packages);
		if (loggerIsDebugEnabled)
			logger.debug("Registering new protocol prefix list: " + packages);

	}

	/**
	 * Registers custom libjitsi <tt>Multiplexer</tt> implementations.
	 */
	@SuppressWarnings("unchecked")
	public static void registerCustomMultiplexers() {

		Collection<String> registeredMuxers = new HashSet<String>(PlugInManager.getPlugInList(null, null, PlugInManager.MULTIPLEXER));

		boolean commit = false;
		for (String className : CUSTOM_MULTIPLEXERS) {
			if (className == null)
				continue;
			if (registeredMuxers.contains(className)) {
				if (logger.isDebugEnabled())
					logger.debug("Multiplexer " + className + " is already " + "registered");
				continue;
			}

			boolean registered;
			Throwable exception = null;
			try {
				Multiplexer multiplexer = (Multiplexer) Class.forName(className).newInstance();

				registered = PlugInManager.addPlugIn(className, multiplexer.getSupportedInputFormats(), multiplexer.getSupportedOutputContentDescriptors(null), PlugInManager.MULTIPLEXER);
			} catch (Throwable ex) {
				registered = false;
				exception = ex;
			}

			if (registered) {
				if (logger.isTraceEnabled()) {
					logger.trace("Codec " + className + " is successfully registered");
				}
			} else {
				logger.warn("Codec " + className + " is NOT successfully registered", exception);
			}

			commit |= registered;
		}

	}
}
