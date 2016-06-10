package com.webgame.main;

//импорт на файловете за връзка с базата данни
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
		conn.close();
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
				usr.setTeamId(rs.getInt("Team"));
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
	 * Извежда таблица с класиране на отборите заявка
	 * http://localhost:8080/WebGame/db/getStandingTable/
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
			query = "INSERT INTO `players`(`Name`, `Age`, `S1`, `S2`, `S3`, `S4`, `Tallent`, `PrimePosition`, `Money`) VALUES ('"
					+ player.getName() + "','" + player.getAge() + "','" + player.getS1() + "','" + player.getS2()
					+ "','" + player.getS3() + "','" + player.getS4() + "','" + player.getTallent() + "','"
					+ player.getPrimePosition() + "','" + player.getMoney() + "')";
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
		}
		conn.close();
		return "JOB DONE!";

	}

	/*
	 * админ метод за разпределяне на играчите в отбори,
	 * вратар(2)защитник(5)център(7)нападател(4)- 18 играча заявка
	 * http://localhost:8080/WebGame/db/distributeplayers
	 */
	@Path("/distributeplayers")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String distributePlayers() throws Exception {
		// заявка за извеждане на Id на отборите
		String query1 = "SELECT Id FROM Team";
		String query2;
		String condPos = null;
		String returnlist = null;
		int teamId, playerId, tempNum;
		// списък с Id на отборите
		List<Integer> teamList = new ArrayList<Integer>();
		// временен лист, вкойто се вкарват играчите за разпределение във всеки
		// отбор(18 играча разделени:вратари2, защ5, цент7 нап4)
		List<Integer> tempList = new ArrayList<Integer>();
		// лист със номерата на играчите
		List<Integer> tempNumbersList = new ArrayList<>();

		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		PreparedStatement st2 = null;
		ResultSet rs = st.executeQuery(query1);
		// извеждане на Id на отборите в лист
		while (rs.next()) {
			teamList.add(rs.getInt("Id"));
		}
		returnlist += teamList.toString();
		// разбъркване на елементите на листа за по-изравнен шанс
		Collections.shuffle(teamList);
		// за всеки елемент(отбор) в листа избираме произволни играчи и ги
		// вкарваме в темп лист, след което ъпдейтваме всички с Id на отбора
		for (int i = 0; i < teamList.size(); i++) {
			// задаване на номерата от 2 до 42
			for (int ii = 2; ii < 42; ii++) {
				tempNumbersList.add(ii);
			}
			// разбъркване на листа
			Collections.shuffle(tempNumbersList);
			// ако първият елемент е < 18 и >28 добавяме 1 на 2-ра позиция в
			// листа за да се назначи номер на вратаря
			if (tempNumbersList.get(0) < 18 || tempNumbersList.get(0) > 28) {
				tempNumbersList.add(1, 1);
			}
			// заявка за произволни 2 играч на позиция вратар
			query2 = "SELECT Id FROM players WHERE PrimePosition = '1' AND TeamId = '0' ORDER BY RAND() LIMIT 2";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}
			// заявка за произволни 5 играча за защитник
			query2 = "SELECT Id FROM players WHERE PrimePosition = '2' AND TeamId = '0' ORDER BY RAND() LIMIT 5";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}
			// заявка за произволни 7 играча за център
			query2 = "SELECT Id FROM players WHERE PrimePosition = '3' AND TeamId = '0' ORDER BY RAND() LIMIT 7";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}
			// заявка за произволни 4 играча за нападател
			query2 = "SELECT Id FROM players WHERE PrimePosition = '4' AND TeamId = '0' ORDER BY RAND() LIMIT 4";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}

			returnlist += "</br>otbor " + (i + 1) + "-" + tempList.toString();
			teamId = teamList.get(i);
			// ъпдейт на играчите с Id на отбора, като се назначават и номерата
			// от временния лист и позиции, като първоначалната схема е 4-4-2
			for (int j = 0; j < tempList.size(); j++) {
				playerId = tempList.get(j);
				if (j == 4 || j == 9 || j > 11) {
					switch (j) {
					case 4:
					case 9:
					case 12:
					case 13:
					case 14:
					case 17:
						condPos = "0";
						break;
					case 15:
						condPos = "13";
						break;
					case 16:
						condPos = "15";
						break;
					}
				} else {
					condPos = Integer.toString(j);
				}
				tempNum = tempNumbersList.get(j);
				query2 = "UPDATE players SET TeamId = '" + teamId + "',Position ='" + condPos + "', PlayNumber ='"
						+ tempNum + "' WHERE Id='" + playerId + "'";
				st2 = conn.prepareStatement(query2);
				st2.execute();
			}
			tempList.clear();
		}
		conn.close();
		return returnlist;
	}

	/*
	 * връща обект от клас Player в JSON формат по зададено Id заявка
	 * http://localhost:8080/WebGame/db/getplayer?id=1
	 */
	@Path("/getplayer")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Player getPlayerById(@QueryParam("id") int id) throws Exception {
		String query = "SELECT * FROM players WHERE Id='" + id + "'";
		Player pl = new Player();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			pl.setId(rs.getInt("Id"));
			pl.setName(rs.getString("Name"));
			pl.setAge(rs.getInt("Age"));
			pl.setS1(rs.getInt("S1"));
			pl.setS2(rs.getInt("S2"));
			pl.setS3(rs.getInt("S3"));
			pl.setS4(rs.getInt("S4"));
			pl.setTallent(rs.getInt("Tallent"));
			pl.setTeamId(rs.getInt("TeamId"));
			pl.setPosition(rs.getInt("Position"));
			pl.setCondition(rs.getInt("Condition"));
			pl.setPrimePosition(rs.getInt("PrimePosition"));
			pl.setMoney(rs.getInt("Money"));
			pl.setPlayNumber(rs.getInt("PlayNumber"));
		}
		conn.close();
		return pl;
	}

	/*
	 * групиране на отбора, създава обект от клас Playstyle и го връща в JSON
	 * заявка http://localhost:8080/WebGame/db/groupteam?teamid=4
	 */
	@Path("/groupteam")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Playstyle groupTeam(@QueryParam("teamid") int teamid) throws Exception {
		String query = "SELECT Id,Position FROM players WHERE TeamId = '" + teamid + "'";
		List<Integer> teamListR = new ArrayList<>();
		int df = 0;
		int md = 0;
		int fw = 0;
		Playstyle pl = new Playstyle();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			switch (rs.getInt("Position")) {
			case 0:
				teamListR.add(rs.getInt("Id"));
				break;
			case 1:
				pl.setGk(getPlayerById(rs.getInt("Id")));
				break;
			case 2:
				pl.setDf1(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 3:
				pl.setDf2(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 4:
				pl.setDf3(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 5:
				pl.setDf4(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 6:
				pl.setDf5(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 7:
				pl.setMd1(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 8:
				pl.setMd2(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 9:
				pl.setMd3(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 10:
				pl.setMd4(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 11:
				pl.setMd5(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 12:
				pl.setFw1(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 13:
				pl.setFw2(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 14:
				pl.setFw3(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 15:
				pl.setFw4(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 16:
				pl.setFw5(getPlayerById(rs.getInt("Id")));
				fw++;
				break;

			}

			// teamList.add(rs.getInt("Id"));
		}
		pl.setR1(getPlayerById(teamListR.get(0)));
		pl.setR2(getPlayerById(teamListR.get(1)));
		pl.setR3(getPlayerById(teamListR.get(2)));
		pl.setR4(getPlayerById(teamListR.get(3)));
		pl.setR5(getPlayerById(teamListR.get(4)));
		pl.setR6(getPlayerById(teamListR.get(5)));
		pl.setR7(getPlayerById(teamListR.get(6)));
		pl.setDf(df);
		pl.setMd(md);
		pl.setFw(fw);
		conn.close();
		return pl;
	}

	/*
	 * генериране на програма по система всеки-срещу-всеки с разменено
	 * гостуване, стартира се еднократно при започване на сезона заявка:
	 * http://localhost:8080/WebGame/db/createrounds
	 */
	@Path("/createrounds")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String createrounds() throws Exception {
		String result = "", query = "SELECT * FROM team";
		String[] s1, s2;
		String tVal = null;
		int i = 0, j = 0, kr = 1;
		List<String> allPairList = new ArrayList<String>();
		List<String> roundsList = new ArrayList<String>();
		List<String> singleRoundList = new ArrayList<String>();
		List<String> tList = new ArrayList<String>();// za otborite ot bazata
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			tList.add(rs.getString("Name"));
		}

		for (i = 0; i < tList.size(); i++) {
			for (j = i; j < tList.size(); j++) {
				if (i != j) {
					allPairList.add(tList.get(i) + ":" + tList.get(j));
				}
			}

		}

		while (!allPairList.isEmpty()) {
			singleRoundList.add(allPairList.get(0));
			for (String elm : allPairList) {
				s1 = elm.split(":");
				for (String elm2 : singleRoundList) {
					s2 = elm2.split(":");
					if (s1[0].equals(s2[0]) || s1[1].equals(s2[1]) || s1[0].equals(s2[1]) || s1[1].equals(s2[0])) {
						tVal = "";
						break;
					} else {
						tVal = elm;
					}

				}
				if (!tVal.equals("")) {
					singleRoundList.add(tVal);
				}
			}
			for (String elmnt : singleRoundList) {
				allPairList.remove(elmnt);
			}
			roundsList.add(singleRoundList.toString());
			singleRoundList.clear();

		}
		Collections.shuffle(roundsList);

		for (String lst : roundsList) {
			result += "============== Кръг " + kr + " =====================</br>";
			lst = lst.replace("[", "");
			lst = lst.replace("]", "");
			s1 = lst.split(",");
			for (i = 0; i < s1.length; i++) {
				s2 = s1[i].split(":");
				result += s1[i] + "</br>";
				query = "INSERT INTO game (GameRound,Team1,Team2) VALUES ('" + kr + "','" + s2[0].trim() + "','"
						+ s2[1].trim() + "')";
				PreparedStatement st2 = conn.prepareStatement(query);
				st2.execute();
			}
			kr++;
		}
		for (String lst : roundsList) {
			result += "============== Кръг " + kr + " =====================</br>";
			lst = lst.replace("[", "");
			lst = lst.replace("]", "");
			s1 = lst.split(",");
			for (i = 0; i < s1.length; i++) {
				s2 = s1[i].split(":");
				result += s1[i] + "</br>";
				query = "INSERT INTO game (GameRound,Team1,Team2) VALUES ('" + kr + "','" + s2[1].trim() + "','"
						+ s2[0].trim() + "')";
				PreparedStatement st3 = conn.prepareStatement(query);
				st3.execute();
			}
			kr++;
		}

		conn.close();
		return result;
	}

	/*
	 * 
	 */

	public Playstyle groupTeamForGame(int teamid) throws Exception {
		String query = "SELECT * FROM players WHERE TeamId = '" + teamid + "'";
		List<Integer> teamListR = new ArrayList<>();
		int df = 0;
		int md = 0;
		int fw = 0;
		Integer attack = 0, defence = 0, speed = 0, technic = 0, condition = 0;
		Playstyle pl = new Playstyle();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			switch (rs.getInt("Position")) {
			case 0:
				teamListR.add(rs.getInt("Id"));
				break;
			case 1:
				pl.setGk(getPlayerById(rs.getInt("Id")));
				attack += pl.getGk().getS1();
				defence += pl.getGk().getS2();
				speed += pl.getGk().getS3();
				technic += pl.getGk().getS4();
				condition += pl.getGk().getCondition();
				break;
			case 2:
				pl.setDf1(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf1().getS1();
				defence += pl.getDf1().getS2();
				speed += pl.getDf1().getS3();
				technic += pl.getDf1().getS4();
				condition += pl.getDf1().getCondition();
				break;
			case 3:
				pl.setDf2(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf2().getS1();
				defence += pl.getDf2().getS2();
				speed += pl.getDf2().getS3();
				technic += pl.getDf2().getS4();
				condition += pl.getDf2().getCondition();
				break;
			case 4:
				pl.setDf3(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf3().getS1();
				defence += pl.getDf3().getS2();
				speed += pl.getDf3().getS3();
				technic += pl.getDf3().getS4();
				condition += pl.getDf3().getCondition();
				break;
			case 5:
				pl.setDf4(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf4().getS1();
				defence += pl.getDf4().getS2();
				speed += pl.getDf4().getS3();
				technic += pl.getDf4().getS4();
				condition += pl.getDf4().getCondition();
				break;
			case 6:
				pl.setDf5(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf5().getS1();
				defence += pl.getDf5().getS2();
				speed += pl.getDf5().getS3();
				technic += pl.getDf5().getS4();
				condition += pl.getDf5().getCondition();
				break;
			case 7:
				pl.setMd1(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd1().getS1();
				defence += pl.getMd1().getS2();
				speed += pl.getMd1().getS3();
				technic += pl.getMd1().getS4();
				condition += pl.getMd1().getCondition();
				break;
			case 8:
				pl.setMd2(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd2().getS1();
				defence += pl.getMd2().getS2();
				speed += pl.getMd2().getS3();
				technic += pl.getMd2().getS4();
				condition += pl.getMd2().getCondition();
				break;
			case 9:
				pl.setMd3(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd3().getS1();
				defence += pl.getMd3().getS2();
				speed += pl.getMd3().getS3();
				technic += pl.getMd3().getS4();
				condition += pl.getMd3().getCondition();
				break;
			case 10:
				pl.setMd4(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd4().getS1();
				defence += pl.getMd4().getS2();
				speed += pl.getMd4().getS3();
				technic += pl.getMd4().getS4();
				condition += pl.getMd4().getCondition();
				break;
			case 11:
				pl.setMd5(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd5().getS1();
				defence += pl.getMd5().getS2();
				speed += pl.getMd5().getS3();
				technic += pl.getMd5().getS4();
				condition += pl.getMd5().getCondition();
				break;
			case 12:
				pl.setFw1(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw1().getS1();
				defence += pl.getFw1().getS2();
				speed += pl.getFw1().getS3();
				technic += pl.getFw1().getS4();
				condition += pl.getFw1().getCondition();
				break;
			case 13:
				pl.setFw2(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw2().getS1();
				defence += pl.getFw2().getS2();
				speed += pl.getFw2().getS3();
				technic += pl.getFw2().getS4();
				condition += pl.getFw2().getCondition();
				break;
			case 14:
				pl.setFw3(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw3().getS1();
				defence += pl.getFw3().getS2();
				speed += pl.getFw3().getS3();
				technic += pl.getFw3().getS4();
				condition += pl.getFw3().getCondition();
				break;
			case 15:
				pl.setFw4(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw4().getS1();
				defence += pl.getFw4().getS2();
				speed += pl.getFw4().getS3();
				technic += pl.getFw4().getS4();
				condition += pl.getFw4().getCondition();
				break;
			case 16:
				pl.setFw5(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw5().getS1();
				defence += pl.getFw5().getS2();
				speed += pl.getFw5().getS3();
				technic += pl.getFw5().getS4();
				condition += pl.getFw5().getCondition();
				break;

			}

			// teamList.add(rs.getInt("Id"));
		}
		pl.setR1(getPlayerById(teamListR.get(0)));
		pl.setR2(getPlayerById(teamListR.get(1)));
		pl.setR3(getPlayerById(teamListR.get(2)));
		pl.setR4(getPlayerById(teamListR.get(3)));
		pl.setR5(getPlayerById(teamListR.get(4)));
		pl.setR6(getPlayerById(teamListR.get(5)));
		pl.setR7(getPlayerById(teamListR.get(6)));
		pl.setDf(df);
		pl.setMd(md);
		pl.setFw(fw);
		query = "SELECT Name FROM team WHERE Id = '" + teamid + "'";
		st = conn.createStatement();
		rs = st.executeQuery(query);
		if (rs.next()) {
			pl.setName(rs.getString("Name"));
		}
		pl.setAttack(attack);
		pl.setDefence(defence);
		pl.setSpeed(speed);
		pl.setTechnic(technic);
		pl.setCondition(condition);
		conn.close();
		return pl;
	}

	/*
	 * тестов метод за мач
	 */
	@Path("/playgame")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String playGame() throws Exception {
		Random r = new Random();
		Status status = new Status();
		String query = "", result = "";
		Integer tVal1, tVal2, attDirection, ballPosition, flang, tVal3, tVal4, tVal5, tVal6, gA = 0, gB = 0, a = 0,
				b = 0;
		Integer checker;
		Game game = new Game();
		query = "SELECT CurrentRound FROM status";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st2;
		PreparedStatement st3;
		ResultSet rs2, rs3;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			status.setRound(rs.getInt(1));
		}

		query = "SELECT * FROM game WHERE GameRound = '" + status.getRound() + "'";
		st = conn.createStatement();
		rs = st.executeQuery(query);
		while (rs.next()) {
			tVal1 = 0;
			tVal2 = 0;
			attDirection = 0;
			checker = 0;
			ballPosition = 0;
			flang = 0;
			tVal3 = 0;
			tVal4 = 0;
			tVal5 = 0;
			tVal6 = 0;
			gA = 0;
			gB = 0;
			query = "SELECT * FROM team WHERE Name ='" + rs.getString("Team1") + "'";
			st2 = conn.createStatement();
			rs2 = st2.executeQuery(query);
			if (rs2.next()) {
				game.setTeamA(groupTeamForGame(rs2.getInt("Id")));// id na
																	// otbora
			}
			query = "SELECT * FROM team WHERE Name ='" + rs.getString("Team2") + "'";
			st2 = conn.createStatement();
			rs2 = st2.executeQuery(query);
			if (rs2.next()) {
				game.setTeamB(groupTeamForGame(rs2.getInt("Id")));// id na
																	// otbora
			}

			// общ коефициент на отбора/сбор от 4 показатели на всички играчи
			tVal1 = game.getTeamA().getAttack() + game.getTeamA().getDefence() + game.getTeamA().getSpeed()
					+ game.getTeamA().getTechnic();
			tVal2 = game.getTeamB().getAttack() + game.getTeamB().getDefence() + game.getTeamB().getSpeed()
					+ game.getTeamB().getTechnic();

			ballPosition = 2;
			flang = 2;
			attDirection = r.nextInt(2) + 1;
			for (int i = 0; i < 90; i++) {
				switch (attDirection) {
				case 1:
					switch (ballPosition) {
					case 1:
						checker = dfVsFw(game.getTeamA(), game.getTeamB());
						if (checker > 50) {
							ballPosition = 2;
							attDirection = 1;
						} else {
							ballPosition = 1;
							attDirection = 2;
						}
						break;
					case 2:
						checker = mdfVsMd(game.getTeamA(), game.getTeamB());
						if (checker > 50) {
							ballPosition = 3;
							attDirection = 1;
						} else {
							ballPosition = 2;
							attDirection = 2;
						}
						break;
					case 3:
						checker = dfVsFw(game.getTeamA(), game.getTeamB());
						if (checker < 50) {
							ballPosition = 3;
							if (r.nextInt(100) > 90) {
								gA++;
							}
							attDirection = 2;
						}
						break;
					}// krai na switch ballPosition
					break;
				case 2:
					switch (ballPosition) {
					case 1:
						checker = dfVsFw(game.getTeamB(), game.getTeamA());
						if (checker < 50) {
							ballPosition = 1;
							if (r.nextInt(100) > 90) {
								gB++;
							}
							attDirection = 1;
						}
						break;
					case 2:
						checker = mdfVsMd(game.getTeamB(), game.getTeamA());
						if (checker < 50) {
							ballPosition = 1;
							attDirection = 2;
						} else {
							ballPosition = 2;
							attDirection = 1;
						}
						break;
					case 3:
						checker = dfVsFw(game.getTeamB(), game.getTeamA());
						if (checker < 50) {
							ballPosition = 2;
							attDirection = 2;
						} else {
							ballPosition = 3;
							attDirection = 1;

						}
						break;
					}

					break;
				}// krai na attdirection
			} // krai na cikyl 180
			result += game.getTeamA().getName() + " " + gA.toString() + " : " + gB.toString() + " "
					+ game.getTeamB().getName() + "</br>";
			// ъпдейт на резултата в таблица
			query = "UPDATE game SET Results = '" + gA.toString() + ":" + gB.toString() + "'WHERE Team1 ='"
					+ game.getTeamA().getName() + "' AND Team2 = '" + game.getTeamB().getName() + "'";
			st3 = conn.prepareStatement(query);
			st3.execute();
			// update na team za klasiraneto
			if (gA > gB) {
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamA().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Wons") + 1;
					tVal3 = rs2.getInt("Points") + 3;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Wons = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamA().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamB().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Loss") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Loss = '" + tVal2.toString()
							+ "' WHERE Name = '" + game.getTeamB().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}

			}
			if (gA < gB) {
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamB().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Wons") + 1;
					tVal3 = rs2.getInt("Points") + 3;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Wons = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamB().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamA().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Loss") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Loss = '" + tVal2.toString()
							+ "' WHERE Name = '" + game.getTeamA().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}

			}
			if (gA.equals(gB)) {
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamB().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Drws") + 1;
					tVal3 = rs2.getInt("Points") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Drws = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamB().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamA().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Drws") + 1;
					tVal3 = rs2.getInt("Points") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Drws = '" + tVal2.toString()
					+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamA().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}

			}
			

		}
		tVal1 = status.getRound() + 1;
		query = "UPDATE status SET CurrentRound = '" + tVal1.toString() + "'";
		st3 = conn.prepareStatement(query);
		st3.execute();

		return result;
	}

	private int dfVsFw(Playstyle plA, Playstyle plB) {
		Integer tVal1, tVal2, tVal3, tVal4;
		Double d;
		Random r = new Random();
		tVal1 = 1;
		tVal2 = 1;
		tVal3 = 1;
		tVal4 = 1;
		if (plA.getDf1() != null) {
			tVal1 = +plA.getDf1().getS2() + plA.getDf1().getS3();
		}
		if (plA.getDf2() != null) {
			tVal1 = +plA.getDf2().getS2() + plA.getDf2().getS3();
		}
		if (plA.getDf3() != null) {
			tVal1 = +plA.getDf3().getS2() + plA.getDf3().getS3();
		}
		if (plA.getDf4() != null) {
			tVal1 = +plA.getDf4().getS2() + plA.getDf4().getS3();
		}
		if (plA.getDf5() != null) {
			tVal1 = +plA.getDf5().getS2() + plA.getDf5().getS3();
		}
		if (plB.getFw1() != null) {
			tVal2 = +plB.getFw1().getS1() + plB.getFw1().getS3();
		}
		if (plB.getFw2() != null) {
			tVal2 = +plB.getFw2().getS1() + plB.getFw2().getS3();
		}
		if (plB.getFw3() != null) {
			tVal2 = +plB.getFw3().getS1() + plB.getFw3().getS3();
		}
		if (plB.getFw4() != null) {
			tVal2 = +plB.getFw4().getS1() + plB.getFw4().getS3();
		}
		if (plB.getFw5() != null) {
			tVal2 = +plB.getFw5().getS1() + plB.getFw5().getS3();
		}
		d = tVal1 / (double) tVal2;
		return r.nextInt((int) Math.ceil(d * 100));

	}

	private int mdfVsMd(Playstyle plA, Playstyle plB) {
		Integer tVal1, tVal2, tVal3, tVal4;
		Double d;
		Random r = new Random();
		tVal1 = 1;
		tVal2 = 1;
		tVal3 = 1;
		tVal4 = 1;
		if (plA.getMd1() != null) {
			tVal1 = +plA.getMd1().getS1() + plA.getMd1().getS3() + plA.getMd1().getS4();
		}
		if (plA.getMd2() != null) {
			tVal1 = +plA.getMd2().getS1() + plA.getMd2().getS3() + plA.getMd2().getS4();
		}
		if (plA.getMd3() != null) {
			tVal1 = +plA.getMd3().getS1() + plA.getMd3().getS3() + plA.getMd3().getS4();
		}

		if (plA.getMd4() != null) {
			tVal1 = +plA.getMd4().getS1() + plA.getMd4().getS3() + plA.getMd4().getS4();
		}
		if (plA.getMd5() != null) {
			tVal1 = +plA.getMd5().getS1() + plA.getMd5().getS3() + plA.getMd5().getS4();
		}

		if (plB.getMd1() != null) {
			tVal2 = +plB.getMd1().getS1() + plB.getMd1().getS3() + plB.getMd1().getS4();
		}
		if (plB.getMd2() != null) {
			tVal2 = +plB.getMd2().getS1() + plB.getMd2().getS3() + plB.getMd2().getS4();
		}
		if (plB.getMd3() != null) {
			tVal2 = +plB.getMd3().getS1() + plB.getMd3().getS3() + plB.getMd3().getS4();
		}
		if (plB.getMd4() != null) {
			tVal2 = +plB.getMd4().getS1() + plB.getMd4().getS3() + plB.getMd4().getS4();
		}
		if (plB.getMd5() != null) {
			tVal2 = +plB.getMd5().getS1() + plB.getMd5().getS3() + plB.getMd5().getS4();
		}

		d = tVal1 / (double) tVal2;
		return r.nextInt((int) Math.ceil(d * 100));
	}

	/*
	 * тестов метод ================
	 */
	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Playstyle test() {
		Player pl = new Player(1, "Novo ime ooD");
		Playstyle plstl = new Playstyle();
		plstl.setGk(pl);
		plstl.setDf1(pl);
		return plstl;
	}
}
