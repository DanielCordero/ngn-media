package javax.media;

import java.util.Vector;

import net.sf.fmj.registry.Registry;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/CaptureDeviceManager.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class CaptureDeviceManager
{
	   public static synchronized boolean addDevice(CaptureDeviceInfo newDevice)
	    {
	        return Registry.getInstance().addDevice(newDevice);
	    }


	    public static synchronized CaptureDeviceInfo getDevice(String deviceName)
	    {
	        for (CaptureDeviceInfo captureDeviceInfo : getDeviceList())
	        {
	            if (captureDeviceInfo.getName().equals(deviceName))
	                return captureDeviceInfo;
	        }
	        return null;
	    }

	    public static synchronized Vector<CaptureDeviceInfo> getDeviceList() // not in javax.media.CaptureDeviceManager
	    {
	        return Registry.getInstance().getDeviceList();
	    }

	    public static synchronized Vector<CaptureDeviceInfo> getDeviceList(Format format)
	    {
	        Vector<CaptureDeviceInfo> result = new Vector<CaptureDeviceInfo>();
	        for (CaptureDeviceInfo captureDeviceInfo : getDeviceList())
	        {
	            if (format == null)
	            {
	                result.add(captureDeviceInfo);
	            }
	            else
	            {
	                for (Format aFormat : captureDeviceInfo.getFormats())
	                {
	                    if (format.matches(aFormat))
	                    {
	                        result.add(captureDeviceInfo);
	                        break;
	                    }
	                }
	            }

	        }
	        return result;
	    }

	    public static synchronized boolean removeDevice(CaptureDeviceInfo device)
	    {
	        return Registry.getInstance().removeDevice(device);
	    }

	    public CaptureDeviceManager()
	    {
	        super();
	    }
	}
