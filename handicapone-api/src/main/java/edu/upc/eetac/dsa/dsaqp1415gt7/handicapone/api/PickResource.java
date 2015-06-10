package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;
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
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Pick;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.PickCollection;

import com.mysql.jdbc.Statement;

@Path("/pick")
public class PickResource {

	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	private String GET_PICK_BY_ID_QUERY = "select p.* from picks p where p.idpick =?";
	private String INSERT_PICK_QUERY = "insert into picks (text, resultado, seguidores, cuota, fechaedicion) values (?, ?, ?, ?, ?)";
	private String DELETE_PICK_QUERY = "delete from picks where idpick=?";
	private String UPDATE_PICK_QUERY = "update picks set text=ifnull(?, text), resultado=ifnull(?, resultado),seguidores=ifnull(?, seguidores),cuota=ifnull(?, cuota),fechaedicion=ifnull(?, fechaedicion) where idpick=?";
	
	private String GET_PICK_QUERY = "select p.* from picks p where p.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_PICK_QUERY_FROM_LAST = "select p.* from picks p where p.creation_timestamp > ? order by creation_timestamp desc";
	private String GET_COMMENTS_FROM_PICK="select c.* from comments c, rel_pickcomment pc where c.idcomment= pc.idcomment and pc.idpick = ? order by last_modified desc";
	
	private String INSERT_REL_PICK_COMMENT="insert into rel_pickcomment(idcomment,idpick) values(?,?)";
	
	@GET
	@Produces(MediaType.HANDICAPONE_API_PICK_COLLECTION)
	public PickCollection getPicks(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		PickCollection picks = new PickCollection();
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
			boolean updateFromLast = after > 0;
			stmt = updateFromLast ? conn
					.prepareStatement(GET_PICK_QUERY_FROM_LAST) : conn 
					.prepareStatement(GET_PICK_QUERY);
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));

			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 20 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;

			while (rs.next()) {
				datos=true;
				Pick pick = new Pick();
				pick.setIdPick(rs.getInt("idpick"));
				pick.setUsername(rs.getString("username"));
				pick.setText(rs.getString("text"));
				pick.setResultado(rs.getInt("resultado"));
				pick.setSeguidores(rs.getInt("seguidores"));
				pick.setCuota(rs.getInt("cuota"));
				pick.setFechaEdicion(rs.getLong("fechaedicion"));
				pick.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				pick.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp")
						.getTime();
				pick.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					picks.setNewestTimestamp(pick.getCreationTimestamp());
				}
				picks.addPicks(pick);
			}
			if(!datos){
				throw new ServerErrorException("La lista de picks est� vac�a",
						Response.Status.INTERNAL_SERVER_ERROR);
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
				throw new ServerErrorException(e.getMessage(),
						Response.Status.SERVICE_UNAVAILABLE);
			}
		}

		return picks;
	}



	@GET
	@Path("/{idpick}")
	@Produces(MediaType.HANDICAPONE_API_PICK)
	public Response getPick(@PathParam("idpick") String idpick,
			@Context Request request) {

		CacheControl cc = new CacheControl();

		Pick pick = getPickFromDatabase(idpick);

		EntityTag eTag = new EntityTag(Long.toString(pick.getLastModified()));

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);


		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}


		rb = Response.ok(pick).cacheControl(cc).tag(eTag);
		

		return rb.build();

	}

	@POST
	@Consumes(MediaType.HANDICAPONE_API_PICK)
	@Produces(MediaType.HANDICAPONE_API_PICK)
	public Pick createPick(Pick pick){
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_PICK_QUERY,
					Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, pick.getText());
			stmt.setInt(2, pick.getCuota());
			stmt.setInt(3, pick.getResultado());
			stmt.setInt(4, pick.getSeguidores());
			stmt.setLong(5, pick.getFechaEdicion());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();// Recuperamos los id generados
			if (rs.next()) {
				
				int idpick = rs.getInt(1);// Recuperamos el ultimo generado

				pick = getPickFromDatabase(Integer.toString(idpick));
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
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
		}

		return pick;
	}
	
	private Pick getPickFromDatabase(String idpick) {
		Pick pick = new Pick();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_PICK_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(idpick));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				pick.setIdPick(rs.getInt("idpick"));
				pick.setUsername(rs.getString("username"));
				pick.setText(rs.getString("text"));
				pick.setResultado(rs.getInt("resultado"));
				pick.setSeguidores(rs.getInt("seguidores"));
				pick.setCuota(rs.getInt("cuota"));
				pick.setFechaEdicion(rs.getLong("fechaedicion"));
				pick.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				pick.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
			} else {
				throw new NotFoundException(
						"No existe un pick con idpick=" + idpick);
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

		return pick;
	}
	
	@DELETE
	@Path("/{idpick}")
	public String deletePick(@PathParam("idpick") String idpick) {
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_PICK_QUERY);
			stmt.setInt(1, Integer.valueOf(idpick));

			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException(
						"There's no pick with idpick=" + idpick);
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
		return "Pick correctly deleted";
	}
	
	@PUT
	@Path("/{idpick}")
	@Consumes(MediaType.HANDICAPONE_API_PICK)
	@Produces(MediaType.HANDICAPONE_API_PICK)
	public Pick updateUser(@PathParam("idpick") String idpick,
			Pick pick) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement(UPDATE_PICK_QUERY);
			stmt.setString(1, pick.getText());
			stmt.setInt(2, pick.getCuota());
			stmt.setInt(3, pick.getResultado());
			stmt.setInt(4, pick.getSeguidores());
			stmt.setLong(5, pick.getFechaEdicion());
			stmt.setString(6, idpick);

			int rows = stmt.executeUpdate(); 
			System.out.println("Query salida: " + stmt);

			if (rows == 0) {
				throw new NotFoundException("No hay un pick con idpick= "
						+ idpick);
			} else {
				System.out.println("pick actualizado");

			}


			 pick = getPickFromDatabase(idpick);

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
		return pick;

	}

	@Path("/{idpick}/comments")
	@GET
	@Produces(MediaType.HANDICAPONE_API_COMMENT_COLLECTION)
	public CommentCollection getPickComments(@PathParam("idpick") String idpick) {
		CommentCollection comments = new CommentCollection();
		comments = getCommentsFromDatabaseByPickid(idpick);
		return comments;

	}

	private CommentCollection getCommentsFromDatabaseByPickid(String idpick) {
		CommentCollection comments = new CommentCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_COMMENTS_FROM_PICK);
			stmt.setString(1, idpick);
			ResultSet rs = stmt.executeQuery();
			System.out.println(stmt);
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Comment comment = new Comment();
				comment.setIdcomment(rs.getInt("idcomment"));
				comment.setUsername(rs.getString("username"));
				comment.setText(rs.getString("text"));
				comment.setFechaEdicion(rs.getLong("fechaedicion"));
				comment.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
				comment.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					comments.setNewestTimestamp(comment.getLastModified());
				}
				comments.addComments(comment);
			}
			comments.setOldestTimestamp(oldestTimestamp);
		} catch (Exception e) {
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
		return comments;
	}
	
	// Post de una cancion en una playllist
		@Path("/{idpick}/comments")
		@POST
		@Consumes(MediaType.HANDICAPONE_API_COMMENT)
		public void uploadRelPickComment(
				@PathParam("idpick") String pickid, Comment comment) {
			Pick pick = getPickFromDatabase(pickid);
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}

			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(INSERT_REL_PICK_COMMENT);
				stmt.setInt(1, comment.getIdcomment());
				stmt.setInt(2, pick.getIdPick());
				stmt.executeUpdate();
				System.out.println(stmt);
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
		}
	
	


}
