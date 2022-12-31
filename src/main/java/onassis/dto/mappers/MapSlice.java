package onassis.dto.mappers;

import onassis.dto.Slice;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapSlice implements RowMapper<Slice> {
	@Override
	public Slice mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Slice(
				rs.getBigDecimal("sl"),
				rs.getInt("c"));
	}
}
