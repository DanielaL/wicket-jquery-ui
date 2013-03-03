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
package com.googlecode.wicket.jquery.ui.interaction;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.IModel;

import com.googlecode.wicket.jquery.ui.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.JQueryContainer;
import com.googlecode.wicket.jquery.ui.old.OldJQueryAjaxBehavior;
import com.googlecode.wicket.jquery.ui.old.OldJQueryEvent;

/**
 * Provides a jQuery droppable area, on which {@link Draggable}<code>s</code> could be dropped.
 *
 * @param <T> the model object type
 * @author Sebastien Briquet - sebfz1
 */
public abstract class Droppable<T> extends JQueryContainer
{
	private static final long serialVersionUID = 1L;

	private OldJQueryAjaxBehavior onDropBehavior;
	private OldJQueryAjaxBehavior onOverBehavior;
	private OldJQueryAjaxBehavior onExitBehavior;
	private transient Draggable<?> draggable = null;  /* object being dragged */

	/**
	 * Constructor
	 * @param id the markup id
	 */
	public Droppable(String id)
	{
		super(id);
	}

	/**
	 * Constructor
	 * @param id the markup id
	 * @param model the {@link IModel}
	 */
	public Droppable(String id, IModel<T> model)
	{
		super(id, model);
	}

	// Getters / Setters //
	/**
	 * Indicates whether the 'over' event is enabled.<br />
	 * If true, the {@link #onOver(AjaxRequestTarget, Draggable)} event will be triggered.
	 * @return false by default
	 */
	protected boolean isOverEventEnabled()
	{
		return false;
	}

	/**
	 * Indicates whether the 'exit' (or 'out') event is enabled.<br />
	 * If true, the {@link #onExit(AjaxRequestTarget, Draggable)} event will be triggered.
	 * @return false by default
	 */
	protected boolean isExitEventEnabled()
	{
		return false;
	}


	// Events //
	@Override
	protected void onInitialize()
	{
		super.onInitialize();

		this.add(this.onDropBehavior = this.newOnDropBehavior());
		this.add(this.onOverBehavior = this.newOnOverBehavior());
		this.add(this.onExitBehavior = this.newOnExitBehavior());
	}

	/**
	 * Triggered by JQueryAjaxBehavior#respond
	 */
	@Override
	public void onEvent(IEvent<?> event)
	{
		if (event.getPayload() instanceof OldJQueryEvent)
		{
			OldJQueryEvent payload = (OldJQueryEvent) event.getPayload();

			// registers the draggable object that starts
			if (payload instanceof Draggable.DragStartEvent)
			{
				this.draggable = (Draggable<?>) event.getSource();
			}

			else if (payload instanceof Droppable.DropEvent)
			{
				this.onDrop(payload.getTarget(), this.draggable);
			}

			else if (payload instanceof Droppable.OverEvent)
			{
				this.onOver(payload.getTarget(), this.draggable);
			}

			else if (payload instanceof Droppable.ExitEvent)
			{
				this.onExit(payload.getTarget(), this.draggable);
			}
		}
	}

	/**
	 * Triggered when a {@link Draggable} has been dropped
	 * @param target the {@link AjaxRequestTarget}
	 * @param draggable the {@link Draggable} object
	 */
	protected abstract void onDrop(AjaxRequestTarget target, Draggable<?> draggable);

	/**
	 * Triggered when a {@link Draggable} overs the droppable area
	 * @param target the {@link AjaxRequestTarget}
	 * @param draggable the {@link Draggable} object
	 * @see #isOverEventEnabled()
	 */
	protected void onOver(AjaxRequestTarget target, Draggable<?> draggable)
	{
	}

	/**
	 * Triggered when a {@link Draggable} exits the droppable area
	 * @param target the {@link AjaxRequestTarget}
	 * @param draggable the {@link Draggable} object
	 * @see #isExitEventEnabled()
	 */
	protected void onExit(AjaxRequestTarget target, Draggable<?> draggable)
	{
	}


	// IJQueryWidget //
	@Override
	public JQueryBehavior newWidgetBehavior(String selector)
	{
		return new JQueryBehavior(selector, "droppable") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(Component component)
			{
				this.setOption("drop", Droppable.this.onDropBehavior.getCallbackFunction());

				if (Droppable.this.isOverEventEnabled())
				{
					this.setOption("over", Droppable.this.onOverBehavior.getCallbackFunction());
				}

				if (Droppable.this.isExitEventEnabled())
				{
					this.setOption("out", Droppable.this.onExitBehavior.getCallbackFunction());
				}
			}
		};
	}


	// Factories //
	/**
	 * Gets a new {@link OldJQueryAjaxBehavior} that will be called on 'drop' javascript event
	 * @return the {@link OldJQueryAjaxBehavior}
	 */
	private OldJQueryAjaxBehavior newOnDropBehavior()
	{
		return new OldJQueryAjaxBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] { CallbackParameter.context("event"), CallbackParameter.context("ui") };
			}

			@Override
			protected OldJQueryEvent newEvent(AjaxRequestTarget target)
			{
				return new DropEvent(target);
			}
		};
	}

	/**
	 * Gets a new {@link OldJQueryAjaxBehavior} that will be called on 'over' javascript event
	 * @return the {@link OldJQueryAjaxBehavior}
	 */
	private OldJQueryAjaxBehavior newOnOverBehavior()
	{
		return new OldJQueryAjaxBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] { CallbackParameter.context("event"), CallbackParameter.context("ui") };
			}

			@Override
			protected OldJQueryEvent newEvent(AjaxRequestTarget target)
			{
				return new OverEvent(target);
			}
		};
	}

	/**
	 * Gets a new {@link OldJQueryAjaxBehavior} that will be called on 'exit' javascript event
	 * @return the {@link OldJQueryAjaxBehavior}
	 */
	private OldJQueryAjaxBehavior newOnExitBehavior()
	{
		return new OldJQueryAjaxBehavior(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected CallbackParameter[] getCallbackParameters()
			{
				return new CallbackParameter[] { CallbackParameter.context("event"), CallbackParameter.context("ui") };
			}

			@Override
			protected OldJQueryEvent newEvent(AjaxRequestTarget target)
			{
				return new ExitEvent(target);
			}
		};
	}


	// Event classes //
	/**
	 * Provides an event object that will be broadcasted by the {@link OldJQueryAjaxBehavior} 'drop' callback
	 */
	public class DropEvent extends OldJQueryEvent
	{
		public DropEvent(AjaxRequestTarget target)
		{
			super(target);
		}
	}

	/**
	 * Provides an event object that will be broadcasted by the {@link OldJQueryAjaxBehavior} 'over' callback
	 */
	public class OverEvent extends OldJQueryEvent
	{
		public OverEvent(AjaxRequestTarget target)
		{
			super(target);
		}
	}

	/**
	 * Provides an event object that will be broadcasted by the {@link OldJQueryAjaxBehavior} 'exit' callback
	 */
	public class ExitEvent extends OldJQueryEvent
	{
		public ExitEvent(AjaxRequestTarget target)
		{
			super(target);
		}
	}
}
