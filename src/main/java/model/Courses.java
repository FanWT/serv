package model;

public class Courses {
	private String course_id;
	private String course_name;
	private Double course_rate;
	private int course_comment_num;
	private Double course_credit;
	private String course_dept;
	
	public String getCourse_id() {
		return course_id;
	}
	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}
	public String getCourse_name() {
		return course_name;
	}
	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}
	public double getCourse_rate() {
		return course_rate;
	}
	public void setCourse_rate(double course_rate) {
		this.course_rate = course_rate;
	}
	public int getCourse_comment_num() {
		return course_comment_num;
	}
	public void setCourse_comment_num(int course_comment_num) {
		this.course_comment_num = course_comment_num;
	}
	
	public String getCourse_dept() {
		return course_dept;
	}
	public void setCourse_dept(String course_dept) {
		this.course_dept = course_dept;
	}
	public Double getCourse_credit() {
		return course_credit;
	}
	public void setCourse_credit(Double course_credit) {
		this.course_credit = course_credit;
	}
}
