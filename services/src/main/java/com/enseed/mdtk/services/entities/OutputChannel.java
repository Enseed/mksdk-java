package com.enseed.mdtk.services.entities;

import java.util.ArrayList;
import java.util.List;

import com.enseed.mdtk.client.MediatekClient;
import com.enseed.mdtk.client.errors.ClientException;
import com.enseed.mdtk.client.errors.HostException;
import com.enseed.mksdk.domain.dto.outputs.OutputChannelCollectionDTO;
import com.enseed.mksdk.domain.dto.outputs.OutputChannelDTO;

public class OutputChannel {
	private OutputChannelDTO m_dto;
	private MediatekClient m_client;
	
	public OutputChannel(MediatekClient client, OutputChannelDTO dto)
	{
		m_client = client;
		m_dto = dto;
	}
	
	public static List<OutputChannel> getAll(MediatekClient client) throws ClientException, HostException
	{
		List<OutputChannel> outputs = new ArrayList<>();
		OutputChannelCollectionDTO outputDTOs = client.getOutputs();
		for(OutputChannelDTO outputDTO : outputDTOs.channels)
		{
			outputs.add(new OutputChannel(client, outputDTO));
		}
		return outputs;
	}

	public static OutputChannel getDefault(MediatekClient client) throws ClientException, HostException
	{
		List<OutputChannel> outputs = getAll(client);
		return outputs.get(0);
	}


	public static OutputChannel get(MediatekClient client, String outputId) throws ClientException, HostException
	{
		OutputChannelDTO dto = client.getOutput(outputId);
		return new OutputChannel(client, dto);
	}

	int getOutputCount()
	{
		return m_dto.outputs.size();
	}
	
	String getOutputName(int outputIndex)
	{
		return m_dto.outputs.get(outputIndex).name;
	}
	
	void display(String resourceId) throws ClientException, HostException
	{
		m_client.putOutput("0", "0", resourceId);
	}
}
