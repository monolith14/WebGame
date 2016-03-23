package com.webgame.main;

//импорт на файловете за връзка с базата данни
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//импорт на джърси библиотеките
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.ParseConversionEvent;

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
	private String changePassword(String passwordOld, String passwordNew, int id) throws Exception {
		String query = "UPDATE login SET Password = '" + passwordNew + "' WHERE Id = '" + id + "' AND Password = '"
				+ passwordOld + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		int upd = st.executeUpdate();
		if (upd == 0) {
			conn.close();
			return "Update error!";
		}
		conn.close();
		return "Паролата е променена!";

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
	 * генериране на име от базата данни, private метод достъпва се от
	 * генератора на играчи
	 */
	private String generatePlayerName() throws Exception {
		String name = "";
		String query1 = "SELECT Name FROM firstname ORDER BY RAND() LIMIT 1";
		String query2 = "SELECT Name FROM lastname ORDER BY RAND() LIMIT 1";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query1);
		if (rs.next()) {
			name += rs.getString("Name") + " ";
		}
		rs = st.executeQuery(query2);
		if (rs.next()) {
			name += rs.getString("Name");
		}

		return name;
	}

	/*
	 * регистрация на нов потребител достъпва се на адрес
	 * http://localhost:8080/webgame/db/register?username=myusername&p1=password
	 * &p2=password&name=myname проверка на паролите дали съвпадат/валидацията
	 * по-добре да се прави с jquery/ ???? да се криптира паролата с md5????
	 * заявка -
	 * http://localhost:8080/WebGame/db/register?username=myname2&password=2&
	 * name =myusername&mail=mymail
	 */
	@Path("/register")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String registerNewUser(@QueryParam("username") String username, @QueryParam("password") String password,
			@QueryParam("mail") String mail, @QueryParam("name") String name) throws Exception {

		if (username.equals("") || mail.equals("") || name.equals("")) {
			return "Check data fields!";
		}
		if (checkUserExist(username, "")) {
			return "User exist!";
		}
		User usr = new User();
		usr.setName(name);
		usr.setUsername(username);
		usr.setPassword(password);
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
		User usr = new User();
		if (checkUserExist(username, password)) {
			String query = "SELECT login.*, team.Name as tTeam FROM login LEFT JOIN team ON login.Team = team.Id WHERE Username = '"
					+ username + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			String teamName = "0";
			if (rs.next()) {
				usr.setId(rs.getInt("Id"));
				usr.setName(rs.getString("Nickname"));
				usr.setMail(rs.getString("Mail"));
				usr.setUsername(rs.getString("Username"));
				usr.setPassword(rs.getString("Password"));
				usr.setStatus(rs.getString("Status"));
				if (rs.getString("tTeam") != null) {
					teamName = rs.getString("tTeam");
				}
				usr.setTeam(teamName);
				usr.setToken(generateToken());
			}
			updateToken(usr.getId(), usr.getToken());

		}
		return usr;
	}

	/*
	 * промяна на потребителска парола заявка -
	 * http://localhost:8080/WebGame/db/chpass?passwordOld=m22&passwordNew=
	 * asdasd&id=1
	 */
	@Path("/chpass")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String changeUserPassword(@QueryParam("passwordOld") String passwordOld,
			@QueryParam("passwordNew") String passwordNew, @QueryParam("id") int id) throws Exception {

		return changePassword(passwordOld, passwordNew, id);
	}

	/*
	 * прочита и връща показателите на отбора по зададено id заявка -
	 * http://localhost:8080/WebGame/db/getpt?team=Пирин
	 */
	@Path("/getpt")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPoints(@QueryParam("team") String team) throws Exception {
		String result = null;
		String query = "SELECT * FROM team WHERE Name = '" + team + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		List<String> statList = new ArrayList<String>();
		if (rs.next()) {
			result = rs.getInt("Stat1") + "|" + rs.getInt("Stat2") + "|" + rs.getInt("Stat3") + "|" + rs.getInt("Stat4")
					+ "|" + rs.getInt("ExtraStat");
			statList.add(result);
		}
		return statList.toString();
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
			String query2 = "UPDATE login SET Team = '" + teamid + "' WHERE Id = '" + uid + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
			st = conn.prepareStatement(query2);
			st.execute();
			conn.close();
			return "Избран клуб - " + team;
		}
		return "error";
	}

	/*
	 * заявка за извеждане на своодните отбори заявка -
	 * http://localhost:8080/WebGame/db/getteams
	 */

	@Path("/getteams")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAvailableTeams() throws Exception {

		String result = "";
		String query = "SELECT * FROM team WHERE UserId = '0'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		List<String> teamList = new ArrayList<String>();

		while (rs.next()) {
			result = rs.getInt("Id") + "|" + rs.getString("Name") + "|" + rs.getInt("Stat1") + "|" + rs.getInt("Stat2")
					+ "|" + rs.getInt("Stat3") + "|" + rs.getInt("Stat4");
			teamList.add(result);
		}
		conn.close();
		return teamList.toString();

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
			team.setExtra(rs.getInt("ExtraStat"));
			team.setPlayed(rs.getInt("Played"));
			team.setWons(rs.getInt("Wons"));
			team.setDraws(rs.getInt("Drws"));
			team.setLoss(rs.getInt("Loss"));
			team.setGoals(rs.getInt("Goals"));
			team.setPoints(rs.getInt("Points"));
			team.setMoney(rs.getInt("Money"));
		}
		return team;
	}

	/*
	 * Извежда таблица с класиране на отборите
	 */
	@Path("/getStandingTable")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getStatndingTable() throws Exception {
		String result = "";
		String query = "SELECT team.*, login.Nickname AS LName FROM team LEFT JOIN login ON team.UserId = login.Id ORDER BY Points DESC ,Goals DESC, Name ASC";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		List<String> teamList = new ArrayList<String>();

		while (rs.next()) {
			result = rs.getString("Name") + "|" + rs.getInt("Played") + "|" + rs.getInt("Wons") + "|"
					+ rs.getInt("Drws") + "|" + rs.getInt("Loss") + "|" + rs.getInt("Goals") + "|" + rs.getInt("Points")
					+ "|" + rs.getString("LName");
			teamList.add(result);
		}
		conn.close();
		return teamList.toString();
	}

	/*
	 * admin метод стартира се в началото на играта генерира играч 1-генерира
	 * име 2-генериране на играч от класа Player 3-записва резултата в базата
	 * заявка http://localhost:8080/WebGame/db/createplayer?qty=5
	 */
	@Path("/createplayer")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String createPlayer(@QueryParam("qty") int qty) throws ClassNotFoundException, Exception {
		String query = "";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		int tmpIntValue, tmpOAll;
		int tmpA = 0;
		int tmpD = 0;
		int tmpT = 0;
		int tmpS = 0;
		Random r = new Random();
		Player player = new Player();
		for (int i = 0; i < qty; i++) {
			player.setName(generatePlayerName());
			// генериране на age 19 - 33, ако е над 24 вадим 4 за да се
			// генерират по-млади играчи
			tmpIntValue = 19 + r.nextInt(14);
			if (tmpIntValue > 24) {
				tmpIntValue -= 4;
			}
			player.setAge(tmpIntValue);
			// задаване на ОА точките в зависимост от age
			tmpOAll = tmpIntValue + 3;
			if (tmpIntValue > 20 && tmpIntValue <= 26) {
				tmpOAll += 6;
			} else if (tmpIntValue > 26) {
				tmpOAll += 4;
			}
			// генериране на позиция
			// 0-12 вратар, 13-38 защ., 39-80център, 81-99 нап.
			tmpIntValue = r.nextInt(100);
			if (tmpIntValue < 12) {
				player.setPrimePosition(1);
			} else if (tmpIntValue > 12 && tmpIntValue <= 38) {
				player.setPrimePosition(2);
			} else if (tmpIntValue > 38 && tmpIntValue <= 80) {
				player.setPrimePosition(3);
			} else if (tmpIntValue > 80) {
				player.setPrimePosition(4);
			}
			// генериране на показателите
			// 19-22(19-22),23-26(23-26(+6),26-33(26-33(+4)),
			switch (player.getPrimePosition()) {
			case 1:
				tmpA = (int) Math.round(tmpOAll * 0.05);
				tmpD = (int) Math.round(tmpOAll * 0.7);
				tmpS = r.nextInt((int) Math.round(1 + (tmpOAll * 0.24)));
				tmpT = tmpOAll - (tmpA + tmpS + tmpD) + 1;
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			case 2:
				tmpA = (int) Math.round(tmpOAll * 0.1);
				tmpD = (int) Math.round(tmpOAll * 0.6);
				tmpS = r.nextInt((int) Math.round(1 + (tmpOAll * 0.29)));
				tmpT = tmpOAll - (tmpA + tmpS + tmpD) + 1;
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			case 3:
				List<Integer> lst = new ArrayList<>();
				lst.add(1);
				lst.add(2);
				lst.add(3);
				lst.add(4);
				Collections.shuffle(lst);
				// проверка 1 елемент
				switch (lst.get(0)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.5);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.5);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.5);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.5);
					break;
				}
				// проверка на втори елемент
				switch (lst.get(1)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.35);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.35);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.35);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.35);
					break;
				}
				// проверка 3 елемент
				switch (lst.get(2)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.1);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.1);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.1);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.1);
					break;
				}
				// проверка четвърти елемнт
				switch (lst.get(3)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.05);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.05);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.05);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.05);
					break;
				}
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			case 4:
				tmpA = (int) Math.round(tmpOAll * 0.7);
				tmpD = (int) Math.round(tmpOAll * 0.05);
				tmpS = r.nextInt((int) Math.round(1 + (tmpOAll * 0.23)));
				tmpT = tmpOAll - (tmpA + tmpS + tmpD) + 1;
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			}
			// талант на играча, в % да се има напредвид при повишаване на
			// показателите
			player.setTallent(1 + r.nextInt(10));
			// цена на играча - база 100 000*(талант*коеф за възраст)%
			switch (player.getAge()) {
			case 19:
				player.setMoney((int) (100000 * (player.getTallent() * 0.30)));
				break;
			case 20:
				player.setMoney((int) (100000 * (player.getTallent() * 0.28)));
				break;
			case 21:
				player.setMoney((int) (100000 * (player.getTallent() * 0.26)));
				break;
			case 22:
				player.setMoney((int) (100000 * (player.getTallent() * 0.24)));
				break;
			case 23:
				player.setMoney((int) (100000 * (player.getTallent() * 0.22)));
				break;
			case 24:
				player.setMoney((int) (100000 * (player.getTallent() * 0.20)));
				break;
			case 25:
				player.setMoney((int) (100000 * (player.getTallent() * 0.16)));
				break;
			case 26:
				player.setMoney((int) (100000 * (player.getTallent() * 0.12)));
				break;
			case 27:
				player.setMoney((int) (100000 * (player.getTallent() * 0.10)));
				break;
			default:
				player.setMoney((int) (100000 * (player.getTallent() * 0.08)));
				break;
			}

			// insert v bazata
			query = "INSERT INTO `players`(`Name`, `Age`, `s1`, `s2`, `s3`, `s4`, `Tallent`, `PrimePosition`, `Money`) VALUES ('"
					+ player.getName() + "','" + player.getAge() + "','" + player.getS1() + "','" + player.getS2()
					+ "','" + player.getS3() + "','" + player.getS4() + "','" + player.getTallent() + "','"
					+ player.getPrimePosition() + "','" + player.getMoney() + "')";
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
		}
		conn.close();
		return "Insert ok";

		// return " Godini:" + player.getAge() + ";Poziciq:" +
		// player.getPrimePosition() + "; Ataka: " + player.getS1()
		// + ";Zashtita: " + player.getS2() + ";Skorost: " + player.getS3() +
		// ";Tehnika: " + player.getS4()
		// + ";talant: " + player.getTallent() + ";money: " + player.getMoney();
	}

	/*
	 * тестов метод ================
	 */
	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User[] test() {
		User usr = new User(1, "myname");
		User usr2 = new User(2, "myname2");
		User retval[] = new User[] { usr, usr2 };
		return retval;
	}
}
