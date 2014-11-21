/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.service.neomedia.codec;

/**
 * Defines constants which are used by both neomedia clients and
 * implementations.
 * 
 * @author Lyubomir Marinov
 * @author Boris Grozev
 */
public class Constants {
	/**
	 * The list of well-known sample rates of audio data used throughout
	 * neomedia.
	 */
	public static final double[] AUDIO_SAMPLE_RATES = { 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000 };
	/**
	 * The Android Surface constant. It is used as VideoFormat pseudo encoding
	 * in which case the object is passed through the buffers instead of byte
	 * array for example.
	 */
	public static final String ANDROID_SURFACE = "android_surface";

	/**
	 * mode : Frame size for the encoding/decoding 20 - 20 ms 30 - 30 ms
	 */
	public static int ILBC_MODE = 30;

	/**
	 * The name of the property used to control the Opus encoder
	 * "audio bandwidth" setting
	 */
	public static final String PROP_OPUS_BANDWIDTH = "net.java.sip.communicator.impl.neomedia.codec.audio.opus.encoder" + ".AUDIO_BANDWIDTH";

	/**
	 * The name of the property used to control the Opus encoder bitrate setting
	 */
	public static final String PROP_OPUS_BITRATE = "net.java.sip.communicator.impl.neomedia.codec.audio.opus.encoder" + ".BITRATE";

	/**
	 * The name of the property used to control the Opus encoder 'complexity'
	 * setting
	 */
	public static final String PROP_OPUS_COMPLEXITY = "net.java.sip.communicator.impl.neomedia.codec.audio.opus.encoder" + ".COMPLEXITY";

	/**
	 * The name of the property used to control the Opus encoder "DTX" setting
	 */
	public static final String PROP_OPUS_DTX = "net.java.sip.communicator.impl.neomedia.codec.audio.opus.encoder" + ".DTX";

	/**
	 * The name of the property used to control whether FEC is enabled for the
	 * Opus encoder
	 */
	public static final String PROP_OPUS_FEC = "net.java.sip.communicator.impl.neomedia.codec.audio.opus.encoder" + ".FEC";

	/**
	 * The name of the property used to control the Opus encoder
	 * "minimum expected packet loss" setting
	 */
	public static final String PROP_OPUS_MIN_EXPECTED_PACKET_LOSS = "net.java.sip.communicator.impl.neomedia.codec.audio.opus.encoder" + ".MIN_EXPECTED_PACKET_LOSS";

	/**
	 * The name of the property used to control whether FEC support is
	 * advertised for SILK
	 */
	public static final String PROP_SILK_ADVERSISE_FEC = "net.java.sip.communicator.impl.neomedia.codec.audio.silk" + ".ADVERTISE_FEC";

	/**
	 * The name of the property used to control the the 'always assume packet
	 * loss' setting for SILK
	 */
	public static final String PROP_SILK_ASSUME_PL = "net.java.sip.communicator.impl.neomedia.codec.audio.silk.encoder" + ".AWLAYS_ASSUME_PACKET_LOSS";

	/**
	 * The name of the property used to control whether FEC is enabled for SILK
	 */
	public static final String PROP_SILK_FEC = "net.java.sip.communicator.impl.neomedia.codec.audio.silk.encoder" + ".USE_FEC";

	/**
	 * The name of the property used to control the SILK 'speech activity
	 * threshold'
	 */
	public static final String PROP_SILK_FEC_SAT = "net.java.sip.communicator.impl.neomedia.codec.audio.silk.encoder" + ".SPEECH_ACTIVITY_THRESHOLD";
}