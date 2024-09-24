package com.widescope.sqlThunder.utils.okta;

import com.widescope.rest.RestInterface;
import org.springframework.web.servlet.ModelAndView;

public class OktaModelAndView implements RestInterface {
	
	private ModelAndView modelAndView;
	
	public OktaModelAndView(final ModelAndView modelAndView) {
		this.setModelAndView(modelAndView);
	}

	public ModelAndView getModelAndView() {
		return modelAndView;
	}

	public void setModelAndView(ModelAndView modelAndView) {
		this.modelAndView = modelAndView;
	}
}
