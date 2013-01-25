/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package org.se2010.emine.events;

/**
 * Interface for components listening on {@link IEMineEventBus} for specific {@link IEMineEvent}s.
 * 
 * @author   Benjamin Friedrich (<a href="mailto:friedrich.benjamin@gmail.com">friedrich.benjamin@gmail.com</a>)
 * @version  1.0 02/2011
 */
public interface IEMineEventListener 
{
	/**
	 * onEvent() is called by the {@link IEMineEventBus} instance
	 * on which the listener is registered when a corresponding
	 * {@link IEMineEvent} is fired via {@link IEMineEventBus#fireEvent(IEMineEvent)}.
	 * 
	 * <b>Note:</b> Every implementation of {@link IEMineEventListener} should
	 * consider thread safety regarding this method as it is usually called
	 * concurrently.
	 * 
	 * @param event  {@link IEMineEvent} instance for which the listener is registered on {@link IEMineEventBus}
	 */
	public void onEvent(IEMineEvent event);
}
