package model;

public class Users {
	private String user_id;
	private int state;
	private String user_name;
	private String user_dept;
	private String user_major;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getUser_dept() {
		return user_dept;
	}
	public void setUser_dept(String user_dept) {
		this.user_dept = user_dept;
	}
	public String getUser_major() {
		return user_major;
	}
	public void setUser_major(String user_major) {
		this.user_major = user_major;
	}

}
