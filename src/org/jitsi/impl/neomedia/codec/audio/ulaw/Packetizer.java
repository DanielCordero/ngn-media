/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.impl.neomedia.codec.audio.ulaw;

/**
 * Overrides the ULaw Packetizer with a different packet size.
 * 
 * @author Thomas Hofer
 */
public class Packetizer extends net.sf.fmj.media.codec.audio.ulaw.Packetizer {

	protected String PLUGIN_NAME = "ULAW Packetizer"; // TODO: hacked for
	// SIP-Communicator

	protected int packetSize; // TODO: hacked for SIP-Communicator

	@Override
	public String getName() {
		return PLUGIN_NAME;
	}

	/**
	 * Constructs a new ULaw <tt>Packetizer</tt>.
	 */
	public Packetizer() {
		// RFC 3551 4.5 Audio Encodings default ms/packet is 20
		packetSize = 160;
		setPacketSize(packetSize);

		PLUGIN_NAME = "ULaw Packetizer";
	}
}
