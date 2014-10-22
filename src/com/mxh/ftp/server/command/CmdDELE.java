﻿

package com.mxh.ftp.server.command;

import java.io.File;

import com.mxh.ftp.server.thread.SessionThread;
import com.mxh.ftp.util.Util;

import android.util.Log;

public class CmdDELE extends FtpCmd implements Runnable {
	protected String input; 
	
	public CmdDELE(SessionThread sessionThread, String input) {
		super(sessionThread, CmdDELE.class.toString());
		this.input = input;
	}
	
	public void run() {
		Log.e("Delete command", input);
		myLog.l(Log.INFO, "DELE executing");
		String param = getParameter(input);
		File storeFile = inputPathToChrootedFile(sessionThread.getWorkingDir(), param);
		if(!storeFile.exists()){
			sessionThread.writeString("250 File does not exist!\r\n");
			return;
		}
		String errString = null;
		if(violatesChroot(storeFile)) {
			errString = "550 Invalid name or chroot violation\r\n";
		} else if(storeFile.isDirectory()) {
			errString = "550 Can't DELE a directory\r\n";
		} else if(!storeFile.delete()) {
			errString = "450 Error deleting file\r\n";
		}
		
		if(errString != null) {
			sessionThread.writeString(errString);
			myLog.l(Log.INFO, "DELE failed: " + errString.trim());
		} else {
			sessionThread.writeString("250 File successfully deleted\r\n");
			Util.deletedFileNotify(storeFile.getPath());
		}
		myLog.l(Log.INFO, "DELE finished");
	}

}
