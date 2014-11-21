/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sf.fmj.media.rtp.rtpmediaformat;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;


/**
 * Implements <tt>RTPDataFormat</tt> for the JMF <tt>Format</tt>.
 * 
 * @param <T>
 *            the type of the wrapped <tt>Format</tt>
 * 
 * @author Lyubomir Marinov
 */
public class RTPMediaFormat {
	/**
	 * The default value of the <tt>clockRate</tt> property of
	 * <tt>VideoMediaFormatImpl</tt>.
	 */
	public static final double DEFAULT_CLOCK_RATE = 90000;

	/**
	 * The constant returned by {@link #getRTPPayloadType()} when the
	 * <tt>MediaFormat</tt> instance describes a format without an RTP payload
	 * type (number) known in RFC 3551 "RTP Profile for Audio and Video
	 * Conferences with Minimal Control".
	 */
	public static final byte RTP_PAYLOAD_TYPE_UNKNOWN = -1;

	/**
	 * The minimum integer that is allowed for use in dynamic payload type
	 * assignment.
	 */
	public static final int MIN_DYNAMIC_PAYLOAD_TYPE = 96;

	/**
	 * The maximum integer that is allowed for use in dynamic payload type
	 * assignment.
	 */
	public static final int MAX_DYNAMIC_PAYLOAD_TYPE = 127;

	/**
	 * The constant to be used as an argument representing number of channels to
	 * denote that a specific number of channels is not specified.
	 */
	public static final int CHANNELS_NOT_SPECIFIED = -1;

	/**
	 * The constant to be used as an argument representing a clock rate to
	 * denote that a specific clock rate is not specified.
	 */
	public static final double CLOCK_RATE_NOT_SPECIFIED = -1;

	/**
	 * The name of the format parameter which specifies the packetization mode
	 * of H.264 RTP payload.
	 */
	public static final String H264_PACKETIZATION_MODE_FMTP = "packetization-mode";

	public static final String H264_SPROP_PARAMETER_SETS_FMTP = "sprop-parameter-sets";

	/**
	 * The name of the <tt>clockRate</tt> property of <tt>MediaFormatImpl</tt>.
	 */
	public static final String CLOCK_RATE_PNAME = "clockRate";

	/**
	 * The value of the <tt>formatParameters</tt> property of
	 * <tt>MediaFormatImpl</tt> when no codec-specific parameters have been
	 * received via SIP/SDP or XMPP/Jingle. Explicitly defined in order to
	 * reduce unnecessary allocations.
	 */
	static final Map<String, String> EMPTY_FORMAT_PARAMETERS = Collections.emptyMap();

	/**
	 * The name of the <tt>encoding</tt> property of <tt>MediaFormatImpl</tt>.
	 */
	public static final String ENCODING_PNAME = "encoding";

	/**
	 * The name of the <tt>formatParameters</tt> property of
	 * <tt>MediaFormatImpl</tt>.
	 */
	public static final String FORMAT_PARAMETERS_PNAME = "fmtps";

	private Map<String, String> codecSettings = new HashMap<String, String>();
	private Map<String, String> formatSettings = new HashMap<String, String>();
	private Map<String, String> advancedSettings = new HashMap<String, String>();

	protected RTPMediaType rtpMediaType;
	protected byte rtpPayloadType;

	/**
	 * Creates a new <tt>MediaFormat</tt> instance for a specific JMF
	 * <tt>Format</tt>.
	 * 
	 * @param format
	 *            the JMF <tt>Format</tt> the new instance is to provide an
	 *            implementation of <tt>MediaFormat</tt> for
	 * @return a new <tt>MediaFormat</tt> instance for the specified JMF
	 *         <tt>Format</tt>
	 */
	public static RTPMediaFormat createInstance(Format format) {
		RTPMediaFormat mediaFormat = null;
		if (format != null) {
			if (format instanceof AudioFormat)
			{
				mediaFormat = new RTPMediaFormat((AudioFormat) format);
			}
			else if (format instanceof VideoFormat)
			{
				mediaFormat = new RTPMediaFormat((VideoFormat) format);
			}
		}
		return mediaFormat;
	}

	/**
	 * The JMF <tt>Format</tt> this instance wraps and provides an
	 * implementation of <tt>MediaFormat</tt> for.
	 */
	protected final Format format;

	/**
	 * Initializes a new <tt>MediaFormatImpl</tt> instance which is to provide
	 * an implementation of <tt>MediaFormat</tt> for a specific <tt>Format</tt>.
	 * 
	 * @param format
	 *            the JMF <tt>Format</tt> the new instance is to provide an
	 *            implementation of <tt>MediaFormat</tt> for
	 */
	public RTPMediaFormat(Format format) {
		this(format, format == null?RTPMediaType.DATA:(format instanceof AudioFormat?RTPMediaType.AUDIO:RTPMediaType.VIDEO), (byte) 0);
	}

	/**
	 * Initializes a new <tt>MediaFormatImpl</tt> instance which is to provide
	 * an implementation of <tt>MediaFormat</tt> for a specific <tt>Format</tt>
	 * and which is to have a specific set of codec-specific parameters.
	 * 
	 * @param format
	 *            the JMF <tt>Format</tt> the new instance is to provide an
	 *            implementation of <tt>MediaFormat</tt> for
	 * @param mediaType
	 *            RTP Stream Type
	 * @param payloadType
	 *            RTP Payload Type
	 */
	public RTPMediaFormat(Format format, RTPMediaType mediaType, byte payloadType) {
		this(format, mediaType, payloadType, null, null);
	}

	/**
	 * Initializes a new <tt>MediaFormatImpl</tt> instance which is to provide
	 * an implementation of <tt>MediaFormat</tt> for a specific <tt>Format</tt>
	 * and which is to have a specific set of codec-specific parameters.
	 * 
	 * @param format
	 *            the JMF <tt>Format</tt> the new instance is to provide an
	 *            implementation of <tt>MediaFormat</tt> for
	 * @param mediaType
	 *            RTP Stream Type
	 * @param payloadType
	 *            RTP Payload Type
	 */
	public RTPMediaFormat(Format format, RTPMediaType mediaType, byte payloadType, Map<String, String> formatSettings, Map<String, String> advancedSettings) {
		if (format == null)
		{
			throw new NullPointerException("format");
		}

		this.format = format;
		this.format.setRtpFormat(this);
		this.rtpMediaType = mediaType;
		this.rtpPayloadType = payloadType;
		this.formatSettings = ((formatSettings == null) || formatSettings.isEmpty()) ? EMPTY_FORMAT_PARAMETERS : new HashMap<String, String>(formatSettings);
		this.advancedSettings = ((advancedSettings == null) || advancedSettings.isEmpty()) ? EMPTY_FORMAT_PARAMETERS : new HashMap<String, String>(advancedSettings);
	}

	/**
	 * Implements MediaFormat#equals(Object) and actually compares the
	 * encapsulated JMF <tt>Format</tt> instances.
	 * 
	 * @param mediaFormat
	 *            the object that we'd like to compare <tt>this</tt> one to. 8*
	 * @return <tt>true</tt> if the JMF <tt>Format</tt> instances encapsulated
	 *         by this class are equal and <tt>false</tt> otherwise.
	 */
	@Override
	public boolean equals(Object mediaFormat) {
		if (this == mediaFormat)
			return true;

		if (!getClass().isInstance(mediaFormat))
			return false;

		@SuppressWarnings("unchecked")
		RTPMediaFormat mediaFormatImpl = (RTPMediaFormat) mediaFormat;

		return getFormat().equals(mediaFormatImpl.getFormat()) && formatParametersAreEqual(getFormatSettings(), mediaFormatImpl.getFormatSettings());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation of <tt>MediaFormatImpl</tt> always returns
	 * <tt>true</tt> because format parameters in general do not cause the
	 * distinction of payload types.
	 * </p>
	 */
	public boolean formatParametersMatch(Map<String, String> fmtps) {
		return true;
	}

	/**
	 * Determines whether a specific set of format parameters is equal to
	 * another set of format parameters in the sense that they define an equal
	 * number of parameters and assign them equal values. Since the values are
	 * <tt>String</tt>s, presumes that a value of <tt>null</tt> is equal to the
	 * empty <tt>String</tt>.
	 * <p>
	 * The two <tt>Map</tt> instances of format parameters to be checked for
	 * equality are presumed to be modifiable in the sense that if the lack of a
	 * format parameter in a given <tt>Map</tt> is equivalent to it having a
	 * specific value, an association of the format parameter to the value in
	 * question may be added to or removed from the respective <tt>Map</tt>
	 * instance for the purposes of determining equality.
	 * </p>
	 * 
	 * @param fmtps1
	 *            the first set of format parameters to be tested for equality
	 * @param fmtps2
	 *            the second set of format parameters to be tested for equality
	 * @return <tt>true</tt> if the specified sets of format parameters are
	 *         equal; <tt>false</tt>, otherwise
	 */
	protected boolean formatParametersAreEqual(Map<String, String> fmtps1, Map<String, String> fmtps2) {
		return formatParametersAreEqual(getEncoding(), fmtps1, fmtps2);
	}

	/**
	 * Determines whether a specific set of format parameters is equal to
	 * another set of format parameters in the sense that they define an equal
	 * number of parameters and assign them equal values. Since the values are
	 * <tt>String</tt>s, presumes that a value of <tt>null</tt> is equal to the
	 * empty <tt>String</tt>.
	 * <p>
	 * The two <tt>Map</tt> instances of format parameters to be checked for
	 * equality are presumed to be modifiable in the sense that if the lack of a
	 * format parameter in a given <tt>Map</tt> is equivalent to it having a
	 * specific value, an association of the format parameter to the value in
	 * question may be added to or removed from the respective <tt>Map</tt>
	 * instance for the purposes of determining equality.
	 * </p>
	 * 
	 * @param encoding
	 *            the encoding (name) related to the two sets of format
	 *            parameters to be tested for equality
	 * @param fmtps1
	 *            the first set of format parameters to be tested for equality
	 * @param fmtps2
	 *            the second set of format parameters to be tested for equality
	 * @return <tt>true</tt> if the specified sets of format parameters are
	 *         equal; <tt>false</tt>, otherwise
	 */
	public static boolean formatParametersAreEqual(String encoding, Map<String, String> fmtps1, Map<String, String> fmtps2) {
		if (fmtps1 == null)
			return (fmtps2 == null) || fmtps2.isEmpty();
		if (fmtps2 == null)
			return (fmtps1 == null) || fmtps1.isEmpty();
		if (fmtps1.size() == fmtps2.size()) {
			for (Map.Entry<String, String> fmtp1 : fmtps1.entrySet()) {
				String key1 = fmtp1.getKey();

				if (!fmtps2.containsKey(key1))
					return false;

				String value1 = fmtp1.getValue();
				String value2 = fmtps2.get(key1);

				/*
				 * Since the values are strings, allow null to be equal to the
				 * empty string.
				 */
				if ((value1 == null) || (value1.length() == 0)) {
					if ((value2 != null) && (value2.length() != 0))
						return false;
				} else if (!value1.equals(value2))
					return false;
			}
			return true;
		} else
			return false;
	}

	/**
	 * Determines whether a specific set of advanced attributes is equal to
	 * another set of advanced attributes in the sense that they define an equal
	 * number of parameters and assign them equal values. Since the values are
	 * <tt>String</tt>s, presumes that a value of <tt>null</tt> is equal to the
	 * empty <tt>String</tt>.
	 * <p>
	 * 
	 * @param adv
	 *            the first set of advanced attributes to be tested for equality
	 * @param adv2
	 *            the second set of advanced attributes to be tested for
	 *            equality
	 * @return <tt>true</tt> if the specified sets of advanced attributes equal;
	 *         <tt>false</tt>, otherwise
	 */
	public boolean advancedAttributesAreEqual(Map<String, String> adv, Map<String, String> adv2) {
		if (adv == null && adv2 != null || adv != null && adv2 == null)
			return false;

		if (adv == null && adv2 == null)
			return true;

		if (adv.size() != adv2.size())
			return false;

		for (Map.Entry<String, String> a : adv.entrySet()) {
			String value = adv2.get(a.getKey());
			if (value == null)
				return false;
			else if (!value.equals(a.getValue()))
				return false;
		}
		return true;
	}

	/**
	 * Returns a <tt>String</tt> representation of the clock rate associated
	 * with this <tt>MediaFormat</tt> making sure that the value appears as an
	 * integer (i.e. its long-casted value is equal to its original one) unless
	 * it is actually a non integer.
	 * 
	 * @return a <tt>String</tt> representation of the clock rate associated
	 *         with this <tt>MediaFormat</tt>.
	 */
	public String getClockRateString() {
		double clockRate = getClockRate();
		long clockRateL = (long) clockRate;

		if (clockRateL == clockRate)
			return Long.toString(clockRateL);
		else
			return Double.toString(clockRate);
	}

	/**
	 * Gets the number of audio channels associated with this
	 * <tt>AudioMediaFormat</tt>.
	 * 
	 * @return the number of audio channels associated with this
	 *         <tt>AudioMediaFormat</tt>
	 * @see AudioMediaFormat#getChannels()
	 */
	public int getChannels() {
		if (getFormat() != null) {
			if (getFormat() instanceof AudioFormat) {
				int channels = ((AudioFormat) format).getChannels();
				return channels <= 0?1:channels;
			} else {
				return Format.NOT_SPECIFIED;
			}
		} else {
			return Format.NOT_SPECIFIED;
		}
	}

	/**
	 * Gets the clock rate associated with this <tt>MediaFormat</tt>.
	 * 
	 * @return the clock rate associated with this <tt>MediaFormat</tt>
	 * @see MediaFormat#getClockRate()
	 */
	public double getClockRate() {
		if (getFormat() != null) {
			if (getFormat() instanceof AudioFormat) {
				return ((AudioFormat) format).getSampleRate();
			} else if (getFormat() instanceof VideoFormat) {
				return ((VideoFormat) format).getClockRate();
			} else {
				return RTPMediaFormat.DEFAULT_CLOCK_RATE;
			}
		} else {
			return RTPMediaFormat.DEFAULT_CLOCK_RATE;
		}
	}

	/**
	 * Gets the frame rate associated with this <tt>MediaFormat</tt>.
	 * 
	 * @return the frame rate associated with this <tt>MediaFormat</tt>
	 * @see VideoMediaFormat#getFrameRate()
	 */
	public float getFrameRate() {
		if (getFormat() != null) {
			if (getFormat() instanceof VideoFormat) {
				return ((VideoFormat) format).getFrameRate();
			} else {
				return Format.NOT_SPECIFIED;
			}
		} else {
			return Format.NOT_SPECIFIED;
		}
	}

	/**
	 * Gets the size of the image that this <tt>VideoMediaFormat</tt> describes.
	 * 
	 * @return a {@link Dimension} instance indicating the image size (in
	 *         pixels) of this <tt>VideoMediaFormat</tt>
	 * @see VideoMediaFormat#getSize()
	 */
	public Dimension getSize() {
		if (getFormat() != null) {
			if (getFormat() instanceof VideoFormat) {
				return ((VideoFormat) format).getSize();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Implements MediaFormat#getEncoding() and returns the encoding of the JMF
	 * <tt>Format</tt> that we are encapsulating here but it is the RFC-known
	 * encoding and not the internal JMF encoding.
	 * 
	 * @return the RFC-known encoding of the JMF <tt>Format</tt> that we are
	 *         encapsulating
	 */
	public String getEncoding() {
		String jmfEncoding = getJMFEncoding();
		String encoding = RTPMediaFormatUtils.jmfEncodingToEncoding(jmfEncoding);

		if (encoding == null) {
			encoding = jmfEncoding;

			int encodingLength = encoding.length();

			if (encodingLength > 3) {
				int rtpPos = encodingLength - 4;

				if ("/rtp".equalsIgnoreCase(encoding.substring(rtpPos)))
					encoding = encoding.substring(0, rtpPos);
			}
		}
		return encoding;
	}

	/**
	 * Returns the JMF <tt>Format</tt> instance that we are wrapping here.
	 * 
	 * @return a reference to that JMF <tt>Format</tt> instance that this class
	 *         is wrapping.
	 */
	public Format getFormat() {
		if (format != null) {
			format.setRtpFormat(this);
			return format;
		} else {
			return null;
		}
	}

	/**
	 * Gets the encoding of the JMF <tt>Format</tt> represented by this instance
	 * as it is known to JMF (in contrast to its RFC name).
	 * 
	 * @return the encoding of the JMF <tt>Format</tt> represented by this
	 *         instance as it is known to JMF (in contrast to its RFC name)
	 */
	public String getJMFEncoding() {
		return format.getEncoding();
	}

	/**
	 * Returns a <tt>String</tt> representation of the real used clock rate
	 * associated with this <tt>MediaFormat</tt> making sure that the value
	 * appears as an integer (i.e. contains no decimal point) unless it is
	 * actually a non integer. This function corrects the problem of the G.722
	 * codec which advertises its clock rate to be 8 kHz while 16 kHz is really
	 * used to encode the stream (that's an error noted in the respective RFC
	 * and kept for the sake of compatibility.).
	 * 
	 * @return a <tt>String</tt> representation of the real used clock rate
	 *         associated with this <tt>MediaFormat</tt>.
	 */
	public String getRealUsedClockRateString() {
		// RFC 1890 erroneously assigned 8 kHz to the RTP clock rate for the
		// G722 payload format. The actual sampling rate for G.722 audio is 16
		// kHz.
		if (this.getEncoding().equalsIgnoreCase("G722")) {
			return "16000";
		}
		return this.getClockRateString();
	}

	/**
	 * Gets the RTP payload type (number) of this <tt>MediaFormat</tt> as it is
	 * known in RFC 3551 "RTP Profile for Audio and Video Conferences with
	 * Minimal Control".
	 * 
	 * @return the RTP payload type of this <tt>MediaFormat</tt> if it is known
	 *         in RFC 3551 "RTP Profile for Audio and Video Conferences with
	 *         Minimal Control"; otherwise, {@link #RTP_PAYLOAD_TYPE_UNKNOWN}
	 * @see MediaFormat#getRTPPayloadType()
	 */
	public byte getRTPPayloadType() {
		if (this.rtpPayloadType == RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN) {
			return RTPMediaFormatUtils.getRTPPayloadType(getJMFEncoding(), getClockRate());
		} else {
			return this.rtpPayloadType;
		}
	}

	/**
	 * Overrides Object#hashCode() because Object#equals(Object) is overridden.
	 * 
	 * @return a hash code value for this <tt>MediaFormat</tt>.
	 */
	@Override
	public int hashCode()
    {
        /*
         * XXX We've experienced a case of JMF's VideoFormat#hashCode()
         * returning different values for instances which are reported equal by
         * VideoFormat#equals(Object) which is inconsistent with the protocol
         * covering the two methods in question and causes problems,
         * for example, with Map. While jmfEncoding is more generic than format,
         * it still provides a relatively good distribution given that we do not
         * have a lot of instances with one and the same jmfEncoding.
         */
        return getJMFEncoding().hashCode() | getFormatSettings().hashCode();
    }

	public RTPMediaType getMediaType() {
		// TODO Auto-generated method stub
		return this.rtpMediaType;
	}

	/**
	 * Returns a <tt>String</tt> representation of this <tt>MediaFormat</tt>
	 * containing, among other things, its encoding and clockrate values.
	 * 
	 * @return a <tt>String</tt> representation of this <tt>MediaFormat</tt>.
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();

		str.append("rtpmap:");
		str.append(getRTPPayloadType());
		str.append(' ');
		str.append(getEncoding());
		str.append('/');
		str.append(getClockRateString());

		/*
		 * If the number of channels is 1, it does not have to be mentioned
		 * because it is the default.
		 */
		if (RTPMediaType.AUDIO.equals(getMediaType())) {
			int channels = ((AudioFormat) getFormat()).getChannels();

			if (channels != 1) {
				str.append('/');
				str.append(channels);
			}
		}

		Map<String, String> formatParameters = getFormatSettings();

		if (!formatParameters.isEmpty()) {
			str.append(" fmtp:");

			boolean prependSeparator = false;

			for (Map.Entry<String, String> formatParameter : formatParameters.entrySet()) {
				if (prependSeparator)
					str.append(';');
				else
					prependSeparator = true;
				str.append(formatParameter.getKey());
				str.append('=');
				str.append(formatParameter.getValue());
			}
		}

		return str.toString();
	}

	/**
	 * Determines whether this <tt>MediaFormat</tt> matches properties of a
	 * specific <tt>MediaFormat</tt>, such as <tt>mediaType</tt>,
	 * <tt>encoding</tt>, <tt>clockRate</tt> and <tt>channels</tt> for
	 * <tt>MediaFormat</tt>s with <tt>mediaType</tt> equal to
	 * {@link MediaType#AUDIO}.
	 * 
	 * @param format
	 *            the {@link MediaFormat} whose properties we'd like to examine
	 *            and compare with ours.
	 */
	public boolean matches(RTPMediaFormat format) {
		if (format == null) {
			return false;
		}

		RTPMediaType mediaType = format.getMediaType();
		String encoding = format.getEncoding();
		double clockRate = format.getClockRate();
		int channels = RTPMediaType.AUDIO.equals(mediaType) ? ((RTPMediaFormat) format).getChannels() : RTPMediaFormat.CHANNELS_NOT_SPECIFIED;
		Map<String, String> fmtps = format.getFormatSettings();
		return matches(mediaType, encoding, clockRate, channels, fmtps);
	}

	/**
	 * Determines whether this <tt>MediaFormat</tt> has specific values for its
	 * properties <tt>mediaType</tt>, <tt>encoding</tt>, <tt>clockRate</tt> and
	 * <tt>channels</tt> for <tt>MediaFormat</tt>s with <tt>mediaType</tt> equal
	 * to {@link MediaType#AUDIO}.
	 * 
	 * @param mediaType
	 *            the type we expect {@link MediaFormat} to have
	 * @param encoding
	 *            the encoding we are looking for.
	 * @param clockRate
	 *            the clock rate that we'd like the format to have.
	 * @param channels
	 *            the number of channels that expect to find in this format
	 * @param fmtps
	 *            the format parameters expected to match these of the specified
	 *            <tt>format</tt>
	 * @return <tt>true</tt> if the specified <tt>format</tt> has specific
	 *         values for its properties <tt>mediaType</tt>, <tt>encoding</tt>,
	 *         <tt>clockRate</tt> and <tt>channels</tt>; otherwise,
	 *         <tt>false</tt>
	 */
	public boolean matches(RTPMediaType mediaType, String encoding, double clockRate, int channels, Map<String, String> fmtps) {
		// mediaType
		// encoding
		if (!getMediaType().equals(mediaType) || !getEncoding().equals(encoding)) {
			return false;
		}

		// clockRate
		if (clockRate != RTPMediaFormat.CLOCK_RATE_NOT_SPECIFIED) {
			double formatClockRate = getClockRate();

			if ((formatClockRate != RTPMediaFormat.CLOCK_RATE_NOT_SPECIFIED) && (formatClockRate != clockRate)) {
				return false;
			}
		}

		// channels
		if (RTPMediaType.AUDIO.equals(mediaType)) {
			if (channels == RTPMediaFormat.CHANNELS_NOT_SPECIFIED) {
				channels = 1;
			}

			int formatChannels = this.getChannels();

			if (formatChannels == RTPMediaFormat.CHANNELS_NOT_SPECIFIED) {
				formatChannels = 1;
			}
			if (formatChannels != channels) {
				return false;
			}
		}

		// formatParameters
		return formatParametersMatch(fmtps);
	}

	/**
	 * Returns a <tt>Map</tt> containing advanced parameters specific to this
	 * particular <tt>MediaFormat</tt>. The parameters returned here are meant
	 * for use in SIP/SDP or XMPP session descriptions.
	 * 
	 * @return a <tt>Map</tt> containing advanced parameters specific to this
	 *         particular <tt>MediaFormat</tt>
	 */
	public Map<String, String> getAdvancedSettings() {
		return (advancedSettings == EMPTY_FORMAT_PARAMETERS) ? EMPTY_FORMAT_PARAMETERS : new HashMap<String, String>(advancedSettings);
	}

	/**
	 * Returns a <tt>Map</tt> containing parameters specific to this particular
	 * <tt>MediaFormat</tt>. The parameters returned here are meant for use in
	 * SIP/SDP or XMPP session descriptions where they get transported through
	 * the "fmtp:" attribute or <parameter/> tag respectively.
	 * 
	 * @return a <tt>Map</tt> containing parameters specific to this particular
	 *         <tt>MediaFormat</tt>.
	 */
	public Map<String, String> getFormatSettings() {
		return (formatSettings == EMPTY_FORMAT_PARAMETERS) ? EMPTY_FORMAT_PARAMETERS : new HashMap<String, String>(formatSettings);
	}

	/**
	 * Returns additional codec settings.
	 * 
	 * @return additional settings represented by a map.
	 */
	public Map<String, String> getCodecSettings() {
		return (codecSettings == EMPTY_FORMAT_PARAMETERS) ? EMPTY_FORMAT_PARAMETERS : new HashMap<String, String>(codecSettings);
	}

	/**
	 * Sets additional codec settings.
	 * 
	 * @param settings
	 *            additional settings represented by a map.
	 */
	public void setAdvancedSettings(Map<String, String> settings) {
		this.advancedSettings = ((settings == null) || settings.isEmpty()) ? EMPTY_FORMAT_PARAMETERS : settings;
	}

	/**
	 * Sets additional codec settings.
	 * 
	 * @param settings
	 *            additional settings represented by a map.
	 */
	public void setFormatSettings(Map<String, String> settings) {
		this.formatSettings = ((settings == null) || settings.isEmpty()) ? EMPTY_FORMAT_PARAMETERS : settings;
	}

	/**
	 * Sets additional codec settings.
	 * 
	 * @param settings
	 *            additional settings represented by a map.
	 */
	public void setCodecSettings(Map<String, String> settings) {
		this.codecSettings = ((settings == null) || settings.isEmpty()) ? EMPTY_FORMAT_PARAMETERS : settings;
	}
}