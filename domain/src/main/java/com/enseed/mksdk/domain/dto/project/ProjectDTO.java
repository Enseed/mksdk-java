package com.enseed.mksdk.domain.dto.project;

import java.net.URI;

public class ProjectDTO {
	
	public static class ProjectLinksDTO
	{
		String images;
	}

	public URI self;
	public String name;
	public String path;
	public ProjectLinksDTO links;
}
