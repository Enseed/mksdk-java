package com.enseed.mdtk.services.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.enseed.mdtk.client.MediatekClient;
import com.enseed.mdtk.client.errors.ClientException;
import com.enseed.mdtk.client.errors.HostException;
import com.enseed.mksdk.core.json.JSONParser;
import com.enseed.mksdk.domain.dto.images.ImageDescriptionCollectionDTO;
import com.enseed.mksdk.domain.dto.images.ImageDescriptionDTO;
import com.enseed.mksdk.domain.dto.project.ProjectDTO;

public class Project
{
	private static Logger m_logger = Logger.getLogger(Project.class);

	private MediatekClient m_client;
	private ProjectDTO m_dto;
	
	public static Project open(MediatekClient client, String systemPath)
	{
		try
		{
			ProjectDTO projectDTO = client.openProject(systemPath);
			return new Project(client, projectDTO);
		}
		catch(Exception ex)
		{
			m_logger.warn("Failed to open project " + systemPath + " (" + client + ")", ex);
			return null;
		}
	}
	
	public static Project current(MediatekClient client)
	{
		try
		{
			ProjectDTO projectDTO = client.getProject();
			return new Project(client, projectDTO);
		}
		catch(Exception ex)
		{
			m_logger.warn("Failed to get the current project (" + client + ")", ex);
			return null;
		}
	}

	public static Project create(MediatekClient client, String systemPath)
	{
		try
		{
			ProjectDTO projectDTO = client.createProject(systemPath);
			return new Project(client, projectDTO);
		}
		catch(Exception ex)
		{
			m_logger.warn("Failed to create project " + systemPath + " (" + client + ")", ex);
			return null;
		}
	}

	public Project(MediatekClient client, ProjectDTO projectDTO)
	{
		m_client = client;
		m_dto = projectDTO;
	}
	
	public void close() throws Exception
	{
		m_client.closeProject();
	}
	
	public String getName()
	{
		return m_dto.name;
	}

	public String getPath()
	{
		return m_dto.path;
	}
	
	public List<Image> getImages() throws ClientException, HostException
	{
		List<Image> images = new ArrayList<>();
		ImageDescriptionCollectionDTO imageDTOs = m_client.getImages();
		for(ImageDescriptionDTO imageDTO : imageDTOs.images)
		{
			images.add(new Image(m_client, imageDTO));
		}
		return images;
	}

	@Override
	public String toString()
	{
		return JSONParser.toString(m_dto);
	}
}
