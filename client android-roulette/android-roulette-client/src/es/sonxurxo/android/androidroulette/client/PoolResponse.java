/*
 * Android Runner is a multiplayer GPS game fully written by "Xurxo Mendez Perez"
 * 
 * Copyright (C) 2010 "Xurxo Mendez Perez"
 *   
 * This file is part of Android Runner.
 * 
 * Android Runner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Android Runner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Android Runner.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.sonxurxo.android.androidroulette.client;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Xurxo Mendez Perez"
 *
 */
public class PoolResponse {

	private List<Message> messages;
	private boolean mateTyping;
	
	public PoolResponse() {
		super();
		this.messages = new ArrayList<Message>();
		this.mateTyping = false;
	}

	public List<Message> getMessages() {
		return this.messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public boolean isMateTyping() {
		return this.mateTyping;
	}

	public void setMateTyping(boolean mateTyping) {
		this.mateTyping = mateTyping;
	}
	
}
