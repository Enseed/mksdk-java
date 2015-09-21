package com.enseed.mksdk.sample;

import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.enseed.mdtk.client.MediatekClient;
import com.enseed.mdtk.services.entities.Image;
import com.enseed.mdtk.services.entities.Project;
import com.enseed.mksdk.core.http.HTTPClient;

public class Main {

	private static void initLogger()
	{
		// Init logger
		ConsoleAppender console = new ConsoleAppender(); //create appender
		//configure the appender
		String PATTERN = "%d [%p] %m - %C{1}%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.INFO);
		console.activateOptions();
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
		Logger.getLogger(HTTPClient.class).setLevel(Level.DEBUG);
	}

	public static void main(String[] args) {
		initLogger();

		try {
			MediatekClient client = new MediatekClient();
			if (!client.isConnected())
			{
				System.out.println("Failed to connect to Mediatek");
				return;
			}
			
			Project proj = Project.open(client, "C:\\Users\\petit\\Documents\\test.mediatek");
			System.out.println("Opened project " + proj.getName());
			System.out.println("Project content:");
			List<Image> imgs = proj.getImages();
			for (Image img : imgs)
			{
				System.out.println(" - " + img.getName());
			}
			
			Image img = imgs.get(0);
			System.out.println("Sending " + imgs.get(0).getName() + " to preview");
			img.sendToPreview();

			System.out.println("Sending " + imgs.get(0).getName() + " to output");
			img.sendToOutput();

			
			//proj.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
