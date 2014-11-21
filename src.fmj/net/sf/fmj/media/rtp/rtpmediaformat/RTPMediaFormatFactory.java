/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.sf.fmj.media.rtp.rtpmediaformat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.media.format.VideoFormat;


import org.jitsi.impl.neomedia.NeomediaServiceUtils;
import org.jitsi.service.neomedia.codec.EncodingConfiguration;
import org.jitsi.util.Logger;

/**
 * Implements <tt>MediaFormatFactory</tt> for the JMF <tt>Format</tt> types.
 * 
 * @author Lyubomir Marinov
 */
public class RTPMediaFormatFactory  {
	
	/**
	 * The <tt>Logger</tt> used by the <tt>MediaFormatFactoryImpl</tt> class and
	 * its instances for logging output.
	 */
	private static final Logger logger = Logger.getLogger(RTPMediaFormatFactory.class);

	/**
	 * Creates a <tt>MediaFormat</tt> for the specified <tt>encoding</tt> with
	 * the specified <tt>clockRate</tt> and a default set of format parameters.
	 * If <tt>encoding</tt> is known to this <tt>MediaFormatFactory</tt>,
	 * returns a <tt>MediaFormat</tt> which is either an
	 * <tt>AudioMediaFormat</tt> or a <tt>VideoMediaFormat</tt> instance.
	 * Otherwise, returns <tt>null</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to create a
	 *            <tt>MediaFormat</tt> for
	 * @param clockRate
	 *            the clock rate in Hz to create a <tt>MediaFormat</tt> for
	 * @return a <tt>MediaFormat</tt> with the specified <tt>encoding</tt> and
	 *         <tt>clockRate</tt> which is either an <tt>AudioMediaFormat</tt>
	 *         or a <tt>VideoMediaFormat</tt> instance if <tt>encoding</tt> is
	 *         known to this <tt>MediaFormatFactory</tt>; otherwise,
	 *         <tt>null</tt>
	 * @see MediaFormatFactory#createMediaFormat(String, double)
	 */
	public RTPMediaFormat createMediaFormat(String encoding, double clockRate) {
		return createMediaFormat(encoding, clockRate, 1);
	}

	/**
	 * Creates a <tt>MediaFormat</tt> for the specified <tt>encoding</tt>,
	 * <tt>clockRate</tt> and <tt>channels</tt> and a default set of format
	 * parameters. If <tt>encoding</tt> is known to this
	 * <tt>MediaFormatFactory</tt>, returns a <tt>MediaFormat</tt> which is
	 * either an <tt>AudioMediaFormat</tt> or a <tt>VideoMediaFormat</tt>
	 * instance. Otherwise, returns <tt>null</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to create a
	 *            <tt>MediaFormat</tt> for
	 * @param clockRate
	 *            the clock rate in Hz to create a <tt>MediaFormat</tt> for
	 * @param channels
	 *            the number of available channels (1 for mono, 2 for stereo) if
	 *            it makes sense for the <tt>MediaFormat</tt> with the specified
	 *            <tt>encoding</tt>; otherwise, ignored
	 * @return a <tt>MediaFormat</tt> with the specified <tt>encoding</tt>,
	 *         <tt>clockRate</tt> and <tt>channels</tt> and a default set of
	 *         format parameters which is either an <tt>AudioMediaFormat</tt> or
	 *         a <tt>VideoMediaFormat</tt> instance if <tt>encoding</tt> is
	 *         known to this <tt>MediaFormatFactory</tt>; otherwise,
	 *         <tt>null</tt>
	 * @see MediaFormatFactory#createMediaFormat(String, double, int)
	 */
	public RTPMediaFormat createMediaFormat(String encoding, double clockRate, int channels) {
		return createMediaFormat(encoding, clockRate, channels, null);
	}

	private RTPMediaFormat createMediaFormat(String encoding, double clockRate, int channels, Map<String, String> fmtps) {
		for (RTPMediaFormat format : getSupportedMediaFormats(encoding, clockRate)) {
			/*
			 * The mediaType, encoding and clockRate properties are sure to
			 * match because format is the result of the search for encoding and
			 * clockRate. We just want to make sure that the channels and the
			 * format parameters match.
			 */
			if (format.matches(format.getMediaType(), format.getEncoding(), format.getClockRate(), channels, fmtps))
				return format;
		}
		return null;
	}

	/**
	 * Creates a <tt>MediaFormat</tt> for the specified <tt>encoding</tt>,
	 * <tt>clockRate</tt> and set of format parameters. If <tt>encoding</tt> is
	 * known to this <tt>MediaFormatFactory</tt>, returns a <tt>MediaFormat</tt>
	 * which is either an <tt>AudioMediaFormat</tt> or a
	 * <tt>VideoMediaFormat</tt> instance. Otherwise, returns <tt>null</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to create a
	 *            <tt>MediaFormat</tt> for
	 * @param clockRate
	 *            the clock rate in Hz to create a <tt>MediaFormat</tt> for
	 * @param formatParams
	 *            any codec specific parameters which have been received via
	 *            SIP/SDP or XMPP/Jingle
	 * @return a <tt>MediaFormat</tt> with the specified <tt>encoding</tt>,
	 *         <tt>clockRate</tt> and set of format parameters which is either
	 *         an <tt>AudioMediaFormat</tt> or a <tt>VideoMediaFormat</tt>
	 *         instance if <tt>encoding</tt> is known to this
	 *         <tt>MediaFormatFactory</tt>; otherwise, <tt>null</tt>
	 * @see MediaFormatFactory#createMediaFormat(String, double, Map, Map)
	 */
	public RTPMediaFormat createMediaFormat(String encoding, double clockRate, Map<String, String> formatParams, Map<String, String> advancedParams) {
		return createMediaFormat(encoding, clockRate, 1, -1, formatParams, advancedParams);
	}

	/**
	 * Creates a <tt>MediaFormat</tt> for the specified <tt>encoding</tt>,
	 * <tt>clockRate</tt>, <tt>channels</tt> and set of format parameters. If
	 * <tt>encoding</tt> is known to this <tt>MediaFormatFactory</tt>, returns a
	 * <tt>MediaFormat</tt> which is either an <tt>AudioMediaFormat</tt> or a
	 * <tt>VideoMediaFormat</tt> instance. Otherwise, returns <tt>null</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) to create a
	 *            <tt>MediaFormat</tt> for
	 * @param clockRate
	 *            the clock rate in Hz to create a <tt>MediaFormat</tt> for
	 * @param frameRate
	 *            the frame rate in number of frames per second to create a
	 *            <tt>MediaFormat</tt> for
	 * @param channels
	 *            the number of available channels (1 for mono, 2 for stereo) if
	 *            it makes sense for the <tt>MediaFormat</tt> with the specified
	 *            <tt>encoding</tt>; otherwise, ignored
	 * @param formatParams
	 *            any codec specific parameters which have been received via
	 *            SIP/SDP or XMPP/Jingle
	 * @param advancedParams
	 *            any parameters which have been received via SIP/SDP or
	 *            XMPP/Jingle
	 * @return a <tt>MediaFormat</tt> with the specified <tt>encoding</tt>,
	 *         <tt>clockRate</tt>, <tt>channels</tt> and set of format
	 *         parameters which is either an <tt>AudioMediaFormat</tt> or a
	 *         <tt>VideoMediaFormat</tt> instance if <tt>encoding</tt> is known
	 *         to this <tt>MediaFormatFactory</tt>; otherwise, <tt>null</tt>
	 * @see MediaFormatFactory#createMediaFormat(String, double, int, float,
	 *      Map, Map)
	 */
	public RTPMediaFormat createMediaFormat(String encoding, double clockRate, int channels, float frameRate, Map<String, String> formatParams, Map<String, String> advancedParams) {
		RTPMediaFormat mediaFormat = createMediaFormat(encoding, clockRate, channels, formatParams);

		if (mediaFormat == null)
			return null;

		/*
		 * MediaFormatImpl is immutable so if the caller wants to change the
		 * format parameters and/or the advanced attributes, we'll have to
		 * create a new MediaFormatImpl.
		 */
		Map<String, String> formatParameters = null;
		Map<String, String> advancedParameters = null;

		if ((formatParams != null) && !formatParams.isEmpty())
			formatParameters = formatParams;
		if ((advancedParams != null) && !advancedParams.isEmpty())
			advancedParameters = advancedParams;

		if ((formatParameters != null) || (advancedParameters != null)) {
			switch (mediaFormat.getMediaType()) {
			case AUDIO:
				mediaFormat = new RTPMediaFormat(mediaFormat.getFormat(), RTPMediaType.AUDIO,RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN,formatParameters, advancedParameters);
				break;
			case VIDEO:
				//VideoMediaFormatImpl videoMediaFormatImpl = (VideoMediaFormatImpl) mediaFormat;

				/*
				 * If the format of VideoMediaFormatImpl is a
				 * ParameterizedVideoFormat, it's possible for the format
				 * parameters of that ParameterizedVideoFormat and of the new
				 * VideoMediaFormatImpl (to be created) to be out of sync. While
				 * it's not technically perfect, it should be practically safe
				 * for the format parameters which distinguish VideoFormats with
				 * the same encoding and clock rate because mediaFormat has
				 * already been created in sync with formatParams (with respect
				 * to the format parameters which distinguish VideoFormats with
				 * the same encoding and clock rate).
				 */
				if (mediaFormat.getFormat() instanceof VideoFormat)
				{
					VideoFormat formats = (VideoFormat)mediaFormat.getFormat();
					formats.setFrameRate(frameRate);
					formats.setClockRate(mediaFormat.getClockRate());
				    mediaFormat = new RTPMediaFormat(formats, RTPMediaType.VIDEO,RTPMediaFormat.RTP_PAYLOAD_TYPE_UNKNOWN, formatParameters, advancedParameters);
				}else
				{
					mediaFormat = null;
				}
				break;
			default:
				mediaFormat = null;
			}
		}
		return mediaFormat;
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s among the specified <tt>mediaFormats</tt>
	 * which have the specified <tt>encoding</tt> and, optionally,
	 * <tt>clockRate</tt>.
	 * 
	 * @param mediaFormats
	 *            the <tt>MediaFormat</tt>s from which to filter out only the
	 *            ones which have the specified <tt>encoding</tt> and,
	 *            optionally, <tt>clockRate</tt>
	 * @param encoding
	 *            the well-known encoding (name) of the <tt>MediaFormat</tt>s to
	 *            be retrieved
	 * @param clockRate
	 *            the clock rate of the <tt>MediaFormat</tt>s to be retrieved;
	 *            {@link #CLOCK_RATE_NOT_SPECIFIED} if any clock rate is
	 *            acceptable
	 * @return a <tt>List</tt> of the <tt>MediaFormat</tt>s among
	 *         <tt>mediaFormats</tt> which have the specified <tt>encoding</tt>
	 *         and, optionally, <tt>clockRate</tt>
	 */
	private List<RTPMediaFormat> getMatchingMediaFormats(RTPMediaFormat[] mediaFormats, String encoding, double clockRate) {
		/*
		 * XXX Use String#equalsIgnoreCase(String) because some clients transmit
		 * some of the codecs starting with capital letters.
		 */

		/*
		 * As per RFC 3551.4.5.2, because of a mistake in RFC 1890 and for
		 * backward compatibility, G.722 should always be announced as 8000 even
		 * though it is wideband. So, if someone is looking for G722/16000,
		 * then: Forgive them, for they know not what they do!
		 */
		if ("G722".equalsIgnoreCase(encoding) && (16000 == clockRate)) {
			clockRate = 8000;
			if (logger.isInfoEnabled())
				logger.info("Suppressing erroneous 16000 announcement for G.722");
		}

		List<RTPMediaFormat> supportedMediaFormats = new ArrayList<RTPMediaFormat>();

		for (RTPMediaFormat mediaFormat : mediaFormats) {
			System.out.println(mediaFormat.getEncoding() + ":" + encoding + "," + mediaFormat.getClockRate() + ":" + clockRate);
			if (mediaFormat.getEncoding().equalsIgnoreCase(encoding) && ((RTPMediaFormat.CLOCK_RATE_NOT_SPECIFIED == clockRate) || (mediaFormat.getClockRate() == clockRate))) {
				supportedMediaFormats.add(mediaFormat);
				System.out.println(mediaFormat.getEncoding() + ":" + encoding + "," + mediaFormat.getClockRate() + ":" + clockRate + " right");
			}
		}
		return supportedMediaFormats;
	}

	/**
	 * Gets the <tt>MediaFormat</tt>s supported by this
	 * <tt>MediaFormatFactory</tt> and the <tt>MediaService</tt> associated with
	 * it and having the specified <tt>encoding</tt> and, optionally,
	 * <tt>clockRate</tt>.
	 * 
	 * @param encoding
	 *            the well-known encoding (name) of the <tt>MediaFormat</tt>s to
	 *            be retrieved
	 * @param clockRate
	 *            the clock rate of the <tt>MediaFormat</tt>s to be retrieved;
	 *            {@link #CLOCK_RATE_NOT_SPECIFIED} if any clock rate is
	 *            acceptable
	 * @return a <tt>List</tt> of the <tt>MediaFormat</tt>s supported by the
	 *         <tt>MediaService</tt> associated with this
	 *         <tt>MediaFormatFactory</tt> and having the specified encoding
	 *         and, optionally, clock rate
	 */
	private List<RTPMediaFormat> getSupportedMediaFormats(String encoding, double clockRate) {
		EncodingConfiguration encodingConfiguration = NeomediaServiceUtils.getMediaServiceImpl().getCurrentEncodingConfiguration();
		List<RTPMediaFormat> supportedMediaFormats = getMatchingMediaFormats(encodingConfiguration.getAllEncodings(RTPMediaType.AUDIO), encoding, clockRate);

		if (supportedMediaFormats.isEmpty())
			supportedMediaFormats = getMatchingMediaFormats(encodingConfiguration.getAllEncodings(RTPMediaType.VIDEO), encoding, clockRate);
		return supportedMediaFormats;
	}
}
