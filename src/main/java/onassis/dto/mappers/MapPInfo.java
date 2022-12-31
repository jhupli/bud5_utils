package onassis.dto.mappers;

import onassis.dto.PInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapPInfo implements RowMapper<PInfo> {

	@Override
	public PInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new PInfo(
                rs.getInt("id"),
				rs.getDate("dc"),
                rs.getDate("d"),				
                rs.getBigDecimal("i"),
				rs.getString("c_descr"),
				rs.getString("a_descr"),
				rs.getString("descr")
				);
	}
}
