/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.impl.neomedia;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.ice4j.socket.MultiplexingDatagramSocket;
import org.jitsi.impl.neomedia.transform.TransformInputStream;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.packetlogging.PacketLoggingService;

/**
 * RTPConnectorInputStream implementation for UDP protocol.
 * 
 * @author Sebastien Vincent
 */
public class RTPConnectorUDPInputStream extends TransformInputStream<DatagramSocket> {
	/**
	 * Initializes a new <tt>RTPConnectorInputStream</tt> which is to receive
	 * packet data from a specific UDP socket.
	 * 
	 * @param socket
	 *            the UDP socket the new instance is to receive data from
	 */
	public RTPConnectorUDPInputStream(DatagramSocket socket) {
		super(socket);
	}

	/**
	 * Log the packet.
	 * 
	 * @param p
	 *            packet to log
	 */
	@Override
	protected void doLogPacket(DatagramPacket p) {
		if (socket.getLocalAddress() == null)
			return;

		// Do not log the packet if this one has been processed (and already
		// logged) by the ice4j stack.
		if (socket instanceof MultiplexingDatagramSocket)
			return;

		PacketLoggingService packetLogging = LibJitsi.getPacketLoggingService();

		if (packetLogging != null)
			packetLogging.logPacket(PacketLoggingService.ProtocolName.RTP, p.getAddress().getAddress(), p.getPort(), socket.getLocalAddress().getAddress(), socket.getLocalPort(), PacketLoggingService.TransportName.UDP, false, p.getData(), p.getOffset(), p.getLength());
	}

	/**
	 * Receive packet.
	 * 
	 * @param p
	 *            packet for receiving
	 * @throws IOException
	 *             if something goes wrong during receiving
	 */
	@Override
	protected void receive(DatagramPacket p) throws IOException {
		socket.receive(p);
	}

	@Override
	protected void setReceiveBufferSize(int receiveBufferSize) throws IOException {
		socket.setReceiveBufferSize(receiveBufferSize);
	}
}
