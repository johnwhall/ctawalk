package com.hall.john.ctawalk.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertiesSettings implements ISettings {

	static enum PropertyKey {
		STOP_ID("stopid"),
		ROUTE_CODE("routecode"),
		API_KEY("apikey"),
		WALK_TIME("time.walk"),
		YELLOW_TIME("time.yellow"),
		GREEN_TIME("time.green");

		private String _key;

		private PropertyKey(String key) {
			_key = key;
		}

		public String getString() {
			return _key;
		}
	}

	@Autowired
	private IConfigStreamProvider _streamProvider;

	private Properties _properties;

	@PostConstruct
	public void postConstruct() throws IOException {
		_properties = new Properties();
		_properties.setProperty(PropertyKey.STOP_ID.getString(), "30141");
		_properties.setProperty(PropertyKey.ROUTE_CODE.getString(), "P");
		_properties.setProperty(PropertyKey.API_KEY.getString(), "invalid");
		_properties.setProperty(PropertyKey.WALK_TIME.getString(), "10");
		_properties.setProperty(PropertyKey.YELLOW_TIME.getString(), "1");
		_properties.setProperty(PropertyKey.GREEN_TIME.getString(), "2");

		try (InputStream is = _streamProvider.getStream()) {
			if (is != null) {
				Properties fileProps = new Properties();
				fileProps.load(is);
				applyOver(_properties, fileProps);
			}
		}

		applyOver(_properties, System.getProperties());
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

}
