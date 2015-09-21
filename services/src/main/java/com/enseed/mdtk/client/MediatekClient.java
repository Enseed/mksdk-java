package com.enseed.mdtk.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.enseed.mdtk.client.errors.ClientException;
import com.enseed.mdtk.client.errors.HostException;
import com.enseed.mksdk.core.http.HTTPClient;
import com.enseed.mksdk.core.http.HTTPClient.HTTPRequest;
import com.enseed.mksdk.core.http.HTTPClient.HTTPRequest.HTTPResponse;
import com.enseed.mksdk.domain.dto.images.ImageDescriptionCollectionDTO;
import com.enseed.mksdk.domain.dto.images.ImageDescriptionDTO;
import com.enseed.mksdk.domain.dto.monitoring.AckDTO;
import com.enseed.mksdk.domain.dto.monitoring.ErrorDTO;
import com.enseed.mksdk.domain.dto.outputs.OutputChannelCollectionDTO;
import com.enseed.mksdk.domain.dto.outputs.OutputChannelDTO;
import com.enseed.mksdk.domain.dto.preview.PreviewCollectionDTO;
import com.enseed.mksdk.domain.dto.preview.PreviewDTO;
import com.enseed.mksdk.domain.dto.project.ProjectDTO;

class WebClientPolicy
{
	public <T> T execute(HTTPRequest request, Class<T> returnType) throws ClientException, HostException
	{
		HTTPResponse response;
		try {
			response = request.execute();
		} catch (ClientProtocolException e) {
			throw new ClientException(e);
		} catch (IOException e) {
			throw new ClientException(e);
		} catch (URISyntaxException e) {
			throw new ClientException(e);
		}
		if (!response.wasSucessful())
		{
			ErrorDTO exception;
			try {
				exception = response.getContent(ErrorDTO.class);
			} catch (UnsupportedOperationException e) {
				throw new ClientException(e);
			} catch (Exception e) {
				throw new ClientException(e);
			}
			throw new HostException(exception);
		}
		try {
			return response.getContent(returnType);
		} catch (UnsupportedOperationException e) {
			throw new ClientException(e);
		} catch (IOException e) {
			throw new ClientException(e);
		}
	}
}

public class MediatekClient {
	private static Logger s_log = Logger.getLogger(MediatekClient.class);
	
	public static String defaultURI = "http://127.0.0.1:8081"; 

	private HTTPClient m_httpClient;
	private WebClientPolicy m_policy = new WebClientPolicy();
	
	public MediatekClient(URI serverURI) throws URISyntaxException
	{
		s_log.info("Connecting to mediatek at " + serverURI);
		m_httpClient = new HTTPClient(serverURI);
	}
	
	public MediatekClient() throws URISyntaxException
	{
		this(new URI(defaultURI));
	}
	
	public boolean isConnected()
	{
		try
		{
			ping();
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}

	//-----------------------------------------------------
	// MONITORING
	//-----------------------------------------------------

	public AckDTO ping() throws Exception
	{
		return m_policy.execute(
				m_httpClient
					.get("/api/monitoring/ping"),
				AckDTO.class);
	}

	//-----------------------------------------------------
	// PROJECTS
	//-----------------------------------------------------
	public ProjectDTO openProject(String systemPath) throws ClientException, HostException
	{
		return m_policy.execute(
				m_httpClient
					.get("/api/project/open")
					.addQueryParam("path", systemPath),
				ProjectDTO.class);
	}

	public ProjectDTO createProject(String systemPath) throws ClientException, HostException
	{
		return m_policy.execute(
				m_httpClient
					.put("/api/project/create")
					.addQueryParam("path", systemPath),
				ProjectDTO.class);
	}

	public AckDTO closeProject() throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/project/close"),
				AckDTO.class);
	}

	public ProjectDTO getProject() throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/project/"),
				ProjectDTO.class);
	}

	//-----------------------------------------------------
	// IMAGES
	//-----------------------------------------------------
	public ImageDescriptionCollectionDTO getImages() throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/images/"),
				ImageDescriptionCollectionDTO.class);
	}

	public ImageDescriptionDTO getImageInfo(String name) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/images/info/{name}")
				.setProperty("name",  name),
				ImageDescriptionDTO.class);
	}

	public byte[] getImage(String name) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/images/{name}")
				.setProperty("name",  name),
				byte[].class);
	}

	public byte[] getImageThumbnail(String name, int size) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/images/{size}/{name}")
				.setProperty("size",  Integer.toString(size))
				.setProperty("name",  name),
				byte[].class);
	}

	//-----------------------------------------------------
	// PREVIEW
	//-----------------------------------------------------
	public PreviewCollectionDTO getPreviews() throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/previews/"),
				PreviewCollectionDTO.class);
	}

	public PreviewDTO getPreview(String name) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/previews/{name}")
				.setProperty("name",  name),
				PreviewDTO.class);
	}

	public AckDTO putPreview(String previewName, String resourceName) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/previews/{previewName}/{resourceName}")
				.setProperty("previewName", previewName)
				.setProperty("resourceName", resourceName),
				AckDTO.class);
	}

	//-----------------------------------------------------
	// OUTPUTS
	//-----------------------------------------------------
	public OutputChannelCollectionDTO getOutputs() throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/outputs/"),
				OutputChannelCollectionDTO.class);
	}

	public OutputChannelDTO getOutput(String name) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/outputs/{name}")
				.setProperty("name",  name),
				OutputChannelDTO.class);
	}

	public AckDTO putOutput(String channelName, String outputName, String resourceName) throws ClientException, HostException {
		return m_policy.execute(
				m_httpClient.get("/api/outputs/{channelName}/{outputName}/{resourceName}")
				.setProperty("channelName", channelName)
				.setProperty("outputName",  outputName)
				.setProperty("resourceName",  resourceName),
				AckDTO.class);
	}
}
