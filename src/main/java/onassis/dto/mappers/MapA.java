package onassis.dto.mappers;

import onassis.dto.A;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapA implements RowMapper<A> {

	@Override
	public A mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new A(
				rs.getLong("id"),
				rs.getString("descr"),
				rs.getBoolean("active"),
				rs.getString("color"),
				rs.getBoolean("credit"));
	}
}
