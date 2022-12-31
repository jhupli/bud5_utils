package onassis.dto.mappers;

import onassis.dto.B;
import onassis.dto.Cb;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapCb implements RowMapper<B> { //only used in tests!!!

	@Override
	public B mapRow(ResultSet rs, int rowNum) throws SQLException {
		Cb cb =
		new Cb(rs.getDate("dc"),
				rs.getBigDecimal("b"),
				rs.getBigDecimal("i"),
				rs.getInt("c"));
		return new B(cb.getD(), cb.getB(), cb.getI(), cb.getC()); //a will have value of c
	}

}
