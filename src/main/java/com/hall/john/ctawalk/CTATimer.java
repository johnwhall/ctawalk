package com.hall.john.ctawalk;

import java.awt.Color;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hall.john.ctawalk.settings.ISettings;

@Component
public class CTATimer {

	@Autowired
	private ITrayIcon _trayIcon;

	@Autowired
	private IArrivalsProvider _arrivalsProvider;

	@Autowired
	private ISettings _settings;

	@Scheduled(fixedRate = 30000)
	public void updateTrayIconColor() {
		int[] arrivals = _arrivalsProvider.getArrivals();
		System.out.println("Got arrivals: " + Arrays.toString(arrivals));

		Color newColor = Color.RED;

		for (int arrival : arrivals) {
			Color bucket = colorBucket(arrival);

			if (bucket == Color.GREEN) {
				newColor = Color.GREEN;
			} else if (bucket == Color.YELLOW && newColor != Color.GREEN) {
				newColor = Color.YELLOW;
			} else if (bucket == Color.RED && newColor != Color.GREEN && newColor != Color.YELLOW) {
				newColor = Color.RED;
			}
		}

		_trayIcon.changeColor(newColor);
	}

	private Color colorBucket(int arrival) {
		if (arrival > _settings.getGreenTime() + _settings.getYellowTime() + _settings.getWalkTime())
			return Color.RED;
		if (arrival > _settings.getYellowTime() + _settings.getWalkTime())
			return Color.GREEN;
		if (arrival > _settings.getWalkTime())
			return Color.YELLOW;
		return Color.RED;
	}

}
