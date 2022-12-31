package onassis.dto.mappers;

import onassis.dto.H;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapH implements RowMapper<H> {

	@Override
	public H mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new H(
				rs.getTimestamp("hd"),
				rs.getString("op"),
				rs.getInt("rownr"),
                rs.getInt("id"),
				rs.getDate("dc"),
                rs.getDate("d"),				
                rs.getBigDecimal("i"),
				rs.getInt("c"),
				rs.getString("c_descr"),
				rs.getInt("a"),
				rs.getString("a_descr"),
				rs.getBoolean("s"),
				rs.getString("g"),
				rs.getString("descr")
				);
	}
}
