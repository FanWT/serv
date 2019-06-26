package model;

import java.util.Date;

public class Downloads {
	private int download_id;
	private String download_name;
	private String user_id;
	private String course_id;
	private int download_size;
    private String download_url;
    private String download_type;
    private Date download_time;
    private int download_num;
	
    public int getDownload_id() {
		return download_id;
	}
	public void setDownload_id(int download_id) {
		this.download_id = download_id;
	}
	public String getDownload_name() {
		return download_name;
	}
	public void setDownload_name(String download_name) {
		this.download_name = download_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public int getDownload_size() {
		return download_size;
	}
	public void setDownload_size(int download_size) {
		this.download_size = download_size;
	}
	public String getDownload_url() {
		return download_url;
	}
	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}
	public String getDownload_type() {
		return download_type;
	}
	public void setDownload_type(String download_type) {
		this.download_type = download_type;
	}
	public int getDownload_num() {
		return download_num;
	}
	public void setDownload_num(int download_num) {
		this.download_num = download_num;
	}
	public String getCourse_id() {
		return course_id;
	}
	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}
	public Date getDownload_time() {
		return download_time;
	}
	public void setDownload_time(Date download_time) {
		this.download_time = download_time;
	}
}
