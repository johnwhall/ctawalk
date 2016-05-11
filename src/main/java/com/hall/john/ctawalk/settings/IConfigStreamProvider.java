package com.hall.john.ctawalk.settings;

import java.io.IOException;
import java.io.InputStream;

public interface IConfigStreamProvider {

	InputStream getStream() throws IOException;

}