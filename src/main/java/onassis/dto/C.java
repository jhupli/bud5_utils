package onassis.dto;

import java.math.BigDecimal;

public class C {
	public Long id;
	public BigDecimal i;

	@Override
	public String toString() {
		return "C{" +
				"id=" + id +
				", i=" + i +
				", descr='" + descr + '\'' +
				", active=" + active +
				", color='" + color + '\'' +
				'}';
	}

	public String descr;
	public Boolean active;
	public String color;

	public C() {
		super();
	}

	public C(Long id, BigDecimal i, String descr, Boolean active, String color) {
		super();
		this.id = id;
		this.i = i;
		this.descr = descr;
		this.active = active;
		this.color = color;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getI() {
		return i;
	}
	public void setI(BigDecimal i) {
		this.i = i;
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
}
