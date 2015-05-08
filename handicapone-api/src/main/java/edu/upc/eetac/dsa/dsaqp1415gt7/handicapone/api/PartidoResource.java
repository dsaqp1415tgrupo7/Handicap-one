package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Partido;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.PartidoCollection;



@Path("/partido")
public class PartidoResource {
	
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	@GET
	@Produces(MediaType.HANDICAPONE_API_PARTIDO_COLLECTION)
	public List<Partido> getPartido() {
		
		List<Partido> partidos = new ArrayList<Partido>();
	
		Connection conn = ConnectDB();

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("select * from partidos;");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Partido partido = new Partido();
				partido.setIdpartido(rs.getInt("idpartido"));
				partido.setLocal(rs.getString("local"));
				partido.setVisitante(rs.getString("visitante"));
				partido.setFechacierre(rs.getLong("fechacierre"));
				partido.setFechapartido(rs.getLong("fechapartido"));
				partidos.add(partido);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		
		return partidos;
	}

	private Connection ConnectDB(){
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		return conn;
	}
}
