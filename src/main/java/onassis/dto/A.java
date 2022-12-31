package onassis.dto;

public class A {
	public Long id;
	public String descr;
	public Boolean active;
	public String color;
	public Boolean credit;
	
	public A() {
		super();
	}
	
	public A(Long id,String descr, Boolean active, String color, Boolean credit) {
		super();
		this.id = id;
		this.descr = descr;
		this.active = active;
		this.color = color;
		this.credit = credit;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Boolean getCredit() {
		return credit;
	}
	public void setCredit(Boolean credit) {
		this.credit = credit;
	}
}
