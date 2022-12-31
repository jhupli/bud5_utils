package onassis.dto.mappers;

import onassis.dto.P;
import onassis.dto.Pb;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapPb implements RowMapper<P> {

	@Override
	public P mapRow(ResultSet rs, int rowNum) throws SQLException {
		return (P) new Pb(
				rs.getLong("id"),
				rs.getDate("dc"),
				rs.getDate("d"),
				rs.getBigDecimal("i"),
				rs.getInt("c"),
				rs.getInt("a"),
				rs.getBoolean("s"),
				rs.getString("g"),
				rs.getString("descr"),
				rs.getBoolean("l"),
				rs.getBigDecimal("b"));
	}
}
