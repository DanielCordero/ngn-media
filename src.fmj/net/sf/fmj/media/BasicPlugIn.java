package net.sf.fmj.media;

import java.lang.reflect.Method;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.PlugIn;
import javax.media.PlugInManager;
import javax.media.format.VideoFormat;

/**
 * Basic implementation for the <tt>PlugIn</tt> interface
 */
public abstract class BasicPlugIn implements PlugIn
{
    private static final boolean DEBUG = false;

    // This is a Package private class
    // Currently used by a few classes in this package
    // Not used in this class as we can use the generic Class.forName
    static public Class<?> getClassForName(String className)
            throws ClassNotFoundException
    {
        try
        {
            return Class.forName(className);
        } catch (Exception e)
        {
        } catch (Error e)
        {
        }
		return null;

    }

    /**
     * Utility to perform format matching.
     */
    public static Format matches(Format in, Format outs[])
    {
        for (int i = 0; i < outs.length; i++)
        {
            if (in.matches(outs[i]))
                return outs[i];
        }

        return null;
    }

    /**
     * Check to see if a particular plugin exists in the registry.
     */
    static public boolean plugInExists(String name, int type)
    {
        Vector cnames = PlugInManager.getPlugInList(null, null, type);
        for (int i = 0; i < cnames.size(); i++)
        {
            if (name.equals((cnames.elementAt(i))))
                return true;
        }
        return false;
    }

    protected Object[] controls = new Control[0];
    /**
     * throws RuntimeException
     */

    private static boolean jdkInit = false;

    private static Method forName3ArgsM;

    private static ClassLoader systemClassLoader;

    private static Method getContextClassLoaderM;

    // utilities for allocating data buffers
    // =====================================

    protected void error()
    {
        throw new RuntimeException(getClass().getName() + " PlugIn error");
    }

    /**
     * Return the control based on a control type for the PlugIn.
     */
    public Object getControl(String controlType)
    {
        try
        {
            Class<?> cls = Class.forName(controlType);
            Object cs[] = getControls();
            for (int i = 0; i < cs.length; i++)
            {
                if (cls.isInstance(cs[i]))
                    return cs[i];
            }
            return null;

        } catch (Exception e)
        { // no such controlType or such control
            return null;
        }
    }

    /**
     * no controls
     */
    public Object[] getControls()
    {
        return controls;
    }

    protected Object getInputData(Buffer inBuffer)
    {
        return inBuffer.getData();
    }

    protected final long getNativeData(Object data)
    {
        return 0;
    }

    protected Object getOutputData(Buffer buffer)
    {
        return buffer.getData();
    }

    /**
     * validate that the Buffer object's data size is at least newSize.
     *
     * @return array with sufficient capacity
     */
    protected byte[] validateByteArraySize(Buffer buffer, int newSize)
    {
        Object objectArray = buffer.getData();
        byte[] typedArray;

        if (objectArray instanceof byte[])
        { // is correct type AND not null
            typedArray = (byte[]) objectArray;
            if (typedArray.length >= newSize)
            { // is sufficient capacity
                return typedArray;
            }

            byte[] tempArray = new byte[newSize]; // re-alloc array
            System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
            typedArray = tempArray;
        } else
        {
            typedArray = new byte[newSize];
        }

        if (DEBUG)
            System.out.println(getClass().getName() + " : allocating byte["
                    + newSize + "] data");

        buffer.setData(typedArray);
        return typedArray;
    }

    protected Object validateData(Buffer buffer, int length, boolean allowNative)
    {
        Format format = buffer.getFormat();
        Class<?> dataType = format.getDataType();

        if (length < 1 && format != null)
        {
            if (format instanceof VideoFormat)
                length = ((VideoFormat) format).getMaxDataLength();
        }

        {
            if (dataType == Format.byteArray)
                return validateByteArraySize(buffer, length);
            else if (dataType == Format.shortArray)
                return validateShortArraySize(buffer, length);
            else if (dataType == Format.intArray)
                return validateIntArraySize(buffer, length);
            else
            {
                System.err.println("Error in validateData");
                return null;
            }
        }
    }

    /**
     * validate that the Buffer object's data size is at least newSize.
     *
     * @return array with sufficient capacity
     */
    protected int[] validateIntArraySize(Buffer buffer, int newSize)
    {
        Object objectArray = buffer.getData();
        int[] typedArray;

        if (objectArray instanceof int[])
        { // is correct type AND not null
            typedArray = (int[]) objectArray;
            if (typedArray.length >= newSize)
            { // is sufficient capacity
                return typedArray;
            }

            int[] tempArray = new int[newSize]; // re-alloc array
            System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
            typedArray = tempArray;
        } else
        {
            typedArray = new int[newSize];
        }

        if (DEBUG)
            System.out.println(getClass().getName() + " : allocating int["
                    + newSize + "] data");

        buffer.setData(typedArray);
        return typedArray;
    }

    /**
     * validate that the Buffer object's data size is at least newSize.
     *
     * @return array with sufficient capacity
     */
    protected short[] validateShortArraySize(Buffer buffer, int newSize)
    {
        Object objectArray = buffer.getData();
        short[] typedArray;

        if (objectArray instanceof short[])
        { // is correct type AND not null
            typedArray = (short[]) objectArray;
            if (typedArray.length >= newSize)
            { // is sufficient capacity
                return typedArray;
            }

            short[] tempArray = new short[newSize]; // re-alloc array
            System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
            typedArray = tempArray;
        } else
        {
            typedArray = new short[newSize];
        }

        if (DEBUG)
            System.out.println(getClass().getName() + " : allocating short["
                    + newSize + "] data");

        buffer.setData(typedArray);
        return typedArray;
    }
}
