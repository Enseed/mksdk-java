package com.enseed.mdtk.services.entities;

import java.net.URI;
import java.util.Map;

import org.joda.time.DateTime;

import com.enseed.mdtk.client.MediatekClient;
import com.enseed.mdtk.client.errors.ClientException;
import com.enseed.mdtk.client.errors.HostException;
import com.enseed.mksdk.core.json.JSONParser;
import com.enseed.mksdk.domain.dto.images.ImageDescriptionDTO;

public class Image {
	private ImageDescriptionDTO m_dto;
	private MediatekClient m_client;
	
	public Image(MediatekClient client, ImageDescriptionDTO dto)
	{
		m_client = client;
		m_dto = dto;
	}

	public String getName()
	{
		return m_dto.name;
	}
	
	public String getSystemPath()
	{
		return m_dto.path;
	}
	
	public URI getImage()
	{
		return m_dto.image;
	}
	
	public Map<String, URI> getThumbnails()
	{
		return m_dto.thumbnails;
	}
	
	public DateTime getLastModified()
	{
		return m_dto.last_modified;
	}
	
	public void sendToPreview() throws ClientException, HostException
	{
		Preview.getDefault(m_client).display(getName());
	}

	public void sendToPreview(String previewName) throws ClientException, HostException
	{
		Preview.getPreview(m_client, previewName).display(getName());
	}

	public void sendToOutput() throws ClientException, HostException
	{
		OutputChannel channel = OutputChannel.getDefault(m_client);
		channel.display(getName());
	}

	@Override
	public String toString()
	{
		return JSONParser.toString(m_dto);
	}
}
