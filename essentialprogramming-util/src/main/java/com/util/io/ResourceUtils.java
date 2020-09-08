package com.util.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ovidiu Lapusan
 */
final class ResourceUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

	private ResourceUtils() {
		throw new IllegalAccessError("Instantiation prohibited");
	}

	static ClassLoader getClassLoader() {
		ClassLoader loader;
		try {
			loader = Thread.currentThread().getContextClassLoader();
		} catch (SecurityException ex) {
			// No thread context class loader -> use class loader of this class.
			LOGGER.info("No thread context class loader -> use class loader of this class", ex);
			loader = ResourceUtils.class.getClassLoader();
			if (loader == null) {
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				try {
					loader = ClassLoader.getSystemClassLoader();
				} catch (SecurityException e) {
					// Cannot access system ClassLoader
					LOGGER.error( "Cannot access system ClassLoader", e);
				}
			}
		}

		return loader;
	}
}
