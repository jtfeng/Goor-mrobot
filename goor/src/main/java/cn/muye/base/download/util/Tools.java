package cn.muye.base.download.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class Tools {
	public static UUID getRandomUUID(){
		return java.util.UUID.randomUUID();
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);
	/**
	 * @param filename propertiy file path
	 * @return 
	 */
	public static Properties readPropertiesFile(String filename){
		 Properties properties = new Properties();
		 try{
			 InputStream inputs = new FileInputStream(filename);
			 properties.load(inputs);
			 inputs.close();
		 }catch(IOException e){
			 LOGGER.error(e.getMessage(), e);
		 }
		 return properties;
	}
}
