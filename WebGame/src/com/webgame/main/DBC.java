package com.webgame.main;

//импорт на файловете за връзка с базата данни
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
//импорт на джърси библиотеките
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

//път за URL за достъп до уеб сървиса  http://localhost:8080/webgame/db/...
//за всеки метод се добавя нова стойност с @Path(/mymethod) и се достъпва чрез  http://localhost:8080/webgame/db/mymethod/
@Path("/db")

public class DBC {
	// данни за връзката с базата
	private String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost/webgame?useUnicode=true&characterEncoding=utf-8";
	private static String dbusername = "root";
	private static String dbpassword = "123";

	/*
	 * проверка за съществуващо потребителско име и парола в зависимост от това
	 * какво се проверява/ако паролата е празно поле се проверява само за
	 * съществуващ потребител/, използва се при регистриране на нов акаунт връща
	 * true/false , при регистрация се проверява само за потр. име, а при login
	 * се проверява и парлата достъпен метод само за класа
	 */
	private Boolean checkUserExist(String username, String password) throws Exception {
		String query = "";
		if (password.equals("")) {
			query = "SELECT * FROM login WHERE Username ='" + username + "'";
		} else {
			query = "SELECT * FROM login WHERE Username ='" + username + "' AND Password ='" + password + "'";
		}
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			return true;
		}
		return false;
	}

	/*
	 * промяна на паролата на потребител, валидацията се прави в jquery и се
	 * подава стринг новата парола и id на потребител достъпен метод само за
	 * класа
	 */
	private String changePassword(String password, int id) throws Exception {
		String query = "UPDATE login SET Password = '" + password + "' WHERE Id = '" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		st.execute();
		conn.close();
		return "Password changed!!";

	}

	/*
	 * проверка за валидност на токен от базата данни валидна само за класа
	 */
	private Boolean checkTocken(String token, int id) throws Exception {
		String query = "SELECT Token FROM login WHERE Id ='" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (rs.next()) {
			String t1 = rs.getString("Token");
			if (t1.equals(token)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * update на токен при login валидно само за класа с 2 подадени параметъра
	 * id и token
	 */
	private String updateToken(int id, String token) throws Exception {
		String query = "UPDATE login SET Token = '" + token + "' WHERE Id = '" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		st.execute();
		conn.close();
		return "Token update ok!";
	}

	/*
	 * генериране на токен за login
	 */

	private String generateToken() {
		Random r = new Random();
		String token = "";
		Integer xx = 1;
		for (int i = 0; i < 20; i++) {
			xx = r.nextInt(3) + 1;
			if (xx.equals(1)) {
				token += (char) (48 + r.nextInt(10));// цифри 1-0
			} else if (xx.equals(2)) {
				token += (char) (65 + r.nextInt(26));// букви главни
			} else if (xx.equals(3)) {
				token += (char) (97 + r.nextInt(26));// букви малки
			}

		}

		return token;// return User data??????????????????????????
	}

	/*
	 * регистрация на нов потребител достъпва се на адрес
	 * http://localhost:8080/webgame/db/register?username=myusername&p1=password
	 * &p2=password&name=myname проверка на паролите дали съвпадат/валидацията
	 * по-добре да се прави с jquery/ ???? да се криптира паролата с md5????
	 * заявка -
	 * http://localhost:8080/WebGame/db/register?username=myname2&p1=2&p2=2&name
	 * =myusername&mail=mymail
	 */
	@Path("/register")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String registerNewUser(@QueryParam("username") String username, @QueryParam("p1") String p1,
			@QueryParam("p2") String p2, @QueryParam("mail") String mail, @QueryParam("name") String name)
					throws Exception {

		if (username.equals("") || !p1.equals(p2) || mail.equals("") || name.equals("")) {
			return "Check data fields!";
		}
		if (checkUserExist(username, "")) {
			return "USer exist!";
		}
		User usr = new User();
		usr.setName(name);
		usr.setUsername(username);
		usr.setPassword(p1);
		usr.setMail(mail);

		String query = "INSERT INTO `login`(`Username`, `Password`, `Nickname`, `Status`, `Mail`) VALUES ('"
				+ usr.getUsername() + "','" + usr.getPassword() + "','" + usr.getName() + "','0','" + usr.getMail()
				+ "')";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		st.execute();
		conn.close();
		return "Добавен потребител: " + usr.getUsername() + " !";
	}

	/*
	 * Вход в системата, проверява се за валиден потребител и парола, ако се
	 * върне true се генерира токен, записва се в базата и се сетва в куки,
	 * връща обект от клас User заявка
	 * -http://localhost:8080/WebGame/db/login?username=myname2&password=m
	 */
	@Path("/login")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User webLogin(@QueryParam("username") String username, @QueryParam("password") String password)
			throws Exception {
		if (checkUserExist(username, password)) {
			User usr = new User();
			String query = "SELECT * FROM login WHERE Username = '" + username + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				usr.setId(rs.getInt("Id"));
				usr.setName(rs.getString("Nickname"));
				usr.setMail(rs.getString("Mail"));
				usr.setUsername(rs.getString("Username"));
				usr.setPassword(rs.getString("Password"));
				usr.setStatus(rs.getString("Status"));
				usr.setTeam(rs.getString("Team"));
				usr.setToken(generateToken());
			}
			updateToken(usr.getId(), usr.getToken());
			return usr;
		}
		return null;
	}

	/*
	 * промяна на потребителска парола заявка -
	 * http://localhost:8080/WebGame/db/chpass?password=m22&id=1
	 */
	@Path("/chpass")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String changeUserPassword(@QueryParam("password") String password, @QueryParam("id") int id)
			throws Exception {
		// да се направи проверка на подадени параметри и тогава да се извика
		// changePassword ??? токен

		return changePassword(password, id);
	}

	/*
	 * прочита и връща показателите на отбора по зададено id заявка -
	 * http://localhost:8080/WebGame/db/getpt?id=1
	 */
	@Path("/getpt")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPoints(@QueryParam("id") int id) throws Exception {
		String result = null;
		String query = "SELECT * FROM team WHERE Id = '" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			result = "Показатели на " + rs.getString("Name") + "</br>Покзател1: " + rs.getInt("Stat1")
					+ " т.</br>Показател2: " + rs.getInt("Stat2") + " т.</br>Показател3: " + rs.getInt("Stat3")
					+ " т.</br>Показател4: " + rs.getInt("Stat4") + " т.</br>За разпределяне: "
					+ rs.getInt("ExtraStat");
		}
		return result;
	}

	/*
	 * запис в базата на показателите на отбора след като потребителя е направил
	 * промяна, задават се 4-те показателя,допълнителните точки, токена за
	 * сравнение и id-то на отбора заявка :
	 * http://localhost:8080/WebGame/db/setpt?id=1&stat1=6&stat2=3&stat3=9&stat4
	 * =2&extra=11&token=00000000000
	 */
	@Path("/setpt")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String setPoints(@QueryParam("id") int id, @QueryParam("stat1") int stat1, @QueryParam("stat2") int stat2,
			@QueryParam("stat3") int stat3, @QueryParam("stat4") int stat4, @QueryParam("extra") int extra,
			@QueryParam("token") String token) throws Exception {
		if (checkTocken(token, id)) {
			String query = "UPDATE team SET Stat1='" + stat1 + "',Stat2='" + stat2 + "',Stat3='" + stat3 + "',Stat4='"
					+ stat4 + "' WHERE Id='" + id + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
			conn.close();
			return "Update complete!";
		}
		return "Update failed!!";

	}

	/*
	 * Избор на отбор от базата, потребителя избира отбор с Id = 0, след което
	 * се записва неговото Id в таблицата на отбора и Id н а отбора в таблица
	 * login заявка -
	 * http://localhost:8080/WebGame/db/pickteam?uid=1&teamid=3&token=
	 * 000000000000&teamname=Barcelona
	 */

	@Path("/pickteam")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String pickTeam(@QueryParam("uid") int uid, @QueryParam("teamid") int teamid,
			@QueryParam("token") String token, @QueryParam("teamname") String team) throws Exception {
		if (checkTocken(token, uid)) {
			String query = "UPDATE team SET UserId = '" + uid + "' WHERE Id = '" + teamid + "'";
			String query2 = "UPDATE login SET Team = '" + team + "' WHERE Id = '" + uid + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
			st = conn.prepareStatement(query2);
			st.execute();
			conn.close();
			return "User picked team - " + team;
		}
		return "error";
	}

	/*
	 * заявка за извеждане на своодните отбори заявка -
	 * http://localhost:8080/WebGame/db/getteams
	 */

	@Path("/getteams")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getAvailableTeams() throws Exception {
		String result = "";
		String query = "SELECT Id, Name, Stat1, Stat2, Stat3, Stat4 FROM team WHERE UserId = '0'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			result += rs.getInt("Id") + "." + rs.getString("Name") + "." + rs.getInt("Stat1") + "." + rs.getInt("Stat2")
					+ "." + rs.getInt("Stat3") + "." + rs.getInt("Stat4") + ";";
		}
		conn.close();
		return result;

	}

	/*
	 * извежда статистика на отбора за изиграни мачове победи, загуби т.н.
	 * подава се id на отбора, връща обект от клас Team и JSON заявка -
	 * http://localhost:8080/WebGame/db/teamstats?id=3
	 */

	@Path("/teamstats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Team getTeamStatistics(@QueryParam("id") int id) throws Exception {
		Team team = new Team();
		String query = "SELECT * FROM team WHERE Id = '" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			team.setId(rs.getInt("Id"));
			team.setName(rs.getString("Name"));
			team.setUid(rs.getInt("UserId"));
			team.setStat1(rs.getInt("Stat1"));
			team.setStat2(rs.getInt("Stat2"));
			team.setStat3(rs.getInt("Stat3"));
			team.setStat4(rs.getInt("Stat4"));
			team.setStat(rs.getInt("ExtraStat"));
			team.setPlayed(rs.getInt("Played"));
			team.setWons(rs.getInt("Wons"));
			team.setDraws(rs.getInt("Drws"));
			team.setLoss(rs.getInt("Loss"));
			team.setGoals(rs.getInt("Goals"));
			team.setPoints(rs.getInt("Points"));
		}
		return team;
	}

	/*
	 * тестов метод ================създава обект от клас User и го връща в JSON
	 */
	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User test() {

		User usr = new User(1, "myname");
		return usr;
	}

}
