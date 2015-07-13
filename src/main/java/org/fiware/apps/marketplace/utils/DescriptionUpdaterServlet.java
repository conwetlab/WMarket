package org.fiware.apps.marketplace.utils;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.fiware.apps.marketplace.bo.DescriptionBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DescriptionUpdaterServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(DescriptionUpdaterServlet.class);
	
	private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	@Override
	public void init() throws ServletException {
		
		super.init();
		
		int period = new Integer(PropertiesUtil.getProperty("descriptions.updatePeriod")).intValue();
		
		executor.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {					
					// Call update all offerings method
					((DescriptionBo) ApplicationContextProvider.getApplicationContext().getBean("descriptionBo"))
							.updateAllDescriptions();;
				} catch (Exception e) {
					logger.warn("Unexpected error", e);
				}
			}
		}, 0, period, TimeUnit.SECONDS);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		executor.shutdown();
	}

}
