package model;

import java.util.Date;

public class Comments {
	private int comment_id;
	private String course_id;
	private String user_id;
	private String comment_content;
	private int comment_state;
	private Date comment_date;
	private Double comment_rate;
	
	
	
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public String getCourse_id() {
		return course_id;
	}
	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	
	public Date getComment_date() {
		return comment_date;
	}
	public void setComment_date(Date comment_date) {
		this.comment_date = comment_date;
	}
	public double getComment_rate() {
		return comment_rate;
	}
	public void setComment_rate(Double comment_rate) {
		this.comment_rate = comment_rate;
	}
	public int getComment_state() {
		return comment_state;
	}
	public void setComment_state(int comment_state) {
		this.comment_state = comment_state;
	}
}
