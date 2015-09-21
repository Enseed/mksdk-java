package com.enseed.mksdk.domain.dto.images;

import java.net.URI;
import java.util.Map;

import org.joda.time.DateTime;

public class ImageDescriptionDTO {
	public URI self;
	public String id;
	public String name;
	public String path;
	public URI image;
	public Map<String, URI> thumbnails;
	public DateTime last_modified;
}
