package onassis.dto;

import java.util.List;

public class LogEntry {
    List<H> hs;
    Integer rownr;
    
	public LogEntry(List<H> hs, Integer rownr) {
		super();
		this.hs = hs;
		this.rownr = rownr;
	}
	
	public List<H> getHs() {
		return hs;
	}
	public void setHs(List<H> hs) {
		this.hs = hs;
	}
	public Integer getRownr() {
		return rownr;
	}
	public void setRownr(Integer rownr) {
		this.rownr = rownr;
	}
       
}
