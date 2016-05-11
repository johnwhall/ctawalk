package com.hall.john.ctawalk;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
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

	private Logger _logger = LoggerFactory.getLogger(getClass());

	@EventListener
	public void onStartup(ContextRefreshedEvent event) {
		while (true) {
			int[] arrivals = _arrivalsProvider.getArrivals();
			updateTrayIconColor(arrivals);

			int calculatedWaitTime = nextWaitTime(arrivals);
			int modifiedWaitTime = calculatedWaitTime + new SecureRandom().nextInt(10) - 5;
			_logger.info("Waiting: Calculated: {} Modified: {}", calculatedWaitTime, modifiedWaitTime);

			try {
				Thread.sleep(modifiedWaitTime * 1000);
			} catch (InterruptedException e) {
				_logger.error("timer interrupted", e);
			}
		}
	}

	void updateTrayIconColor(int[] arrivals) {
		_logger.info("Got arrivals: " + Arrays.toString(arrivals));

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

	int nextWaitTime(int[] arrivals) {
		int wait = _settings.getMaxWaitTime();

		for (int arrival : arrivals) {
			Integer ttc = timeToChange(arrival);
			if (ttc != null) {
				wait = Math.min(wait, ttc / 2);
			}
		}

		wait = Math.max(wait, _settings.getMinWaitTime());
		return wait;
	}

	private Integer timeToChange(int arrival) {
		int time = 1;
		Color bucket = colorBucket(arrival);
		--arrival;

		while (arrival >= 0 && bucket.equals(colorBucket(arrival))) {
			time++;
			arrival--;
		}

		if (arrival < 0) {
			return null;
		}

		return time;
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
