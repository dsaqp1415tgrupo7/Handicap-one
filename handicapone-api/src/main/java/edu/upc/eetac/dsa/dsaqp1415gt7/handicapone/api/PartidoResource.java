package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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


import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Comment;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.CommentCollection;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Partido;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.PartidoCollection;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Pick;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.PickCollection;

import com.mysql.jdbc.Statement;



@Path("/partidos")
public class PartidoResource {
	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private String GET_PARTIDO_BY_ID_QUERY = "select p.* from partidos p where p.idpartido=?";
	private String INSERT_PARTIDO_QUERY = "insert into partidos (username, local, visitante, fechacierre, fechapartido) values (?, ?, ?, ?, ?)";
	private String DELETE_PARTIDO_QUERY = "delete from partidos where idpartido=?";
	private String UPDATE_PARTIDO_QUERY = "update partidos set username=ifnull(?,username),local=ifnull(?, local), visitante=ifnull(?, visitante),fechacierre=ifnull(?,fechacierre),fechapartido=ifnull(?,fechapartido) where idpartido=?";

	private String GET_PARTIDOS_QUERY = "select p.* from partidos p where p.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_PARTIDOS_QUERY_FROM_LAST = "select p.* from partidos p where p.creation_timestamp > ? order by creation_timestamp desc";
	private String GET_PARTIDOS_QUERY_VAL = "select p.* from partidos p where p.validado=0 and p.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_PARTIDOS_QUERY_FROM_LAST_VAL = "select p.* from partidos p where p.validado=0 and p.creation_timestamp > ? order by creation_timestamp desc";
	private String GET_PICKS_FROM_PARTIDO="select p.* from picks p, rel_partidopick pp where p.idpick= pp.idpick and pp.idpartido = ?   order by last_modified desc";
	

	/**
	 * Método getPartidos
	 * Método en el que se recupera la lista de partidos siempre y cuando hayan partidos disponibles.
	 * Si la lista está vacía se deberá mostrar un mensaje
	 * @param length
	 * @param before
	 * @param after
	 * @return
	 */
	@GET
	@Produces(MediaType.HANDICAPONE_API_PARTIDO_COLLECTION)
	public PartidoCollection getPartidos(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		PartidoCollection partidos = new PartidoCollection();
		boolean datos=false;

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;// actualizamos si after es mayor
												// que cero
			stmt = updateFromLast ? conn
					.prepareStatement(GET_PARTIDOS_QUERY_FROM_LAST) : conn // operador
																			// terciario
																			// si
																			// es
																			// true
																			// ejecuta
																			// una
																			// cosa
																			// si
																			// es
																			// false,
																			// otra
					.prepareStatement(GET_PARTIDOS_QUERY);
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));

			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 20 : length;// si length es negativo o
														// 0 el valor es 5 sino
														// el que te pasen.
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				datos=true;
				Partido partido = new Partido();
				partido.setIdpartido(rs.getInt("idpartido"));
				partido.setUsername(rs.getString("username"));
				partido.setLocal(rs.getString("local"));
				partido.setVisitante(rs.getString("visitante"));
				partido.setFechacierre(rs.getString("fechacierre"));
				partido.setFechapartido(rs.getString("fechapartido"));
				partido.setValidado(rs.getInt("validado"));
				partido.setResultado(rs.getInt("resultado"));
				partido.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				partido.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				partido.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					partidos.setNewestTimestamp(partido.getCreationTimestamp());
				}
				partidos.addPartido(partido);
			}
			if(!datos){
				throw new ServerErrorException("La lista de partidos está vacía",
						Response.Status.INTERNAL_SERVER_ERROR);
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
				throw new ServerErrorException(e.getMessage(),
						Response.Status.SERVICE_UNAVAILABLE);
			}
		}

		return partidos;
	}
	
	 


	/**
	 * Método getPartido
	 * Método en el que se le pasa el idpartido y si existe retorna el objeto Partido asociado
	 * , en cambio si no existe lanza una NotFoundException con el mensaje: "There's no partido with idpartido= ?
	 * @param idpartido
	 * @param request
	 * @return
	 */
	@GET
	@Path("/{idpartido}")
	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
	public Response getPartido(@PathParam("idpartido") String idpartido,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();

		Partido partido = getPartidoFromDatabase(idpartido);

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(partido.getLastModified()));// etiqueta
																					// q
																					// creo
																					// el
																					// servidor
																					// y
																					// la
																					// asocia
																					// al
																					// recurso
																					// (stingid)
																					// =>
																					// no
																					// enviar
																					// cosas
																					// q
																					// ya
																					// se
																					// saben
		// Si ahora alguien cambia algo del recurso, se cambia la etiqueta, el
		// servidor; si el cliente pide algo el servidor ve q no coinciden
		// etiquetas es decir la version esta desactualizada, devuelve lo que
		// pide el cliente y lo actualiza, el cliente tb actualiza lo que ya
		// tenia
		// Verify if it matched with etag available in http request
		// en este caso crea la etiqueta por el campo lastmidified sino esta ese
		// cambio....buscarse la vida para saber cuando se modifica!
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {// coinciden etiquetas
			return rb.cacheControl(cc).tag(eTag).build();
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(partido).cacheControl(cc).tag(eTag);// no coinciden y
																// creamos la
																// etiqueta

		return rb.build();

	}

	/**
	 * Método createPartido
	 * Método en el que le pasamos el objeto Partido que queremos crear , si el usuario asociado existe 
	 * lo creará correctamente, en cambio si este usuario no existe lanzará una Excepción
	 * 
	 * @param partido
	 * @return Partido (Retorna el objeto Partido con el id generado)
	 */
	
	@POST
	@Consumes(MediaType.HANDICAPONE_API_PARTIDO)
	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
	public Partido createPartido(Partido partido) {
		validatePartido(partido);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_PARTIDO_QUERY,
					Statement.RETURN_GENERATED_KEYS);// return devuelve el
														// primary key, este
														// sera el sitingId

			stmt.setString(1, partido.getUsername());
			stmt.setString(2, partido.getLocal());
			stmt.setString(3, partido.getVisitante());
			stmt.setString(4, partido.getFechacierre());
			stmt.setString(5, partido.getFechapartido());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();// copiamos aqui el idpartido
			if (rs.next()) {
				int idpartido = rs.getInt(1);// lo grabamo en idpartido

				partido = getPartidoFromDatabase(Integer.toString(idpartido));
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
				throw new ServerErrorException(e.getMessage(),
						Response.Status.SERVICE_UNAVAILABLE);
			}
		}

		return partido;
	}

	
	/**
	 * Método deletePartido
	 * Método en el que se le pasa un idPartido y si este existe se elimina de BDD si no existe lanza una 
	 * NotFoundException con el siguiente mensaje: There's no partido with idpartido=?
	 * @param idpartido
	 * @return String (Devuelve el mensaje de que se ha eliminado correctamente)
	 */
	@DELETE
	@Path("/{idpartido}")
	public String deletePartido(@PathParam("idpartido") String idpartido) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_PARTIDO_QUERY);
			stmt.setInt(1, Integer.valueOf(idpartido));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException(
						"There's no partido with idpartido=" + idpartido);// da
																			// un
																			// errror
																			// especial
																			// si
																			// intentamos
																			// borrar
																			// algo
																			// que
																			// no
																			// exixte
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.SERVICE_UNAVAILABLE);
			}
			
			
		}
		
		return "Partido correctly deleted";
	}

	
	/**
	 * Método updatePartido
	 * método en el que se le pasa como parametro el id del partido que queremos actualizar y un objeto partido
	 * con los campos que se quieren actualizar. Si ese partido existe se realiza la actualización de los campos
	 * oportunos y sino se lanza una NotFoundException con el siguiente mensaje: No hay un partido con idpartido=?
	 * @param idpartido
	 * @param partido
	 * @return Partido 
	 */
	@PUT
	@Path("/{idpartido}")
	@Consumes(MediaType.HANDICAPONE_API_PARTIDO)
	@Produces(MediaType.HANDICAPONE_API_PARTIDO)
	public Partido updatePartido(@PathParam("idpartido") String idpartido,
			Partido partido) {


		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			// if (song_name != null) {
			stmt = conn.prepareStatement(UPDATE_PARTIDO_QUERY);
			stmt.setString(1, partido.getUsername());
			stmt.setString(2, partido.getLocal());
			stmt.setString(3, partido.getVisitante());
			stmt.setString(4, partido.getFechacierre());
			stmt.setString(5, partido.getFechapartido());
			stmt.setString(6, idpartido);


			int rows = stmt.executeUpdate(); // para añadir con los datos de la
												// BBDD
			System.out.println("Query salida: " + stmt);

			if (rows == 0) {
				throw new NotFoundException("There's no partido with idpartido="
						+ idpartido);
			} else {
				System.out.println("partido actualizado");

			}

			//Recuperamos el partido para ver que se han actualizado los campos de manera correcta
			partido = getPartidoFromDatabase(idpartido);

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
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

	
	/**
	 * Método al que se le pasa un objeto Partido 
	 * para validar que el partido creado debe tener obligatoriamente el equipo local y visitante informado
	 * y que tienen un limite de longitud de caracteres. Si esto no se cumple lanza un BadRequestException
	 * con el motivo
	 * @param partido
	 */
	private void validatePartido(Partido partido) {
		if (partido.getLocal() == null)
			throw new BadRequestException("The local team can't be null.");
		if (partido.getVisitante() == null)
			throw new BadRequestException("The visitor team can't be null.");
		if (partido.getLocal().length() > 100)
			throw new BadRequestException(
					"local can't be greater than 100 characters.");
		if (partido.getVisitante().length() > 500)
			throw new BadRequestException(
					"visit can't be greater than 500 characters.");
	}

	
	//TODO No es necesario aunque se revisara
	/*private void validateUpdatePartido(Partido partido) {
		if (partido.getLocal() != null && partido.getLocal().length() > 100)
			throw new BadRequestException(
					"Local can't be greater than 100 characters.");
		if (partido.getVisitante() != null
				&& partido.getVisitante().length() > 500)
			throw new BadRequestException(
					"Visitor can't be greater than 500 characters.");
	}*/

	
	/**
	 * A Partir de un idPartido retorna toda la información de ese partido en el objeto Partido
	 *  si realmente existe en BDD sino retorna una NotFoundException con el mensaje: No existe un partido con idpartido=?
	 * @param idpartido
	 * @return Partido
	 */
	private Partido getPartidoFromDatabase(String idpartido) {
		Partido partido = new Partido();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
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
				partido.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
			} else {
				throw new NotFoundException(
						"There's no partido with idpartido= " + idpartido);
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
				throw new ServerErrorException(e.getMessage(),
						Response.Status.SERVICE_UNAVAILABLE);
			}
		}

		return partido;
	}
	
	@Path("/{idpartido}/picks")
	@GET
	@Produces(MediaType.HANDICAPONE_API_PICK_COLLECTION)
	public PickCollection getPartidoPicks(@PathParam("idpartido") String idpartido) {
		PickCollection picks = new PickCollection();
		picks = getPicksFromDatabaseByPartidoid(idpartido);
		return picks;

	}

	private PickCollection getPicksFromDatabaseByPartidoid(String idpartido) {
		PickCollection picks = new PickCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_PICKS_FROM_PARTIDO);
			stmt.setString(1, idpartido);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Pick pick = new Pick();
				pick.setUsername(rs.getString("username"));
				pick.setText(rs.getString("text"));
				pick.setSeguidores(rs.getInt("seguidores"));
				pick.setResultado(rs.getInt("resultado"));
				pick.setCuota(rs.getInt("cuota"));
				pick.setFechaEdicion(rs.getLong("fechaedicion"));
				pick.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
				pick.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					picks.setNewestTimestamp(pick.getLastModified());
				}
				picks.addPicks(pick);
			}
			picks.setOldestTimestamp(oldestTimestamp);
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
		return picks;
	}

	

	/*private void validateUser(String idpartido) {
		Partido partido = getPartidoFromDatabase(idpartido);
		String username = partido.getUsername();
		if (!security.getUserPrincipal().getName().equals(username))
			throw new ForbiddenException(
					"You are not allowed to modify this sting.");
	}*/

	// /////////////////
}
