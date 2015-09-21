package com.enseed.mksdk.core.http;

import java.util.HashMap;
import java.util.Map;

public class HTTPHeaders
{
	Map<String, String> m_values = new HashMap<String, String>();
 	
	public void setHeader(String key, String value) 
 	{
 		if (value == null) 
 		{
 			m_values.remove(key);
 		}
 		else 
 		{
 			m_values.put(key, value);
 		}
 	}
	
	public Map<String, String> getHeaders()
	{
		return m_values;
	}
}