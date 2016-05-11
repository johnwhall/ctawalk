package com.hall.john.ctawalk.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

@Component
public class ConfigFileStreamProvider implements IConfigStreamProvider {

	@Override
	public InputStream getStream() throws IOException {
		try {
			return new FileInputStream(new File(System.getProperty("user.home"), ".ctawalk.properties"));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
