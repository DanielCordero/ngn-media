package net.sf.fmj.media.rtp.rtpmediaformat;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.sdp.SdpConstants;

import org.jitsi.impl.neomedia.device.ScreenDeviceImpl;
import org.jitsi.service.neomedia.device.ScreenDevice;
import org.jitsi.util.OSUtils;

/**
 * The <tt>RTPMediaFormatFactory</tt> is rtp media format manager
 * 
 * @author Emil Ivov
 */
public class RTPMediaFormatUtils {
	/**
	 * An empty array with <tt>MediaFormat</tt> element type. Explicitly defined
	 * in order to reduce unnecessary allocations, garbage collection.
	 */
	public static final RTPMediaFormat[] EMPTY_MEDIA_FORMATS = new RTPMediaFormat[0];

	/**
	 * An empty array with <tt>Format</tt> element type. Explicitly defined in
	 * order to reduce unnecessary allocations, garbage collection.
	 */
	public static final Format[] EMPTY_FORMATS = new Format[0];

	/**
	 * The <tt>Map</tt> of JMF-specific encodings to well-known encodings as
	 * defined in RFC 3551.
	 */
	private static final Map<String, String> jmfEncodingToEncodings = new HashMap<String, String>();

	/**
	 * The maximum number of channels for audio that is available through
	 * <tt>MediaUtils</tt>.
	 */
	public static final int MAX_AUDIO_CHANNELS = 0;

	/**
	 * The maximum sample rate for audio that is available through
	 * <tt>MediaUtils</tt>.
	 */
	public static final double MAX_AUDIO_SAMPLE_RATE = 0;

	/**
	 * The maximum sample size in bits for audio that is available through
	 * <tt>MediaUtils</tt>.
	 */
	public static final int MAX_AUDIO_SAMPLE_SIZE_IN_BITS = 0;

	/**
	 * The native byte order of the hardware upon which this Java virtual
	 * machine is running expressed in the <tt>endian</tt> term of
	 * {@link AudioFormat}.
	 */
	public static final int NATIVE_AUDIO_FORMAT_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? AudioFormat.BIG_ENDIAN : AudioFormat.LITTLE_ENDIAN;

	/**
	 * Audio RTP Media Format Map Key = Encoding
	 */
	private static final LinkedHashMap<String, RTPMediaFormat[]> audioRTPMediaFormats = new LinkedHashMap<String, RTPMediaFormat[]>();

	/**
	 * Video RTP Media Format Map Key = Encoding
	 */
	private static final LinkedHashMap<String, RTPMediaFormat> videoRTPMediaFormats = new LinkedHashMap<String, RTPMediaFormat>();

	static {
		// PCMU和PCMA
		addMediaFormats(RTPMediaType.AUDIO, (byte) SdpConstants.PCMU, AudioFormat.ULAW_RTP, "PCMU", null, new Format[] { new AudioFormat("PCMU", 8000, -1, 1) });
		addMediaFormats(RTPMediaType.AUDIO, (byte) SdpConstants.PCMA, AudioFormat.ALAW_RTP, "PCMA", null, new Format[] { new AudioFormat("PCMA", 8000, -1, 1) });

		// linear NOT clock rate format
		Format[] linearnot = new AudioFormat[] { new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED, 16, 2, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.LINEAR, Format.NOT_SPECIFIED, 8, 2, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.LINEAR, AudioFormat.LINEAR, null, linearnot);

		// linear format
		Format[] linearteam = new AudioFormat[] { new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED), new AudioFormat(AudioFormat.LINEAR, 12000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED), new AudioFormat(AudioFormat.LINEAR, 16000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED), new AudioFormat(AudioFormat.LINEAR, 24000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED), new AudioFormat(AudioFormat.LINEAR, 32000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED), new AudioFormat(AudioFormat.LINEAR, 48000, 16, 1, RTPMediaFormatUtils.NATIVE_AUDIO_FORMAT_ENDIAN, AudioFormat.SIGNED), new AudioFormat(AudioFormat.LINEAR, 48000, 16, 2, RTPMediaFormatUtils.NATIVE_AUDIO_FORMAT_ENDIAN, AudioFormat.SIGNED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.LINEAR, AudioFormat.LINEAR, null, linearteam);

		// ALAW_RTP format
		Format[] alawrtpFormat = new AudioFormat[] { new AudioFormat(AudioFormat.ALAW_RTP, Format.NOT_SPECIFIED, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, 8, Format.NOT_SPECIFIED, Format.byteArray) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.ALAW_RTP, AudioFormat.ALAW_RTP, null, alawrtpFormat);

		// G722_RTP format
		Format[] g722team = new AudioFormat[] { new AudioFormat(AudioFormat.G722_RTP, 8000, Format.NOT_SPECIFIED /*
																												 * *
																												 * sampleSizeInBits
																												 */, 1) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.G722_RTP, AudioFormat.G722_RTP, null, g722team);

		// ILBC_RTP format
		Format[] ILBCRTPteam = new AudioFormat[] { new AudioFormat(AudioFormat.ILBC_RTP, 8000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.ILBC_RTP, AudioFormat.ILBC_RTP, null, ILBCRTPteam);

		// SPEEX_RTP format
		Format[] SPEEXRTPteam = new AudioFormat[] { new AudioFormat(AudioFormat.SPEEX_RTP, 8000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SPEEX_RTP, 16000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SPEEX_RTP, 32000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.SPEEX_RTP, AudioFormat.SPEEX_RTP, null, SPEEXRTPteam);

		// Silk_RTP format
		Format[] SilkTeam = new AudioFormat[] { new AudioFormat(AudioFormat.SILK_RTP, 8000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SILK_RTP, 12000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SILK_RTP, 16000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SILK_RTP, 24000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.SILK_RTP, AudioFormat.SILK_RTP, null, SilkTeam);

		// OPUS_RTP format
		Format[] OPUSRTPteam = new AudioFormat[] { new AudioFormat(AudioFormat.OPUS_RTP, 48000, Format.NOT_SPECIFIED, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.byteArray),new AudioFormat(AudioFormat.OPUS_RTP, 48000, Format.NOT_SPECIFIED, 2, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.byteArray) };
		Map<String, String> opusFormatParams = new HashMap<String, String>();
		opusFormatParams.put("useinbandfec", "0");
		opusFormatParams.put("usedtx", "1");
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.OPUS_RTP, AudioFormat.OPUS_RTP, opusFormatParams, OPUSRTPteam);		
		
		// G722 format
		Format[] g722team333 = new AudioFormat[] { new AudioFormat(AudioFormat.G722, 8000, Format.NOT_SPECIFIED /*
																												 * *
																												 * sampleSizeInBits
																												 */, 1) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.G722_RTP, AudioFormat.G722, null, g722team333);

		// ALAW format
		Format[] alawFormat = new AudioFormat[] { new AudioFormat(AudioFormat.ALAW, 8000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.ALAW, Format.NOT_SPECIFIED, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, 8, Format.NOT_SPECIFIED, Format.byteArray) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.ALAW_RTP, AudioFormat.ALAW, null, alawFormat);

		// ILBC format
		Format[] ILBCRTPteam3334 = new AudioFormat[] { new AudioFormat(AudioFormat.ILBC, 8000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.ILBC_RTP, AudioFormat.ILBC, null, ILBCRTPteam3334);

		// SPEEX format
		Format[] SPEEXRTPteam333 = new AudioFormat[] { new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SPEEX, 16000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SPEEX, 32000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.SPEEX_RTP, AudioFormat.SPEEX, null, SPEEXRTPteam333);

		// Silk format
		Format[] SilkTeam333 = new AudioFormat[] { new AudioFormat(AudioFormat.SILK, 8000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SILK, 12000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SILK, 16000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED), new AudioFormat(AudioFormat.SILK, 24000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.SILK_RTP, AudioFormat.SILK, null, SilkTeam333);

		// ULAW format
		Format[] ULAWteam = new AudioFormat[] { new AudioFormat(AudioFormat.ULAW, 8000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.ULAW, AudioFormat.ULAW, null, ULAWteam);

		// OPUS format
		Format[] OPUSRTPteam333 = new AudioFormat[] { new AudioFormat("opus", 48000, 16, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.byteArray),new AudioFormat("opus", 48000, 16, 2, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.byteArray) };
		Map<String, String> opusFormatParams333 = new HashMap<String, String>();
		opusFormatParams.put("useinbandfec", "0");
		opusFormatParams.put("usedtx", "1");
		addMediaFormats(RTPMediaType.AUDIO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, AudioFormat.OPUS_RTP, "opus", opusFormatParams333, OPUSRTPteam333);

		addMediaFormats(RTPMediaType.AUDIO, (byte) SdpConstants.G722, AudioFormat.G722_RTP, "G722", null, new Format[] { new AudioFormat("G722", 8000, -1, 1) });

		Map<String, String> g729FormatParams = new HashMap<String, String>();
		g729FormatParams.put("annexb", "no");

		addMediaFormats(RTPMediaType.AUDIO, (byte) SdpConstants.G729, AudioFormat.G729_RTP, "G729", g729FormatParams, new Format[] { new AudioFormat("G729", 8000, -1, 1) });

		// Although we use "red" and "ulpfec" as jmf encodings here, FMJ
		// should never see RTP packets of these types. Such packets should
		// handled by transform engines before being passed to FMJ.
		addMediaFormats(RTPMediaType.VIDEO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, VideoFormat.RED, VideoFormat.RED, null, new VideoFormat[] { new VideoFormat(VideoFormat.RED) });
		addMediaFormats(RTPMediaType.VIDEO, RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, VideoFormat.ULPFEC, VideoFormat.ULPFEC, null, new VideoFormat[] { new VideoFormat(VideoFormat.ULPFEC) });

		/* H264 */
		Map<String, String> h264FormatParams = new HashMap<String, String>();
		String packetizationMode = RTPMediaFormat.H264_PACKETIZATION_MODE_FMTP;
		Map<String, String> h264AdvancedAttributes = new HashMap<String, String>();

		/*
		 * Disable PLI because the periodic intra-refresh feature of FFmpeg/x264
		 * is used.
		 */
		// h264AdvancedAttributes.put("rtcp-fb", "nack pli");

		/*
		 * XXX The initialization of MediaServiceImpl is very complex so it is
		 * wise to not reference it at the early stage of its initialization.
		 */
		ScreenDevice screen = ScreenDeviceImpl.getDefaultScreenDevice();
		java.awt.Dimension res = (screen == null) ? null : screen.getSize();

		h264AdvancedAttributes.put("imageattr", createImageAttr(null, res));

		// baseline profile, common features, HD capable level 3.1
		h264FormatParams.put("profile-level-id", "42E01f");

		// packetization-mode=1
		h264FormatParams.put(packetizationMode, "1");
		addMediaFormats(RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, "H264", RTPMediaType.VIDEO, VideoFormat.H264_RTP, h264FormatParams, h264AdvancedAttributes);

		// packetization-mode=0
		/*
		 * XXX At the time of this writing,
		 * EncodingConfiguration#compareEncodingPreferences(MediaFormat,
		 * MediaFormat) is incomplete and considers two MediaFormats to be equal
		 * if they have an equal number of format parameters (given that the
		 * encodings and clock rates are equal, of course). Either fix the
		 * method in question or don't add a format parameter for
		 * packetization-mode 0 (which is equivalent to having
		 * packetization-mode explicitly defined as 0 anyway, according to the
		 * respective RFC).
		 */
		h264FormatParams.remove(packetizationMode);
		addMediaFormats(RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, "H264", RTPMediaType.VIDEO, VideoFormat.H264_RTP, h264FormatParams, h264AdvancedAttributes);

		/* H263+ */
		Map<String, String> h263FormatParams = new HashMap<String, String>();
		Map<String, String> h263AdvancedAttributes = new LinkedHashMap<String, String>();

		/*
		 * The maximum resolution we can receive is the size of our screen
		 * device.
		 */
		if (res != null)
			h263FormatParams.put("CUSTOM", res.width + "," + res.height + ",2");
		h263FormatParams.put("VGA", "2");
		h263FormatParams.put("CIF", "1");
		h263FormatParams.put("QCIF", "1");

		addMediaFormats(RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, "H263-1998", RTPMediaType.VIDEO, VideoFormat.H263P_RTP, h263FormatParams, h263AdvancedAttributes);

		addMediaFormats(RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, "VP8", RTPMediaType.VIDEO, VideoFormat.VP8_RTP, null, null);
	}

	/**
	 * Adds a new mapping of a specific RTP payload type to a list of
	 * <tt>MediaFormat</tt>s of a specific <tt>MediaType</tt>, with a specific
	 * JMF encoding and, optionally, with specific clock rates.
	 * 
	 * @param payloadType
	 *            the RTP payload type to be associated with a list of
	 *            <tt>MediaFormat</tt>s
	 * @param encoding
	 *            the well-known encoding (name) corresponding to
	 *            <tt>rtpPayloadType</tt> (in contrast to the JMF-specific
	 *            encoding specified by <tt>jmfEncoding</tt>)
	 * @param mediaType
	 *            the <tt>MediaType</tt> of the <tt>MediaFormat</tt>s to be
	 *            associated with <tt>rtpPayloadType</tt>
	 * @param jmfEncoding
	 *            the JMF encoding of the <tt>MediaFormat</tt>s to be associated
	 *            with <tt>rtpPayloadType</tt>
	 * @param formats
	 *            the optional list of clock rates of the <tt>MediaFormat</tt>s
	 *            to be associated with <tt>rtpPayloadType</tt>
	 */
	private static void addMediaFormats(RTPMediaType mediaType, byte payloadType, String jmfEncoding, String encoding, Map<String, String> formatSettings, Format[] formats) {
		addMediaFormats(mediaType, payloadType, jmfEncoding, encoding, formatSettings, null, formats);
	}

	/**
	 * Adds a new mapping of a specific RTP payload type to a list of
	 * <tt>MediaFormat</tt>s of a specific <tt>MediaType</tt>, with a specific
	 * JMF encoding and, optionally, with specific clock rates.
	 * 
	 * @param rtpPayloadType
	 *            the RTP payload type to be associated with a list of
	 *            <tt>MediaFormat</tt>s
	 * @param encoding
	 *            the well-known encoding (name) corresponding to
	 *            <tt>rtpPayloadType</tt> (in contrast to the JMF-specific
	 *            encoding specified by <tt>jmfEncoding</tt>)
	 * @param mediaType
	 *            the <tt>MediaType</tt> of the <tt>MediaFormat</tt>s to be
	 *            associated with <tt>rtpPayloadType</tt>
	 * @param jmfEncoding
	 *            the JMF encoding of the <tt>MediaFormat</tt>s to be associated
	 *            with <tt>rtpPayloadType</tt>
	 * @param formatParameters
	 *            the set of format-specific parameters of the
	 *            <tt>MediaFormat</tt>s to be associated with
	 *            <tt>rtpPayloadType</tt>
	 * @param advancedAttributes
	 *            the set of advanced attributes of the <tt>MediaFormat</tt>s to
	 *            be associated with <tt>rtpPayload</tt>
	 * @param clockRates
	 *            the optional list of clock rates of the <tt>MediaFormat</tt>s
	 *            to be associated with <tt>rtpPayloadType</tt>
	 */
	private static void addMediaFormats(byte rtpPayloadType, String encoding, RTPMediaType mediaType, String jmfEncoding, Map<String, String> formatParameters, Map<String, String> advancedAttributes, double... clockRates) {
		addMediaFormats(rtpPayloadType, encoding, mediaType, jmfEncoding, 1 /* channel */, formatParameters, advancedAttributes, clockRates);
	}

	/**
	 * Adds a new mapping of a specific RTP payload type to a list of
	 * <tt>MediaFormat</tt>s of a specific <tt>MediaType</tt>, with a specific
	 * JMF encoding and, optionally, with specific clock rates.
	 * 
	 * @param rtpPayloadType
	 *            the RTP payload type to be associated with a list of
	 *            <tt>MediaFormat</tt>s
	 * @param encoding
	 *            the well-known encoding (name) corresponding to
	 *            <tt>rtpPayloadType</tt> (in contrast to the JMF-specific
	 *            encoding specified by <tt>jmfEncoding</tt>)
	 * @param mediaType
	 *            the <tt>MediaType</tt> of the <tt>MediaFormat</tt>s to be
	 *            associated with <tt>rtpPayloadType</tt>
	 * @param jmfEncoding
	 *            the JMF encoding of the <tt>MediaFormat</tt>s to be associated
	 *            with <tt>rtpPayloadType</tt>
	 * @param channels
	 *            number of channels
	 * @param formatParameters
	 *            the set of format-specific parameters of the
	 *            <tt>MediaFormat</tt>s to be associated with
	 *            <tt>rtpPayloadType</tt>
	 * @param advancedAttributes
	 *            the set of advanced attributes of the <tt>MediaFormat</tt>s to
	 *            be associated with <tt>rtpPayload</tt>
	 * @param clockRates
	 *            the optional list of clock rates of the <tt>MediaFormat</tt>s
	 *            to be associated with <tt>rtpPayloadType</tt>
	 */
	@SuppressWarnings("unchecked")
	public static void addMediaFormats(byte rtpPayloadType, String encoding, RTPMediaType mediaType, String jmfEncoding, int channels, Map<String, String> formatParameters, Map<String, String> advancedAttributes, double... clockRates) {
		int clockRateCount = clockRates.length;
		List<Format> formats = new ArrayList<Format>(clockRateCount);

		if (clockRateCount > 0) {
			for (double clockRate : clockRates) {
				Format format;

				switch (mediaType) {
				case AUDIO:
					if (channels == 1) {
						format = new AudioFormat(jmfEncoding);
						((AudioFormat) format).setSampleRate(clockRate);
					} else {
						format = new AudioFormat(jmfEncoding, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, channels);
					}

					break;
				case VIDEO:
					VideoFormat vf = new VideoFormat(jmfEncoding);
					vf.setClockRate(RTPMediaFormat.DEFAULT_CLOCK_RATE);
					format = vf;
					break;
				default:
					throw new IllegalArgumentException("mediaType");
				}

				formats.add(format);
			}
		} else {
			Format format;
			double clockRate;

			switch (mediaType) {
			case AUDIO:
				AudioFormat audioFormat = new AudioFormat(jmfEncoding);

				format = audioFormat;
				clockRate = audioFormat.getSampleRate();
				break;
			case VIDEO:
				VideoFormat vf = new VideoFormat(jmfEncoding);
				vf.setClockRate(RTPMediaFormat.DEFAULT_CLOCK_RATE);
				format = vf;
				break;
			default:
				throw new IllegalArgumentException("mediaType");
			}

			formats.add(format);
		}

		if (formats.size() > 0) {
			addMediaFormats(mediaType, rtpPayloadType, jmfEncoding, encoding, formatParameters, advancedAttributes, formats.toArray(EMPTY_FORMATS));
		}
	}

	/**
	 * Adds a new mapping of a specific RTP payload type to a list of
	 * <tt>MediaFormat</tt>s of a specific <tt>MediaType</tt>, with a specific
	 * JMF encoding and, optionally, with specific clock rates.
	 * 
	 * @param payloadType
	 *            the RTP payload type to be associated with a list of
	 *            <tt>MediaFormat</tt>s
	 * @param encoding
	 *            the well-known encoding (name) corresponding to
	 *            <tt>rtpPayloadType</tt> (in contrast to the JMF-specific
	 *            encoding specified by <tt>jmfEncoding</tt>)
	 * @param mediaType
	 *            the <tt>MediaType</tt> of the <tt>MediaFormat</tt>s to be
	 *            associated with <tt>rtpPayloadType</tt>
	 * @param jmfEncoding
	 *            the JMF encoding of the <tt>MediaFormat</tt>s to be associated
	 *            with <tt>rtpPayloadType</tt>
	 * @param formats
	 *            the optional list of clock rates of the <tt>MediaFormat</tt>s
	 *            to be associated with <tt>rtpPayloadType</tt>
	 */
	public static void addMediaFormats(RTPMediaType mediaType, byte payloadType, String jmfEncoding, String encoding, Map<String, String> formatSettings, Map<String, String> advancedSettings, Format[] formats) {
		if (formats != null && formats.length > 0) {
			if (mediaType.equals(RTPMediaType.AUDIO)) {
				List<RTPMediaFormat> list = new ArrayList<RTPMediaFormat>();
				for (Format format : formats) {
					RTPMediaFormat rmf = new RTPMediaFormat(format, mediaType, payloadType);
					rmf.setFormatSettings(formatSettings);
					rmf.setAdvancedSettings(advancedSettings);
					list.add(rmf);
				}
				if (list.size() > 0) {
					if (audioRTPMediaFormats.containsKey(encoding)) {
						for (RTPMediaFormat mf : audioRTPMediaFormats.get(encoding)) {
							list.add(mf);
						}
					}

					audioRTPMediaFormats.put(encoding, list.toArray(EMPTY_MEDIA_FORMATS));
				}
			} else if (mediaType.equals(RTPMediaType.VIDEO)) {
				((VideoFormat) formats[0]).setClockRate(RTPMediaFormat.DEFAULT_CLOCK_RATE);

				RTPMediaFormat rmfs = new RTPMediaFormat(formats[0], mediaType, payloadType);
				rmfs.setFormatSettings(formatSettings);
				rmfs.setAdvancedSettings(advancedSettings);
				videoRTPMediaFormats.put(encoding, rmfs);
			}

			jmfEncodingToEncodings.put(jmfEncoding, encoding);
		}
	}

	/**
	 * Creates value of an imgattr.
	 * 
	 * http://tools.ietf.org/html/draft-ietf-mmusic-image-attributes-04
	 * 
	 * @param sendSize
	 *            maximum size peer can send
	 * @param maxRecvSize
	 *            maximum size peer can display
	 * @return string that represent imgattr that can be encoded via SIP/SDP or
	 *         XMPP/Jingle
	 */
	public static String createImageAttr(java.awt.Dimension sendSize, java.awt.Dimension maxRecvSize) {
		StringBuffer img = new StringBuffer();

		/* send width */
		if (sendSize != null) {
			/* single value => send [x=width,y=height] */
			/*
			 * img.append("send [x="); img.append((int)sendSize.getWidth());
			 * img.append(",y="); img.append((int)sendSize.getHeight());
			 * img.append("]");
			 */
			/* send [x=[min-max],y=[min-max]] */
			img.append("send [x=[0-");
			img.append((int) sendSize.getWidth());
			img.append("],y=[0-");
			img.append((int) sendSize.getHeight());
			img.append("]]");
			/*
			 * else { // range img.append(" send [x=[");
			 * img.append((int)minSendSize.getWidth()); img.append("-");
			 * img.append((int)maxSendSize.getWidth()); img.append("],y=[");
			 * img.append((int)minSendSize.getHeight()); img.append("-");
			 * img.append((int)maxSendSize.getHeight()); img.append("]]"); }
			 */
		} else {
			/* can send "all" sizes */
			img.append("send *");
		}

		/* receive size */
		if (maxRecvSize != null) {
			/*
			 * basically we can receive any size up to our screen display size
			 */

			/* recv [x=[min-max],y=[min-max]] */
			img.append(" recv [x=[0-");
			img.append((int) maxRecvSize.getWidth());
			img.append("],y=[0-");
			img.append((int) maxRecvSize.getHeight());
			img.append("]]");
		} else {
			/* accept all sizes */
			img.append(" recv *");
		}

		return img.toString();
	}

	/**
	 * Gets the index of a specific <tt>MediaFormat</tt> instance within the
	 * internal storage of <tt>MediaUtils</tt>. Since the index is in the
	 * internal storage which may or may not be one and the same for the various
	 * <tt>MediaFormat</tt> instances and which may or may not be searched for
	 * the purposes of determining the index, the index is not to be used as a
	 * way to determine whether <tt>MediaUtils</tt> knows the specified
	 * <tt>mediaFormat</tt>
	 * 
	 * @param mediaFormat
	 *            the <tt>MediaFormat</tt> to determine the index of
	 * @return the index of the specified <tt>mediaFormat</tt> in the internal
	 *         storage of <tt>MediaUtils</tt>
	 */
	public static int getMediaFormatIndexWithSameMediaType(RTPMediaFormat mediaFormat) {
		if (mediaFormat != null) {
			RTPMediaFormat[] list = getMediaFormats(mediaFormat.getMediaType());
			if (list != null && list.length > 0) {
				int result = -1;
				int index = 0;
				for (RTPMediaFormat rmf : list) {
					if (rmf != null && mediaFormat != null && rmf.matches(mediaFormat)) {
						result = index;
						break;
					}

					index++;
				}
				return result;
			} else {
				return -1;
			}

		} else {
			return -1;
		}
	}

	/**
	 * Gets a <tt>MediaFormat</tt> predefined in <tt>MediaUtils</tt> which
	 * represents a specific JMF <tt>Format</tt>. If there is no such
	 * representing <tt>MediaFormat</tt> in <tt>MediaUtils</tt>, returns
	 * <tt>null</tt>.
	 * 
	 * @param format
	 *            the JMF <tt>Format</tt> to get the <tt>MediaFormat</tt>
	 *            representation for
	 * @return a <tt>MediaFormat</tt> predefined in <tt>MediaUtils</tt> which
	 *         represents <tt>format</tt> if any; <tt>null</tt> if there is no
	 *         such representing <tt>MediaFormat</tt> in <tt>MediaUtils</tt>
	 */
	@SuppressWarnings("unchecked")
	public static RTPMediaFormat getMediaFormat(Format format) {
		double clockRate;

		if (format instanceof AudioFormat) {
			clockRate = ((AudioFormat) format).getSampleRate();
		} else if (format instanceof VideoFormat) {
			clockRate = RTPMediaFormat.DEFAULT_CLOCK_RATE;
		} else {
			clockRate = Format.NOT_SPECIFIED;
		}

		for (RTPMediaFormat mediaFormat : getMediaFormats(format.getEncoding())) {
			if (format.matches(mediaFormat.getFormat()))
				return mediaFormat;
		}

		return null;
	}

	/**
	 * Gets the <tt>MediaFormat</tt> known to <tt>MediaUtils</tt> and having the
	 * specified well-known <tt>encoding</tt> (name) and <tt>clockRate</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) of the <tt>MediaFormat</tt> to
	 *            get
	 * @param clockRate
	 *            the clock rate of the <tt>MediaFormat</tt> to get
	 * @return the <tt>MediaFormat</tt> known to <tt>MediaUtils</tt> and having
	 *         the specified <tt>encoding</tt> and <tt>clockRate</tt>
	 */
	public static RTPMediaFormat getMediaFormat(String encoding, double clockRate) {
		for (RTPMediaFormat format : getMediaFormats(encoding))
			if ((format.getClockRate() == clockRate))
				return format;
		return null;
	}

	/**
	 * Gets the <tt>MediaFormat</tt> known to <tt>MediaUtils</tt> and having the
	 * specified well-known <tt>encoding</tt> (name) and <tt>clockRate</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) of the <tt>MediaFormat</tt> to
	 *            get
	 * @param clockRate
	 *            the clock rate of the <tt>MediaFormat</tt> to get
	 * @return the <tt>MediaFormat</tt> known to <tt>MediaUtils</tt> and having
	 *         the specified <tt>encoding</tt> and <tt>clockRate</tt>
	 */
	public static RTPMediaFormat getMediaFormat(String encoding, double clockRate, int sampleSizeInBits) {
		for (RTPMediaFormat format : getMediaFormats(encoding))
			if ((format.getClockRate() == clockRate) && ((AudioFormat) format.getFormat()).getSampleSizeInBits() == sampleSizeInBits)
				return format;
		return null;
	}

	/**
	 * Gets the <tt>MediaFormat</tt> known to <tt>MediaUtils</tt> and having the
	 * specified well-known <tt>encoding</tt> (name) and <tt>clockRate</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) of the <tt>MediaFormat</tt> to
	 *            get
	 * @param clockRate
	 *            the clock rate of the <tt>MediaFormat</tt> to get
	 * @return the <tt>MediaFormat</tt> known to <tt>MediaUtils</tt> and having
	 *         the specified <tt>encoding</tt> and <tt>clockRate</tt>
	 */
	public static RTPMediaFormat getMediaFormat(String encoding, double clockRate, int sampleSizeInBits, int channels) {
		for (RTPMediaFormat format : getMediaFormats(encoding))
			if ((format.getClockRate() == clockRate) && ((AudioFormat) format.getFormat()).getSampleSizeInBits() == sampleSizeInBits && ((AudioFormat) format.getFormat()).getChannels() == channels)
				return format;
		return null;
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s known to <tt>MediaUtils</tt> and being of
	 * the specified <tt>MediaType</tt>.
	 * 
	 * @param mediaType
	 *            the <tt>MediaType</tt> of the <tt>MediaFormat</tt>s to get
	 * @return the <tt>MediaFormat</tt>s known to <tt>MediaUtils</tt> and being
	 *         of the specified <tt>mediaType</tt>
	 */
	public static RTPMediaFormat[] getMediaFormats(RTPMediaType mediaType) {
		List<RTPMediaFormat> mediaFormats = new ArrayList<RTPMediaFormat>();

		if (mediaType.equals(RTPMediaType.AUDIO)) {
			for (Map.Entry<String, RTPMediaFormat[]> entry : audioRTPMediaFormats.entrySet()) {
				for (RTPMediaFormat format : entry.getValue()) {
					mediaFormats.add(format);
				}
			}
		} else {
			for (Map.Entry<String, RTPMediaFormat> entry : videoRTPMediaFormats.entrySet()) {
				mediaFormats.add(entry.getValue());
			}
		}

		return mediaFormats.toArray(EMPTY_MEDIA_FORMATS);
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s known to <tt>MediaUtils</tt> and being of
	 * the specified <tt>MediaType</tt>.
	 * 
	 * @param mediaType
	 *            the <tt>MediaType</tt> of the <tt>MediaFormat</tt>s to get
	 * @return the <tt>MediaFormat</tt>s known to <tt>MediaUtils</tt> and being
	 *         of the specified <tt>mediaType</tt>
	 */
	public static RTPMediaFormat[] getMediaFormats(RTPMediaType mediaType, byte payloadType) {
		List<RTPMediaFormat> mediaFormats = new ArrayList<RTPMediaFormat>();

		if (mediaType.equals(RTPMediaType.AUDIO)) {
			for (Map.Entry<String, RTPMediaFormat[]> entry : audioRTPMediaFormats.entrySet()) {
				for (RTPMediaFormat format : entry.getValue()) {
					if (format.getRTPPayloadType() == payloadType) {
						mediaFormats.add(format);
					}
				}
			}
		} else {
			for (Map.Entry<String, RTPMediaFormat> entry : videoRTPMediaFormats.entrySet()) {
				if (entry.getValue().getRTPPayloadType() == payloadType) {
					mediaFormats.add(entry.getValue());
				}
			}
		}

		return mediaFormats.toArray(EMPTY_MEDIA_FORMATS);
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s predefined in <tt>MediaUtils</tt> with a
	 * specific well-known encoding (name) as defined by RFC 3551 "RTP Profile
	 * for Audio and Video Conferences with Minimal Control".
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to get the corresponding
	 *            <tt>MediaFormat</tt>s of
	 * @return a <tt>List</tt> of <tt>MediaFormat</tt>s corresponding to the
	 *         specified encoding (name)
	 */
	@SuppressWarnings("unchecked")
	public static List<RTPMediaFormat> getMediaFormats(String encoding) {
		List<RTPMediaFormat> mediaFormats = new ArrayList<RTPMediaFormat>();
		if (audioRTPMediaFormats.containsKey(encoding)) {
			for (RTPMediaFormat mf : audioRTPMediaFormats.get(encoding)) {
				mediaFormats.add(mf);
			}
		} else {
			if (videoRTPMediaFormats.containsKey(encoding)) {
				mediaFormats.add(videoRTPMediaFormats.get(encoding));
			}
		}

		return mediaFormats;
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s predefined in <tt>MediaUtils</tt> with a
	 * specific well-known encoding (name) as defined by RFC 3551 "RTP Profile
	 * for Audio and Video Conferences with Minimal Control".
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to get the corresponding
	 *            <tt>MediaFormat</tt>s of
	 * @return a <tt>List</tt> of <tt>MediaFormat</tt>s corresponding to the
	 *         specified encoding (name)
	 */
	@SuppressWarnings("unchecked")
	public static List<RTPMediaFormat> getMediaFormats(String encoding, double clockRate) {
		List<RTPMediaFormat> mediaFormats = new ArrayList<RTPMediaFormat>();
		if (audioRTPMediaFormats.containsKey(encoding)) {
			for (RTPMediaFormat mf : audioRTPMediaFormats.get(encoding)) {
				if (mf.getClockRate() == clockRate) {
					mediaFormats.add(mf);
				}
			}
		} else {
			if (videoRTPMediaFormats.containsKey(encoding)) {
				if (videoRTPMediaFormats.get(encoding).getClockRate() == clockRate) {
					mediaFormats.add(videoRTPMediaFormats.get(encoding));
				}
			}
		}

		return mediaFormats;
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s predefined in <tt>MediaUtils</tt> with a
	 * specific well-known encoding (name) as defined by RFC 3551 "RTP Profile
	 * for Audio and Video Conferences with Minimal Control".
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to get the corresponding
	 *            <tt>MediaFormat</tt>s of
	 * @return a <tt>List</tt> of <tt>MediaFormat</tt>s corresponding to the
	 *         specified encoding (name)
	 */
	@SuppressWarnings("unchecked")
	public static List<RTPMediaFormat> getMediaFormats(String encoding, double clockRate, int sampleSizeInBits) {
		List<RTPMediaFormat> mediaFormats = new ArrayList<RTPMediaFormat>();
		if (audioRTPMediaFormats.containsKey(encoding)) {
			for (RTPMediaFormat mf : audioRTPMediaFormats.get(encoding)) {
				if (mf.getClockRate() == clockRate && ((AudioFormat) mf.getFormat()).getSampleSizeInBits() == sampleSizeInBits) {
					mediaFormats.add(mf);
				}
			}
		} else {
			if (videoRTPMediaFormats.containsKey(encoding)) {
				mediaFormats.add(videoRTPMediaFormats.get(encoding));
			}
		}

		return mediaFormats;
	}

	/**
	 * Gets the RTP payload type corresponding to a specific JMF encoding and
	 * clock rate.
	 * 
	 * @param jmfEncoding
	 *            the JMF encoding as returned by {@link Format#getEncoding()}
	 *            or the respective <tt>AudioFormat</tt> and
	 *            <tt>VideoFormat</tt> encoding constants to get the
	 *            corresponding RTP payload type of
	 * @param clockRate
	 *            the clock rate to be taken into account in the search for the
	 *            RTP payload type if the JMF encoding does not uniquely
	 *            identify it
	 * @return the RTP payload type corresponding to the specified JMF encoding
	 *         and clock rate if known in RFC 3551 "RTP Profile for Audio and
	 *         Video Conferences with Minimal Control"; otherwise,
	 *         {@link MediaFormat#RTP_PAYLOAD_TYPE_UNKNOWN}
	 */
	public static byte getRTPPayloadType(String jmfEncoding, double clockRate) {
		if (jmfEncoding == null)
			return RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN;
		else if (jmfEncoding.equals(AudioFormat.ULAW_RTP))
			return SdpConstants.PCMU;
		else if (jmfEncoding.equals(AudioFormat.ALAW_RTP))
			return SdpConstants.PCMA;
		else if (jmfEncoding.equals(AudioFormat.GSM_RTP))
			return SdpConstants.GSM;
		else if (jmfEncoding.equals(AudioFormat.G723_RTP))
			return SdpConstants.G723;
		else if (jmfEncoding.equals(AudioFormat.DVI_RTP) && (clockRate == 8000))
			return SdpConstants.DVI4_8000;
		else if (jmfEncoding.equals(AudioFormat.DVI_RTP) && (clockRate == 16000))
			return SdpConstants.DVI4_16000;
		else if (jmfEncoding.equals(AudioFormat.ALAW))
			return SdpConstants.PCMA;
		else if (jmfEncoding.equals(AudioFormat.G722))
			return SdpConstants.G722;
		else if (jmfEncoding.equals(AudioFormat.G722_RTP))
			return SdpConstants.G722;
		else if (jmfEncoding.equals(AudioFormat.GSM))
			return SdpConstants.GSM;
		else if (jmfEncoding.equals(AudioFormat.GSM_RTP))
			return SdpConstants.GSM;
		else if (jmfEncoding.equals(AudioFormat.G728_RTP))
			return SdpConstants.G728;
		else if (jmfEncoding.equals(AudioFormat.G729_RTP))
			return SdpConstants.G729;
		else if (jmfEncoding.equals(VideoFormat.H263_RTP))
			return SdpConstants.H263;
		else if (jmfEncoding.equals(VideoFormat.JPEG_RTP))
			return SdpConstants.JPEG;
		else if (jmfEncoding.equals(VideoFormat.H261_RTP))
			return SdpConstants.H261;
		else
			return 123;
	}

	/**
	 * Gets the well-known encoding (name) as defined in RFC 3551 "RTP Profile
	 * for Audio and Video Conferences with Minimal Control" corresponding to a
	 * given JMF-specific encoding.
	 * 
	 * @param jmfEncoding
	 *            the JMF encoding to get the corresponding well-known encoding
	 *            of
	 * @return the well-known encoding (name) as defined in RFC 3551 "RTP
	 *         Profile for Audio and Video Conferences with Minimal Control"
	 *         corresponding to <tt>jmfEncoding</tt> if any; otherwise,
	 *         <tt>null</tt>
	 */
	public static String jmfEncodingToEncoding(String jmfEncoding) {
		return jmfEncodingToEncodings.get(jmfEncoding);
	}

	/**
	 * Encoding To JmfEncoding
	 * 
	 * @param Encoding
	 *            the JMF encoding to get the corresponding well-known encoding
	 *            of
	 * @return JmfEncoding
	 */
	public static String encodingToJmfEncoding(String encodings) {
		String jmfEncoding = null;

		for (Map.Entry<String, String> jmfEncodingToEncoding : jmfEncodingToEncodings.entrySet())
			if (jmfEncodingToEncoding.getValue().equals(encodings)) {
				jmfEncoding = jmfEncodingToEncoding.getKey();
				break;
			}
		return jmfEncoding;
	}
}