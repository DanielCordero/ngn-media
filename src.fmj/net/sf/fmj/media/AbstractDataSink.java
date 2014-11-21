package net.sf.fmj.media;

import java.util.ArrayList;
import java.util.List;

import javax.media.DataSink;
import javax.media.MediaLocator;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;

/**
 * Abstract base class to implement DataSink.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractDataSink implements DataSink
{
    private final List<DataSinkListener> listeners
        = new ArrayList<DataSinkListener>();

    protected MediaLocator outputLocator;

    public void addDataSinkListener(DataSinkListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    public MediaLocator getOutputLocator()
    {
        return outputLocator;
    }

    protected void notifyDataSinkListeners(DataSinkEvent event)
    {
        DataSinkListener[] listenersCopy;

        synchronized (listeners)
        {
            listenersCopy
                = listeners.toArray(new DataSinkListener[listeners.size()]);
        }

        for (DataSinkListener listener : listenersCopy)
        {
            listener.dataSinkUpdate(event);
        }
    }

    public void removeDataSinkListener(DataSinkListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    public void setOutputLocator(MediaLocator output)
    {
        this.outputLocator = output;
    }
}
