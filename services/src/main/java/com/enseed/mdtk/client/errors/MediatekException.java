package com.enseed.mdtk.client.errors;


public class MediatekException extends Exception {
	private static final long serialVersionUID = 3057210247236021838L;
	
	public MediatekException(String what)
	{
		super(what);
	}

	public MediatekException(String what, Exception cause)
	{
		super(what, cause);
	}

	public MediatekException(Exception cause)
	{
		super(cause);
	}
}
