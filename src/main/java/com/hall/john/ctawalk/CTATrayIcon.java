package com.hall.john.ctawalk;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class CTATrayIcon implements ITrayIcon {

	private TrayIcon _trayIcon = new TrayIcon(imageFromColor(Color.RED));

	@PostConstruct
	public void postConstruct() throws AWTException {
		SystemTray.getSystemTray().add(_trayIcon);
	}

	@Override
	public void changeColor(Color color) {
		Image oldImg = _trayIcon.getImage();
		_trayIcon.setImage(imageFromColor(color));
		oldImg.flush();
	}

	private static Image imageFromColor(Color color) {
		BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		return img;
	}

}
