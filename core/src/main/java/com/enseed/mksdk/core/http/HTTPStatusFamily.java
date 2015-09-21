package com.enseed.mksdk.core.http;

public enum HTTPStatusFamily
{
    INFORMATIONAL_1XX(100),		// 1xx HTTP status codes.
    SUCCESSFUL_2XX(200), 		// 2xx HTTP status codes.
    REDIRECTION_3XX(300), 		// 3xx HTTP status codes.
    CLIENT_ERROR_4XX(400),		// 4xx HTTP status codes.
    SERVER_ERROR_5XX(500), 		// 5xx HTTP status codes.
    OTHER(0);				// Other, unrecognized HTTP status codes.
    
    private final int m_value;

    HTTPStatusFamily(int value)
    {
    	m_value = value;
    }
    
    public static HTTPStatusFamily fromInt(int value) {
    	switch(value/100)
    	{
    	case 1:
    		return INFORMATIONAL_1XX;
    	case 2:
    		return SUCCESSFUL_2XX;
    	case 3:
    		return REDIRECTION_3XX;
    	case 4:
    		return CLIENT_ERROR_4XX;
    	case 5:
    		return SERVER_ERROR_5XX;
    	default:
    		return OTHER;
    	}
    }

    public int getValue()
    {
    	return m_value; 
    }
}