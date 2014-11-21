/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.service.neomedia;

import net.sf.fmj.media.rtp.rtpmediaformat.RTPMediaType;

import org.jitsi.impl.neomedia.AbstractRTPConnector;
import org.jitsi.service.neomedia.event.SrtpListener;

/**
 * Controls SRTP encryption in the MediaStream.
 * 
 * @author Damian Minkov
 */
public interface SrtpControl {
	public static final String RTP_SAVP = "RTP/SAVP";

	public static final String RTP_SAVPF = "RTP/SAVPF";

	/**
	 * Adds a <tt>cleanup()</tt> method to
	 * <tt>org.jitsi.impl.neomedia.transform.TransformEngine</tt> which is to go
	 * in hand with the <tt>cleanup()</tt> method of <tt>SrtpControl</tt>.
	 * 
	 * @author Lyubomir Marinov
	 */
	public interface TransformEngine extends org.jitsi.impl.neomedia.transform.TransformEngine {
		/**
		 * Cleans up this <tt>TransformEngine</tt> and prepares it for garbage
		 * collection.
		 */
		public void cleanup();
	}

	/**
	 * Cleans up this <tt>SrtpControl</tt> and its <tt>TransformEngine</tt>.
	 * 
	 * @param user
	 *            the instance which requests the clean up.
	 */
	public void cleanup(Object user);

	/**
	 * Gets the default secure/insecure communication status for the supported
	 * call sessions.
	 * 
	 * @return default secure communication status for the supported call
	 *         sessions.
	 */
	public boolean getSecureCommunicationStatus();

	/**
	 * Gets the <tt>SrtpControlType</tt> of this instance.
	 * 
	 * @return the <tt>SrtpControlType</tt> of this instance
	 */
	public SrtpControlType getSrtpControlType();

	/**
	 * Returns the <tt>SrtpListener</tt> which listens for security events.
	 * 
	 * @return the <tt>SrtpListener</tt> which listens for security events
	 */
	public SrtpListener getSrtpListener();

	/**
	 * Returns the transform engine currently used by this stream.
	 * 
	 * @return the RTP stream transformation engine
	 */
	public TransformEngine getTransformEngine();

	/**
	 * Indicates if the key exchange method is dependent on secure transport of
	 * the signaling channel.
	 * 
	 * @return <tt>true</tt> when secure signaling is required to make the
	 *         encryption secure; <tt>false</tt>, otherwise.
	 */
	public boolean requiresSecureSignalingTransport();

	/**
	 * Sets the <tt>RTPConnector</tt> which is to use or uses this SRTP engine.
	 * 
	 * @param connector
	 *            the <tt>RTPConnector</tt> which is to use or uses this SRTP
	 *            engine
	 */
	public void setConnector(AbstractRTPConnector connector);

	/**
	 * When in multistream mode, enables the master session.
	 * 
	 * @param masterSession
	 *            whether current control, controls the master session.
	 */
	public void setMasterSession(boolean masterSession);

	/**
	 * Sets the multistream data, which means that the master stream has
	 * successfully started and this will start all other streams in this
	 * session.
	 * 
	 * @param master
	 *            The security control of the master stream.
	 */
	public void setMultistream(SrtpControl master);

	/**
	 * Sets a <tt>SrtpListener</tt> that will listen for security events.
	 * 
	 * @param srtpListener
	 *            the <tt>SrtpListener</tt> that will receive the events
	 */
	public void setSrtpListener(SrtpListener srtpListener);

	/**
	 * Starts and enables zrtp in the stream holding this control.
	 * 
	 * @param mediaType
	 *            the media type of the stream this control controls.
	 */
	public void start(RTPMediaType mediaType);

	/**
	 * Registers <tt>user</tt> as an instance which is currently using this
	 * <tt>SrtpControl</tt>.
	 * 
	 * @param user
	 */
	public void registerUser(Object user);
}
