package com.enseed.mdtk.client.errors;


public class ClientException extends MediatekException {
	private static final long serialVersionUID = 6758357567738205680L;
	
	public ClientException(Exception cause)
	{
		super(cause);
	}

	public ClientException(String what, Exception cause)
	{
		super(what, cause);
	}
}
