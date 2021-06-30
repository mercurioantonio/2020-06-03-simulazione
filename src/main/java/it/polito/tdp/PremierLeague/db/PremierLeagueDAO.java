package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Float gol, Map<Integer, Player> idMap){
		String sql = "SELECT p.* "
				+ "FROM actions a, players p "
				+ "WHERE a.PlayerID = p.PlayerID "
				+ "GROUP BY a.PlayerID "
				+ "HAVING AVG(a.Goals) > ?"
				+ "";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setFloat(1, gol);
			ResultSet res = st.executeQuery();
			while (res.next()) {
                
				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				idMap.put(player.getPlayerID(), player);
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	public List<Adiacenza> listArchi(Map<Integer, Player> idMap){
		String sql = "SELECT a1.PlayerID AS a1ID, a1.MatchID, a1.TeamID, a2.PlayerID AS a2ID, a2.MatchID, a2.TeamID, SUM(a1.TimePlayed)-SUM(a2.TimePlayed) AS peso "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID > a2.PlayerID AND a1.TeamID != a2.TeamID AND a1.MatchID = a2.MatchID AND a1.`Starts` = '1' AND a2.`Starts` = '1' "
				+ "GROUP BY a1.PlayerID, a2.PlayerID "
				+ "HAVING peso != 0";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(idMap.containsKey(res.getInt("a1ID")) && idMap.containsKey(res.getInt("a2ID"))) {
					result.add(  new Adiacenza(idMap.get(res.getInt("a1ID")), idMap.get(res.getInt("a2ID")), res.getInt("peso"))  );
				}
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
