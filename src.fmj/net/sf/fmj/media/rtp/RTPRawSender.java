package net.sf.fmj.media.rtp;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.media.rtp.RTPConnector;
import javax.media.rtp.SessionAddress;

import net.sf.fmj.media.Log;
import net.sf.fmj.media.rtp.util.Packet;
import net.sf.fmj.media.rtp.util.PacketConsumer;
import net.sf.fmj.media.rtp.util.PacketFilter;
import net.sf.fmj.media.rtp.util.RTPPacket;
import net.sf.fmj.media.rtp.util.RTPPacketSender;
import net.sf.fmj.media.rtp.util.UDPPacket;
import net.sf.fmj.media.rtp.util.UDPPacketSender;

public class RTPRawSender extends PacketFilter
{
    private InetAddress destaddr;
    private int destport;
    private DatagramSocket socket;
    private RTPConnector rtpConnector;

    public RTPRawSender(int port, String address) throws UnknownHostException,
            IOException
    {
        socket = null;
        rtpConnector = null;
        destaddr = InetAddress.getByName(address);
        destport = port;
        super.destAddressList = null;
    }

    public RTPRawSender(int port, String address, UDPPacketSender sender)
            throws UnknownHostException, IOException
    {
        this(port, address);
        socket = sender.getSocket();
        setConsumer(sender);
        super.destAddressList = null;
    }

    public RTPRawSender(RTPPacketSender sender)
    {
        socket = null;
        rtpConnector = null;
        rtpConnector = sender.getConnector();
        setConsumer(sender);
    }

    public void assemble(RTPPacket p)
    {
        int len = p.calcLength();
        p.assemble(len, false);
    }

    @Override
    public String filtername()
    {
        return "RTP Raw Packet Sender";
    }

    public InetAddress getRemoteAddr()
    {
        return destaddr;
    }

    public int getSendBufSize()
    {
        try
        {
            if (socket != null)
            {
                Class<?> cls = socket.getClass();
                Method m = cls.getMethod("getSendBufferSize");
                Integer res = (Integer) m.invoke(socket);
                return res.intValue();
            }
            if (rtpConnector != null)
                return rtpConnector.getSendBufferSize();
        } catch (Exception e)
        {
        }
        return -1;
    }

    @Override
    public Packet handlePacket(Packet p)
    {
        assemble((RTPPacket) p);
        PacketConsumer consumer = getConsumer();
        if (consumer instanceof RTPPacketSender)
        {
            return p;
        } else
        {
            UDPPacket udpp = new UDPPacket();
            udpp.received = false;
            udpp.data = p.data;
            udpp.offset = p.offset;
            udpp.length = p.length;
            udpp.remoteAddress = destaddr;
            udpp.remotePort = destport;
            return udpp;
        }
    }

    @Override
    public Packet handlePacket(Packet p, int i)
    {
        return null;
    }

    @Override
    public Packet handlePacket(Packet p, SessionAddress sessionAddress)
    {
        assemble((RTPPacket) p);
        PacketConsumer consumer = getConsumer();
        if (consumer instanceof RTPPacketSender)
        {
            return p;
        } else
        {
            UDPPacket udpp = new UDPPacket();
            udpp.received = false;
            udpp.data = p.data;
            udpp.offset = p.offset;
            udpp.length = p.length;
            udpp.remoteAddress = sessionAddress.getDataAddress();
            udpp.remotePort = sessionAddress.getDataPort();
            return udpp;
        }
    }

    public void setDestAddresses(Vector destAddresses)
    {
        super.destAddressList = destAddresses;
    }

    public void setSendBufSize(int size)
    {
        try
        {
            if (socket != null)
            {
                Class<?> cls = socket.getClass();
                Method m = cls.getMethod("setSendBufferSize",
                        new Class[] { Integer.TYPE });
                m.invoke(socket, new Object[] { new Integer(size) });
            } else if (rtpConnector != null)
                rtpConnector.setSendBufferSize(size);
        } catch (Exception e)
        {
            Log.comment("Cannot set send buffer size: " + e);
        }
    }
}
