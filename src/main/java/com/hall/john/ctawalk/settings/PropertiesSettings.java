package com.hall.john.ctawalk.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertiesSettings implements ISettings {

	public static enum PropertyKey {
		STOP_ID("stopid", "30141"),
		ROUTE_CODE("routecode", "P"),
		API_KEY("apikey", "invalid"),
		WALK_TIME("time.walk", "600"),
		YELLOW_TIME("time.yellow", "60"),
		GREEN_TIME("time.green", "120"),
		MIN_WAIT_TIME("time.wait.min", "30"),
		MAX_WAIT_TIME("time.wait.max", "600");

		private String _key;
		private String _defaultValue;

		private PropertyKey(String key, String defaultValue) {
			_key = key;
			_defaultValue = defaultValue;
		}

		public String getString() {
			return _key;
		}

		public String getDefaultValue() {
			return _defaultValue;
		}
	}

	@Autowired
	private IConfigStreamProvider _streamProvider;

	private Logger _logger = LoggerFactory.getLogger(getClass());

	private Properties _properties;

	@PostConstruct
	public void postConstruct() throws IOException {
		_properties = new Properties();
		for (PropertyKey key : PropertyKey.values()) {
			_properties.setProperty(key.getString(), key.getDefaultValue());
		}

		try (InputStream is = _streamProvider.getStream()) {
			if (is != null) {
				Properties fileProps = new Properties();
				fileProps.load(is);
				applyOver(_properties, fileProps);
			}
		}

		applyOver(_properties, System.getProperties());

		if (getMinWaitTime() < 30) {
			_logger.warn("Ignoring invalid time.wait.min: {}", getMinWaitTime());
			_properties.setProperty(PropertyKey.MIN_WAIT_TIME.getString(), "30");
		}
	}

	private void applyOver(Properties base, Properties overrides) {
		for (String key : base.stringPropertyNames()) {
			String overrideValue = overrides.getProperty(key);
			if (overrideValue != null) {
				base.setProperty(key, overrideValue);
			}
		}
	}

	@Override
	public String getStopID() {
		return _properties.getProperty(PropertyKey.STOP_ID.getString());
	}

	@Override
	public String getRouteCode() {
		return _properties.getProperty(PropertyKey.ROUTE_CODE.getString());
	}

	@Override
	public String getAPIKey() {
		return _properties.getProperty(PropertyKey.API_KEY.getString());
	}

	@Override
	public int getWalkTime() {
		return Integer.valueOf(_properties.getProperty(PropertyKey.WALK_TIME.getString()));
	}

	@Override
	public int getYellowTime() {
		return Integer.valueOf(_properties.getProperty(PropertyKey.YELLOW_TIME.getString()));
	}

	@Override
	public int getGreenTime() {
		return Integer.valueOf(_properties.getProperty(PropertyKey.GREEN_TIME.getString()));
	}

	@Override
	public int getMinWaitTime() {
		return Integer.valueOf(_properties.getProperty(PropertyKey.MIN_WAIT_TIME.getString()));
	}

	@Override
	public int getMaxWaitTime() {
		return Integer.valueOf(_properties.getProperty(PropertyKey.MAX_WAIT_TIME.getString()));
	}

}
