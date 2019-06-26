package model;

import java.util.Date;

public class Ddls {
	private int ddl_id;
	private String user_id;
	private String ddl_content;
	private String ddl_title;
	private Date ddl_time;
	
	public int getDdl_id() {
		return ddl_id;
	}
	public void setDdl_id(int ddl_id) {
		this.ddl_id = ddl_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Date getDdl_time() {
		return ddl_time;
	}
	public void setDdl_time(Date ddl_time) {
		this.ddl_time = ddl_time;
	}
	public String getDdl_content() {
		return ddl_content;
	}
	public void setDdl_content(String ddl_content) {
		this.ddl_content = ddl_content;
	}
	public String getDdl_title() {
		return ddl_title;
	}
	public void setDdl_title(String ddl_title) {
		this.ddl_title = ddl_title;
	}
}
