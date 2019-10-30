package org.ace.insurance.web.common.myanmarLanguae;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyanmarLanguae {
	private static Properties myanConfig;
	static {
		try {
			myanConfig = new Properties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream("/LANGUAGE_en.properties");
			myanConfig.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load LANGUAGE_en.properties");
		}
	}

	public static String getMyanmarLanguaeString(String lable) {
		return myanConfig.getProperty(lable);
	}

}
