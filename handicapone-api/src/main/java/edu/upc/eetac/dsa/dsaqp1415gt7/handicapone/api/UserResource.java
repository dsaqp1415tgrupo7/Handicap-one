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
import com.mysql.jdbc.Statement;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Comment;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.CommentCollection;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.Pick;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.PickCollection;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.User;
import edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model.UserCollection;

@Path("/user")
public class UserResource {
	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	private String GET_USER_BY_USERNAME_QUERY = "select u.* from users u where u.username=?";
	private String INSERT_USER_QUERY = "insert into users (username, rol, name, email, password) values (?, ?, ?, ?,MD5(?))";
	private String DELETE_USER_QUERY = "delete from users where username=?";
	private String UPDATE_USER_QUERY = "update users set rol=ifnull(?, rol), name=ifnull(?, name),username=ifnull(?, username),email=ifnull(?, email),password=ifnull(?, password) where username=?";
	
	private String GET_USERS_QUERY = "select u.* from users u where u.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_USERS_QUERY_FROM_LAST = "select u.* from users u where u.creation_timestamp > ? order by creation_timestamp desc";
	
	@GET
	@Produces(MediaType.HANDICAPONE_API_USER_COLLECTION)
	public UserCollection getUsers(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		UserCollection users = new UserCollection();
		boolean datos=false;

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			boolean updateFromLast = after > 0;
			stmt = updateFromLast ? conn
					.prepareStatement(GET_USERS_QUERY_FROM_LAST) : conn 
					.prepareStatement(GET_USERS_QUERY);
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
					User user = new User();
					user.setUsername(rs.getString("username"));
					user.setRol(rs.getString("rol"));
					user.setName(rs.getString("name"));
					user.setEmail(rs.getString("email"));
					user.setPassword(rs.getString("password"));
					user.setLastModified(rs.getTimestamp("last_modified")
							.getTime());
					user.setCreationTimestamp(rs.getTimestamp(
							"creation_timestamp").getTime());
					oldestTimestamp = rs.getTimestamp("creation_timestamp")
							.getTime();
					user.setLastModified(oldestTimestamp);
					if (first) {
						first = false;
						users.setNewestTimestamp(user.getCreationTimestamp());
					}
					users.addUser(user);
				}
			if(!datos){
				throw new ServerErrorException("La lista de usuarios est� vac�a",
						Response.Status.INTERNAL_SERVER_ERROR);
			}
			
			users.setOldestTimestamp(oldestTimestamp);
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

		return users;
	}



	@GET
	@Path("/{username}")
	@Produces(MediaType.HANDICAPONE_API_USER)
	public Response getPartido(@PathParam("username") String username,
			@Context Request request) {

		CacheControl cc = new CacheControl();

		User user = getUserFromDatabase(username);

		EntityTag eTag = new EntityTag(Long.toString(user.getLastModified()));

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);


		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}


		rb = Response.ok(user).cacheControl(cc).tag(eTag);
		

		return rb.build();

	}

	@POST
	@Consumes(MediaType.HANDICAPONE_API_USER)
	@Produces(MediaType.HANDICAPONE_API_USER)
	public User createUser(User user){
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_USER_QUERY,
					Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getRol());
			stmt.setString(3, user.getName());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getPassword());
			stmt.executeUpdate();
			System.out.println("Query salida: " + stmt);
			ResultSet rs = stmt.getGeneratedKeys();// Recuperamos los id generados
			if (rs.next()) {
				int iduser = rs.getInt(1);// Recuperamos el ultimo generado

				user = getUserFromDatabase(Integer.toString(iduser));
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

		return user;
	}
	
	private User getUserFromDatabase(String username) {
		User user = new User();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setRol(rs.getString("rol"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setLastModified(rs.getTimestamp("last_modified")
						.getTime());
				user.setCreationTimestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
			} else {
				throw new NotFoundException(
						"No existe un user con username=" + username);
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

		return user;
	}
	
	@DELETE
	@Path("/{username}")
	public String deleteUser(@PathParam("username") String username) {
		// validateUser(idpartido);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_USER_QUERY);
			stmt.setString(1, username);

			int rows = stmt.executeUpdate();
			System.out.println("Query salida: " + stmt);
			if (rows == 0)
				throw new NotFoundException(
						"There's no user with username=" + username);
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
		return "User correctly deleted";
	}
	
	@PUT
	@Path("/{username}")
	@Consumes(MediaType.HANDICAPONE_API_USER)
	@Produces(MediaType.HANDICAPONE_API_USER)
	public User updateUser(@PathParam("username") String username,
			User user) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement(UPDATE_USER_QUERY);
			stmt.setString(1, user.getRol());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getUsername());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getPassword());
			stmt.setString(6, username);


			int rows = stmt.executeUpdate(); 
			System.out.println("Query salida: " + stmt);

			if (rows == 0) {
				throw new NotFoundException("No hay un usuario llamado "
						+ username);
			} else {
				System.out.println("usuario actualizado");

			}


			 user = getUserFromDatabase(username);

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
		return user;

	}
	
	//Picks de usuario
	@Path("/{username}/picks")
	@GET
	@Produces(MediaType.HANDICAPONE_API_PICK_COLLECTION)
	public PickCollection getUserPicks(
			@PathParam("username") String username) {
		PickCollection picks = new PickCollection();
		picks = getPicksFromDatabaseByQuery(queryGetUserPicks(), username);
		return picks;

	}
	
	private PickCollection getPicksFromDatabaseByQuery(String Query,
			String username) {
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
			stmt = conn.prepareStatement(Query);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Pick pick = new Pick();
				pick.setUsername(rs.getString("username"));
				pick.setResultado(rs.getInt("resultado"));
				pick.setSeguidores(rs.getInt("seguidores"));
				pick.setText(rs.getString("text"));
				pick.setCuota(rs.getInt("cuota"));
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


	private String queryGetUserPicks() {

		return "select p.*, u.username from picks p,  users u where  u.username = p.username and p.username = ? order by last_modified desc";
	}
	
	
	//Picks de usuario
	@Path("/{username}/comments")
	@GET
	@Produces(MediaType.HANDICAPONE_API_COMMENT_COLLECTION)
	public CommentCollection getUserComments(
			@PathParam("username") String username) {
		CommentCollection comments = new CommentCollection();
		comments = getCommentsFromDatabaseByQuery(queryGetUserComments(), username);
		return comments;

	}



	private CommentCollection getCommentsFromDatabaseByQuery(
			String Query, String username) {
		
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
			stmt = conn.prepareStatement(Query);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			while (rs.next()) {
				Comment comment = new Comment();
				comment.setUsername(rs.getString("username"));
				comment.setFechaEdicion(rs.getLong("fechaedicion"));
				comment.setText(rs.getString("username"));
				comment.setText(rs.getString("text"));
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
		return comments;
	}



	private String queryGetUserComments() {
		return "select c.*, u.username from comments c,  users u where  u.username = c.username and c.username = ? order by last_modified desc";

	}
	




	
	
}
