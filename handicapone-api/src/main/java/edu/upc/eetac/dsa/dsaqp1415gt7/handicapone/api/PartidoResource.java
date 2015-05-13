package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.mysql.jdbc.Statement;

import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.MediaType;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Partido;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.PartidoCollection;



@Path("/partido")
public class PartidoResource {
	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	private String GET_PARTIDO_BY_ID_QUERY = "select p.*, u.name from partidos p, users u where u.username=p.username and p.idpartido=?";
	private String INSERT_PARTIDO_QUERY = "insert into partidos (username, local, visitante, fechacierre, fechapartido) values (?, ?, ?, ?, ?)";
	private String DELETE_PARTIDO_QUERY = "delete from partidos where idpartido=?";
	private String UPDATE_PARTIDO_QUERY = "update partidos set local=ifnull(?, local), visitante=ifnull(?, visitante) where idpartido=?";//si el valor del param q pasas es nulo, el valor q añades en la bbdd es el q habia, subject, y si no el parametro.
	private String GET_PARTIDOS_QUERY = "select p.*, u.name from partidos p, users u where u.username=p.username and p.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_PARTIDOS_QUERY_FROM_LAST = "select p.*, u.name from partidos p, users u where u.username=p.username and p.creation_timestamp > ? order by creation_timestamp desc";
	 
	
	//private String UPDATE_PARTIDO_QUERY = "update partidos set local=ifnull(?, local), visitante=ifnull(?, visitante), fechacierra=ifnull(?, fechacierre), fechapartido=ifnull(?, fechapartido) where idpartido=?";//si el valor del param q pasas es nulo, el valor q añades en la bbdd es el q habia, subject, y si no el parametro.

	
	
	@GET
	@Produces(MediaType.HANDICAPONE_API_PARTIDO_COLLECTION)
	public PartidoCollection getPartidos(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		PartidoCollection partidos = new PartidoCollection();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;//actualizamos si after es mayor que cero
			stmt = updateFromLast ? conn
					.prepareStatement(GET_PARTIDOS_QUERY_FROM_LAST) : conn //operador terciario si es true ejecuta una cosa si es false, otra
					.prepareStatement(GET_PARTIDOS_QUERY);
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
					length = (length <= 0) ? 10 : length;//si length es negativo o 0 el valor es 5 sino el que te pasen.
					stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Partido partido = new Partido();
				partido.setIdpartido(rs.getInt("idpartido"));
				partido.setUsername(rs.getString("username"));
				partido.setLocal(rs.getString("local"));
				partido.setVisitante(rs.getString("visitante"));
				partido.setFechacierre(rs.getString("fechacierre"));
				partido.setFechapartido(rs.getString("fechapartido"));
				partido.setLastModified(rs.getTimestamp("last_modified").getTime());
				partido.setCreationTimestamp(rs.getTimestamp("creation_timestamp").getTime()); 
				oldestTimestamp = rs.getTimestamp("creation_timestamp").getTime();
				partido.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					partidos.setNewestTimestamp(partido.getCreationTimestamp());
				}
				partidos.addPartido(partido);
			}
			partidos.setOldestTimestamp(oldestTimestamp);
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
	
	////////////////////
	////////////////////
	////////////////////
	////////////////////
	////////////////////
	
	
	
	@GET
	@Path("/{idpartido}")
	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
	public Response getPartido(@PathParam("idpartido") String idpartido,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();
	 
		Partido partido = getPartidoFromDatabase(idpartido);
	 
		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(partido.getLastModified()));//etiqueta q creo el servidor y la asocia al recurso (stingid) => no enviar cosas q ya se saben
		//Si ahora alguien cambia algo del recurso, se cambia la etiqueta, el servidor; si el cliente pide algo el servidor ve q no coinciden etiquetas es decir la version esta desactualizada, devuelve lo que pide el cliente y lo actualiza, el cliente tb actualiza lo que ya tenia
		// Verify if it matched with etag available in http request
		//en este caso crea la etiqueta por el campo lastmidified sino esta ese cambio....buscarse la vida para saber cuando se modifica!
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);
	 
		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {//coinciden etiquetas 
			return rb.cacheControl(cc).tag(eTag).build();
		}
	 
		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(partido).cacheControl(cc).tag(eTag);//no coinciden y creamos la etiqueta
	 
		return rb.build();
	
	}
	@POST
	@Consumes(MediaType.HANDICAPONE_API_PARTIDO)
	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
	public Partido createPartido(Partido partido) {
		validatePartido(partido);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_PARTIDO_QUERY,
					Statement.RETURN_GENERATED_KEYS);//return devuelve el primary key, este sera el sitingId
	 
			stmt.setString(1, partido.getUsername());
			//stmt.setString(1, security.getUserPrincipal().getName());//hace q el usuario q crea sea el correcto y no se usurpen 

			stmt.setString(2, partido.getLocal());
			stmt.setString(3, partido.getVisitante());
			stmt.setString(4, partido.getFechacierre());
			stmt.setString(5, partido.getFechapartido());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();//copiamos aqui el idpartido
			if (rs.next()) {
				int idpartido = rs.getInt(1);//lo grabamo en idpartido
	 
				partido = getPartidoFromDatabase(Integer.toString(idpartido));
			} else {
				// Something has failed...
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return partido;
	}
	@DELETE
	@Path("/{idpartido}")
	public void deletePartido(@PathParam("idpartido") String idpartido) {
		//validateUser(idpartido);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_PARTIDO_QUERY);
			stmt.setInt(1, Integer.valueOf(idpartido));
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)throw new NotFoundException("There's no partido with idpartido="
					+ idpartido);//da un errror especial si intentamos borrar algo que no exixte
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
//	@PUT
//	@Path("/{idpartido}")
//	@Consumes(MediaType.HANDICAPONE_API_PARTIDO)
//	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
//	public Partido updatePartido(@PathParam("idpartido") String idpartido, Partido partido) {
//		validateUser(idpartido);
//		validateUpdatePartido(partido);
//
//		Connection conn = null;
//		try {
//			conn = ds.getConnection();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	 
//		PreparedStatement stmt = null;
//		try {
//			stmt = conn.prepareStatement(UPDATE_PARTIDO_QUERY);
//			stmt.setString(1, partido.getLocal());
//			stmt.setString(2, partido.getVisitante());
//			//stmt.setInt(3, Integer.valueOf(idpartido));
//			
////			stmt.setString(1, partido.getLocal());
////			stmt.setString(2, partido.getVisitante());
////			stmt.setString(3, partido.getFechacierre());
////			stmt.setString(4, partido.getFechapartido());
////			stmt.setInt(5, Integer.valueOf(idpartido));
//	 
//			int rows = stmt.executeUpdate();
//			
//			if (rows == 0){
//				throw new NotFoundException("No hay una cancion llamada " + idpartido);
//				}
//			else {
//				System.out.println("canción actualizada");
//				
//			}
////			if (rows == 1)
////				partido = getPartidoFromDatabase(idpartido);
////			else {
////				throw new NotFoundException("There's no sting with stingid="
////						+ idpartido);
////			}
//	 
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (stmt != null)
//					stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//			}
//		}
//	 
//	return partido;
//	}
////////////////////////
	
	
	 @PUT
		@Path("/{idpartido}")
		@Consumes(MediaType.HANDICAPONE_API_PARTIDO)
		@Produces(MediaType.HANDICAPONE_API_PARTIDO)
		public Partido updatePartido(@PathParam("idpartido") String idpartido, Partido partido) {	
			
			// solo puede el registrado
			//if (!security.isUserInRole("registered"))
			//	throw new ForbiddenException("You are not allowed to delete a book");
			
			//Falta por añadir que el usuario que edite la cancion sea el creador
			// Felipe solo edita lo de Felipe
			
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}

			PreparedStatement stmt = null;
			try {
				
					//if (song_name != null) {
						stmt = conn.prepareStatement(UPDATE_PARTIDO_QUERY);
						stmt.setString(1, partido.getLocal());
						stmt.setString(2, partido.getVisitante());
						stmt.setString(3, idpartido);
						//stmt.setString(1, song_name);
						//stmt.setInt(2,Integer.valueOf(songid));
						
						
						int rows = stmt.executeUpdate(); // para añadir con los datos de la BBDD
						System.out.println("Query salida: " + stmt);
						
						if (rows == 0){
							throw new NotFoundException("No hay una cancion llamada " + idpartido);
							}
						else {
							System.out.println("canción actualizada");
							
						}
			
					//}
					
										
				// si todo va bien...

//Hace el put pero no saca la lista, falta por acabar					
				
				//song = getSongFromDatabase(song_name);

				

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (stmt != null)
						stmt.close();
					conn.close();
				} catch (SQLException e) {
					throw new ServerErrorException(e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR);
				}
			}
			return partido;

		}
	
	
	
	
	
	////////////////////
	private void validatePartido(Partido partido) {//da errores si se sobrepasan los caracteres de subject y 
		if (partido.getLocal() == null)
			throw new BadRequestException("The local team can't be null.");
		if (partido.getVisitante() == null)
			throw new BadRequestException("The visitor team can't be null.");
		if (partido.getLocal().length() > 100)
			throw new BadRequestException("local can't be greater than 100 characters.");
		if (partido.getVisitante().length() > 500)
			throw new BadRequestException("visit can't be greater than 500 characters.");
	}
	private void validateUpdatePartido(Partido partido) {
		if (partido.getLocal() != null && partido.getLocal().length() > 100)
			throw new BadRequestException(
					"Local can't be greater than 100 characters.");
		if (partido.getVisitante() != null && partido.getVisitante().length() > 500)
			throw new BadRequestException(
					"Visitor can't be greater than 500 characters.");
	}
	private Partido getPartidoFromDatabase(String idpartido) {
		Partido partido = new Partido();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_PARTIDO_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(idpartido));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				partido.setIdpartido(rs.getInt("idpartido"));
				partido.setUsername(rs.getString("username"));
				partido.setFechacierre(rs.getString("fechacierre"));
				partido.setFechapartido(rs.getString("fechapartido"));
				partido.setLocal(rs.getString("local"));
				partido.setVisitante(rs.getString("visitante"));
				partido.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				partido.setCreationTimestamp(rs
						.getTimestamp("creation_timestamp").getTime());
			} else {
				throw new NotFoundException("No existe un partido con idpartido="
						+ idpartido);
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
	 
		return partido;
	}
	private void validateUser(String idpartido) {
	    Partido partido = getPartidoFromDatabase(idpartido);
	    String username = partido.getUsername();
		if (!security.getUserPrincipal().getName()
				.equals(username))
			throw new ForbiddenException(
					"You are not allowed to modify this sting.");
	}
	
	

	
	///////////////////
}
	
	
	
	
	
	
	
	
	
	
//	@GET
//	@Produces(MediaType.HANDICAPONE_API_PARTIDO_COLLECTION)
//	public List<Partido> getPartido() {
//		
//		List<Partido> partidos = new ArrayList<Partido>();
//	
//		Connection conn = ConnectDB();
//
//		PreparedStatement stmt = null;
//		try {
//			stmt = conn.prepareStatement("select * from partidos;");
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				Partido partido = new Partido();
//				partido.setIdpartido(rs.getInt("idpartido"));
//				partido.setLocal(rs.getString("local"));
//				partido.setVisitante(rs.getString("visitante"));
//				partido.setFechacierre(rs.getLong("fechacierre"));
//				partido.setFechapartido(rs.getLong("fechapartido"));
//				partidos.add(partido);
//			}
//		} catch (SQLException e) {
//			throw new ServerErrorException(e.getMessage(),
//					Response.Status.INTERNAL_SERVER_ERROR);
//		} finally {
//			try {
//				if (stmt != null)
//					stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//			}
//		}
//		
//		return partidos;
//	}
//	
//	@GET
//	@Path("/{id}")
//	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
//	public List<Partido> getPicks(@PathParam("id") String id) {
//		
//		List<Partido> partidos = new ArrayList<Partido>();
//		
//		Connection conn = ConnectDB(); 
//		
//		PreparedStatement stmt = null;
//		try {
//			stmt = conn.prepareStatement("select * from partidos where idpartido = ?");
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				Partido partido = new Partido();
//				partido.setIdpartido(rs.getInt("idpartido"));
//				partido.setLocal(rs.getString("local"));
//				partido.setVisitante(rs.getString("visitante"));
//				partido.setFechacierre(rs.getLong("fechacierre"));
//				partido.setFechapartido(rs.getLong("fechapartido"));
//				partidos.add(partido);
//			}
//		} catch (SQLException e) {
//			throw new ServerErrorException(e.getMessage(),
//					Response.Status.INTERNAL_SERVER_ERROR);
//		} finally {
//			try {
//				if (stmt != null)
//					stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//			}
//		}
//		
//		return partidos;
//	
//	}
//	
//
//	private Connection ConnectDB(){
//		Connection conn = null;
//		try {
//			conn = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServerErrorException("Could not connect to the database",
//					Response.Status.SERVICE_UNAVAILABLE);
//		}
//		
//		return conn;
//	}
//}
