package model;

import java.util.Date;

public class Questions {
	private int question_id;
	private String question_content;
	private String user_id;
	private Date question_time;
	private String question_tag1;
	private String question_tag2;
	private String question_tag3;
	private String question_title;
	private int question_answer_num;
	
	public int getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(int question_id) {
		this.question_id = question_id;
	}
	public String getQuestion_content() {
		return question_content;
	}
	public void setQuestion_content(String question_content) {
		this.question_content = question_content;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getQuestion_tag1() {
		return question_tag1;
	}
	public void setQuestion_tag1(String question_tag1) {
		this.question_tag1 = question_tag1;
	}
	public Date getQuestion_time() {
		return question_time;
	}
	public void setQuestion_time(Date question_time) {
		this.question_time = question_time;
	}
	public String getQuestion_tag2() {
		return question_tag2;
	}
	public void setQuestion_tag2(String question_tag2) {
		this.question_tag2 = question_tag2;
	}
	public String getQuestion_tag3() {
		return question_tag3;
	}
	public void setQuestion_tag3(String question_tag3) {
		this.question_tag3 = question_tag3;
	}
	public String getQuestion_title() {
		return question_title;
	}
	public void setQuestion_title(String question_title) {
		this.question_title = question_title;
	}
	public int getQuestion_answer_num() {
		return question_answer_num;
	}
	public void setQuestion_answer_num(int question_answer_num) {
		this.question_answer_num = question_answer_num;
	}
}
