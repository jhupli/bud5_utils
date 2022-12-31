package onassis.dto;

import java.math.BigDecimal;
import java.util.Date;

public class Cb {
	Date d;
	BigDecimal b;
	BigDecimal i;
	Integer c;

	public Cb(Date d, BigDecimal b, BigDecimal i,Integer c) {
		super();
		this.d = d;
		this.b = b;
		this.i = i;
		this.c = c;
	}

	public Date getD() {
		return d;
	}

	public void setD(Date d) {
		this.d = d;
	}

	public BigDecimal getB() {
		return b;
	}

	public void setB(BigDecimal b) {
		this.b = b;
	}

	public BigDecimal getI() {
		return i;
	}

	public void setI(BigDecimal i) {
		this.i = i;
	}

    public Integer getC() { return c; }

    public void setC(Integer c) { this.c = c; }
}
