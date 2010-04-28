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

package es.sonxurxo.android.androidroulette.server.model.entities.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.sonxurxo.android.androidroulette.server.model.entities.message.Message;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User;

/**
 * @author "Xurxo Mendez Perez"
 *
 */
public class Chat implements Serializable {

	private static final long serialVersionUID = 7620181914291217639L;

	/**
	 * Acts like id
	 */
	private long id;
	
	/**
	 * One of the users in the chat
	 */
	private User user1;
	
	/**
	 * The other user
	 */
	private User user2;
	
	/**
	 * List of messages sent from user1 to user2
	 */
	private List<Message> messagesFromUser1;
	
	/**
	 * List of messages sent from user2 to user1
	 */
	private List<Message> messagesFromUser2;
	
	/**
	 * Specifies if one of the users has ended the chat
	 */
	private boolean ended;

	/**
	 * Creates a new chat between user1 and user2
	 * @param id
	 * @param user1
	 * @param user2
	 */
	public Chat(long id, User user1, User user2) {
		super();
		this.id = id;
		this.user1 = user1;
		this.user1.setChat(this);
		this.user2 = user2;
		this.user2.setChat(this);
		this.messagesFromUser1 = new ArrayList<Message>();
		this.messagesFromUser2 = new ArrayList<Message>();
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser1() {
		return this.user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return this.user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public List<Message> getMessagesFromUser1() {
		return this.messagesFromUser1;
	}

	public void setMessagesFromUser1(List<Message> messagesFromUser1) {
		this.messagesFromUser1 = messagesFromUser1;
	}

	public List<Message> getMessagesFromUser2() {
		return this.messagesFromUser2;
	}

	public void setMessagesFromUser2(List<Message> messagesFromUser2) {
		this.messagesFromUser2 = messagesFromUser2;
	}

	public boolean isCancelled() {
		return this.ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
	public synchronized List<Message> getMessagesTo(User user, int lastReadMessage) {
		return this.user1 == user ? this.getMessagesFromUser2().
        		subList(lastReadMessage, this.getMessagesFromUser2().size()) :
        			this.getMessagesFromUser1().subList(lastReadMessage, this.getMessagesFromUser1().size());
	}
	
	public synchronized void addMessage(User user, Message message) {
		if (this.user1 == user) {
			this.messagesFromUser1.add(message);
		}
		else {
			this.messagesFromUser2.add(message);
		}
	}
}
