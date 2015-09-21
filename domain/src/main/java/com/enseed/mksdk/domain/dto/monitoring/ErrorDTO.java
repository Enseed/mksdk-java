package com.enseed.mksdk.domain.dto.monitoring;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

public class ErrorDTO {
	public DateTime time;
	public UUID id;
	public String type;
	public String message;
	public List<String> stack;
	public ErrorDTO cause;
}

