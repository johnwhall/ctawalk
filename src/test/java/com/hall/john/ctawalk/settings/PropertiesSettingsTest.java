package com.hall.john.ctawalk.settings;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hall.john.ctawalk.TestUtils;
import com.hall.john.ctawalk.settings.PropertiesSettings.PropertyKey;

public class PropertiesSettingsTest {

	@Mock
	private IConfigStreamProvider _streamProvider;

	@InjectMocks
	private PropertiesSettings _settings;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		TestUtils.clearSettingsProperties();
	}

	@After
	public void tearDown() {
		TestUtils.clearSettingsProperties();
	}

	@Test
	public void invalidMinWaitFileOverrideIsIgnored() throws IOException {
		InputStream stream = TestUtils.propertiesStreamFor(PropertyKey.MIN_WAIT_TIME, "1");
		Mockito.when(_streamProvider.getStream()).thenReturn(stream);
		_settings.postConstruct();
		Assert.assertEquals(PropertyKey.MIN_WAIT_TIME.getDefaultValue(), Integer.toString(_settings.getMinWaitTime()));
	}

	@Test
	public void invalidMinWaitOverrideIsIgnored() throws IOException {
		System.setProperty(PropertyKey.MIN_WAIT_TIME.getString(), "1");
		_settings.postConstruct();
		Assert.assertEquals(PropertyKey.MIN_WAIT_TIME.getDefaultValue(), Integer.toString(_settings.getMinWaitTime()));
	}
}
