package es.sonxurxo.android.androidroulette.client;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
	
	private static final int SEARCHING_MATE_DIALOG_ID = 0;
	private static final int SERVER_MESSAGE_DIALOG_ID = 1;
	
	public static final int CHAT_REQUEST_CODE = 1;
	
	public static final int CHAT_END_CHAT_RESULT_CODE = 1;
	public static final int CHAT_ENDED_RESULT_CODE = 2;
	
	protected StartTask startTask = new StartTask();
	protected Thread thread;
	
	protected String serverMessage;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button)this.findViewById(R.id.startButton);
        button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Main.this.thread = new Thread(Main.this.startTask);
				Main.this.thread.start();
				showDialog(Main.SEARCHING_MATE_DIALOG_ID);
			}
        	
        });
        HttpHelper httpHelper = HttpHelper.getInstance();
		try {
			this.serverMessage = httpHelper.ping();
        	if (this.serverMessage != null) {
        		showDialog(SERVER_MESSAGE_DIALOG_ID);
        	}
		} catch (Exception e) {
			// Empty on purpose
		}
    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case Main.SEARCHING_MATE_DIALOG_ID:
    		ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(this.getResources().getText(R.string.pleaseWait));
            String loading = this.getResources().getText(R.string.searchingMate).toString();
            dialog.setMessage(loading + "...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface theDialog, int keyCode,
						KeyEvent event) {
					if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			        	Main.this.startTask.finish();
			    		HttpHelper httpHelper = HttpHelper.getInstance();
			    		try {
			    			httpHelper.cancel();
			    		} catch (Exception e) {
			    			// Empty on purpose
			    		}
			        }
					return false;
				}
            	
            });
            return dialog;
    	case SERVER_MESSAGE_DIALOG_ID:
    		return new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.btn_star)
            .setTitle(this.getResources().getString(R.string.important))
            .setMessage(this.serverMessage)
            .setPositiveButton(R.string.ok, null)
           .create();
    	}
    	return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case Main.CHAT_REQUEST_CODE:
			Main.this.thread = null;
			switch(resultCode) {
			case Main.CHAT_END_CHAT_RESULT_CODE:
				Toast.makeText(this, getResources().getString(R.string.youCancelled), Toast.LENGTH_LONG).show();
				break;
			case Main.CHAT_ENDED_RESULT_CODE:
				Toast.makeText(this, getResources().getString(R.string.mateCancelled), Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	private class StartHandler extends Handler {

		public StartHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(android.os.Message msg) {
			
			try {
				dismissDialog(Main.SEARCHING_MATE_DIALOG_ID);
			} catch (IllegalArgumentException e) {
				// Empty on purpose
			}
			
			NoMateFoundException noMateFoundException = (NoMateFoundException)msg.getData().getSerializable("NoMateFoundException");
        	if (noMateFoundException != null) {
	        	Toast.makeText(Main.this, getResources().getString(R.string.noMateFound), Toast.LENGTH_LONG).show();
		       	return;
	        }
        	ConnectTimeoutException cE = (ConnectTimeoutException)msg.getData().getSerializable("ConnectTimeoutException");
        	if (cE != null) {
	        	Toast.makeText(Main.this, getResources().getString(R.string.noMateFound), Toast.LENGTH_LONG).show();
		       	return;
	        }
        	Exception e = (Exception)msg.getData().getSerializable("Exception");
        	if (e != null) {
//	        	Toast.makeText(Main.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		       	return;
	        }
        	Intent intent = new Intent(Main.this, Chat.class);
			Main.this.startActivityForResult(intent, Main.CHAT_REQUEST_CODE);
		}
		
	}
	
	private class StartTask implements Runnable {

		private boolean shouldRun = true;
		
		StartTask() {
			// Empty on purpose
		}
		
		public void finish() {
			this.shouldRun = false;
		}
		
		public void run() {
			Looper.prepare();
			StartHandler handler = new StartHandler(Looper.getMainLooper());
			Bundle data = new Bundle();
			android.os.Message msg = new android.os.Message();
			try {
				HttpHelper helper = HttpHelper.getInstance();
				helper.start();
				if (this.shouldRun) {
					msg.setData(data);
					handler.sendMessage(msg);
					this.shouldRun = true;
				}
				
			} catch (NoMateFoundException e) {
				if (!this.shouldRun) {
					this.shouldRun = true;
					handler.removeCallbacks(this);
					return;
				}
				data.putSerializable("NoMateFoundException", e);
	        	msg.setData(data);
				handler.sendMessage(msg);
			} catch (ConnectTimeoutException e) {
				if (!this.shouldRun) {
					this.shouldRun = true;
					handler.removeCallbacks(this);
					return;
				}
				data.putSerializable("ConnectTimeoutException", e);
	        	msg.setData(data);
				handler.sendMessage(msg);
			} catch (Exception e) {
				if (!this.shouldRun) {
					this.shouldRun = true;
					handler.removeCallbacks(this);
					return;
				}
				data.putSerializable("Exception", e);
	        	msg.setData(data);
				handler.sendMessage(msg);
	        }
		}
	}
}