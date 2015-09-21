package com.enseed.mksdk.core.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerInit {

	private static boolean initialized = false;

	public static void Init(Level level, String optionalFileName)
	{
		if (initialized)
			return;
		
		initialized = true;
		ConsoleAppender console = new ConsoleAppender(); //create appender
		
		//configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(level);
		console.activateOptions();
		
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);

		if (!StringUtils.isBlank(optionalFileName))
		{
			FileAppender fa = new FileAppender();
			fa.setName("FileLogger");
			fa.setFile("mylog.log");
			fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
			fa.setThreshold(Level.DEBUG);
			fa.setAppend(true);
			fa.activateOptions();

			//add appender to any Logger (here is root)
			Logger.getRootLogger().addAppender(fa);
		}
	}
}
