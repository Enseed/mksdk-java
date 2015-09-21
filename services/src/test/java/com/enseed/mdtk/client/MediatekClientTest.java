package com.enseed.mdtk.client;

import java.net.URI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.enseed.mksdk.core.json.JSONParser;
import com.enseed.mksdk.core.logging.LoggerInit;
import com.enseed.mksdk.domain.dto.monitoring.AckDTO;

public class MediatekClientTest {
	
	private static Logger logger = Logger.getLogger(MediatekClientTest.class);

	{
		LoggerInit.Init(Level.INFO, null);
	}

	//@Test
	public void test_IsConnected() throws Exception
	{
		MediatekClient client = new MediatekClient();
		org.junit.Assert.assertTrue(client.isConnected());
	}

	//@Test
	public void test_IsNotConnected() throws Exception
	{
		MediatekClient client = new MediatekClient(new URI("http://127.0.0.1:5555"));
		org.junit.Assert.assertFalse(client.isConnected());
	}

	//@Test
	public void test_Ping() throws Exception
	{
		MediatekClient client = new MediatekClient();
		AckDTO ping = client.ping();
	
		logger.info(JSONParser.toString(ping));

		org.junit.Assert.assertNotNull(ping.time);
	}
}
