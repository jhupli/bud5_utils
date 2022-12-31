package onassis.dto;

import java.math.BigDecimal;
import java.util.Date;

public class PInfo {
	Integer id;
	Date dc;
    Date d;
    BigDecimal i;
    String c_descr;
    String a_descr;
	String descr;

    public PInfo(Integer id, Date dc, Date d, BigDecimal i, String c_descr, String a_descr, String descr) {
		super();
		this.id = id;
		this.dc = dc;
		this.d = d;
		this.i = i;
		this.c_descr = c_descr;
		this.a_descr = a_descr;
		this.descr = descr;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDc() {
		return dc;
	}

	public void setDc(Date dc) {
		this.dc = dc;
	}

	public Date getD() {
		return d;
	}

	public void setD(Date d) {
		this.d = d;
	}

	public BigDecimal getI() {
		return i;
	}

	public void setI(BigDecimal i) {
		this.i = i;
	}

	public String getC_descr() {
		return c_descr;
	}

	public void setC_descr(String c_descr) {
		this.c_descr = c_descr;
	}

	public String getA_descr() {
		return a_descr;
	}

	public void setA_descr(String a_descr) {
		this.a_descr = a_descr;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	@Override
	public String toString() {
		return "PInfo{" +
				"id=" + id +
				", dc=" + dc +
				", d=" + d +
				", i=" + i +
				", c_descr='" + c_descr + '\'' +
				", a_descr='" + a_descr + '\'' +
				", descr='" + descr + '\'' +
				'}';
	}
}
