package model;

import java.io.Serializable;

public class Tag_question implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tag_id;
	private int question_id;
	public int getTag_id() {
		return tag_id;
	}
	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}
	public int getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(int question_id) {
		this.question_id = question_id;
	}
}
