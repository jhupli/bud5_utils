package onassis.dto.mappers;

import onassis.dto.C;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapC implements RowMapper<C> {

	@Override
	public C mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new C(
				rs.getLong("id"),
				rs.getBigDecimal("i"),
				rs.getString("descr"),
				rs.getBoolean("active"),
				rs.getString("color"));
	}
}
