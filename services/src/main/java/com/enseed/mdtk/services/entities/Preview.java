package com.enseed.mdtk.services.entities;

import com.enseed.mdtk.client.MediatekClient;
import com.enseed.mdtk.client.errors.ClientException;
import com.enseed.mdtk.client.errors.HostException;
import com.enseed.mksdk.core.json.JSONParser;
import com.enseed.mksdk.domain.dto.preview.PreviewCollectionDTO;
import com.enseed.mksdk.domain.dto.preview.PreviewDTO;

public class Preview {
	private PreviewDTO m_dto;
	private MediatekClient m_client;
	
	public Preview(MediatekClient client, PreviewDTO dto)
	{
		m_client = client;
		m_dto = dto;
	}
	
	public static Preview getPreview(MediatekClient client, String previewName) throws ClientException, HostException
	{
		PreviewDTO previewDTO = client.getPreview(previewName);
		return new Preview(client, previewDTO);
	}

	public static Preview getDefault(MediatekClient client) throws ClientException, HostException
	{
		PreviewCollectionDTO previews = client.getPreviews();
		for (PreviewDTO previewDTO : previews.previews)
		{
			if (previewDTO.is_default)
				return new Preview(client, previewDTO);
		}
		
		if (!previews.previews.isEmpty())
			return new Preview(client, previews.previews.get(0));
		
		return null;
	}
	
	public String getId()
	{
		return m_dto.id;
	}
	
	public void display(String resourceName) throws ClientException, HostException
	{
		m_client.putPreview(getId(), resourceName);
	}

	@Override
	public String toString()
	{
		return JSONParser.toString(m_dto);
	}
}
