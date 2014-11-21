package javax.media;

import java.util.Vector;
import java.util.logging.Logger;

import net.sf.fmj.registry.Registry;
import net.sf.fmj.utility.LoggerSingleton;

public class PackageManager
{
    private static final Logger logger = LoggerSingleton.logger;

    private static Registry registry = Registry.getInstance();

    public static synchronized Vector<String> getContentPrefixList()
    {
        return registry.getContentPrefixList();
    }

    public static synchronized Vector<String> getProtocolPrefixList()
    {
        return registry.getProtocolPrefixList();
    }

    public static synchronized void setContentPrefixList(Vector list)
    {
        registry.setContentPrefixList(list);
    }

    public static synchronized void setProtocolPrefixList(Vector list)
    {
        registry.setProtocolPrefixList(list);
    }

    public PackageManager()
    { // nothing to do
    }
    
}
