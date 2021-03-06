/******************************************************************************
 * Copyright (C) Devamatre Inc. 2009 - 2018. All rights reserved.
 * 
 * This code is licensed to Devamatre under one or more contributor license 
 * agreements. The reproduction, transmission or use of this code, in source 
 * and binary forms, with or without modification, are permitted provided 
 * that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Devamatre reserves the right to modify the technical specifications and or 
 * features without any prior notice.
 *****************************************************************************/
package com.rslakra.httpclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ServerSocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rslakra.core.CoreHelper;
import com.rslakra.core.IOHelper;
import com.rslakra.core.JSONHelper;
import com.rslakra.core.SecurityHelper;

/**
 * This class handles the HTTP(S) requests.
 * 
 * @author Rohtash Singh
 * @version 1.0.0
 * @since May 1, 2015 3:24:11 PM
 */
public final class HTTPHelper {
	
	/** SEPERATOR_HASH */
	public static final String SEPERATOR_HASH = "#".intern();
	/** COLON_DOUBLE_SLASH */
	public static final String COLON_DOUBLE_SLASH = "://".intern();
	/** EMPTY_STRING */
	public static final String EMPTY_STRING = "".intern();
	
	/* CUSTOM Constants. */
	public static final String METHOD_NAME = "methodName".intern();
	public static final String DEVICE_ID = "deviceId".intern();
	
	/**
	 * 
	 *
	 * @author Rohtash Singh Lakra
	 * @date 03/15/2017 01:58:44 PM
	 */
	interface Headers {
		String ACCEPT = "Accept".intern();
		String ACCEPT_ENCODING = "Accept-Encoding".intern();
		String ACCEPT_LANGUAGE = "Accept-Language".intern();
		String CONTENT_TYPE = "Content-Type".intern();
		String CONTENT_LENGTH = "Content-Length".intern();
		
		String EXPIRES = "Expires".intern();
		String PRAGMA = "Pragma".intern();
		String PRAGMA_PUBLIC = "public".intern();
		String CACHE_CONTROL = "Cache-Control".intern();
		String USER_AGENT = "User-Agent".intern();
		String CONTENT_DISPOSITION = "Content-Disposition".intern();
		
		String SET_COOKIE = "Set-Cookie".intern();
		String COOKIE = "Cookie".intern();
		String LOCATION = "Location".intern();
		String SERVER = "Server".intern();
		String TRANSFER_ENCODING = "Transfer-Encoding".intern();
	}
	
	interface Values {
		
		String SCHEMA_HTTP = "http".intern();
		String SCHEMA_HTTPS = "https".intern();
		String HTTP_VERSION = "HTTP/1.1".intern();
		int STATUS_CODE_OK = 200;
		
		int HTTP_CONNECTION_TIMEOUT_SECONDS = 45;
		int HTTP_READ_TIMEOUT_SECONDS = 45;
		
		// Accept
		String ACCEPT_ALL = "*/*".intern();
		
		// AcceptEncoding
		String ENCODING_GZIP_DEFLATE = "gzip, deflate".intern();
		
		// AcceptLanguage
		String EN_US = "en-us".intern();
		String EN_US_Q = "en-US,en;q=0.5".intern();
		
		// ContentDisposition
		String FILE_NAME_EQUAL = "fileName=".intern();
		
		// ContentType
		String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded".intern();
		String CONTENT_TYPE_FORM_URLENCODED_UTF8 = "application/x-www-form-urlencoded;charset=UTF-8".intern();
		String CONTENT_TYPE_APPLICATION_JAVA_X_SERIALIZED_OBJECT = "application/x-java-serialized-object".intern();
		String CONTENT_TYPE_MULTIPART_FORM_DATA = "multipart/form-data".intern();
		String CONTENY_TYPE_JSON = "application/json".intern();
		
		// Pragma
		String NO_CACHE = "no-cache".intern();
	}
	
	interface Methods {
		String HEAD = "HEAD".intern();
		String GET = "GET".intern();
		String POST = "POST".intern();
		String OPTIONS = "OPTIONS".intern();
		String PUT = "PUT".intern();
		String DELETE = "DELETE".intern();
		String TRACE = "TRACE".intern();
	}
	
	/* Enables cookies at application level. */
	static {
		initCookieManager();
	}
	
	/* USE_SSL_FACTORY */
	private static final boolean USE_SSL_FACTORY = true;
	
	/* USE_FULLY_QUALIFIED_HOSTNAME */
	private static final boolean USE_FULLY_QUALIFIED_HOSTNAME = false;
	
	/* KERNEL_VERSION */
	private static final String KERNEL_VERSION = System.getProperty("os.version");
	
	/* OS_VERSION */
	private static final String OS_VERSION = getOSVersion();
	
	/** mimeTypes */
	private static Map<String, String> mimeTypes;
	
	/* urlToDomainMap */
	private static final Map<String, String> urlToDomainMap = new HashMap<String, String>(3);
	
	/* headersIgnored */
	private static String[] headersIgnored;
	
	/* excludedHeaders */
	private static List<String> excludedHeaders;
	
	/* excludedParameters */
	private static List<String> excludedParameters;
	
	/* excludedMethods */
	private static List<String> excludedMethods;
	
	// deviceModel - used in user-agent
	private static String deviceModel;
	
	// Singleton object
	private HTTPHelper() {
		throw new UnsupportedOperationException("Object creation is not allowed!");
	}
	
	/**
	 * Initializes the cooky manager.
	 */
	public static void initCookieManager() {
		if (CookieHandler.getDefault() == null) {
			CookieHandler.setDefault(new CookieManager());
		}
	}
	
	/**
	 * Returns the value from of the selected index, if the map is null
	 * otherwise null.
	 * 
	 * @param map
	 * @param keyIndex
	 * @return
	 */
	public static Object getKeyValue(Map<?, ?> map, int keyIndex) {
		return map.keySet().toArray()[keyIndex];
	}
	
	/**
	 * Returns the value of the given key, if exists otherwise null.
	 * 
	 * @param mapObjects
	 * @param key
	 * @return
	 */
	public static Object getKeyValue(Map<String, Object> mapObjects, String key) {
		return ((mapObjects != null && key != null) ? mapObjects.get(key) : null);
	}
	
	/**
	 * Returns the new instance of the specified class type.
	 * 
	 * @param type
	 * @param className
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type, String className) throws Exception {
		Class<?> klass = Class.forName(className);
		return (T) (klass == null ? null : klass.newInstance());
	}
	
	/**
	 * Converts the list into the set.
	 * 
	 * @param args
	 * @return
	 */
	public static <T> Set<T> asSet(List<T> args) {
		Set<T> set = new HashSet<T>();
		if (!CoreHelper.isNullOrEmpty(args)) {
			set.addAll(args);
		}
		
		return set;
	}
	
	/**
	 * Returns true of the collection contains the specified value otherwise
	 * false.
	 * 
	 * @param collection
	 * @param value
	 * @return
	 */
	public static <T> boolean contains(Collection<T> collection, T value) {
		return ((!CoreHelper.isNullOrEmpty(collection)) && collection.contains(value));
	}
	
	/**
	 * Returns true of the <code>keyValues</code> map contains the specified
	 * <code>key</code> otherwise false.
	 * 
	 * @param keyValues
	 * @param key
	 * @return
	 */
	public static <K, V> boolean keyExists(Map<K, V> keyValues, K key) {
		return (!CoreHelper.isNullOrEmpty(keyValues) && keyValues.keySet().contains(key));
	}
	
	/**
	 * Converts the string to set after splitting the string with the specified
	 * delimiter.
	 * 
	 * @param value
	 * @param delimiter
	 * @return
	 */
	public static Set<String> asSet(String valueString, String delimiter) {
		Set<String> setStrings = new HashSet<String>();
		if (CoreHelper.isNotNullOrEmpty(valueString) && CoreHelper.isNotNullOrEmpty(delimiter)) {
			String[] tokens = valueString.split(delimiter);
			if (!CoreHelper.isNullOrEmpty(tokens)) {
				for (int i = 0; i < tokens.length; i++) {
					setStrings.add(tokens[i].trim());
				}
			}
		}
		
		return setStrings;
	}
	
	/**
	 * 
	 * @param arguments
	 * @return
	 */
	@SafeVarargs
	public static <T> Set<T> asSet(T... arguments) {
		Set<T> set = new HashSet<T>();
		if (arguments != null && arguments.length > 0) {
			for (T obj : arguments) {
				set.add(obj);
			}
		}
		
		return set;
	}
	
	/**
	 * Converts the <code>arguments</code> into the
	 * <code>java.util.ArrayList()</code> object.
	 * 
	 * @param arguments
	 * @return
	 */
	public static <T> List<T> asList(Collection<T> arguments) {
		return new ArrayList<T>(arguments);
	}
	
	/**
	 * Returns the <code>java.util.ArrayList()</code> object that can be used to
	 * perform <code>add()</code>, <code>remove()</code> operations on that
	 * array list. if you use the <code>java.util.Arrays#asList(T... a)</code>,
	 * you will not be able to perform those operations as the latter returns a
	 * fixed-size list backed by the specified array. If the <code>args</code>
	 * are null, it returns null.
	 * 
	 * For more details, see: <code>java.util.Arrays#asList(T... a)</code>.
	 * 
	 * @param args
	 * @return
	 * @see java.util.Arrays#asList(T... a)
	 */
	public static <T> List<T> asList(T... args) {
		List<T> list = null;
		if (args != null) {
			list = new ArrayList<T>();
			for (int i = 0; i < args.length; i++) {
				list.add(args[i]);
			}
		}
		
		return list;
	}
	
	/**
	 * Returns an array containing all elements contained in this {@code List}.
	 * If the specified array is large enough to hold the elements, the
	 * specified array is used, otherwise an array of the same type is created.
	 * If the specified array is used and is larger than this {@code List}, the
	 * array element following the collection elements is set to null.
	 * 
	 * @param array
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<T> list, Class<T> classType) {
		T[] listArray = null;
		if (!CoreHelper.isNullOrEmpty(list)) {
			listArray = (T[]) Array.newInstance(classType, list.size());
			listArray = list.toArray(listArray);
		}
		
		return listArray;
	}
	
	/**
	 * Returns the <code>java.util.ArrayList()</code> object which is of the
	 * specified type.
	 * 
	 * @param args
	 * @return
	 * @see java.util.Arrays#asList(T... a)
	 */
	public static List<Integer> asIntList(List<String> args) {
		List<Integer> list = null;
		if (args != null) {
			list = new ArrayList<Integer>();
			for (int i = 0; i < args.size(); i++) {
				list.add(Integer.parseInt(args.get(i)));
			}
		}
		
		return list;
	}
	
	/**
	 * Returns true if the <T> type is equals to any of the specified <T> types
	 * otherwise false.
	 * 
	 * @param type
	 * @param types
	 * @return
	 */
	public static <T> boolean equals(T type, T... types) {
		boolean result = false;
		if (CoreHelper.isNotNull(type) && !CoreHelper.isNullOrEmpty(types)) {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(type)) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns true if the <code>string</code> matches any of the specified
	 * <code>args</code> otherwise false.
	 * 
	 * @param ignoreCase
	 * @param string
	 * @param args
	 * @return
	 */
	public static boolean equalsAnyone(boolean ignoreCase, String string, String... args) {
		if (CoreHelper.isNotNullOrEmpty(string) && args != null) {
			for (int i = 0; i < args.length; i++) {
				if (ignoreCase) {
					if (string.equalsIgnoreCase(args[i])) {
						return true;
					}
				} else {
					if (string.equals(args[i])) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if the <code>string</code> matches any of the specified
	 * <code>args</code> otherwise false.
	 * 
	 * @param string
	 * @param args
	 * @return
	 */
	public static boolean equalsAnyone(String string, String... args) {
		return equalsAnyone(false, string, args);
	}
	
	/**
	 * 
	 * @return
	 */
	public static <T> List<T> getEmptyList() {
		return new ArrayList<T>();
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static <T extends Comparable<? super T>> List<T> sortByKeys(List<T> list) {
		List<T> linkedList = new LinkedList<T>(list);
		Collections.sort(linkedList);
		return linkedList;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static <K extends Comparable<K>, V extends Comparable<V>> SortedMap<K, V> sortByNaturalOrder(Map<K, V> map) {
		return new TreeMap<K, V>(map);
	}
	
	/**
	 * Converts the normal map to sorted map.
	 * 
	 * @param map
	 * @return
	 */
	public static SortedMap<String, Object> convertToSortedMap(Map<String, ? extends Object> map) {
		return new TreeMap<String, Object>(map);
	}
	
	/**
	 * Sorts the specified map by keys in natural order.
	 * 
	 * @param map
	 * @return
	 * @throws NullPointerException
	 *             - if map contains the null as key
	 */
	public static <K extends Comparable<K>, V extends Comparable<V>> Map<K, V> sortByKeys(Map<K, V> map) {
		List<K> keys = new LinkedList<K>(map.keySet());
		Collections.sort(keys);
		
		/*
		 * LinkedHashMap will keep the keys in the order they are inserted which
		 * is currently sorted on natural ordering
		 */
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		for (K key : keys) {
			sortedMap.put(key, map.get(key));
		}
		
		return sortedMap;
	}
	
	/**
	 * Sorts the specified map by values in natural order.
	 * 
	 * @param map
	 * @return
	 * @throws NullPointerException
	 *             - if map contains the null as key
	 */
	public static <K extends Comparable<K>, V extends Comparable<V>> Map<K, V> sortByValues(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			/**
			 * @param o1
			 * @param o2
			 * @return
			 */
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		/*
		 * LinkedHashMap will keep the keys in the order they are inserted which
		 * is currently sorted on natural ordering
		 */
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}
	
	/**
	 * Converts an object into string if the object is not null and instance of
	 * String otherwise empty string.
	 * 
	 * @param object
	 * @return
	 */
	public static String objectToString(Object object) {
		return (object != null ? object.toString() : "");
	}
	
	/**
	 * Returns true if the specified object is an instance of any of the
	 * specified classTypes otherwise false.
	 * 
	 * @param object
	 * @param classTypes
	 * @return
	 */
	public static boolean isInstanceOf(Object object, Class<?>... classTypes) {
		boolean instanceOf = false;
		if (CoreHelper.isNotNull(object) && !CoreHelper.isNullOrEmpty(classTypes)) {
			for (int i = 0; i < classTypes.length; i++) {
				if (classTypes[i].isInstance(object)) {
					instanceOf = true;
					break;
				}
			}
		}
		
		return instanceOf;
	}
	
	/**
	 * 
	 * @param object
	 * @param classType
	 * @return
	 */
	public static <T> boolean isNotNullAndInstanceOf(Object object, Class<T> classType) {
		return (object != null && classType.isInstance(object));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// ////////////////// Thread Utility Methods ///////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Causes the current thread which sent this message to sleep for the given
	 * interval of time (given in milliseconds). The precision is not guaranteed
	 * - the thread may sleep more or less than requested.
	 * 
	 * @param time
	 * @see Thread#sleep()
	 */
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ex) {
			/* ignore it. */
		}
	}
	
	/**
	 * Returns the current Thread ID.
	 * 
	 * @return
	 */
	public static long currentThreadId() {
		return Thread.currentThread().getId();
	}
	
	/**
	 * Returns the current thread signature (like name, id, priority and thread
	 * group etc.).
	 * 
	 * @return
	 */
	public static String currentThreadSignature() {
		Thread currentThread = Thread.currentThread();
		StringBuilder sBuilder = new StringBuilder("[");
		sBuilder.append(currentThread.getName());
		sBuilder.append(" <id:").append(currentThread.getId());
		sBuilder.append(", priority:").append(currentThread.getPriority());
		sBuilder.append(", threadGroupName:").append(currentThread.getThreadGroup().getName());
		sBuilder.append(">]");
		
		return sBuilder.toString();
	}
	
	/**
	 * Returns the class simple name.
	 * 
	 * @param klass
	 * @return
	 */
	public static String getClassSimpleName(Class<?> klass) {
		return (CoreHelper.isNull(klass) ? null : klass.getSimpleName());
	}
	
	/**
	 * Returns the class simple name for this object.
	 * 
	 * @param object
	 * @return
	 */
	public static String getClassSimpleName(Object object) {
		String classSimpleName = null;
		if (CoreHelper.isNotNull(object)) {
			Class<?> klass = object.getClass().getEnclosingClass();
			if (CoreHelper.isNull(klass)) {
				klass = object.getClass();
			}
			classSimpleName = getClassSimpleName(klass);
		}
		
		return classSimpleName;
	}
	
	/**
	 * Converts the normal map to sorted map.
	 * 
	 * @param map
	 * @return
	 */
	public static SortedMap<String, Object> toSortedMap(Map<String, ? extends Object> map) {
		return new TreeMap<String, Object>(map);
	}
	
	/**
	 * 
	 * @return
	 */
	public static <T> List<T> emptyList() {
		return new ArrayList<T>();
	}
	
	/**
	 * Returns an empty ArrayList if the specified list is null. Otherwise,
	 * returns the list itself.
	 */
	public static <T> List<T> makeEmptyIfNull(List<T> list) {
		return (list == null ? new ArrayList<T>() : list);
	}
	
	/**
	 * Returns true if the specified object is an instance of any of the
	 * specified classes.
	 */
	public static boolean instanceOfAny(Object object, Class<?>... classes) {
		boolean result = false;
		if (object != null) {
			for (Class<?> klass : classes) {
				if (klass != null && klass.isInstance(object)) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the deviceModel.
	 * 
	 * @return
	 */
	public static String getDeviceModel() {
		return deviceModel;
	}
	
	/**
	 * The deviceModel to be set.
	 * 
	 * @param deviceModel
	 */
	public static void setDeviceModel(String newDeviceModel) {
		deviceModel = newDeviceModel;
	}
	
	/**
	 * Returns the OS Version.
	 * 
	 * @return
	 */
	public static String getOSVersion() {
		return System.getProperty("os.version");
	}
	
	/**
	 * Returns the default 'User-Agent' value for these requests. This value is
	 * generated in the same way, used by IPad and Android devices. This is the
	 * mandatory property and must pass with each request.
	 * 
	 * NOTE: - Please don't make change in this user agent string. It is used to
	 * send the client requests to server (like iPad and Android).
	 * 
	 * @param appBundleIdentifier
	 * @param serverReleaseVersion
	 * @param appType
	 * @param deviceModel
	 * @return
	 */
	public static String getUserAgentString(String appBundleIdentifier, String appType, String deviceModel) {
		StringBuilder userAgentBuilder = new StringBuilder();
		
		/* These properties are mandatory for the server requests. */
		// prepare user-agent value
		if (CoreHelper.isNotNullOrEmpty(appBundleIdentifier)) {
			userAgentBuilder.append("OSType=").append(CoreHelper.getJVMName());
		}
		userAgentBuilder.append(";OSVer=").append(OS_VERSION);
		if (CoreHelper.isNotNullOrEmpty(appType)) {
			userAgentBuilder.append(";AppType=").append(appType);
		}
		if (CoreHelper.isNotNullOrEmpty(appBundleIdentifier)) {
			userAgentBuilder.append(";ABI=").append(appBundleIdentifier);
		}
		Locale localeDefault = Locale.getDefault();
		userAgentBuilder.append(";LCL=").append(localeDefault.toString());
		userAgentBuilder.append(";Lang=").append(localeDefault.getLanguage());
		userAgentBuilder.append(";DM=").append(deviceModel);
		userAgentBuilder.append(";AppType=").append(appType);
		userAgentBuilder.append(";KVer=").append(KERNEL_VERSION);
		
		return userAgentBuilder.toString();
	}
	
	/**
	 * Returns the default 'User-Agent' value for these requests. This value is
	 * generated in the same way, used by IPad and Android devices. This is the
	 * mandatory property and must pass with each request.
	 * 
	 * @return
	 */
	public static String getUserAgentString() {
		return getUserAgentString(null, null, getDeviceModel());
	}
	
	/**
	 * Returns the <code>URL</code> object for the specified
	 * <code>urlString</code> .
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static URL newURL(String urlString) throws IOException {
		return new URL(urlString);
	}
	
	/**
	 * Returns the <code>URL</code> object for the specified
	 * <code>baseUrl</code> and <code>urlSuffix</code>.
	 * 
	 * @param baseUrl
	 * @param urlSuffix
	 * @return
	 * @throws IOException
	 */
	public static URL newURL(String baseUrl, String urlSuffix) throws IOException {
		URL url = null;
		if (CoreHelper.isNullOrEmpty(urlSuffix)) {
			url = newURL(baseUrl);
		} else {
			url = new URL(newURL(baseUrl), urlSuffix);
		}
		
		return url;
	}
	
	/**
	 * Closes the socket.
	 * 
	 * @param socket
	 */
	public static void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ex) {
				System.err.println();
				System.err.println(ex);
			}
		}
	}
	
	/**
	 * Closes the socket.
	 * 
	 * @param socket
	 */
	public static void close(ServerSocket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public static boolean isPortAvailable(String host, int port) {
		System.out.println();
		System.out.println("+isPortAvailable(" + host + ", " + port + ")");
		Socket socket = null;
		boolean portAvailable = false;
		try {
			/*
			 * If there is no exception, it means something is using the port
			 * and has responded.
			 */
			socket = new Socket(host, port);
		} catch (Exception ex) {
			portAvailable = true;
			System.err.println(ex);
		} finally {
			close(socket);
		}
		
		System.out.println("-isPortAvailable(), portAvailable:" + portAvailable);
		return portAvailable;
	}
	
	/**
	 * Returns true if the specified port is not used otherwise false.
	 * 
	 * @param port
	 * @return
	 */
	public static boolean isPortAvailable(int port) {
		System.out.println("+isPortAvailable(" + port + ")");
		ServerSocket socket = null;
		boolean available = false;
		try {
			/*
			 * If there is no exception, it means something is using the port
			 * and has responded.
			 */
			// socket = new ServerSocket(port);
			socket = ServerSocketFactory.getDefault().createServerSocket(port);
			socket.setReuseAddress(true);
		} catch (Exception ex) {
			available = true;
			System.err.println(ex);
		} finally {
			close(socket);
		}
		
		System.out.println("-isPortAvailable(), available:" + available);
		return available;
	}
	
	/**
	 * Returns the server URL based on the values provided in the
	 * Config.properties file. If the urlSuffix is null or empty, the base URL
	 * of the server is returned.
	 * 
	 * @param baseServerUrl
	 * @param urlSuffix
	 * @return
	 */
	public static String getServerUrl(String baseServerUrl, String urlSuffix) {
		StringBuilder urlString = new StringBuilder();
		if (CoreHelper.isNotNullOrEmpty(baseServerUrl)) {
			urlString.append(baseServerUrl);
		}
		
		// append urlPrefix, if available.
		if (!CoreHelper.isNullOrEmpty(urlSuffix)) {
			if (urlSuffix.startsWith(IOHelper.SLASH)) {
				urlString.append(urlSuffix);
			} else {
				urlString.append(IOHelper.SLASH).append(urlSuffix);
			}
		}
		
		return urlString.toString();
	}
	
	/**
	 * Returns the host name extracted from the <code>urlString</code>.
	 * 
	 * @return
	 */
	public static String getHostName(String urlString) {
		// check in cache first
		String hostName = urlToDomainMap.get(urlString);
		if (CoreHelper.isNullOrEmpty(hostName)) {
			if (USE_FULLY_QUALIFIED_HOSTNAME) {
				hostName = getHostNameFromUrl(urlString);
			} else {
				hostName = getHostNameFromUrlWithoutSubdomain(urlString);
			}
			
			// put in domain cache
			urlToDomainMap.put(urlString, hostName);
		}
		
		return hostName;
	}
	
	/**
	 * Returns the MIME TYPE for the specified <code>extensionType</code>.
	 * 
	 * @param resourceExtension
	 * @return
	 */
	public static String getMimeType(String extensionType) {
		if (mimeTypes == null) {
			mimeTypes = new HashMap<String, String>();
			mimeTypes.put("css", "text/css");
			mimeTypes.put("eot", "application/vnd.ms-fontobject");
			mimeTypes.put("gif", "image/gif");
			mimeTypes.put("html", "text/html");
			mimeTypes.put("htm", "text/html");
			mimeTypes.put("ico", "image/ico");
			mimeTypes.put("jpeg", "image/jpeg");
			mimeTypes.put("jpg", "image/jpeg");
			mimeTypes.put("js", "application/javascript");
			mimeTypes.put("json", "application/json");
			mimeTypes.put("m4a", "audio/mp4a-latm");
			mimeTypes.put("pdf", "application/pdf");
			mimeTypes.put("png", "image/png");
			mimeTypes.put("svg", "image/svg+xml");
			mimeTypes.put("ttf", "font/opentype");
			mimeTypes.put("woff", "font/woff");
			mimeTypes.put("woff2", "font/woff2");
		}
		
		return mimeTypes.get(extensionType);
	}
	
	/**
	 * Returns true if the connection to the server is available and returns
	 * some results otherwise false.
	 *
	 * @param urlString
	 * @param urlSuffix
	 * @return
	 */
	public static boolean isServerReachable(String baseServerUrl, String urlSuffix) {
		System.out.println("+isServerReachable(" + baseServerUrl + ", " + urlSuffix + ")");
		boolean serverReachable = false;
		String urlString = getServerUrl(baseServerUrl, urlSuffix);
		// OperationResponse operationResponse =
		// HTTPUtil.executeRequest(urlString, Methods.GET, null, true);
		// serverReachable = (Status.SUCCESS == operationResponse.getStatus());
		
		HttpURLConnection urlConnection = null;
		try {
			System.out.println("Checking server reachability for urlString:" + urlString);
			urlConnection = openHttpURLConnection(urlString, null);
			setConnectTimeoutProperties(urlConnection);
			serverReachable = (urlConnection != null && (urlConnection.getResponseCode() == 200 || urlConnection.getContent() != null));
		} catch (Exception ex) {
			serverReachable = false;
		} finally {
			close(urlConnection);
		}
		
		System.out.println("-isServerReachable(), serverReachable:" + serverReachable);
		return serverReachable;
	}
	
	/**
	 * Logs the <code>HttpURLConnection</code> object.
	 * 
	 * @param urlConnection
	 * @throws IOException
	 */
	public static void logURLConnection(HttpURLConnection urlConnection) throws IOException {
		IOHelper.debug("+logURLConnection(" + urlConnection + ")");
		
		/* extract request parameters, if available. */
		if (urlConnection != null) {
			IOHelper.debug("Request Method:" + urlConnection.getRequestMethod());
			// debug("Headers:" + urlConnection.getHeaderFields());
			Map<String, List<String>> requestHeader = urlConnection.getHeaderFields();
			for (String key : requestHeader.keySet()) {
				List<String> listValue = requestHeader.get(key);
				IOHelper.debug("Key:" + key + ", Value:" + listValue);
			}
			
		}
		
		IOHelper.debug("-logURLConnection()");
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// //////////////////////// HttpServlet Methods ////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the methodName extracted from the request parameters.
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestMethodName(HttpServletRequest servletRequest) {
		String requestMethodName = null;
		if (CoreHelper.isNotNull(servletRequest)) {
			String[] paramValue = (String[]) servletRequest.getParameterMap().get(METHOD_NAME);
			if (!CoreHelper.isNullOrEmpty(paramValue)) {
				requestMethodName = paramValue[0].toString();
			}
		}
		
		return requestMethodName;
	}
	
	/**
	 * Returns the request headers as the <code>Map<String, Object></code>
	 * object after sorts based on the name.
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getRequestHeaders(HttpServletRequest servletRequest) {
		Map<String, String> requestHeaders = new TreeMap<String, String>();
		if (CoreHelper.isNotNull(servletRequest)) {
			try {
				/* extract request headers, if available. */
				@SuppressWarnings("unchecked")
				Enumeration<String> headerNames = servletRequest.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					String headerValue = servletRequest.getHeader(headerName);
					IOHelper.debug("headerName:" + headerName + ", headerValue:" + headerValue);
					requestHeaders.put(headerName, headerValue);
				}
			} catch (Exception ex) {
				IOHelper.error(ex);
			}
		}
		
		return requestHeaders;
	}
	
	/**
	 * Sets the default headers to the specified response.
	 * 
	 * @param servletResponse
	 */
	public static void setDefaultHeaders(HttpServletResponse servletResponse) {
		servletResponse.setDateHeader(Headers.EXPIRES, -1);
		servletResponse.setHeader(Headers.PRAGMA, Headers.PRAGMA_PUBLIC);
		servletResponse.setHeader(Headers.CACHE_CONTROL, Values.NO_CACHE);
	}
	
	/**
	 * Prints the servletRequest.
	 * 
	 * @param servletRequest
	 * @throws IOException
	 */
	public static void logServletRequest(HttpServletRequest servletRequest) throws IOException {
		IOHelper.debug("+logServletRequest(" + servletRequest + ")");
		/* extract request parameters, if available. */
		if (servletRequest != null) {
			for (Object key : servletRequest.getParameterMap().keySet()) {
				String value = servletRequest.getParameter(key.toString());
				System.out.println("key:" + key.toString() + ", value:" + value);
			}
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// ///////////// HttpURLConnection/HttpsURLConnection Methods //////////////
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the <code>Proxy</code> object for the specified
	 * <code>proxyHost</code> and <code>proxyPort</code>.
	 * 
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 * @throws IOException
	 */
	public static Proxy getProxy(String proxyHost, int proxyPort) throws IOException {
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	}
	
	/**
	 * Returns the <code>HttpURLConnection</code> object for the specified
	 * <code>url</code>. if the <code>proxy</code> is available the
	 * <code>proxy</code> is used for the request.
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection openHttpURLConnection(URL url, Proxy proxy) throws IOException {
		return (CoreHelper.isNotNull(url) ? (HttpURLConnection) (CoreHelper.isNotNull(proxy) ? url.openConnection(proxy) : url.openConnection()) : null);
	}
	
	/**
	 * Returns the <code>HttpURLConnection</code> object for the specified
	 * <code>url</code>.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection openHttpURLConnection(URL url) throws IOException {
		return (CoreHelper.isNotNull(url) ? (HttpURLConnection) url.openConnection() : null);
	}
	
	/**
	 * Returns the <code>HttpURLConnection</code> object for the specified
	 * <code>urlString</code>.
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection openHttpURLConnection(String urlString) throws IOException {
		return openHttpURLConnection(newURL(urlString));
	}
	
	/**
	 * Returns the <code>HttpURLConnection</code> object for the specified
	 * <code>baseUrl</code> and <code>urlSuffix</code>.
	 * 
	 * @param baseUrl
	 * @param urlSuffix
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection openHttpURLConnection(String baseUrl, String urlSuffix) throws IOException {
		return openHttpURLConnection(newURL(baseUrl, urlSuffix));
	}
	
	/**
	 * Returns the <code>HttpsURLConnection</code> object for the specified
	 * <code>url</code>. if the <code>proxy</code> is available the
	 * <code>proxy</code> is used for the request.
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException
	 */
	public static HttpsURLConnection openHttpsURLConnection(URL url, Proxy proxy) throws IOException {
		return (CoreHelper.isNotNull(url) && url.getProtocol().equals("https") ? (HttpsURLConnection) (CoreHelper.isNotNull(proxy) ? url.openConnection(proxy) : url.openConnection()) : null);
	}
	
	/**
	 * Returns the <code>HttpsURLConnection</code> object for the specified
	 * <code>url</code>.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpsURLConnection openHttpsURLConnection(URL url) throws IOException {
		return openHttpsURLConnection(url, null);
	}
	
	/**
	 * Returns the <code>HttpsURLConnection</code> object for the specified
	 * <code>urlString</code>.
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static HttpsURLConnection openHttpsURLConnection(String urlString) throws IOException {
		return openHttpsURLConnection(newURL(urlString));
	}
	
	/**
	 * Returns the <code>HttpsURLConnection</code> object for the specified
	 * <code>baseUrl</code> and <code>urlSuffix</code>.
	 * 
	 * @param baseUrl
	 * @param urlSuffix
	 * @return
	 * @throws IOException
	 */
	public static HttpsURLConnection openHttpsURLConnection(String baseUrl, String urlSuffix) throws IOException {
		return openHttpsURLConnection(newURL(baseUrl, urlSuffix));
	}
	
	/**
	 * Closes the <code>HttpURLConnection</code> connection.
	 * 
	 * @param urlConnection
	 */
	public static void close(HttpURLConnection urlConnection) {
		if (CoreHelper.isNotNull(urlConnection)) {
			try {
				// Crucial, according to the documentation.
				urlConnection.disconnect();
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}
	}
	
	/**
	 * Sets the default properties of the specified <code>urlConnection</code>
	 * object.
	 * 
	 * @param urlConnection
	 * @throws IOException
	 */
	public static void setConnectTimeoutProperties(HttpURLConnection urlConnection) throws IOException {
		if (CoreHelper.isNotNull(urlConnection)) {
			urlConnection.setConnectTimeout(Values.HTTP_CONNECTION_TIMEOUT_SECONDS * 1000);
			urlConnection.setReadTimeout(Values.HTTP_READ_TIMEOUT_SECONDS * 1000);
		}
	}
	
	/**
	 * Sets the default properties of the given <code>HttpURLConnection</code>
	 * object.
	 * 
	 * @param urlConnection
	 * @param requestMethod
	 * @param doInputOutput
	 * @param useCaches
	 * @throws IOException
	 */
	public static void setConnectionDefaultProperties(final HttpURLConnection urlConnection, final String requestMethod) throws IOException {
		if (CoreHelper.isNotNull(urlConnection)) {
			// set connection timeout properties.
			setConnectTimeoutProperties(urlConnection);
			
			/*
			 * Sets the flag indicating whether this URLConnection allows input.
			 * It cannot be set after the connection is established.
			 */
			urlConnection.setDoInput(true);
			
			// request method (i.e GET/POST/PUT etc)
			if (CoreHelper.isNotNullOrEmpty(requestMethod)) {
				urlConnection.setRequestMethod(requestMethod);
				urlConnection.setDoOutput(Methods.POST.equalsIgnoreCase(requestMethod));
			} else {
				urlConnection.setRequestMethod(Methods.POST);
				urlConnection.setDoOutput(true);
			}
			
			// add device-id in request.
			urlConnection.addRequestProperty(DEVICE_ID, SecurityHelper.uniqueDeviceIdString());
			// user-agent (default string generated for Android)
			urlConnection.addRequestProperty(Headers.USER_AGENT, getUserAgentString());
			// other default properties
			urlConnection.setRequestProperty(Headers.ACCEPT_LANGUAGE, Locale.getDefault().toString());
		}
	}
	
	/**
	 * Sets the default properties of the given <code>HttpURLConnection</code>
	 * object.
	 * 
	 * @param urlConnection
	 * @param doInputOutput
	 * @throws IOException
	 */
	public static void setConnectionInputAndOutput(final HttpURLConnection urlConnection) throws IOException {
		if (CoreHelper.isNotNull(urlConnection)) {
			/*
			 * Sets the flag indicating whether this URLConnection allows input.
			 * It cannot be set after the connection is established.
			 */
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
		}
	}
	
	/**
	 * Sets the <code>useCaches</code> and <code>defaultUseCaches</code>
	 * properties of the given <code>HttpURLConnection</code> object.
	 * 
	 * @param urlConnection
	 * @throws IOException
	 */
	public static void setConnectionUseCaches(final HttpURLConnection urlConnection) throws IOException {
		if (CoreHelper.isNotNull(urlConnection)) {
			/*
			 * Sets the flag indicating whether this connection allows to use
			 * caches or not. This method can only be called prior to the
			 * connection establishment.
			 */
			urlConnection.setUseCaches(false);
			urlConnection.setDefaultUseCaches(false);
		}
	}
	
	/**
	 * Returns the <code>mapCookies</code> as string.
	 * 
	 * @param mapCookies
	 */
	public static String toCookyString(Map<String, String> mapCookies) {
		String cookies = null;
		if (!CoreHelper.isNullOrEmpty(mapCookies)) {
			/** Add Cookies. */
			StringBuilder cookyBuilder = new StringBuilder();
			for (String key : mapCookies.keySet()) {
				String value = mapCookies.get(key);
				if (CoreHelper.isNotNullOrEmpty(value)) {
					if (equals(Headers.COOKIE, key)) {
						cookyBuilder.append(value).append(";");
					} else {
						// value = SecurityHelper.encodeWithURLEncoder(value,
						// UTF_8);
						cookyBuilder.append(key).append("=").append(value).append(";");
					}
				}
			}
			
			if (!CoreHelper.isNullOrEmpty(cookyBuilder)) {
				cookies = cookyBuilder.toString();
				cookyBuilder = null;
			}
		}
		
		return cookies;
	}
	
	/**
	 * Returns the <code>Cooky</code> string.
	 * 
	 * @param mapCookies
	 * @return
	 */
	public static Map<String, String> mergeCookies(final Map<String, String> mapCookies) {
		Map<String, String> mergedCookies = new HashMap<String, String>();
		if (!CoreHelper.isNullOrEmpty(mapCookies)) {
			/** Merge Cookies. */
			for (String key : mapCookies.keySet()) {
				String value = mapCookies.get(key);
				if (CoreHelper.isNotNullOrEmpty(value)) {
					if (equals(Headers.COOKIE, key)) {
						Map<String, String> oldCookies = extractCookies(value);
						if (!CoreHelper.isNullOrEmpty(oldCookies)) {
							mergedCookies.putAll(oldCookies);
						}
					} else {
						mergedCookies.put(key, value);
					}
				}
			}
		}
		
		return mergedCookies;
	}
	
	/**
	 * The <code>cookies</code> to be set to the specified
	 * <code>urlConnection</code>.
	 * 
	 * @param urlConnection
	 * @param cookies
	 */
	public static void setRequestCookies(HttpURLConnection urlConnection, String cookies) {
		if (CoreHelper.isNotNull(urlConnection) && CoreHelper.isNotNullOrEmpty(cookies)) {
			IOHelper.debug(Headers.COOKIE + ":" + cookies);
			urlConnection.setRequestProperty(Headers.COOKIE, cookies);
		}
	}
	
	/**
	 * The <code>mapCookies</code> to be set to the specified
	 * <code>urlConnection</code>.
	 * 
	 * @param urlConnection
	 * @param mapCookies
	 */
	public static void setRequestCookies(HttpURLConnection urlConnection, Map<String, String> mapCookies) {
		setRequestCookies(urlConnection, toCookyString(mapCookies));
	}
	
	/**
	 * Converts the <code>requestParameters</code> into
	 * <code>urlQueryString</code>.
	 * 
	 * @param requestParameters
	 * @return
	 */
	public static String toUrlQueryString(Map<String, Object> requestParameters) {
		String urlQueryString = null;
		if (!CoreHelper.isNullOrEmpty(requestParameters)) {
			StringBuilder aueryString = new StringBuilder();
			boolean firstParam = true;
			for (String key : requestParameters.keySet()) {
				if (firstParam) {
					firstParam = false;
				} else {
					aueryString.append("&");
				}
				
				String value = String.valueOf(requestParameters.get(key));
				value = SecurityHelper.encodeWithURLEncoder(value, IOHelper.UTF_8);
				aueryString.append(key).append("=").append(value);
			}
			
			urlQueryString = aueryString.toString();
			/* mark available for garbage-collection. */
			aueryString = null;
		}
		
		return urlQueryString;
	}
	
	/**
	 * Returns true if the request is GET otherwise false.
	 * 
	 * @param urlConnection
	 * @return
	 */
	public static boolean isGetRequest(HttpURLConnection urlConnection) {
		return (Methods.GET.equalsIgnoreCase(urlConnection.getRequestMethod()));
	}
	
	/**
	 * Adds the specified <code>queryString</code> to the specified
	 * <code>urlConnection</code>.
	 * 
	 * @param urlConnection
	 * @param requestParameters
	 */
	public static void setQueryString(HttpURLConnection urlConnection, final String queryString) throws IOException {
		System.out.println("setQueryString(" + urlConnection + ", " + queryString + ")");
		if (CoreHelper.isNotNull(urlConnection) && CoreHelper.isNotNullOrEmpty(queryString)) {
			IOHelper.writeBytes(queryString.getBytes(), urlConnection.getOutputStream(), true);
		}
	}
	
	/**
	 * Adds the specified <code>requestParameters</code> to the specified
	 * <code>urlConnection</code>.
	 * 
	 * @param urlConnection
	 * @param requestParameters
	 */
	public static void setRequestParameters(HttpURLConnection urlConnection, final Map<String, Object> requestParameters) throws IOException {
		setQueryString(urlConnection, toUrlQueryString(requestParameters));
	}
	
	/**
	 * Converts the <code>encodedParameters</code> string into
	 * <code>Map<String, String></code>.
	 * 
	 * @param encodedParameters
	 * @return
	 */
	public static Map<String, Object> toRequestParameters(String encodedParameters) {
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		if (CoreHelper.isNotNullOrEmpty(encodedParameters)) {
			String decodedParameters = SecurityHelper.decodeWithURLDecoder(encodedParameters);
			String[] paramTokens = decodedParameters.split("&");
			if (!CoreHelper.isNullOrEmpty(paramTokens)) {
				for (int i = 0; i < paramTokens.length; i++) {
					String[] tokens = paramTokens[i].split("=");
					if (!CoreHelper.isNullOrEmpty(tokens) && tokens.length > 1) {
						requestParameters.put(tokens[0], tokens[1]);
					}
				}
			}
		}
		
		return requestParameters;
	}
	
	/**
	 * The <code>requestHeaders</code> to be set to the specified
	 * <code>urlConnection</code>.
	 * 
	 * @param urlConnection
	 * @param requestHeaders
	 */
	public static void setRequestHeaders(HttpURLConnection urlConnection, Map<String, String> requestHeaders) {
		if (CoreHelper.isNotNull(urlConnection) && !CoreHelper.isNullOrEmpty(requestHeaders)) {
			for (String headerKey : requestHeaders.keySet()) {
				String headerValue = requestHeaders.get(headerKey);
				System.out.println("headerKey:" + headerKey + ", headerValue:" + headerValue);
				if (CoreHelper.isNotNullOrEmpty(headerValue)) {
					if (equals(Headers.COOKIE, headerKey)) {
						String currentValue = urlConnection.getHeaderField(Headers.COOKIE);
						if (CoreHelper.isNotNullOrEmpty(currentValue)) {
							headerValue += ";" + currentValue;
						}
					}
					urlConnection.setRequestProperty(headerKey, headerValue);
				}
			}
		}
	}
	
	/**
	 * Executes the <code>httpMethod</code> request of the specified
	 * <code>urlString</code> with <code>requestHeaders</code> and
	 * <code>requestParameters</code> using <code>HttpURLConnection</code>. The
	 * <code>OperationResponse</code> object is returned as response which
	 * contains the data/error, if any, including response headers information.
	 * 
	 * @param urlString
	 * @param httpMethod
	 * @param requestHeaders
	 * @param requestParameters
	 * @param closeStream
	 * @return
	 */
	public static HttpResponse executeRequest(final String urlString, final String httpMethod, final Map<String, String> requestHeaders, final Map<String, Object> requestParameters, final boolean closeStream) {
		System.out.println("+executeRequest(" + urlString + ", " + httpMethod + ", " + requestHeaders + ", " + requestParameters + ", " + closeStream + ")");
		
		long startTime = System.currentTimeMillis();
		HttpResponse operationResponse = new HttpResponse();
		HttpURLConnection urlConnection = null;
		
		try {
			if (CoreHelper.isNullOrEmpty(urlString)) {
				throw new IllegalArgumentException("Server URL must provide!");
			}
			
			// open connection and set default header values.
			if (Methods.GET.equalsIgnoreCase(httpMethod)) {
				StringBuilder requestBuilder = new StringBuilder(urlString);
				String queryString = toUrlQueryString(requestParameters);
				if (CoreHelper.isNotNullOrEmpty(queryString)) {
					requestBuilder.append("?").append(queryString);
				}
				urlConnection = openHttpURLConnection(requestBuilder.toString());
			} else {
				urlConnection = openHttpURLConnection(urlString);
			}
			
			setConnectionInputAndOutput(urlConnection);
			setConnectionUseCaches(urlConnection);
			setConnectionDefaultProperties(urlConnection, httpMethod);
			
			// // add default cookies, if any
			// String urlCookies = CookieManager.getDefault().get(urlString,
			// null);
			// if(isNotNullOrEmpty(urlCookies)) {
			// if(requestHeaders.containsKey(Headers.COOKIE)) {
			// String requestCookies = requestHeaders.get(Headers.COOKIE);
			// if(isNotNullOrEmpty(requestCookies)) {
			// urlCookies += ";" + requestCookies;
			// }
			// } else {
			// requestHeaders.put(Headers.COOKIE, urlCookies);
			// }
			// }
			
			// add request header for the request.
			setRequestHeaders(urlConnection, requestHeaders);
			
			// encode parameters, if any
			setRequestParameters(urlConnection, requestParameters);
			
			// Connect and get the response.
			operationResponse.setResponseCode(urlConnection.getResponseCode());
			operationResponse.setResponseHeaders(urlConnection.getHeaderFields());
			if (operationResponse.getResponseCode() == 200) {
				byte[] dataBytes = IOHelper.readBytes(urlConnection.getInputStream(), closeStream);
				operationResponse.setDataBytes(dataBytes);
				// Final cleanup:
				dataBytes = null;
			}
		} catch (Throwable throwable) {
			System.err.println(throwable);
			operationResponse.setError(throwable);
		} finally {
			close(urlConnection);
			long diff = System.currentTimeMillis() - startTime;
			System.out.println("-executeRequest(), CALL took " + diff + " ms.");
		}
		
		return operationResponse;
	}
	
	/**
	 * Executes the <code>httpMethod</code> request of the specified
	 * <code>urlString</code> with <code>requestParameters</code> using
	 * <code>HttpURLConnection</code>. The <code>OperationResponse</code> object
	 * is returned as response which contains the data/error, if any, including
	 * response headers information.
	 * 
	 * @param urlString
	 * @param httpMethod
	 * @param requestParameters
	 * @param closeStream
	 * @return
	 */
	public static HttpResponse executeRequest(final String urlString, final String httpMethod, final Map<String, Object> requestParameters, final boolean closeStream) {
		return executeRequest(urlString, httpMethod, null, requestParameters, closeStream);
	}
	
	/**
	 * Executes the GET request of the specified <code>urlString</code> with
	 * <code>requestHeaders</code> and <code>requestParameters</code> using
	 * <code>HttpURLConnection</code>. The <code>OperationResponse</code> object
	 * is returned as response which contains the data/error, if any, including
	 * response headers information.
	 * 
	 * @param urlString
	 * @param requestHeaders
	 * @param requestParameters
	 * @param closeStream
	 * @return
	 */
	public static HttpResponse executeGetRequest(final String urlString, final Map<String, String> requestHeaders, final Map<String, Object> requestParameters, final boolean closeStream) {
		return executeRequest(urlString, Methods.GET, requestHeaders, requestParameters, closeStream);
	}
	
	/**
	 * Executes the GET request of the specified <code>urlString</code> with
	 * <code>requestHeaders</code> and <code>requestParameters</code> using
	 * <code>HttpURLConnection</code>. The <code>OperationResponse</code> object
	 * is returned as response which contains the data/error, if any, including
	 * response headers information.
	 * 
	 * @param urlString
	 * @param requestHeaders
	 * @param requestParameters
	 * @param closeStream
	 * @return
	 */
	public static HttpResponse executeGetRequest(final String urlString, final Map<String, Object> requestParameters, final boolean closeStream) {
		return executeGetRequest(urlString, null, requestParameters, closeStream);
	}
	
	/**
	 * Executes the POST request of the specified <code>urlString</code> with
	 * <code>requestHeaders</code> and <code>requestParameters</code> using
	 * <code>HttpURLConnection</code>. The <code>OperationResponse</code> object
	 * is returned as response which contains the data/error, if any, including
	 * response headers information.
	 * 
	 * @param urlString
	 * @param requestHeaders
	 * @param requestParameters
	 * @param closeStream
	 * @return
	 */
	public static HttpResponse executePostRequest(final String urlString, final Map<String, String> requestHeaders, final Map<String, Object> requestParameters, final boolean closeStream) {
		return executeRequest(urlString, Methods.POST, requestHeaders, requestParameters, closeStream);
	}
	
	/**
	 * Executes the POST request of the specified <code>urlString</code> with
	 * <code>requestHeaders</code> and <code>requestParameters</code> using
	 * <code>HttpURLConnection</code>. The <code>OperationResponse</code> object
	 * is returned as response which contains the data/error, if any, including
	 * response headers information.
	 * 
	 * @param urlString
	 * @param requestHeaders
	 * @param requestParameters
	 * @param closeStream
	 * @return
	 */
	public static HttpResponse executePostRequest(final String urlString, final Map<String, Object> requestParameters, final boolean closeStream) {
		return executePostRequest(urlString, null, requestParameters, closeStream);
	}
	
	/**
	 * Returns the value of the specified key from the headers. It might be this
	 * method throw ClassCastException, if the return value is different than
	 * string.
	 * 
	 * @param headers
	 * @param key
	 * @return
	 */
	public static String getHeader(Map<String, List<String>> headers, String key) {
		// System.out.println("+getHeader(" + headers + ", " + key +
		// ")");
		String value = null;
		if (!CoreHelper.isNullOrEmpty(headers)) {
			List<String> headerValues = headers.get(key);
			// System.out.println("headerValues:" + headerValues);
			if (!CoreHelper.isNullOrEmpty(headerValues)) {
				value = headerValues.get(0);
			}
		}
		
		System.out.println("-getHeader(" + key + "), value:" + value);
		return value;
	}
	
	/**
	 * Returns the headers of the specified operationResult.
	 * 
	 * @param operationResult
	 * @return
	 */
	public static Map<String, List<String>> getHeaders(HttpResponse operationResponse) {
		return (CoreHelper.isNull(operationResponse) ? null : operationResponse.getResponseHeaders());
	}
	
	/**
	 * Returns the response type.
	 * 
	 * @param headers
	 * @return
	 */
	public static String getMimeType(Map<String, List<String>> headers) {
		String mimeType = null;
		if (!CoreHelper.isNullOrEmpty(headers)) {
			mimeType = headers.get(Headers.CONTENT_TYPE).get(0);
			if (mimeType.indexOf(";") != -1) {
				mimeType = mimeType.substring(0, mimeType.indexOf(";")).trim();
			}
		}
		
		return mimeType;
	}
	
	/**
	 * Returns the key/value pair of extracted cookies from the
	 * <code>stringCookies</code> headers.
	 * 
	 * @param stringCookies
	 * @return
	 */
	public static Map<String, String> extractCookies(String stringCookies) {
		Map<String, String> mapCookies = null;
		System.out.println("+extractCookies(" + stringCookies + ")");
		if (CoreHelper.isNotNullOrEmpty(stringCookies)) {
			mapCookies = new HashMap<String, String>();
			String[] cookies = stringCookies.split(";");
			for (String cookie : cookies) {
				Pairs<String, String> pairs = Pairs.newPair(cookie);
				if (pairs != null) {
					mapCookies.put(pairs.getKey(), pairs.getValue());
				}
			}
		}
		
		System.out.println("-extractCookies(), mapCookies:" + mapCookies);
		return mapCookies;
	}
	
	/**
	 * Returns the cookies extracted from the <code>responseHeaders</code>
	 * headers.
	 * 
	 * @param responseHeaders
	 * @return
	 */
	public static Map<String, String> extractCookies(Map<String, List<String>> responseHeaders) {
		Map<String, String> mapCookies = null;
		System.out.println("+extractCookies(" + responseHeaders + ")");
		if (!CoreHelper.isNullOrEmpty(responseHeaders)) {
			List<String> allCookies = responseHeaders.get(Headers.SET_COOKIE);
			System.out.println("allCookies:" + allCookies);
			if (!CoreHelper.isNullOrEmpty(allCookies)) {
				mapCookies = new HashMap<String, String>();
				for (String stringCookie : allCookies) {
					Map<String, String> extractedCookies = extractCookies(stringCookie);
					if (!CoreHelper.isNullOrEmpty(extractedCookies)) {
						mapCookies.putAll(extractedCookies);
					}
				}
			}
		}
		
		System.out.println("-extractCookies(), mapCookies:" + mapCookies);
		return mapCookies;
	}
	
	/**
	 * Returns the value of the specified key from the headers. It might be this
	 * method throw ClassCastException, if the return value is different than
	 * string.
	 * 
	 * @param operationResult
	 * @param key
	 * @return
	 */
	public static String getHeader(HttpResponse operationResponse, String key) {
		return getHeader(getHeaders(operationResponse), key);
	}
	
	/**
	 * Returns the value of the specified key from the headers as boolean.
	 * 
	 * @param operationResult
	 * @param key
	 * @return
	 */
	public static boolean getHeaderAsBoolean(HttpResponse operationResponse, String key) {
		return Boolean.parseBoolean(getHeader(operationResponse, key));
	}
	
	/**
	 * Return the value of the key from the specified mapKeyValues. If no value
	 * is found, the default is returned.
	 * 
	 * @param mapKeyValues
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Object getValueForKey(Map<String, Object> mapKeyValues, String key, Object defaultValue) {
		// System.out.println("+getValueForKey(" + mapKeyValues + ",
		// " + key +
		// ", " + defaultValue + ")");
		Object value = null;
		if (!CoreHelper.isNullOrEmpty(mapKeyValues) && !CoreHelper.isNullOrEmpty(key)) {
			value = mapKeyValues.get(key);
		}
		
		// return default value if the value is null or empty.
		if (CoreHelper.isNull(value)) {
			value = defaultValue;
		}
		
		// System.out.println("-getValueForKey(), value:" + value);
		return value;
	}
	
	/**
	 * Return the value of the key from the specified mapKeyValues.
	 * 
	 * @param mapKeyValues
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValueForKey(Map<String, Object> mapKeyValues, String key) {
		return (T) getValueForKey(mapKeyValues, key, null);
	}
	
	/**
	 * Return the value of the key from the specified keyValuesMap.
	 * 
	 * @param mapKeyValues
	 * @param key
	 * @return
	 */
	public static String getValueForKeyAsString(Map<String, Object> mapKeyValues, String key) {
		return (String) getValueForKey(mapKeyValues, key, null);
	}
	
	/**
	 * Return the value of the key from the specified keyValuesMap as boolean.
	 * 
	 * @param keyValuesMap
	 * @param key
	 * @return
	 */
	public static boolean getValueForKeyAsBoolean(Map<String, Object> mapKeyValues, String key) {
		return Boolean.parseBoolean(getValueForKeyAsString(mapKeyValues, key));
	}
	
	/**
	 * Return the value of the key from the specified keyValuesMap as boolean.
	 * 
	 * @param keyValuesMap
	 * @param key
	 * @return
	 */
	public static int getValueForKeyAsInteger(Map<String, Object> mapKeyValues, String key) {
		return Integer.parseInt(getValueForKeyAsString(mapKeyValues, key));
	}
	
	/**
	 * Return the value of the key from the specified keyValuesMap as boolean.
	 * 
	 * @param keyValuesMap
	 * @param key
	 * @return
	 */
	public static long getValueForKeyAsLong(Map<String, Object> mapKeyValues, String key) {
		return Long.parseLong(getValueForKeyAsString(mapKeyValues, key));
	}
	
	/**
	 * Return the value of the key from the specified keyValuesMap as boolean.
	 * 
	 * @param keyValuesMap
	 * @param key
	 * @return
	 */
	public static List<?> getValueForKeyAsList(Map<String, Object> mapKeyValues, String key) {
		return ((List<?>) getValueForKey(mapKeyValues, key));
	}
	
	/**
	 * Converts the header value <code>List<String></code> bvHttpRequest string.
	 * 
	 * @param headers
	 * @return
	 */
	public static Map<String, String> headerValuesAsString(Map<String, List<String>> headers) {
		Map<String, String> mapHeaders = new HashMap<String, String>();
		if (!CoreHelper.isNullOrEmpty(headers)) {
			Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
			for (Map.Entry<String, List<String>> entry : entries) {
				mapHeaders.put(entry.getKey(), entry.getValue().get(0));
			}
		}
		
		return mapHeaders;
	}
	
	/**
	 * 
	 * @param contentDisposition
	 * @return
	 */
	public static String getContentDispositionFileNameValue(String contentDisposition) {
		System.out.println("+getContentDispositionFileNameValue(" + contentDisposition + ")");
		
		String valueFileName = null;
		if (!CoreHelper.isNullOrEmpty(contentDisposition)) {
			int fileNameIndex = contentDisposition.indexOf(Values.FILE_NAME_EQUAL);
			if (fileNameIndex > -1 && fileNameIndex < contentDisposition.length() - 1) {
				valueFileName = contentDisposition.substring(fileNameIndex + Values.FILE_NAME_EQUAL.length());
			}
		}
		
		System.out.println("-getContentDispositionFileNameValue(), valueFileName:" + valueFileName);
		return valueFileName;
	}
	
	/**
	 * Returns the list of excluded headers.
	 * 
	 * @return
	 */
	public static List<String> getExcludedHeaders() {
		if (CoreHelper.isNull(excludedHeaders)) {
			excludedHeaders = new ArrayList<String>();
			/*
			 * excluded the following headers from the request/response headers.
			 */
			excludedHeaders.add("Host");
			excludedHeaders.add("Accept");
			excludedHeaders.add("Origin");
			excludedHeaders.add("X-Requested-With");
			excludedHeaders.add("User-Agent");
			excludedHeaders.add("Content-Length");
			excludedHeaders.add("Referer");
			excludedHeaders.add("Accept-Encoding");
			excludedHeaders.add("Accept-Language");
			excludedHeaders.add("Cookie");
		}
		
		return excludedHeaders;
	}
	
	/**
	 * Returns the list of ignored header.
	 * 
	 * @return
	 */
	public static String[] getHeadersIgnored() {
		if (CoreHelper.isNull(headersIgnored)) {
			headersIgnored = toArray(getExcludedHeaders(), String.class);
		}
		
		return headersIgnored;
	}
	
	/**
	 * Returns the list of excluded parameter keys.
	 * 
	 * @return
	 */
	public static List<String> getExcludedParameters() {
		if (CoreHelper.isNull(excludedParameters)) {
			excludedParameters = new ArrayList<String>();
			/* remove the following parameters before generating hashCode. */
		}
		
		return excludedMethods;
	}
	
	/**
	 * Returns true if the given paramName is part of an excludedParameters set
	 * otherwise false.
	 * 
	 * @param paramName
	 * @param excludedParameters
	 * @return
	 */
	public static boolean isExcludedParameter(String paramName) {
		boolean excludedParameter = false;
		if (CoreHelper.isNotNullOrEmpty(paramName) && !CoreHelper.isNullOrEmpty(getExcludedParameters())) {
			for (int i = 0; i < excludedParameters.size(); i++) {
				if (excludedParameters.contains(paramName)) {
					excludedParameter = true;
					break;
				}
			}
		}
		
		return excludedParameter;
	}
	
	/**
	 * Removes the parameters which are excluded from the request hash code.
	 * 
	 * @param sortedParameters
	 */
	public static void removeExcludedParameters(SortedMap<String, Object> sortedParameters) {
		if (!CoreHelper.isNullOrEmpty(sortedParameters) && !CoreHelper.isNullOrEmpty(getExcludedParameters())) {
			for (int i = 0; i < excludedParameters.size(); i++) {
				sortedParameters.remove(excludedParameters.get(i));
			}
		}
	}
	
	/**
	 * Returns the hash string generated using the specified parameters.
	 * 
	 * @param requestParameters
	 * @return
	 */
	public static String paramValuesAsHashString(SortedMap<String, Object> requestParameters) {
		String valuesAsHashString = null;
		if (!CoreHelper.isNullOrEmpty(requestParameters)) {
			SortedMap<String, Object> sortedParameters = toSortedMap(requestParameters);
			/* remove existing custom parameters, if available */
			removeExcludedParameters(sortedParameters);
			
			String paramValuesAsString = paramValuesAsString(sortedParameters);
			valuesAsHashString = SecurityHelper.paramValueAsHashString(paramValuesAsString);
		}
		
		return valuesAsHashString;
	}
	
	/**
	 * Returns the hash string generated using the specified parameters.
	 * 
	 * @param requestParameters
	 * @return
	 */
	public static String paramValuesAsHashString(Map<String, ? extends Object> requestParameters) {
		return paramValuesAsHashString(toSortedMap(requestParameters));
	}
	
	/**
	 * Returns true if the specified requestMethodName is an excludedMethods set
	 * otherwise false.
	 * 
	 * @param requestMethodName
	 * @param excludedMethods
	 * @return
	 */
	public static boolean isExcludedMethodRequest(String requestMethodName, String... excludedMethods) {
		boolean excludedMethodRequest = false;
		if (!CoreHelper.isNullOrEmpty(requestMethodName) && !CoreHelper.isNullOrEmpty(excludedMethods)) {
			for (int i = 0; i < excludedMethods.length; i++) {
				if (excludedMethods[i].equalsIgnoreCase(requestMethodName)) {
					excludedMethodRequest = true;
					break;
				}
			}
		}
		
		return excludedMethodRequest;
	}
	
	/**
	 * Returns the list of black listed methods.
	 * 
	 * @return
	 */
	public static List<String> getExcludedMethods() {
		if (CoreHelper.isNull(excludedMethods)) {
			excludedMethods = new ArrayList<String>();
			/* add more methods if required. */
		}
		
		return excludedMethods;
	}
	
	/**
	 * Filters the request parameters.
	 * 
	 * @param requestParameters
	 * @param excludedMethodRequest
	 */
	public static void filterRequestParameters(SortedMap<String, Object> requestParameters, boolean excludedMethodRequest) {
		if (excludedMethodRequest && !CoreHelper.isNullOrEmpty(requestParameters)) {
			SortedMap<String, Object> filteredParameters = new TreeMap<String, Object>(requestParameters);
			for (String key : filteredParameters.keySet()) {
				if (("rand".equals(key) || "_".equals(key))) {
					System.out.println("Filtered parameter, key:" + key + ", value:" + filteredParameters.get(key));
					requestParameters.remove(key);
				}
			}
		}
	}
	
	/**
	 * Returns the request parameters as the <code>List<NameValuePair></code>
	 * object after sorts based on the name.
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static SortedMap<String, Object> getRequestParameters(HttpServletRequest servletRequest) throws IOException {
		SortedMap<String, Object> requestParameters = new TreeMap<String, Object>();
		if (servletRequest != null) {
			/* extract request parameters, if available. */
			for (Object key : servletRequest.getParameterMap().keySet()) {
				String value = servletRequest.getParameter(key.toString());
				// System.out.println("Adding parameters, key:" +
				// key.toString()
				// + ", value:" + value);
				requestParameters.put(key.toString(), value);
			}
		}
		
		return requestParameters;
	}
	
	/**
	 * Returns the string generated using the specified parameters (only
	 * parameter values excluding keys).
	 * 
	 * @param requestParameters
	 * @return
	 */
	public static String paramValuesAsString(String... paramValues) {
		String valuesAsString = null;
		if (!CoreHelper.isNullOrEmpty(paramValues)) {
			StringBuilder hashBuffer = new StringBuilder();
			Arrays.sort(paramValues);
			for (int i = 0; i < paramValues.length; i++) {
				String paramValue = CoreHelper.isNullOrEmpty(paramValues[i]) ? "" : paramValues[i];
				hashBuffer.append(paramValue);
			}
			
			valuesAsString = hashBuffer.toString();
			hashBuffer = null;
		}
		
		System.out.println("paramValuesAsString(), valuesAsString:" + valuesAsString);
		return valuesAsString;
	}
	
	/**
	 * Returns the string generated using the specified parameters (only
	 * parameter values excluding keys).
	 * 
	 * @param requestParameters
	 * @return
	 */
	public static String paramValuesAsString(List<String> paramValues) {
		String valuesAsString = null;
		if (!CoreHelper.isNullOrEmpty(paramValues)) {
			StringBuilder hashBuffer = new StringBuilder();
			Collections.sort(paramValues);
			for (String paramValue : paramValues) {
				paramValue = CoreHelper.isNullOrEmpty(paramValue) ? "" : paramValue;
				hashBuffer.append(paramValue);
			}
			
			valuesAsString = hashBuffer.toString();
			hashBuffer = null;
		}
		
		System.out.println("paramValuesAsString(), valuesAsString:" + valuesAsString);
		return valuesAsString;
	}
	
	/**
	 * Returns the string generated using the specified parameters (only
	 * parameter values excluding keys).
	 * 
	 * @param requestParameters
	 * @return
	 */
	public static String paramValuesAsString(SortedMap<String, ? extends Object> requestParameters) {
		String valuesAsString = null;
		if (!CoreHelper.isNullOrEmpty(requestParameters)) {
			StringBuilder hashBuffer = new StringBuilder();
			/* the iteration should be name in the same order each time. */
			for (String key : requestParameters.keySet()) {
				Object value = requestParameters.get(key);
				String valueAsString = CoreHelper.isNull(value) ? "" : value.toString();
				hashBuffer.append(valueAsString);
			}
			
			valuesAsString = hashBuffer.toString();
			hashBuffer = null;
		}
		
		System.out.println("paramValuesAsString(), valuesAsString:" + valuesAsString);
		return valuesAsString;
	}
	
	/**
	 * Adds the given values into the hashBuffer.
	 * 
	 * @param hashBuffer
	 * @param values
	 */
	public static void addToHashBuffer(final StringBuilder hashBuffer, String... values) {
		if (CoreHelper.isNotNull(hashBuffer) && !CoreHelper.isNullOrEmpty(values)) {
			for (int i = 0; i < values.length; i++) {
				if (!CoreHelper.isNullOrEmpty(values[i])) {
					hashBuffer.append(values[i]);
				}
			}
		}
	}
	
	/**
	 * Converts the responseHeaders from <code>Map<String, List<String>></code>
	 * to <code>Map<String, String></code>.
	 * 
	 * @param responseHeaders
	 * @return
	 */
	public static Map<String, String> toResponseHeaders(Map<String, List<String>> responseHeaders) {
		Map<String, String> toResponseHeaders = new HashMap<String, String>();
		for (String key : responseHeaders.keySet()) {
			List<String> listValue = responseHeaders.get(key);
			if (listValue.size() > 1 && toResponseHeaders.containsKey(key)) {
				StringBuilder valueBuilder = new StringBuilder();
				for (int i = 0; i < listValue.size(); i++) {
					valueBuilder.append(listValue.get(i));
					if (i < listValue.size() - 1) {
						valueBuilder.append(";");
					}
				}
			} else {
				toResponseHeaders.put(key, listValue.get(0));
			}
		}
		
		return toResponseHeaders;
	}
	
	/**
	 * Returns the host name extracted from the specified urlString.
	 * 
	 * @param urlString
	 * @return
	 */
	public static String getHostNameFromUrl(String urlString) {
		String hostName = urlString;
		if (!CoreHelper.isNullOrEmpty(urlString)) {
			int startIndex = urlString.indexOf("://");
			if (startIndex >= 0) {
				startIndex += "://".length();
				int endIndex = urlString.lastIndexOf(":");
				if (endIndex > -1 && endIndex < startIndex) {
					int slashIndex = urlString.lastIndexOf(IOHelper.SLASH);
					if (slashIndex != -1) {
						hostName = urlString.substring(startIndex, slashIndex);
					} else {
						hostName = urlString.substring(startIndex);
					}
				} else {
					hostName = urlString.substring(startIndex, endIndex);
				}
			}
		}
		
		return hostName;
	}
	
	/**
	 * Returns true if the string contains only digits. The $ avoids a partial
	 * match, i.e. 1b.
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isDigits(String string) {
		return (CoreHelper.isNotNullOrEmpty(string) && string.matches("^[0-9]*$"));
	}
	
	/**
	 * Returns the host name extracted from the urlString.
	 * 
	 * @param urlString
	 * @return
	 */
	public static String getHostNameFromUrlWithoutSubdomain(String urlString) {
		String hostName = null;
		try {
			hostName = new URL(urlString).getHost();
			String ipString = hostName.replace(".", EMPTY_STRING);
			// check if just numbers
			if (!isDigits(ipString)) {
				int dotIndex = hostName.lastIndexOf(".");
				if (dotIndex != -1) {
					String hostDomainOnly = hostName.substring(0, dotIndex);
					int lastDotIndex = hostDomainOnly.lastIndexOf(".");
					if (lastDotIndex != -1) {
						hostName = hostName.substring(lastDotIndex + 1, hostDomainOnly.length()) + hostName.substring(dotIndex);
					}
				}
			}
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
		
		return hostName;
	}
	
	/**
	 * Returns true if the value starts with any of the specified prefixes
	 * otherwise false.
	 * 
	 * @param value
	 * @param prefixes
	 * @return
	 */
	public static boolean startsWith(String value, String... prefixes) {
		boolean result = false;
		if (!CoreHelper.isNullOrEmpty(value) && prefixes != null) {
			for (String prefix : prefixes) {
				if (value.startsWith(prefix)) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the stack trace as string.
	 * 
	 * @param error
	 * @return
	 */
	public static String toString(Throwable error) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			PrintWriter writer = new PrintWriter(outputStream);
			error.printStackTrace(writer);
			writer.flush();
			writer.close();
			outputStream.close();
		} catch (Exception ex) {
			System.err.println(ex);
		}
		
		return new String(outputStream.toByteArray());
	}
	
	/**
	 * Returns true if the <code>string</code> contains any of the specified
	 * <code>args</code> otherwise false.
	 * 
	 * @param ignoreCase
	 * @param string
	 * @param args
	 * @return
	 */
	public static boolean containsAnyone(boolean ignoreCase, String string, String... args) {
		if (CoreHelper.isNotNullOrEmpty(string) && args != null) {
			for (int i = 0; i < args.length; i++) {
				if (ignoreCase) {
					Locale defaultLocale = Locale.getDefault();
					if (string.toLowerCase(defaultLocale).contains(args[i].toLowerCase(defaultLocale))) {
						return true;
					}
				} else {
					if (string.contains(args[i])) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if the <code>string</code> contains any of the specified
	 * <code>args</code> otherwise false.
	 * 
	 * @param string
	 * @param args
	 * @return
	 */
	public static boolean containsAnyone(String string, String... args) {
		return containsAnyone(false, string, args);
	}
	
	/**
	 * Added to handle the key/value pair.
	 *
	 * @author Rohtash Singh Lakra
	 * @date 04/19/2017 11:12:00 AM
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static class Pairs<K, V> implements Serializable {
		
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;
		
		private final K key;
		private final V value;
		
		/**
		 * Extracts the key and value from the specified
		 * <code>keyValueString</code> based on the equals to (=) sign, if its
		 * not null or empty otherwise null.
		 * 
		 * @param keyValueString
		 * @return
		 */
		public static Pairs<String, String> newPair(String keyValueString) {
			Pairs<String, String> pairs = null;
			if (CoreHelper.isNotNullOrEmpty(keyValueString)) {
				int equalIndex = keyValueString.indexOf("=");
				if (equalIndex != -1) {
					String key = keyValueString.substring(0, equalIndex).trim();
					int lastIndex = keyValueString.indexOf(";");
					String value = null;
					if (lastIndex != -1) {
						value = keyValueString.substring(equalIndex + 1, lastIndex).trim();
					} else {
						value = keyValueString.substring(equalIndex + 1).trim();
					}
					pairs = new Pairs<String, String>(key, value);
				}
			}
			
			return pairs;
		}
		
		/**
		 * 
		 * @param key
		 * @param value
		 * @param hash
		 * @param nextEntry
		 */
		public Pairs(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		/**
		 * Returns the key.
		 * 
		 * @return
		 */
		public final K getKey() {
			return key;
		}
		
		/**
		 * Return the value.
		 * 
		 * @return
		 */
		public final V getValue() {
			return value;
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public final boolean equals(Object object) {
			if (!(object instanceof Pairs)) {
				return false;
			}
			Pairs<?, ?> pairs = (Pairs<?, ?>) object;
			return (getKey().equals(pairs.getKey()) && getValue().equals(pairs.getValue()));
		}
		
		/**
		 * Returns the hash code of this object.
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public final int hashCode() {
			return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
		}
		
		/**
		 * Returns the string representation of this object.
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public final String toString() {
			return (getKey() + "=" + getValue());
		}
	}
	
	/**
	 * Create an HostnameVerifier that hardwires the expected hostname. Note
	 * that is different than the URL's hostname: example.com versus example.org
	 * 
	 * @author Rohtash Singh Lakra
	 * @date 04/17/2017 12:40:31 PM
	 *
	 */
	public static final class AllHostVerifier implements HostnameVerifier {
		
		public AllHostVerifier() {
		}
		
		/*
		 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
		 * javax.net.ssl.SSLSession)
		 */
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
	
	/**
	 * A custom X509TrustManager implementation that trusts a specified server
	 * certificate in addition to those that are in the system TrustStore. Also
	 * handles an out-of-order certificate chain, as is often produced by
	 * Apache's mod_ssl
	 *
	 * @author Rohtash Singh Lakra
	 * @date 04/17/2017 05:57:55 PM
	 * @see http://chariotsolutions.com/blog/post/https-with-client-certificates-on/
	 *      https://github.com/rfreedman/android-ssl
	 */
	public static final class CustomTrustManager implements X509TrustManager {
		
		/* originalX509TrustManager */
		private final X509TrustManager originalX509TrustManager;
		
		/* trustStore */
		private final KeyStore trustStore;
		
		/**
		 * @param trustStore
		 *            A KeyStore containing the server certificate that should
		 *            be trusted
		 * @throws NoSuchAlgorithmException
		 * @throws KeyStoreException
		 */
		public CustomTrustManager(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
			this.trustStore = trustStore;
			
			TrustManagerFactory originalTrustManagerFactory = TrustManagerFactory.getInstance("X509");
			originalTrustManagerFactory.init((KeyStore) null);
			
			TrustManager[] originalTrustManagers = originalTrustManagerFactory.getTrustManagers();
			originalX509TrustManager = (X509TrustManager) originalTrustManagers[0];
		}
		
		/**
		 * No-op. Never invoked by client, only used in server-side
		 * implementations
		 * 
		 * @return
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
		
		/**
		 * No-op. Never invoked by client, only used in server-side
		 * implementations
		 * 
		 * @return
		 */
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
		}
		
		/**
		 * Given the partial or complete certificate chain provided by the peer,
		 * build a certificate path to a trusted root and return if it can be
		 * validated and is trusted for client SSL authentication based on the
		 * authentication type. The authentication type is determined by the
		 * actual certificate used. For instance, if RSAPublicKey is used, the
		 * authType should be "RSA". Checking is case-sensitive. Defers to the
		 * default trust manager first, checks the cert supplied in the ctor if
		 * that fails.
		 * 
		 * @param chain
		 *            the server's certificate chain
		 * @param authType
		 *            the authentication type based on the client certificate
		 * @throws java.security.cert.CertificateException
		 */
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
			try {
				originalX509TrustManager.checkServerTrusted(chain, authType);
			} catch (CertificateException originalException) {
				try {
					X509Certificate[] reorderedChain = reorderCertificateChain(chain);
					CertPathValidator validator = CertPathValidator.getInstance("PKIX");
					CertificateFactory factory = CertificateFactory.getInstance("X509");
					CertPath certPath = factory.generateCertPath(Arrays.asList(reorderedChain));
					PKIXParameters params = new PKIXParameters(trustStore);
					params.setRevocationEnabled(false);
					validator.validate(certPath, params);
				} catch (Exception ex) {
					throw originalException;
				}
			}
			
		}
		
		/**
		 * Puts the certificate chain in the proper order, to deal with
		 * out-of-order certificate chains as are sometimes produced by Apache's
		 * mod_ssl
		 * 
		 * @param chain
		 *            the certificate chain, possibly with bad ordering
		 * @return the re-ordered certificate chain
		 */
		private X509Certificate[] reorderCertificateChain(X509Certificate[] chain) {
			X509Certificate[] reorderedChain = new X509Certificate[chain.length];
			List<X509Certificate> certificates = Arrays.asList(chain);
			
			int position = chain.length - 1;
			X509Certificate rootCert = findRootCert(certificates);
			reorderedChain[position] = rootCert;
			
			X509Certificate cert = rootCert;
			while ((cert = findSignedCert(cert, certificates)) != null && position > 0) {
				reorderedChain[--position] = cert;
			}
			
			return reorderedChain;
		}
		
		/**
		 * A helper method for certificate re-ordering. Finds the root
		 * certificate in a possibly out-of-order certificate chain.
		 * 
		 * @param certificates
		 *            the certificate change, possibly out-of-order
		 * @return the root certificate, if any, that was found in the list of
		 *         certificates
		 */
		private X509Certificate findRootCert(List<X509Certificate> certificates) {
			X509Certificate rootCert = null;
			
			for (X509Certificate cert : certificates) {
				X509Certificate signer = findSigner(cert, certificates);
				// no signer present, or self-signed
				if (signer == null || signer.equals(cert)) {
					rootCert = cert;
					break;
				}
			}
			
			return rootCert;
		}
		
		/**
		 * A helper method for certificate re-ordering. Finds the first
		 * certificate in the list of certificates that is signed by the
		 * sigingCert.
		 */
		private X509Certificate findSignedCert(X509Certificate signingCert, List<X509Certificate> certificates) {
			X509Certificate signed = null;
			
			for (X509Certificate cert : certificates) {
				Principal signingCertSubjectDN = signingCert.getSubjectDN();
				Principal certIssuerDN = cert.getIssuerDN();
				if (certIssuerDN.equals(signingCertSubjectDN) && !cert.equals(signingCert)) {
					signed = cert;
					break;
				}
			}
			
			return signed;
		}
		
		/**
		 * A helper method for certificate re-ordering. Finds the certificate in
		 * the list of certificates that signed the signedCert.
		 */
		private X509Certificate findSigner(X509Certificate signedCert, List<X509Certificate> certificates) {
			X509Certificate signer = null;
			
			for (X509Certificate cert : certificates) {
				Principal certSubjectDN = cert.getSubjectDN();
				Principal issuerDN = signedCert.getIssuerDN();
				if (certSubjectDN.equals(issuerDN)) {
					signer = cert;
					break;
				}
			}
			
			return signer;
		}
	}
	
	/**
	 * Creates the generic SSL factory.
	 *
	 * @author Rohtash Singh Lakra
	 * @date 04/12/2017 05:17:15 PM
	 */
	public static final class SSLFactory {
		
		/* PKCS12 */
		public static final String PKCS12 = "PKCS12";
		
		/* JKS */
		public static final String JKS = "JKS";
		
		/* TLS_VER_1 */
		public static final String TLS_VER_1 = "TLSv1";
		
		/* TLS_VER_2 */
		public static final String TLS_VER_2 = "TLSv2";
		
		/* SUN_X509 */
		public static final String SUN_X509 = "SunX509";
		
		/* instance */
		private static SSLFactory instance;
		
		/* trustAllSSLSocketFactory */
		private SSLSocketFactory trustAllSSLSocketFactory;
		
		/* hostNameVerifier */
		private HostnameVerifier hostNameVerifier;
		
		/**
		 * 
		 */
		private SSLFactory() {
		}
		
		/**
		 * Returns the SSL Context.
		 * 
		 * @param tlsVersion
		 * @param keyManagers
		 * @param trustManagers
		 * @param secureRandom
		 * @return
		 * @throws NoSuchAlgorithmException
		 * @throws KeyManagementException
		 */
		public SSLContext getSSLContext(String tlsVersion, KeyManager[] keyManagers, TrustManager[] trustManagers, SecureRandom secureRandom) throws NoSuchAlgorithmException, KeyManagementException {
			System.out.println("+getSSLContext(" + tlsVersion + ", " + keyManagers + ", " + trustManagers + ", " + secureRandom + "):");
			
			SSLContext sslContext = SSLContext.getInstance(tlsVersion);
			sslContext.init(keyManagers, trustManagers, secureRandom);
			
			System.out.println("-getSSLContext(), sslContext:" + sslContext);
			return sslContext;
		}
		
		/**
		 * Returns the SSL Context.
		 * 
		 * @param tlsVersion
		 * @param keyManagers
		 * @param trustManagers
		 * @return
		 * @throws NoSuchAlgorithmException
		 * @throws KeyManagementException
		 */
		public SSLContext getSSLContext(String tlsVersion, KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
			return getSSLContext(tlsVersion, null, trustManagers, null);
		}
		
		/**
		 * Returns the SSL Context.
		 * 
		 * @param tlsVersion
		 * @param trustManagers
		 * @param secureRandom
		 * @return
		 * @throws NoSuchAlgorithmException
		 * @throws KeyManagementException
		 */
		public SSLContext getSSLContext(String tlsVersion, TrustManager[] trustManagers, SecureRandom secureRandom) throws NoSuchAlgorithmException, KeyManagementException {
			return getSSLContext(tlsVersion, null, trustManagers, secureRandom);
		}
		
		/**
		 * Returns the SSL Factory.
		 * 
		 * @return
		 */
		public static SSLFactory getInstance() {
			if (instance == null) {
				synchronized (SSLFactory.class) {
					if (instance == null) {
						instance = new SSLFactory();
					}
				}
			}
			
			return instance;
		}
		
		/**
		 * Creates the SSL Socket Factory.
		 * 
		 * @return
		 * @throws Exception
		 */
		private SSLSocketFactory createTrustAllSSLSocketFactory() throws Exception {
			TrustManager[] trustEveryone = new TrustManager[] { new X509TrustManager() {
				/*
				 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
				 */
				public X509Certificate[] getAcceptedIssuers() {
					// return new X509Certificate[0];
					return new java.security.cert.X509Certificate[] {};
				}
				
				/*
				 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.
				 * security. cert.X509Certificate[], java.lang.String)
				 */
				public void checkClientTrusted(X509Certificate[] chain, String authType) {
				}
				
				/*
				 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.
				 * security. cert.X509Certificate[], java.lang.String)
				 */
				public void checkServerTrusted(X509Certificate[] chain, String authType) {
				}
			} };
			
			// Create an SSLContext that uses our TrustManager
			SSLContext sslContext = getSSLContext("TLS", trustEveryone, SecurityHelper.newSecureRandom());
			
			// use a SocketFactory from our SSLContext
			return sslContext.getSocketFactory();
		}
		
		/**
		 * Returns the SSL Socket Factory.
		 * 
		 * @return
		 */
		public SSLSocketFactory getTrustAllSSLSocketFactory() {
			if (trustAllSSLSocketFactory == null) {
				try {
					trustAllSSLSocketFactory = createTrustAllSSLSocketFactory();
				} catch (Exception ex) {
					System.err.println(ex);
				}
			}
			
			return trustAllSSLSocketFactory;
		}
		
		/**
		 * Returns the trustManagerFactory;
		 * 
		 * @param trustKeyStore
		 * @return
		 * @throws NoSuchAlgorithmException
		 * @throws KeyStoreException
		 */
		public TrustManager[] getTrustManagers(final KeyStore trustKeyStore) throws NoSuchAlgorithmException, KeyStoreException {
			System.out.println("+getTrustManagers(" + trustKeyStore + ")");
			TrustManager[] trustManagers = null;
			
			// Create a TrustManager that trusts the CAs in our KeyStore
			String trustFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			System.out.println("trustFactoryAlgorithm:" + trustFactoryAlgorithm);
			TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(trustFactoryAlgorithm);
			trustFactory.init(trustKeyStore);
			trustManagers = trustFactory.getTrustManagers();
			
			System.out.println("-createTrustManagerFactory(), trustManagers:" + trustManagers);
			return trustManagers;
		}
		
		/**
		 * Creates the SSL Socket Factory.
		 * 
		 * @param certInputStream
		 * @param secureRandom
		 * @return
		 * @throws Exception
		 */
		private SSLSocketFactory createTrustSSLSocketFactory(InputStream certInputStream, SecureRandom secureRandom) throws Exception {
			X509Certificate certificate = SecurityHelper.newX509Certificate(certInputStream, true);
			
			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			System.out.println("keyStoreType:" + keyStoreType);
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			String alias = certificate.getSubjectX500Principal().getName();
			keyStore.setCertificateEntry(alias, certificate);
			
			// Create a TrustManager that trusts the CAs in our KeyStore
			final TrustManager[] trustManagers = getTrustManagers(keyStore);
			final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
			
			final TrustManager[] wrappedTrustManagers = new TrustManager[] { new X509TrustManager() {
				/*
				 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
				 */
				public X509Certificate[] getAcceptedIssuers() {
					return trustManager.getAcceptedIssuers();
				}
				
				/*
				 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.
				 * security. cert.X509Certificate[], java.lang.String)
				 */
				public void checkClientTrusted(X509Certificate[] chain, String authType) {
					try {
						trustManager.checkClientTrusted(chain, authType);
					} catch (CertificateException ex) {
						System.err.println(ex);
					}
				}
				
				/*
				 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.
				 * security. cert.X509Certificate[], java.lang.String)
				 */
				public void checkServerTrusted(X509Certificate[] chain, String authType) {
					try {
						trustManager.checkServerTrusted(chain, authType);
					} catch (CertificateException ex) {
						System.err.println(ex);
					}
				}
			} };
			
			// Create an SSLContext that uses our TrustManager
			SSLContext sslContext = getSSLContext(TLS_VER_1, wrappedTrustManagers, secureRandom);
			
			// use a SocketFactory from our SSLContext
			return sslContext.getSocketFactory();
		}
		
		/**
		 * Returns the HostnameVerifier.
		 * 
		 * @param hostName
		 * @return
		 */
		public HostnameVerifier getHostNameVerifier() {
			if (hostNameVerifier == null) {
				hostNameVerifier = new AllHostVerifier();
			}
			
			return hostNameVerifier;
		}
		
		/**
		 * The SSL socket factor to be set.
		 * 
		 * @param urlConnection
		 */
		public void setSSLSocketFactory(HttpURLConnection urlConnection) {
			if (urlConnection instanceof HttpsURLConnection) {
				SSLSocketFactory sslSocketFactory = null;
				sslSocketFactory = getTrustAllSSLSocketFactory();
				((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
				((HttpsURLConnection) urlConnection).setHostnameVerifier(getHostNameVerifier());
			}
		}
		
		/**
		 * Produces a KeyStore from a PKCS12 (.p12) certificate file, typically
		 * the client certificate
		 * 
		 * @param p12CertInputStream
		 * @param p12CertPass
		 * @param closeStream
		 * @return
		 * @throws GeneralSecurityException
		 * @throws IOException
		 */
		public KeyStore loadPKCS12KeyStore(InputStream p12CertInputStream, char[] p12CertPass, boolean closeStream) throws GeneralSecurityException, IOException {
			KeyStore keyStore = KeyStore.getInstance(PKCS12);
			keyStore.load(p12CertInputStream, p12CertPass);
			if (closeStream) {
				IOHelper.safeClose(p12CertInputStream);
			}
			
			return keyStore;
		}
		
		/**
		 * Produces a KeyStore from a PKCS12 (.p12) certificate file, typically
		 * the client certificate
		 * 
		 * @param p12CertFileName
		 * @param p12CertPassword
		 * @return
		 * @throws GeneralSecurityException
		 * @throws IOException
		 */
		public KeyStore loadPKCS12KeyStore(String p12CertFileName, String p12CertPassword) throws GeneralSecurityException, IOException {
			return loadPKCS12KeyStore(IOHelper.toInputStream(IOHelper.readBytes(p12CertFileName)), p12CertPassword.toCharArray(), false);
		}
		
		/**
		 * Reads and decodes a base-64 encoded DER certificate (a .pem
		 * certificate), typically the server's CA certificate.
		 * 
		 * @param pemCertificateStream
		 * @return
		 * @throws IOException
		 */
		public byte[] loadPEMCertificate(InputStream pemCertificateStream) throws IOException {
			byte[] pemDecodedBytes = null;
			BufferedReader bufferedReader = null;
			try {
				final StringBuilder pemBuilder = new StringBuilder();
				bufferedReader = new BufferedReader(new InputStreamReader(pemCertificateStream));
				String line = bufferedReader.readLine();
				while (line != null) {
					if (!line.startsWith("--")) {
						pemBuilder.append(line);
					}
					line = bufferedReader.readLine();
				}
				
				pemDecodedBytes = Base64.getDecoder().decode(pemBuilder.toString());
			} finally {
				IOHelper.safeClose(bufferedReader);
			}
			
			return pemDecodedBytes;
		}
		
		/**
		 * Creates an SSLContext with the client and server certificates
		 * 
		 * @param p12CertFileName
		 *            A File containing the client certificate
		 * @param p12CertPassword
		 *            Password for the client certificate
		 * @param caCertString
		 *            A String containing the server certificate
		 * @return An initialized SSLContext
		 * @throws Exception
		 */
		private SSLContext createSSLContext(String p12CertFileName, String p12CertPassword, String caCertString) throws Exception {
			final KeyStore keyStore = loadPKCS12KeyStore(p12CertFileName, p12CertPassword);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
			kmf.init(keyStore, p12CertPassword.toCharArray());
			KeyManager[] keyManagers = kmf.getKeyManagers();
			
			final KeyStore trustStore = loadPEMTrustStore(caCertString);
			TrustManager[] trustManagers = { new CustomTrustManager(trustStore) };
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);
			
			return sslContext;
		}
		
		/**
		 * Produces a KeyStore from a String containing a PEM certificate
		 * (typically, the server's CA certificate)
		 * 
		 * @param certificateString
		 *            A String containing the PEM-encoded certificate
		 * @return a KeyStore (to be used as a trust store) that contains the
		 *         certificate
		 * @throws Exception
		 */
		private KeyStore loadPEMTrustStore(String certificateString) throws Exception {
			byte[] pemDecodedBytes = loadPEMCertificate(new ByteArrayInputStream(certificateString.getBytes()));
			ByteArrayInputStream derInputStream = new ByteArrayInputStream(pemDecodedBytes);
			
			X509Certificate x509Certificate = SecurityHelper.newX509Certificate(derInputStream, true);
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null);
			String alias = x509Certificate.getSubjectX500Principal().getName();
			trustStore.setCertificateEntry(alias, x509Certificate);
			
			return trustStore;
		}
		
	}
	
	/**
	 * Contains all possible results from a network operation.
	 *
	 * @author Rohtash Singh Lakra
	 * @date 05/26/2017 03:22:28 PM
	 */
	public static final class HttpResponse implements Cloneable {
		
		/** The response headers */
		private Map<String, List<String>> requestHeaders;
		
		/* responseCode */
		private int responseCode;
		
		/** responseHeaders */
		private Map<String, List<String>> responseHeaders;
		private String jsonResponseHeaders;
		
		/* dataBytes */
		private byte[] dataBytes;
		
		/* error */
		private Throwable error;
		
		/**
		 * 
		 */
		public HttpResponse() {
			reset();
		}
		
		/**
		 * Returns the requestHeaders.
		 * 
		 * @return
		 */
		public Map<String, List<String>> getRequestHeaders() {
			return requestHeaders;
		}
		
		/**
		 * The requestHeaders to be set.
		 * 
		 * @param requestHeaders
		 */
		public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
			this.requestHeaders = requestHeaders;
		}
		
		/**
		 * Returns the responseCode.
		 * 
		 * @return
		 */
		public int getResponseCode() {
			return responseCode;
		}
		
		/**
		 * The responseCode to be set.
		 * 
		 * @param responseCode
		 */
		public void setResponseCode(int responseCode) {
			System.out.println("setResponseCode(" + responseCode + ")");
			this.responseCode = responseCode;
		}
		
		/**
		 * Returns the responseHeaders.
		 * 
		 * @return the responseHeaders
		 */
		public Map<String, List<String>> getResponseHeaders() {
			return responseHeaders;
		}
		
		/**
		 * The responseHeaders to be set.
		 * 
		 * @param responseHeaders
		 *            the responseHeaders to set
		 */
		public void setResponseHeaders(final Map<String, List<String>> responseHeaders) {
			System.out.println("responseHeaders:" + responseHeaders);
			this.responseHeaders = responseHeaders;
			if (!CoreHelper.isNullOrEmpty(responseHeaders)) {
				jsonResponseHeaders = JSONHelper.toJSONString(responseHeaders);
			}
		}
		
		/**
		 * 
		 * @return
		 */
		public String getJsonResponseHeaders() {
			return jsonResponseHeaders;
		}
		
		/**
		 * The responseHeaders to be set.
		 * 
		 * @param responseHeaders
		 *            the responseHeaders to set
		 */
		public void setJsonResponseHeaders(byte[] jsonResponseHeaders) {
			if (!CoreHelper.isNullOrEmpty(jsonResponseHeaders)) {
				String jsonResponseHeader = IOHelper.toUTF8String(jsonResponseHeaders);
				this.responseHeaders = JSONHelper.jsonHeadersAsMap(jsonResponseHeader);
			}
		}
		
		/**
		 * Returns the dataBytes.
		 * 
		 * @return
		 */
		public byte[] getDataBytes() {
			return dataBytes;
		}
		
		/**
		 * The dataBytes to be set.
		 * 
		 * @param dataBytes
		 */
		public void setDataBytes(byte[] dataBytes) {
			System.out.println("setDataBytes(" + dataBytes + ")");
			this.dataBytes = dataBytes;
			System.out.println("dataBytes:" + IOHelper.toUTF8String(dataBytes));
		}
		
		/**
		 * @return the error
		 */
		public Throwable getError() {
			return error;
		}
		
		/**
		 * @param error
		 *            the error to set
		 */
		public void setError(Throwable error) {
			System.out.println("setError(" + error + ")");
			this.error = error;
		}
		
		/**
		 * Returns success if my {@link #status} is any of my SUCCESS states.
		 */
		public boolean isSuccess() {
			return (responseCode == 200);
		}
		
		/**
		 * Returns true if my {@link #status} is any of my ERROR states, or if
		 * my {@link #error} or {@link #failureBO} fields have anything in them.
		 */
		public boolean isError() {
			return (this.error != null);
		}
		
		/**
		 * Returns the response type.
		 * 
		 * @return
		 */
		public String getMimeType() {
			String mimeType = null;
			
			if (responseHeaders != null) {
				mimeType = responseHeaders.get("Content-Type").get(0);
				if (mimeType.indexOf(";") != -1) {
					mimeType = mimeType.substring(0, mimeType.indexOf(";")).trim();
				}
			}
			
			return mimeType;
		}
		
		/**
		 * Resets this object.
		 */
		public void reset() {
			System.out.println("reset()");
			requestHeaders = null;
			responseCode = 0;
			responseHeaders = null;
			dataBytes = null;
			error = null;
		}
		
		/**
		 * Creates exact copy of an object.
		 * 
		 * @see java.lang.Object#clone()
		 */
		public HttpResponse clone() {
			HttpResponse cloneObject = null;
			try {
				cloneObject = (HttpResponse) super.clone();
				cloneObject.requestHeaders = this.requestHeaders;
				cloneObject.responseCode = this.responseCode;
				cloneObject.responseHeaders = this.responseHeaders;
				cloneObject.jsonResponseHeaders = this.jsonResponseHeaders;
				cloneObject.dataBytes = this.dataBytes;
				cloneObject.error = this.error;
			} catch (CloneNotSupportedException ex) {
				System.err.println(ex);
				// This should never happen
				throw new InternalError(ex.toString());
			}
			
			return cloneObject;
		}
		
		/**
		 * Returns the string representation of this object.
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("===================== Operation Response (Start) =====================\n");
			sBuilder.append("requestHeaders:").append(getRequestHeaders()).append("\n");
			sBuilder.append("ResponseCode:").append(getResponseCode()).append("\n");
			sBuilder.append("ResponseHeaders:").append(getResponseHeaders()).append("\n");
			sBuilder.append("dataBytes:").append(IOHelper.toUTF8String(getDataBytes())).append("\n");
			
			if (this.error != null) {
				sBuilder.append("\n\n").append(HTTPHelper.toString(getError())).append("\n\n");
			}
			
			sBuilder.append("===================== Operation Response (End) =====================\n");
			
			return sBuilder.toString();
		}
	}
	
}
