package onassis.dto.mappers;

import onassis.dto.B;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapB implements RowMapper<B> {

	@Override
	public B mapRow(ResultSet rs, int rowNum) throws SQLException {
		B result =
		new B(rs.getDate("d"),
				rs.getBigDecimal("b"),
				rs.getBigDecimal("i"),
				rs.getBigDecimal("e"),
				rs.getInt("a"),
				rs.getBoolean("l"));
		result.setSmallestb(rs.getBigDecimal("smallestb"));
		return result;
	}

}
