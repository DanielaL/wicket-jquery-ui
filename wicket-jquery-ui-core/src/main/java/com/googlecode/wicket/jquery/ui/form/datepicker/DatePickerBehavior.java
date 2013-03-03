/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.wicket.jquery.ui.form.datepicker;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;

import com.googlecode.wicket.jquery.ui.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.JQueryEvent;
import com.googlecode.wicket.jquery.ui.Options;
import com.googlecode.wicket.jquery.ui.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.ui.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.ui.ajax.JQueryAjaxPostBehavior;
import com.googlecode.wicket.jquery.ui.utils.RequestCycleUtils;

/**
 * Provides a jQuery datepicker behavior
 *
 * @author Sebastien Briquet - sebfz1
 */
public class DatePickerBehavior extends JQueryBehavior implements IJQueryAjaxAware, IDatePickerListener
{
	private static final long serialVersionUID = 1L;
	private static final String METHOD = "datepicker";

	private JQueryAjaxBehavior onSelectBehavior = null;

	/**
	 * Constructor
	 * @param selector the html selector (ie: "#myId")
	 */
	public DatePickerBehavior(String selector)
	{
		this(selector, new Options());
	}

	/**
	 * Constructor
	 * @param selector the html selector (ie: "#myId")
	 * @param options the {@link Options}
	 */
	public DatePickerBehavior(String selector, Options options)
	{
		super(selector, METHOD, options);
	}

	// Methods //
	@Override
	public void bind(Component component)
	{
		super.bind(component);

		if (this.isOnSelectEventEnabled())
		{
			component.add(this.onSelectBehavior = this.newOnSelectBehavior());
		}
	}

	// Properties //
	@Override
	public boolean isOnSelectEventEnabled()
	{
		return false;
	}

	// Events //
	@Override
	public void onConfigure(Component component)
	{
		super.onConfigure(component);

		if (this.onSelectBehavior != null)
		{
			this.setOption("onSelect", this.onSelectBehavior.getCallbackFunction());
		}
	}

	@Override
	public final void onAjax(AjaxRequestTarget target, JQueryEvent event)
	{
		if (event instanceof SelectEvent)
		{
			this.onSelect(target, ((SelectEvent) event).getDateText());
		}
	}

	// IDatePickerListener //
	@Override
	public void onSelect(AjaxRequestTarget target, String date)
	{
	}

	// Factories //
	/**
	 * Gets a new {@link JQueryAjaxBehavior} that will be called on 'select' javascript method
	 * @param source {@link Component} to which the event returned by {@link #newEvent(AjaxRequestTarget)} will be broadcasted.
	 * @return the {@link JQueryAjaxBehavior}
	 */
	protected JQueryAjaxBehavior newOnSelectBehavior()
	{
		return new JQueryAjaxPostBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				//function( dateText, inst ) {  .. }
				return new CallbackParameter[] { CallbackParameter.explicit("dateText"), CallbackParameter.context("inst") };
			}

			@Override
			protected JQueryEvent newEvent()
			{
				return new SelectEvent();
			}
		};
	}


	// Event classes //
	/**
	 * Provides an event object that will be broadcasted by the {@link JQueryAjaxBehavior} select callback
	 *
	 */
	protected static class SelectEvent extends JQueryEvent
	{
		private final String date;

		public SelectEvent()
		{
			this.date = RequestCycleUtils.getPostParameterValue("dateText").toString();
		}

		public String getDateText()
		{
			return this.date;
		}
	}
}