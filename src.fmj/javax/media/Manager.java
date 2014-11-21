package javax.media;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushDataSource;
import javax.media.protocol.SourceCloneable;

import net.sf.fmj.media.protocol.CloneableCaptureDevicePullBufferDataSource;
import net.sf.fmj.media.protocol.CloneableCaptureDevicePullDataSource;
import net.sf.fmj.media.protocol.CloneableCaptureDevicePushBufferDataSource;
import net.sf.fmj.media.protocol.CloneableCaptureDevicePushDataSource;
import net.sf.fmj.media.protocol.CloneablePullBufferDataSource;
import net.sf.fmj.media.protocol.CloneablePullDataSource;
import net.sf.fmj.media.protocol.CloneablePushBufferDataSource;
import net.sf.fmj.media.protocol.CloneablePushDataSource;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Manager.html"
 * target="_blank">this class in the JMF Javadoc</a>.
 * 
 * @author Ken Larson
 * 
 */
public final class Manager {


	public static final int MAX_SECURITY = 1;

	public static final int CACHING = 2;

	public static final int LIGHTWEIGHT_RENDERER = 3;

	public static final int PLUGIN_PLAYER = 4;

	private static final Map<Integer, Object> hints = new HashMap<Integer, Object>();

	static {
		hints.put(Integer.valueOf(MAX_SECURITY), Boolean.FALSE);
		hints.put(Integer.valueOf(CACHING), Boolean.TRUE);
		hints.put(Integer.valueOf(LIGHTWEIGHT_RENDERER), Boolean.FALSE);
		hints.put(Integer.valueOf(PLUGIN_PLAYER), Boolean.FALSE);

	}



	public static DataSource createCloneableDataSource(DataSource source) {
		if (source instanceof SourceCloneable)
			return source;

		if (source instanceof PushBufferDataSource) {
			if (source instanceof CaptureDevice)
				return new CloneableCaptureDevicePushBufferDataSource((PushBufferDataSource) source);
			else
				return new CloneablePushBufferDataSource((PushBufferDataSource) source);
		} else if (source instanceof PullBufferDataSource) {
			if (source instanceof CaptureDevice)
				return new CloneableCaptureDevicePullBufferDataSource((PullBufferDataSource) source);
			else
				return new CloneablePullBufferDataSource((PullBufferDataSource) source);
		} else if (source instanceof PushDataSource) {
			if (source instanceof CaptureDevice)
				return new CloneableCaptureDevicePushDataSource((PushDataSource) source);
			else
				return new CloneablePushDataSource((PushDataSource) source);
		} else if (source instanceof PullDataSource) {
			if (source instanceof CaptureDevice)
				return new CloneableCaptureDevicePullDataSource((PullDataSource) source);
			else
				return new CloneablePullDataSource((PullDataSource) source);
		} else
			throw new IllegalArgumentException("Unknown or unsupported DataSource type: " + source);
	}

	public static DataSink createDataSink(DataSource datasource, MediaLocator destLocator) throws NoDataSinkException {
		// final String protocol = destLocator.getProtocol();
		//
		// for (String handlerClassName : getDataSinkClassList(protocol)) {
		// try {
		// final Class<?> handlerClass = Class.forName(handlerClassName);
		// if (!DataSink.class.isAssignableFrom(handlerClass) &&
		// !DataSinkProxy.class.isAssignableFrom(handlerClass))
		// continue; // skip any classes that will not be matched
		// // below.
		//
		// final MediaHandler handler = (MediaHandler)
		// handlerClass.newInstance();
		//
		// handler.setSource(datasource);
		// if (handler instanceof DataSink) {
		// DataSink dataSink = (DataSink) handler;
		// dataSink.setOutputLocator(destLocator);
		// return dataSink;
		// } else if (handler instanceof DataSinkProxy) {
		// // If the MediaHandler is a DataSinkProxy, obtain the
		// // content type of the proxy using the getContentType()
		// // method.
		// // Now obtain a list of MediaHandlers that support the
		// // protocol of the Medialocator and the content type
		// // returned by the proxy
		// // i.e. look for
		// // content-prefix.media.datasink.protocol.content-type.Handler
		// final DataSinkProxy mediaProxy = (DataSinkProxy) handler;
		//
		// Vector<String> handlerClassList2 = getDataSinkClassList(protocol +
		// "." + toPackageFriendly(mediaProxy.getContentType(destLocator)));
		//
		// for (String handlerClassName2 : handlerClassList2) {
		// try {
		// final Class<?> handlerClass2 = Class.forName(handlerClassName2);
		// if (!DataSink.class.isAssignableFrom(handlerClass2))
		// continue; // skip any classes that will not be
		// // matched below.
		// final MediaHandler handler2 = (MediaHandler)
		// handlerClass2.newInstance();
		// handler2.setSource(mediaProxy.getDataSource());
		// if (handler2 instanceof DataSink) {
		// DataSink dataSink = (DataSink) handler2;
		// dataSink.setOutputLocator(destLocator);
		// return (DataSink) handler2;
		// }
		//
		// } catch (ClassNotFoundException e) {
		// logger.finer("createDataSink: " + e); // no need for
		// // call
		// // stack
		// continue;
		// } catch (IncompatibleSourceException e) {
		// logger.fine("createDataSink(" + datasource + ", " + destLocator +
		// "), proxy=" + mediaProxy.getDataSource() + ": " + e); // no
		// // need
		// // for
		// // call
		// // stack
		// continue;
		// } catch (NoClassDefFoundError e) {
		// logger.log(Level.FINE, "" + e, e);
		// continue;
		// } catch (Exception e) {
		// logger.log(Level.FINE, "" + e, e);
		// continue;
		// }
		// }
		//
		// }
		// } catch (ClassNotFoundException e) {
		// logger.finer("createDataSink: " + e); // no need for call stack
		// continue;
		// } catch (IncompatibleSourceException e) {
		// logger.fine("createDataSink(" + datasource + ", " + destLocator +
		// "): " + e); // no
		// // need
		// // for
		// // call
		// // stack
		// continue;
		// } catch (NoClassDefFoundError e) {
		// logger.log(Level.FINE, "" + e, e);
		// continue;
		// } catch (Exception e) {
		// logger.log(Level.FINE, "" + e, e);
		// continue;
		// }
		// }
		//
		// throw new NoDataSinkException();
		return null;
	}

	// this method has a fundamental flaw (carried over from JMF): the
	// DataSource may not be
	// accepted by the Handler. So createPlayer(createDataSource(MediaLocator))
	// is not equivalent to
	// createPlayer(MediaLocator)
	public static DataSource createDataSource(MediaLocator sourceLocator) throws java.io.IOException, NoDataSourceException {
		final String protocol = sourceLocator.getProtocol();
		DataSource dataSource = null;
		if (protocol.equals("wasapi")) {
			dataSource = new org.jitsi.impl.neomedia.jmfext.media.protocol.wasapi.DataSource();
		} else if (protocol.equals("directshow")) {
			dataSource = new org.jitsi.impl.neomedia.jmfext.media.protocol.directshow.DataSource();

		}
		if (dataSource != null) {
			dataSource.setLocator(sourceLocator);
			dataSource.connect();
		}
		return dataSource;
	}

	public static Processor createProcessor(DataSource source) throws IncompatibleSourceException, IOException {
		net.sf.fmj.media.MediaProcessor handler = new net.sf.fmj.media.MediaProcessor();
		handler.setSource(source);
		return handler;
	}


	public static Object getHint(int hint) {
		return hints.get(hint);
	}

}
