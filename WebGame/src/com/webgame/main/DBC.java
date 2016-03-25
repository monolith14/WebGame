package com.webgame.main;

//������ �� ��������� �� ������ � ������ �����
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//������ �� ������ ������������
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.ParseConversionEvent;

//��� �� URL �� ������ �� ��� �������  http://localhost:8080/webgame/db/...
//�� ����� ����� �� ������ ���� �������� � @Path(/mymethod) � �� �������� ����  http://localhost:8080/webgame/db/mymethod/
@Path("/db")

public class DBC {
	// ����� �� �������� � ������
	private String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost/webgame?useUnicode=true&characterEncoding=utf-8";
	private static String dbusername = "root";
	private static String dbpassword = "123";

	/*
	 * �������� �� ������������ ������������� ��� � ������ � ���������� �� ����
	 * ����� �� ���������/��� �������� � ������ ���� �� ��������� ���� ��
	 * ����������� ����������/, �������� �� ��� ������������ �� ��� ������ �����
	 * true/false , ��� ����������� �� ��������� ���� �� ����. ���, � ��� login
	 * �� ��������� � ������� �������� ����� ���� �� �����
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
	 * ������� �� �������� �� ����������, ����������� �� ����� � jquery � ��
	 * ������ ������ ������ ������ � id �� ���������� �������� ����� ���� ��
	 * �����
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
		return "�������� � ���������!";

	}

	/*
	 * �������� �� ��������� �� ����� �� ������ ����� ������� ���� �� �����
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
	 * update �� ����� ��� login ������� ���� �� ����� � 2 �������� ����������
	 * id � token
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
	 * ���������� �� ����� �� login
	 */

	private String generateToken() {
		Random r = new Random();
		String token = "";
		Integer xx = 1;
		for (int i = 0; i < 20; i++) {
			xx = r.nextInt(3) + 1;
			if (xx.equals(1)) {
				token += (char) (48 + r.nextInt(10));// ����� 1-0
			} else if (xx.equals(2)) {
				token += (char) (65 + r.nextInt(26));// ����� ������
			} else if (xx.equals(3)) {
				token += (char) (97 + r.nextInt(26));// ����� �����
			}

		}

		return token;// return User data??????????????????????????
	}

	/*
	 * ���������� �� ��� �� ������ �����, private ����� �������� �� ��
	 * ���������� �� ������
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
	 * ����������� �� ��� ���������� �������� �� �� �����
	 * http://localhost:8080/webgame/db/register?username=myusername&p1=password
	 * &p2=password&name=myname �������� �� �������� ���� ��������/�����������
	 * ��-����� �� �� ����� � jquery/ ???? �� �� �������� �������� � md5????
	 * ������ -
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
		return "������� ����������: " + usr.getUsername() + " !";
	}

	/*
	 * ���� � ���������, ��������� �� �� ������� ���������� � ������, ��� ��
	 * ����� true �� �������� �����, ������� �� � ������ � �� ����� � ����,
	 * ����� ����� �� ���� User ������
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
	 * ������� �� ������������� ������ ������ -
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
	 * ������� � ����� ������������ �� ������ �� �������� id ������ -
	 * http://localhost:8080/WebGame/db/getpt?team=�����
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
	 * ����� � ������ �� ������������ �� ������ ���� ���� ����������� � ��������
	 * �������, ������� �� 4-�� ����������,�������������� �����, ������ ��
	 * ��������� � id-�� �� ������ ������ :
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
	 * ����� �� ����� �� ������, ����������� ������ ����� � Id = 0, ���� �����
	 * �� ������� �������� Id � ��������� �� ������ � Id � � ������ � �������
	 * login ������ -
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
			return "������ ���� - " + team;
		}
		return "error";
	}

	/*
	 * ������ �� ��������� �� ��������� ������ ������ -
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
	 * ������� ���������� �� ������ �� �������� ������ ������, ������ �.�.
	 * ������ �� id �� ������, ����� ����� �� ���� Team � JSON ������ -
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
	 * ������� ������� � ��������� �� �������� ������
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
	 * admin ����� �������� �� � �������� �� ������ �������� ����� 1-��������
	 * ��� 2-���������� �� ����� �� ����� Player 3-������� ��������� � ������
	 * ������ http://localhost:8080/WebGame/db/createplayer?qty=5
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
			// ���������� �� age 19 - 33, ��� � ��� 24 ����� 4 �� �� ��
			// ��������� ��-����� ������
			tmpIntValue = 19 + r.nextInt(14);
			if (tmpIntValue > 24) {
				tmpIntValue -= 4;
			}
			player.setAge(tmpIntValue);
			// �������� �� �� ������� � ���������� �� age
			tmpOAll = tmpIntValue + 3;
			if (tmpIntValue > 20 && tmpIntValue <= 26) {
				tmpOAll += 6;
			} else if (tmpIntValue > 26) {
				tmpOAll += 4;
			}
			// ���������� �� �������
			// 0-12 ������, 13-38 ���., 39-80������, 81-99 ���.
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
			// ���������� �� ������������
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
				// �������� 1 �������
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
				// �������� �� ����� �������
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
				// �������� 3 �������
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
				// �������� �������� ������
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
			// ������ �� ������, � % �� �� ��� ��������� ��� ���������� ��
			// ������������
			player.setTallent(1 + r.nextInt(10));
			// ���� �� ������ - ���� 100 000*(������*���� �� �������)%
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
	 * ����� ����� �� ������������ �� �������� � ������,
	 * ������(2)��������(5)������(7)���������(4)- 18 ������ ������
	 * http://localhost:8080/WebGame/db/distributeplayers
	 */
	@Path("/distributeplayers")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String distributePlayers() throws Exception {
		// ������ �� ��������� �� Id �� ��������
		String query1 = "SELECT Id FROM Team";
		String query2, condPos;
		String returnlist = null;
		int teamId, playerId, tempNum;
		// ������ � Id �� ��������
		List<Integer> teamList = new ArrayList<Integer>();
		// �������� ����, ������ �� ������� �������� �� ������������� ��� �����
		// �����(18 ������ ���������:�������2, ���5, ����7 ���4)
		List<Integer> tempList = new ArrayList<Integer>();
		// ���� ��� �������� �� ��������
		List<Integer> tempNumbersList = new ArrayList<>();

		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		PreparedStatement st2 = null;
		ResultSet rs = st.executeQuery(query1);
		// ��������� �� Id �� �������� � ����
		while (rs.next()) {
			teamList.add(rs.getInt("Id"));
		}
		returnlist += teamList.toString();
		// ����������� �� ���������� �� ����� �� ��-�������� ����
		Collections.shuffle(teamList);
		// �� ����� �������(�����) � ����� �������� ���������� ������ � ��
		// �������� � ���� ����, ���� ����� ���������� ������ � Id �� ������
		for (int i = 0; i < teamList.size(); i++) {
			// �������� �� �������� �� 2 �� 42
			tempNumbersList = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
					23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42);
			// ����������� �� �����
			Collections.shuffle(tempNumbersList);
			// ��� ������� ������� � < 18 � >28 �������� 1 �� 2-�� ������� �
			// ����� �� �� �� ������� ����� �� �������
			if (tempNumbersList.get(0) < 18 || tempNumbersList.get(0) > 28) {
				tempNumbersList.add(1, 1);
			}
			// ������ �� ���������� 2 ����� �� ������� ������
			query2 = "SELECT Id FROM players WHERE PrimePosition = '1' AND TeamId = '0' ORDER BY RAND() LIMIT 2";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}
			// ������ �� ���������� 5 ������ �� ��������
			query2 = "SELECT Id FROM players WHERE PrimePosition = '2' AND TeamId = '0' ORDER BY RAND() LIMIT 5";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}
			// ������ �� ���������� 7 ������ �� ������
			query2 = "SELECT Id FROM players WHERE PrimePosition = '3' AND TeamId = '0' ORDER BY RAND() LIMIT 7";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}
			// ������ �� ���������� 4 ������ �� ���������
			query2 = "SELECT Id FROM players WHERE PrimePosition = '4' AND TeamId = '0' ORDER BY RAND() LIMIT 4";
			rs = st.executeQuery(query2);
			while (rs.next()) {
				tempList.add(rs.getInt("Id"));
			}

			returnlist += "</br>otbor " + (i + 1) + "-" + tempList.toString();
			teamId = teamList.get(i);
			// ������ �� �������� � Id �� ������, ���� �� ���������� � ��������
			// �� ��������� ���� � �������, ���� �������������� ����� � 4-4-2
			for (int j = 0; j < tempList.size(); j++) {
				playerId = tempList.get(j);
				if (j == 4 || j == 9 || j == 12 || j == 14 || j == 16 || j == 17) {
					condPos = "0";
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
	 * ����� ����� �� ���� Player � JSON ������ �� �������� Id ������
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
	 * ��������� �� ������, ������� ����� �� ���� Playstyle � �� ����� � JSON
	 * ������ http://localhost:8080/WebGame/db/groupteam?teamid=4
	 */
	@Path("/groupteam")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Playstyle groupTeam(@QueryParam("teamid") int teamid) throws Exception {
		String query = "SELECT Id,Position FROM players WHERE TeamId = '" + teamid
				+ "' AND Position != '0' ORDER BY Position ASC";
		List<Integer> teamList = new ArrayList<>();
		Playstyle pl = new Playstyle();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			teamList.add(rs.getInt("Id"));
		}
		pl.setGk(getPlayerById(teamList.get(0)));
		pl.setDf1(getPlayerById(teamList.get(1)));
		pl.setDf2(getPlayerById(teamList.get(2)));
		pl.setDf4(getPlayerById(teamList.get(3)));
		pl.setDf5(getPlayerById(teamList.get(4)));
		pl.setMd1(getPlayerById(teamList.get(5)));
		pl.setMd2(getPlayerById(teamList.get(6)));
		pl.setMd4(getPlayerById(teamList.get(7)));
		pl.setMd5(getPlayerById(teamList.get(8)));
		pl.setFw2(getPlayerById(teamList.get(9)));
		pl.setFw4(getPlayerById(teamList.get(10)));
		return pl;
	}

	/*
	 * ������ ����� ================
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
