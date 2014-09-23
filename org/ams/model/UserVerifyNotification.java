package org.ams.model;

import org.ams.db.User;

public class UserVerifyNotification {
	
	public static final int USER_NOT_EXIST = 1;
	public static final int PASSWORD_UNCORRECT = 2;
	
	private User user;
	private int result;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
}
