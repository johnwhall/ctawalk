package com.hall.john.ctawalk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hall.john.ctawalk.settings.PropertiesSettings.PropertyKey;

public abstract class TestUtils {

	public static InputStream propertiesStreamFor(PropertyKey key, Object value) throws IOException {
		Properties props = new Properties();
		props.setProperty(key.getString(), value.toString());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		props.store(baos, null);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	public static void clearSettingsProperties() {
		for (PropertyKey key : PropertyKey.values()) {
			System.clearProperty(key.getString());
		}
	}

	private TestUtils() {
	}

}
