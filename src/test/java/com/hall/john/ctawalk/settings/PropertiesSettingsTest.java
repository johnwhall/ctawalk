package com.hall.john.ctawalk.settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hall.john.ctawalk.settings.PropertiesSettings.PropertyKey;

@RunWith(Parameterized.class)
public class PropertiesSettingsTest {

	@Mock
	private IConfigStreamProvider _streamProvider;

	@InjectMocks
	private PropertiesSettings _settings;

	@Parameter
	public PropertyKey _key;

	@Parameter(1)
	public Function<PropertiesSettings, Object> _func;

	@Parameter(2)
	public Object _defautValue;

	@Parameter(3)
	public Object _overrideValue1;

	@Parameter(4)
	public Object _overrideValue2;

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> params = new ArrayList<Object[]>();

		Function<PropertiesSettings, Object> func = (x) -> x.getStopID();
		params.add(new Object[] { PropertyKey.STOP_ID, func, "30141", "30000", "10000" });

		func = (x) -> x.getRouteCode();
		params.add(new Object[] { PropertyKey.ROUTE_CODE, func, "P", "Brn", "Pink" });

		func = (x) -> x.getAPIKey();
		params.add(new Object[] { PropertyKey.API_KEY, func, "invalid", "yes", "no" });

		func = (x) -> x.getWalkTime();
		params.add(new Object[] { PropertyKey.WALK_TIME, func, 600, 0, 1 });

		func = (x) -> x.getYellowTime();
		params.add(new Object[] { PropertyKey.YELLOW_TIME, func, 60, 2, 3 });

		func = (x) -> x.getGreenTime();
		params.add(new Object[] { PropertyKey.GREEN_TIME, func, 120, 4, 5 });

		func = (x) -> x.getMinWaitTime();
		params.add(new Object[] { PropertyKey.MIN_WAIT_TIME, func, 30, 6, 7 });

		func = (x) -> x.getMaxWaitTime();
		params.add(new Object[] { PropertyKey.MAX_WAIT_TIME, func, 600, 8, 9 });

		return params;
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		System.clearProperty(_key.getString());
	}

	@Test
	public void testDefault() throws IOException {
		_settings.postConstruct();
		Assert.assertEquals(_defautValue, _func.apply(_settings));
	}

	@Test
	public void testFileOverride() throws IOException {
		Mockito.when(_streamProvider.getStream()).thenReturn(propertiesStreamFor(_key, _overrideValue1));
		_settings.postConstruct();
		Assert.assertEquals(_overrideValue1, _func.apply(_settings));
	}

	@Test
	public void testSystemPropertyOverride() throws IOException {
		System.setProperty(_key.getString(), _overrideValue2.toString());
		_settings.postConstruct();
		Assert.assertEquals(_overrideValue2, _func.apply(_settings));
	}

	@Test
	public void testSystemPropertyOverrideTrumpsFileOverride() throws IOException {
		Mockito.when(_streamProvider.getStream()).thenReturn(propertiesStreamFor(_key, _overrideValue1));
		System.setProperty(_key.getString(), _overrideValue2.toString());
		_settings.postConstruct();
		Assert.assertEquals(_overrideValue2, _func.apply(_settings));
	}

	private static InputStream propertiesStreamFor(PropertyKey key, Object value) throws IOException {
		Properties props = new Properties();
		props.setProperty(key.getString(), value.toString());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		props.store(baos, null);

		return new ByteArrayInputStream(baos.toByteArray());
	}

}
