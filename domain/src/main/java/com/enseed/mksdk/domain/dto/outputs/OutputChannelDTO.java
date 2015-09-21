package com.enseed.mksdk.domain.dto.outputs;

import java.net.URI;
import java.util.List;

import com.enseed.mksdk.domain.dto.ResolutionDTO;

public class OutputChannelDTO {
	public URI self;
	public String id;
	public String type;
	public ResolutionDTO resolution;
	public OutputChannelSyncDTO sync;
	public List<LiveOutputDTO> outputs;
}
