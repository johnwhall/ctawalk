package com.hall.john.ctawalk;

import java.util.Arrays;
import java.util.Collection;

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

import com.hall.john.ctawalk.settings.ISettings;

@RunWith(Parameterized.class)
public class CTATimerNextWaitTest {

	@Mock
	private ISettings _settings;

	@Mock
	private IArrivalsProvider _provider;

	@Mock
	private ITrayIcon _trayIcon;

	@InjectMocks
	private CTATimer _timer;

	@Parameter
	public int[] _arrivals;

	@Parameter(1)
	public int _expectedWait;

	@Parameters
	public static Collection<Object[]> data() {
		// @formatter:off
		return Arrays.asList(new Object[][] { { new int[] { }, 300 }, // no arrivals: wait max
			                                  { new int[] { 1 }, 300 }, // can't walk in time: wait max
			                                  { new int[] { 130 }, 30 }, // ideal update < min: wait min
			                                  { new int[] { 200 }, 40 }, // ideal update 80: wait 80 / 2 = 40
			                                  { new int[] { 250 }, 30 }, // ideal update < min: wait min
			                                  { new int[] { 320 }, 40 }, // ideal update 80: wait 80 / 2 = 40
			                                  { new int[] { 370 }, 30 }, // ideal update < min: wait min
			                                  { new int[] { 440 }, 40 }, // ideal update 0: wait 80 / 2 = 40
			                                  { new int[] { 130, 200 }, 30 }, // pick lower update
			                                  { new int[] { 200, 130 }, 30 }, // pick lower update
			                                  });
		// @formatter:on
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(_settings.getWalkTime()).thenReturn(120);
		Mockito.when(_settings.getYellowTime()).thenReturn(120);
		Mockito.when(_settings.getGreenTime()).thenReturn(120);
		Mockito.when(_settings.getMinWaitTime()).thenReturn(30);
		Mockito.when(_settings.getMaxWaitTime()).thenReturn(300);
	}

	@Test
	public void doTest() {
		Assert.assertEquals(_expectedWait, _timer.nextWaitTime(_arrivals));
	}

}
