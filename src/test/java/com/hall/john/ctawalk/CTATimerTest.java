package com.hall.john.ctawalk;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;

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
public class CTATimerTest {

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
	public Color _expectedColor;

	@Parameters
	public static Collection<Object[]> data() {
		// @formatter:off
		return Arrays.asList(new Object[][] { { new int[] { }, Color.RED },
			                                  { new int[] { 0 }, Color.RED },
			                                  { new int[] { 1 }, Color.RED },
			                                  { new int[] { 2 }, Color.YELLOW },
			                                  { new int[] { 3 }, Color.YELLOW },
			                                  { new int[] { 4 }, Color.GREEN },
			                                  { new int[] { 5 }, Color.GREEN },
			                                  { new int[] { 6 }, Color.GREEN },
			                                  { new int[] { 7 }, Color.RED },
			                                  { new int[] { 0, 2 }, Color.YELLOW },
			                                  { new int[] { 0, 4 }, Color.GREEN },
			                                  { new int[] { 7, 2 }, Color.YELLOW },
			                                  { new int[] { 7, 4 }, Color.GREEN },
			                                  { new int[] { 2, 4 }, Color.GREEN },
			                                  });
		// @formatter:on
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(_settings.getWalkTime()).thenReturn(1);
		Mockito.when(_settings.getYellowTime()).thenReturn(2);
		Mockito.when(_settings.getGreenTime()).thenReturn(3);
	}

	@Test
	public void doTest() {
		Mockito.when(_provider.getArrivals()).thenReturn(_arrivals);
		_timer.updateTrayIconColor();
		Mockito.verify(_trayIcon).changeColor(_expectedColor);
	}

}
