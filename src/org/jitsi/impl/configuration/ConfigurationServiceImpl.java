/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.impl.configuration;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jitsi.service.configuration.ConfigPropertyVetoException;
import org.jitsi.service.configuration.ConfigVetoableChangeListener;
import org.jitsi.service.configuration.ConfigurationService;
import org.jitsi.service.fileaccess.FileAccessService;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.util.Logger;

/**
 * A straightforward implementation of the <tt>ConfigurationService</tt> using
 * an XML or a .properties file for storing properties. Currently only
 * <tt>String</tt> properties are meaningfully saved (we should probably
 * consider how and whether we should take care of the rest).
 * 
 * @author Emil Ivov
 * @author Damian Minkov
 * @author Lyubomir Marinov
 * @author Dmitri Melnikov
 */
public class ConfigurationServiceImpl implements ConfigurationService {
	/**
	 * The <tt>Logger</tt> used by this <tt>ConfigurationServiceImpl</tt>
	 * instance for logging output.
	 */
	private final Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

	/**
	 * A set of properties deployed with the application during install time.
	 * Contrary to the properties in {@link #immutableDefaultProperties} the
	 * ones in this map can be overridden with call to the
	 * <tt>setProperty()</tt> methods. Still, re-setting one of these properties
	 * to <tt>null</tt> would cause for its initial value to be restored.
	 */
	private Map<String, String> defaultProperties = new HashMap<String, String>();

	/**
	 * Our event dispatcher.
	 */
	private final ChangeEventDispatcher changeEventDispatcher = new ChangeEventDispatcher(this);

	public ConfigurationServiceImpl() {
	}

	/**
	 * Sets the property with the specified name to the specified value. Calling
	 * this method would first trigger a PropertyChangeEvent that will be
	 * dispatched to all VetoableChangeListeners. In case no complaints
	 * (PropertyVetoException) have been received, the property will be actually
	 * changed and a PropertyChangeEvent will be dispatched.
	 * <p>
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param property
	 *            the object that we'd like to be come the new value of the
	 *            property.
	 * 
	 * @throws ConfigPropertyVetoException
	 *             in case someone is not happy with the change.
	 */
	public void setProperty(String propertyName, Object property) throws ConfigPropertyVetoException {
		setProperty(propertyName, property, false);
	}

	/**
	 * Sets the property with the specified name to the specified. Calling this
	 * method would first trigger a PropertyChangeEvent that will be dispatched
	 * to all VetoableChangeListeners. In case no complaints
	 * (PropertyVetoException) have been received, the property will be actually
	 * changed and a PropertyChangeEvent will be dispatched. This method also
	 * allows the caller to specify whether or not the specified property is a
	 * system one.
	 * <p>
	 * 
	 * @param propertyName
	 *            the name of the property to change.
	 * @param property
	 *            the new value of the specified property.
	 * @param isSystem
	 *            specifies whether or not the property being is a System
	 *            property and should be resolved against the system property
	 *            set. If the property has previously been specified as system
	 *            then this value is internally forced to true.
	 * @throws ConfigPropertyVetoException
	 *             in case someone is not happy with the change.
	 */
	public void setProperty(String propertyName, Object property, boolean isSystem) throws ConfigPropertyVetoException {
		Object oldValue = getProperty(propertyName);

		// first check whether the change is ok with everyone
		if (changeEventDispatcher.hasVetoableChangeListeners(propertyName))
			changeEventDispatcher.fireVetoableChange(propertyName, oldValue, property);

		doSetProperty(propertyName, property, isSystem);

		if (changeEventDispatcher.hasPropertyChangeListeners(propertyName))
			changeEventDispatcher.firePropertyChange(propertyName, oldValue, property);
	}

	/*
	 * Implements ConfigurationService#setProperties(Map). Optimizes the setting
	 * of properties by performing a single saving of the property store to the
	 * configuration file which is known to be slow because it involves
	 * converting the whole store to a string representation and writing a file
	 * to the disk.
	 * 
	 * @throws ConfigPropertyVetoException in case someone is not happy with the
	 * change.
	 */
	public void setProperties(Map<String, Object> properties) throws ConfigPropertyVetoException {
		// first check whether the changes are ok with everyone
		Map<String, Object> oldValues = new HashMap<String, Object>(properties.size());
		for (Map.Entry<String, Object> property : properties.entrySet()) {
			String propertyName = property.getKey();
			Object oldValue = getProperty(propertyName);

			oldValues.put(propertyName, oldValue);

			if (changeEventDispatcher.hasVetoableChangeListeners(propertyName))
				changeEventDispatcher.fireVetoableChange(propertyName, oldValue, property.getValue());
		}

		for (Map.Entry<String, Object> property : properties.entrySet())
			doSetProperty(property.getKey(), property.getValue(), false);
		for (Map.Entry<String, Object> property : properties.entrySet()) {
			String propertyName = property.getKey();

			if (changeEventDispatcher.hasPropertyChangeListeners(propertyName))
				changeEventDispatcher.firePropertyChange(propertyName, oldValues.get(propertyName), property.getValue());
		}
	}

	/**
	 * Performs the actual setting of a property with a specific name to a
	 * specific new value without asking <code>VetoableChangeListener</code>,
	 * storing into the configuration file and notifying
	 * <code>PrpoertyChangeListener</code>s.
	 * 
	 * @param propertyName
	 *            the name of the property which is to be set to a specific
	 *            value
	 * @param property
	 *            the value to be assigned to the property with the specified
	 *            name
	 * @param isSystem
	 *            <tt>true</tt> if the property with the specified name is to be
	 *            set as a system property; <tt>false</tt>, otherwise
	 */
	private void doSetProperty(String propertyName, Object property, boolean isSystem) {
		defaultProperties.put(propertyName, property.toString());
	}

	/**
	 * Removes the property with the specified name. Calling this method would
	 * first trigger a PropertyChangeEvent that will be dispatched to all
	 * VetoableChangeListeners. In case no complaints (PropertyVetoException)
	 * have been received, the property will be actually changed and a
	 * PropertyChangeEvent will be dispatched. All properties with prefix
	 * propertyName will also be removed.
	 * <p>
	 * 
	 * @param propertyName
	 *            the name of the property to change.
	 */
	public void removeProperty(String propertyName) {
		List<String> childPropertyNames = getPropertyNamesByPrefix(propertyName, false);

		// remove all properties
		for (String pName : childPropertyNames) {
			removePropertyInternal(pName);
		}
	}

	/**
	 * Removes the property with the specified name. Calling this method would
	 * first trigger a PropertyChangeEvent that will be dispatched to all
	 * VetoableChangeListeners. In case no complaints (PropertyVetoException)
	 * have been received, the property will be actually changed and a
	 * PropertyChangeEvent will be dispatched. All properties with prefix
	 * propertyName will also be removed.
	 * <p>
	 * Does not store anything.
	 * 
	 * @param propertyName
	 *            the name of the property to change.
	 */
	private void removePropertyInternal(String propertyName) {
		Object oldValue = getProperty(propertyName);
		// first check whether the change is ok with everyone
		if (changeEventDispatcher.hasVetoableChangeListeners(propertyName))
			changeEventDispatcher.fireVetoableChange(propertyName, oldValue, null);

		defaultProperties.remove(propertyName);

		if (changeEventDispatcher.hasPropertyChangeListeners(propertyName))
			changeEventDispatcher.firePropertyChange(propertyName, oldValue, null);
	}

	/**
	 * Returns the value of the property with the specified name or null if no
	 * such property exists.
	 * 
	 * @param propertyName
	 *            the name of the property that is being queried.
	 * @return the value of the property with the specified name.
	 */
	public Object getProperty(String propertyName) {
		return defaultProperties.get(propertyName);
	}

	/**
	 * Returns a <tt>java.util.List</tt> of <tt>String</tt>s containing all
	 * property names.
	 * 
	 * @return a <tt>java.util.List</tt>containing all property names
	 */
	public List<String> getAllPropertyNames() {
		List<String> resultKeySet = new LinkedList<String>();

		for (String key : defaultProperties.keySet()) {
			resultKeySet.add(key);
		}

		return resultKeySet;
	}

	/**
	 * Returns a <tt>java.util.List</tt> of <tt>String</tt>s containing the all
	 * property names that have the specified prefix. Depending on the value of
	 * the <tt>exactPrefixMatch</tt> parameter the method will (when false) or
	 * will not (when exactPrefixMatch is true) include property names that have
	 * prefixes longer than the specified <tt>prefix</tt> param.
	 * <p>
	 * Example:
	 * <p>
	 * Imagine a configuration service instance containing 2 properties only:<br>
	 * <code>
	 * net.java.sip.communicator.PROP1=value1<br>
	 * net.java.sip.communicator.service.protocol.PROP1=value2
	 * </code>
	 * <p>
	 * A call to this method with a prefix="net.java.sip.communicator" and
	 * exactPrefixMatch=true would only return the first property -
	 * net.java.sip.communicator.PROP1, whereas the same call with
	 * exactPrefixMatch=false would return both properties as the second prefix
	 * includes the requested prefix string.
	 * <p>
	 * In addition to stored properties this method will also search the default
	 * mutable and immutable properties.
	 * 
	 * @param prefix
	 *            a String containing the prefix (the non dotted non-caps part
	 *            of a property name) that we're looking for.
	 * @param exactPrefixMatch
	 *            a boolean indicating whether the returned property names
	 *            should all have a prefix that is an exact match of the the
	 *            <tt>prefix</tt> param or whether properties with prefixes that
	 *            contain it but are longer than it are also accepted.
	 * @return a <tt>java.util.List</tt>containing all property name String-s
	 *         matching the specified conditions.
	 */
	public List<String> getPropertyNamesByPrefix(String prefix, boolean exactPrefixMatch) {
		HashSet<String> resultKeySet = new HashSet<String>();

		// first fill in the names from the immutable default property set
		Set<String> propertyNameSet;
		String[] namesArray;

		// finally, get property names from mutable default property set.
		if (defaultProperties.size() > 0) {
			propertyNameSet = defaultProperties.keySet();

			namesArray = propertyNameSet.toArray(new String[propertyNameSet.size()]);

			getPropertyNamesByPrefix(prefix, exactPrefixMatch, namesArray, resultKeySet);
		}

		return new ArrayList<String>(resultKeySet);
	}

	/**
	 * Updates the specified <tt>String</tt> <tt>resulSet</tt> to contain all
	 * property names in the <tt>names</tt> array that partially or completely
	 * match the specified prefix. Depending on the value of the
	 * <tt>exactPrefixMatch</tt> parameter the method will (when false) or will
	 * not (when exactPrefixMatch is true) include property names that have
	 * prefixes longer than the specified <tt>prefix</tt> param.
	 * 
	 * @param prefix
	 *            a String containing the prefix (the non dotted non-caps part
	 *            of a property name) that we're looking for.
	 * @param exactPrefixMatch
	 *            a boolean indicating whether the returned property names
	 *            should all have a prefix that is an exact match of the the
	 *            <tt>prefix</tt> param or whether properties with prefixes that
	 *            contain it but are longer than it are also accepted.
	 * @param names
	 *            the list of names that we'd like to search.
	 * 
	 * @return a reference to the updated result set.
	 */
	private Set<String> getPropertyNamesByPrefix(String prefix, boolean exactPrefixMatch, String[] names, Set<String> resultSet) {
		for (String key : names) {
			if (exactPrefixMatch) {
				int ix = key.lastIndexOf('.');

				if (ix == -1)
					continue;

				String keyPrefix = key.substring(0, ix);

				if (prefix.equals(keyPrefix))
					resultSet.add(key);
			} else {
				if (key.startsWith(prefix))
					resultSet.add(key);
			}
		}

		return resultSet;
	}

	/**
	 * Returns a <tt>List</tt> of <tt>String</tt>s containing the property names
	 * that have the specified suffix. A suffix is considered to be everything
	 * after the last dot in the property name.
	 * <p>
	 * For example, imagine a configuration service instance containing two
	 * properties only:
	 * </p>
	 * <code>
	 * net.java.sip.communicator.PROP1=value1
	 * net.java.sip.communicator.service.protocol.PROP1=value2
	 * </code>
	 * <p>
	 * A call to this method with <tt>suffix</tt> equal to "PROP1" will return
	 * both properties, whereas the call with <tt>suffix</tt> equal to
	 * "communicator.PROP1" or "PROP2" will return an empty <tt>List</tt>. Thus,
	 * if the <tt>suffix</tt> argument contains a dot, nothing will be found.
	 * </p>
	 * 
	 * @param suffix
	 *            the suffix for the property names to be returned
	 * @return a <tt>List</tt> of <tt>String</tt>s containing the property names
	 *         which contain the specified <tt>suffix</tt>
	 */
	public List<String> getPropertyNamesBySuffix(String suffix) {
		List<String> resultKeySet = new LinkedList<String>();

		for (String key : defaultProperties.keySet()) {
			int ix = key.lastIndexOf('.');

			if ((ix != -1) && suffix.equals(key.substring(ix + 1)))
				resultKeySet.add(key);
		}
		return resultKeySet;
	}

	/**
	 * Adds a PropertyChangeListener to the listener list.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeEventDispatcher.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeEventDispatcher.removePropertyChangeListener(listener);
	}

	/**
	 * Adds a PropertyChangeListener to the listener list for a specific
	 * property.
	 * 
	 * @param propertyName
	 *            one of the property names listed above
	 * @param listener
	 *            the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeEventDispatcher.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list for a specific
	 * property.
	 * 
	 * @param propertyName
	 *            a valid property name
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeEventDispatcher.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Adds a VetoableChangeListener to the listener list.
	 * 
	 * @param listener
	 *            the VetoableChangeListener to be added
	 */
	public void addVetoableChangeListener(ConfigVetoableChangeListener listener) {
		changeEventDispatcher.addVetoableChangeListener(listener);
	}

	/**
	 * Removes a VetoableChangeListener from the listener list.
	 * 
	 * @param listener
	 *            the VetoableChangeListener to be removed
	 */
	public void removeVetoableChangeListener(ConfigVetoableChangeListener listener) {
		changeEventDispatcher.removeVetoableChangeListener(listener);
	}

	/**
	 * Adds a VetoableChangeListener to the listener list for a specific
	 * property.
	 * 
	 * @param propertyName
	 *            one of the property names listed above
	 * @param listener
	 *            the VetoableChangeListener to be added
	 */
	public void addVetoableChangeListener(String propertyName, ConfigVetoableChangeListener listener) {
		changeEventDispatcher.addVetoableChangeListener(propertyName, listener);
	}

	/**
	 * Removes a VetoableChangeListener from the listener list for a specific
	 * property.
	 * 
	 * @param propertyName
	 *            a valid property name
	 * @param listener
	 *            the VetoableChangeListener to be removed
	 */
	public void removeVetoableChangeListener(String propertyName, ConfigVetoableChangeListener listener) {
		changeEventDispatcher.removeVetoableChangeListener(propertyName, listener);
	}

	/**
	 * Returns the value of the specified java system property. In case the
	 * value was a zero length String or one that only contained whitespaces,
	 * null is returned. This method is for internal use only. Users of the
	 * configuration service are to use the getProperty() or getString() methods
	 * which would automatically determine whether a property is system or not.
	 * 
	 * @param propertyName
	 *            the name of the property whose value we need.
	 * @return the value of the property with name propertyName or null if the
	 *         value had length 0 or only contained spaces tabs or new lines.
	 */
	private static String getSystemProperty(String propertyName) {
		String retval = System.getProperty(propertyName);
		if ((retval != null) && (retval.trim().length() == 0))
			retval = null;
		return retval;
	}

	/**
	 * Returns the String value of the specified property (minus all
	 * encompasssing whitespaces)and null in case no property value was mapped
	 * against the specified propertyName, or in case the returned property
	 * string had zero length or contained whitespaces only.
	 * 
	 * @param propertyName
	 *            the name of the property that is being queried.
	 * @return the result of calling the property's toString method and null in
	 *         case there was no vlaue mapped against the specified
	 *         <tt>propertyName</tt>, or the returned string had zero length or
	 *         contained whitespaces only.
	 */
	public String getString(String propertyName) {
		Object propValue = getProperty(propertyName);
		if (propValue == null)
			return null;

		String propStrValue = propValue.toString().trim();

		return (propStrValue.length() > 0) ? propStrValue : null;
	}

	/**
	 * Returns the String value of the specified property and null in case no
	 * property value was mapped against the specified propertyName, or in case
	 * the returned property string had zero length or contained whitespaces
	 * only.
	 * 
	 * @param propertyName
	 *            the name of the property that is being queried.
	 * @param defaultValue
	 *            the value to be returned if the specified property name is not
	 *            associated with a value in this
	 *            <code>ConfigurationService</code>
	 * @return the result of calling the property's toString method and
	 *         <code>defaultValue</code> in case there was no value mapped
	 *         against the specified <tt>propertyName</tt>, or the returned
	 *         string had zero length or contained whitespaces only.
	 */
	public String getString(String propertyName, String defaultValue) {
		String value = getString(propertyName);
		return value != null ? value : defaultValue;
	}

	/**
	 * Implements ConfigurationService#getBoolean(String, boolean).
	 */
	public boolean getBoolean(String propertyName, boolean defaultValue) {
		String stringValue = getString(propertyName);

		return (stringValue == null) ? defaultValue : Boolean.parseBoolean(stringValue);
	}

	/**
	 * Gets the value of a specific property as a signed decimal integer. If the
	 * specified property name is associated with a value in this
	 * <tt>ConfigurationService</tt>, the string representation of the value is
	 * parsed into a signed decimal integer according to the rules of
	 * {@link Integer#parseInt(String)} . If parsing the value as a signed
	 * decimal integer fails or there is no value associated with the specified
	 * property name, <tt>defaultValue</tt> is returned.
	 * 
	 * @param propertyName
	 *            the name of the property to get the value of as a signed
	 *            decimal integer
	 * @param defaultValue
	 *            the value to be returned if parsing the value of the specified
	 *            property name as a signed decimal integer fails or there is no
	 *            value associated with the specified property name in this
	 *            <tt>ConfigurationService</tt>
	 * @return the value of the property with the specified name in this
	 *         <tt>ConfigurationService</tt> as a signed decimal integer;
	 *         <tt>defaultValue</tt> if parsing the value of the specified
	 *         property name fails or no value is associated in this
	 *         <tt>ConfigurationService</tt> with the specified property name
	 */
	public int getInt(String propertyName, int defaultValue) {
		String stringValue = getString(propertyName);
		int intValue = defaultValue;

		if ((stringValue != null) && (stringValue.length() > 0)) {
			try {
				intValue = Integer.parseInt(stringValue);
			} catch (NumberFormatException ex) {
				logger.error(propertyName + " does not appear to be an integer. " + "Defaulting to " + defaultValue + ".", ex);
			}
		}
		return intValue;
	}

	/**
	 * Gets the value of a specific property as a signed decimal long integer.
	 * If the specified property name is associated with a value in this
	 * <tt>ConfigurationService</tt>, the string representation of the value is
	 * parsed into a signed decimal long integer according to the rules of
	 * {@link Long#parseLong(String)} . If parsing the value as a signed decimal
	 * long integer fails or there is no value associated with the specified
	 * property name, <tt>defaultValue</tt> is returned.
	 * 
	 * @param propertyName
	 *            the name of the property to get the value of as a signed
	 *            decimal long integer
	 * @param defaultValue
	 *            the value to be returned if parsing the value of the specified
	 *            property name as a signed decimal long integer fails or there
	 *            is no value associated with the specified property name in
	 *            this <tt>ConfigurationService</tt>
	 * @return the value of the property with the specified name in this
	 *         <tt>ConfigurationService</tt> as a signed decimal long integer;
	 *         <tt>defaultValue</tt> if parsing the value of the specified
	 *         property name fails or no value is associated in this
	 *         <tt>ConfigurationService</tt> with the specified property name
	 */
	public long getLong(String propertyName, long defaultValue) {
		String stringValue = getString(propertyName);
		long longValue = defaultValue;

		if ((stringValue != null) && (stringValue.length() > 0)) {
			try {
				longValue = Long.parseLong(stringValue);
			} catch (NumberFormatException ex) {
				logger.error(propertyName + " does not appear to be a longinteger. " + "Defaulting to " + defaultValue + ".", ex);
			}
		}
		return longValue;
	}

}
