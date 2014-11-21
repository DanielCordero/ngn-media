package net.sf.fmj.media.rtp;

import java.net.InetAddress;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.fmj.media.Log;
import net.sf.fmj.media.rtp.util.RTPMediaThread;

public class RTCPReporter implements Runnable
{
    private static final Logger logger
            = Logger.getLogger(RTCPReporter.class.getName());

    RTCPTransmitter transmit;
    SSRCCache cache;
    RTPMediaThread reportthread;
    Random myrand;
    boolean restart;
    boolean closed;
    InetAddress host;
    String cname;

    public RTCPReporter(SSRCCache cache, RTCPTransmitter t)
    {
        restart = false;
        closed = false;
        this.cache = cache;
        setTransmitter(t);
        reportthread = new RTPMediaThread(this, "RTCP Reporter");
        reportthread.useControlPriority();
        reportthread.setDaemon(true);
        reportthread.start();
    }

    public void close(String reason)
    {
        synchronized (reportthread)
        {
            closed = true;
            reportthread.notify();
        }
        releasessrc(reason);
        transmit.close();
    }

    public void releasessrc(String reason)
    {
        transmit.bye(reason);
        transmit.ssrcInfo.setOurs(false);
        transmit.ssrcInfo = null;
    }

    public void run()
    {
        if (restart)
            restart = false;
        do
        {
            double delay = cache
                    .calcReportInterval(cache.ourssrc.sender, false);

            if (logger.isLoggable(Level.FINEST))
            {
                logger.finest(new StringBuilder()
                        .append("RTCP reporting for ")
                        .append(cache.audio ? "audio " : "video ")
                        .append("running again after ")
                        .append(delay)
                        .append(" ms.")
                        .toString());
            }

            synchronized (reportthread)
            {
                try
                {
                    reportthread.wait((long) delay);
                } catch (InterruptedException e)
                {
                    Log.dumpStack(e);
                }
            }
            if (closed)
                return;
            if (!restart)
                transmit.report();
            else
                restart = false;
        } while (true);
    }

    public void setTransmitter(RTCPTransmitter t)
    {
        transmit = t;
    }
}
