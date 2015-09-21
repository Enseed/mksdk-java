package com.enseed.mdtk.client.errors;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.enseed.mksdk.core.json.JSONParser;
import com.enseed.mksdk.domain.dto.monitoring.ErrorDTO;

public class HostException extends MediatekException {
	private static final long serialVersionUID = 7058750926495711053L;
	private ErrorDTO m_errorDTO;
	
	public HostException(ErrorDTO errorDTO)
	{
		super(errorDTO.message);
		m_errorDTO = errorDTO;
	}

	public DateTime time() { return m_errorDTO.time; }
	public UUID id() { return m_errorDTO.id; }
	public String type() { return m_errorDTO.type; }
	public List<String> details() { return m_errorDTO.stack; }
	
	@Override
	public String toString()
	{
		return JSONParser.toString(m_errorDTO);
	}
}