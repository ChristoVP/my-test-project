package com.herc.ewcm.core.services;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.commons.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.herc.ewcm.core.common.HercCommonUtils;

@Component(name = "com.herc.ewcm.core.services.HercFeedbackService", label = "HercFeedbackService", description = "Handles all api calls of Herc HercFeedbackService", specVersion = "1.1", immediate = true, metatype = true)
@Service(value = { HercFeedbackService.class })
public class HercFeedbackService {
	
	@Reference
	private HercCommonUtils hercCommonUtils;
	private static final Logger log = LoggerFactory.getLogger(HercSolutionService.class);
	public JsonObject provideFeedback(String userid,String subject,String comments,String account)
	{
		
		
		return null;
		
	}

}
