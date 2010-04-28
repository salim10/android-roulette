package es.sonxurxo.android.androidroulette.client;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class Chat extends Activity {

	private static final int END_CHAT_DIALOG_ID = 0;
	
	private static final int END_CHAT_MENU_ID = 0;
	
	protected static final int POOLING_DELAY = 10000;
	
	private static final String DATE_FORMAT = "HH:mm:ss";
	
	protected Button sendButton;
	private TextView chatWindow;
	protected TextView status;
	protected EditText input;

	protected String messageToSent;
	
	protected boolean showingWelcomeMessage;
	
	protected boolean cycle = true;
	
	protected long lastReadMessage = 0;
	
	protected PoolTask poolTask = new PoolTask();
	protected Thread poolThread = new Thread(Chat.this.poolTask);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        
        this.chatWindow = (TextView)this.findViewById(R.id.chatWindow);
        this.status = (TextView)this.findViewById(R.id.status);
        this.input = (EditText)this.findViewById(R.id.input);
        this.input.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER) && !Chat.this.input.getText().toString().equals("")) {
		        	Chat.this.doSendMessage();
		        	return true;
		        }
		        if (Chat.this.showingWelcomeMessage) {
		        	Chat.this.status.setText("");
		        }
		        Chat.this.sendButton.setEnabled(!Chat.this.input.getText().toString().equals(""));
		        return false;
		    }
        	
        });
        this.sendButton = (Button)this.findViewById(R.id.sendButton);
        this.sendButton.setEnabled(false);
        this.sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Chat.this.doSendMessage();
			}
        	
        });
        this.status.setText(getResources().getString(R.string.welcomeMessage));
        this.showingWelcomeMessage = true;
    }
    
    protected void cycle() {
		Chat.this.poolThread.start();
    }
    
    @Override
	protected void onStart() {
    	this.cycle = true;
    	this.cycle();
    	super.onStart();
    }
    
    @Override
	protected void onStop() {
    	this.cycle = false;
    	super.onStop();
	}
    
    protected void doSendMessage() {
    	this.cycle = false;
    	this.messageToSent = Chat.this.input.getText().toString();
		Chat.this.input.setText("");
		Chat.this.sendButton.setEnabled(false);
		Chat.this.input.setEnabled(false);
		try {
			HttpHelper helper = HttpHelper.getInstance();
			
			PoolResponse result = helper.send(Chat.this.messageToSent, Chat.this.lastReadMessage);
			
			Message myMessage = result.getMessages().get(result.getMessages().size() - 1);
			myMessage.setText(Chat.this.messageToSent);
			
			// We sort them here to user built-in sort method
			Collections.sort(result.getMessages(), new DateMessagesComparator());
			
			Chat.this.input.setEnabled(true);
			Chat.this.lastReadMessage += result.getMessages().size() - 1;
			
			for (Message message : result.getMessages()) {
				Chat.this.addMessage(message, message == myMessage);
			}
			
			Chat.this.setMateIsTypingMessageEnabled(result.isMateTyping());
			
		    Chat.this.cycle = true;
		} catch (EndedException e) {
			Chat.this.cycle = false;
    		Chat.this.setResult(Main.CHAT_ENDED_RESULT_CODE);
       		Chat.this.finish();
		} catch (ConnectTimeoutException e) {
//			Toast.makeText(Chat.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
//			Toast.makeText(Chat.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	this.showDialog(Chat.END_CHAT_DIALOG_ID);
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
	protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case Chat.END_CHAT_DIALOG_ID:
	    	return new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
	        .setTitle(this.getResources().getString(R.string.endChat))
	        .setMessage(this.getResources().getString(R.string.areYourSure))
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Chat.this.endChat();
				}
	
	        })
	        .setNegativeButton(R.string.no, null)
	        .create();
    	}
    	return null;
	}
    
    protected void endChat() {
    	Chat.this.input.setEnabled(false);
    	Chat.this.chatWindow.setEnabled(false);
    	Chat.this.sendButton.setEnabled(false);
    	Chat.this.cycle = false;
    	HttpHelper helper = HttpHelper.getInstance();
		try {
	    	helper.end();
			Chat.this.cycle = false;
	   		Chat.this.setResult(Main.CHAT_END_CHAT_RESULT_CODE);
	   		Chat.this.finish();
		} catch (ConnectTimeoutException e) {
//			Toast.makeText(Chat.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
//			Toast.makeText(Chat.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void setMateIsTypingMessageEnabled(boolean enabled) {
    	this.showingWelcomeMessage = false;
    	if (enabled) {
    		this.status.setText(getResources().getString(R.string.mateTyping));
    	}
    	else {
    		this.status.setText("");
    	}
    }
    
	protected void addMessage(Message message, boolean sent) {
    	
    	String messageText = new String();
    	String nickName;
    	int nickNameColor;
    	if (sent) {
    		nickName = getResources().getString(R.string.myNickName);
    		nickNameColor = Color.BLUE;
    	}
    	else {
    		nickName = getResources().getString(R.string.mateNickName);
    		nickNameColor = Color.RED;
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat
    		(Chat.DATE_FORMAT, Locale.US);
    	String dateString = sdf.format(message.getDate().getTime());
    	messageText = messageText.concat("(");
    	messageText = messageText.concat(dateString);
    	messageText = messageText.concat(") ");
    	messageText = messageText.concat(nickName);
    	messageText = messageText.concat(": ");
    	messageText = messageText.concat(message.getText());
    	messageText = messageText.concat("\n");
    	this.chatWindow.append(messageText);
    	
    	Spannable sText = (Spannable) this.chatWindow.getText();
    	sText.setSpan(new ForegroundColorSpan(nickNameColor), sText.length() - messageText.length(), 
    			sText.length() - messageText.length() + dateString.length() + 1 + nickName.length() + 2, 0);
    	this.chatWindow.append(sText, sText.length() - messageText.length(), 
    			sText.length() - messageText.length());
    	
    	((ScrollView)this.findViewById(R.id.scrollView)).scrollBy(0, 100000);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, END_CHAT_MENU_ID, 0, getResources().getText(R.string.endChat)).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    switch(item.getItemId()) {
	    case END_CHAT_MENU_ID:
	    	showDialog(END_CHAT_DIALOG_ID);
	    	break;
	    }
	    return true;
	}

	private class PoolHandler extends Handler {

		public PoolHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(android.os.Message msg) {
			
			EndedException endedException = (EndedException)msg.getData().getSerializable("EndedException");
        	if (endedException != null) {
        		Chat.this.cycle = false;
        		Chat.this.setResult(Main.CHAT_ENDED_RESULT_CODE);
	       		Chat.this.finish();
	        }
        	else {
	        	ConnectTimeoutException cE = (ConnectTimeoutException)msg.getData().getSerializable("ConnectTimeoutException");
	        	if (cE != null) {
//		        	Toast.makeText(Chat.this, cE.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			    }
	        	else {
		        	Exception e = (Exception)msg.getData().getSerializable("Exception");
		        	if (e != null) {
//			        	Toast.makeText(Chat.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			        }
		        	else {
		        		Chat.this.setMateIsTypingMessageEnabled(msg.getData().getBoolean("mateTyping"));
			        	if (msg.getData().getBoolean("noMessages")) {
			        		// Empty on purpose
			        	}
			        	else {
			        		Message [] messagesArray = (Message []) msg.getData().getSerializable("messages");
			        		Chat.this.lastReadMessage += messagesArray.length;
			        		for (Message message : messagesArray) {
			        			Chat.this.addMessage(message, false);
			        		}
			        	}
		        	}
	        	}
	        }
		}
	}
	
	private class PoolTask implements Runnable {

		PoolTask() {
			// Empty on purpose
		}
		
		private void execute(PoolHandler handler) {
			Bundle data = new Bundle();
			android.os.Message msg = new android.os.Message();
			try {
				HttpHelper helper = HttpHelper.getInstance();
				PoolResponse response = helper.pool(Chat.this.lastReadMessage, Chat.this.sendButton.isEnabled());
				data.putBoolean("mateTyping", response.isMateTyping());
				if (response.getMessages().size() == 0) {
					data.putBoolean("noMessages", true);
					msg.setData(data);
					handler.sendMessage(msg);
					return;
				}
				
				// We sort them here to user built-in sort method
				Collections.sort(response.getMessages(), new DateMessagesComparator());
				Message [] messagesArray = new Message [response.getMessages().size()];
				for (int i = 0; i < response.getMessages().size(); i++) {
					messagesArray[i] = response.getMessages().get(i);
				}
				data.putSerializable("messages", messagesArray);
				msg.setData(data);
				handler.sendMessage(msg);
				
			} catch (EndedException e) {
	        	data.putSerializable("EndedException", e);
	        	msg.setData(data);
				handler.sendMessage(msg);
			} catch (ConnectTimeoutException e) {
	        	data.putSerializable("ConnectTimeoutException", e);
	        	msg.setData(data);
				handler.sendMessage(msg);
			} catch (Exception e) {
	        	data.putSerializable("Exception", e);
	        	msg.setData(data);
				handler.sendMessage(msg);
	        }
		}
		
		public void run() {
			Looper.prepare();
			PoolHandler handler = new PoolHandler(Looper.getMainLooper());
			
			while (true) {
	        	try {
	        		Thread.sleep(Chat.POOLING_DELAY);
	        		if (Chat.this.cycle) {
	        			this.execute(handler);
	        		}
	        	} catch (InterruptedException e) {
	        		// Empty on purpose
	        	}
	        }
		}
	}
	
	protected class DateMessagesComparator implements Comparator<Message> {

		@Override
		public int compare(Message message1, Message message2) {
			return message1.getDate().after(message2.getDate()) ? 1 : -1;
		}
		
	}
}