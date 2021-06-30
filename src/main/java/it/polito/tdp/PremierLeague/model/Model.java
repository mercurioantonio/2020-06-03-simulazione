package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private List<Player> dreamTeam;
	private int maxTitolarità = 0;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<Integer, Player>();
	}
	
	public void creaGrafo(float gol) {
		grafo = new SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao.listAllPlayers(gol, idMap);
		
		Graphs.addAllVertices(grafo, idMap.values());
		
		for(Adiacenza a : dao.listArchi(idMap)) {
			if(grafo.getEdge(a.getP1(), a.getP2()) == null) {
				if(a.getPeso() > 0) {
				Graphs.addEdgeWithVertices(grafo, a.getP1(), a.getP2(), a.getPeso());
				}
				else {
					Graphs.addEdgeWithVertices(grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
				}
			}
		}
	}

	public int getVertici() {
		return grafo.vertexSet().size();
	}
	
	public int getArchi() {
		return grafo.edgeSet().size();
	}
	
	public String getMigliore() {
		Player migliore = null;
		int battuti = 0;
		for(Player p : grafo.vertexSet()) {
			if(grafo.outDegreeOf(p) > battuti) {
				battuti = grafo.outDegreeOf(p);
				migliore = p;
			}
		}
		
		List<Battuti> battutiList = new ArrayList<Battuti>();
		for(DefaultWeightedEdge p : grafo.outgoingEdgesOf(migliore))
			battutiList.add(new Battuti(grafo.getEdgeTarget(p), (int) grafo.getEdgeWeight(p)));
		Collections.sort(battutiList, new ComparatoreBattuti());
		String result = migliore.toString() + "\n" + "GIOCATORI BATTUTI:" + "\n";
		for(Battuti b : battutiList) {
			result += b.toString() + "\n";
		}
		return result ;
	}
	
	class ComparatoreBattuti implements Comparator<Battuti>{
		@Override
		public int compare(Battuti b1, Battuti b2) {
			if(b1.getPeso() > b2.getPeso())
				return -1;
			else
				return 1;
			  
		}
	}
	
	
	
	public void findDreamTeam (int k){
		List<Player> parziale = new ArrayList<Player>();
		
		cerca(k, parziale, 0);
	}

	private void cerca(int k, List<Player> parziale, int l) {
		//caso terminale
		int sommaTitolarità = 0;
		if(l==k) {
			for(Player p : parziale) {
				int sommaU =0;
				int sommaE =0;
			    for(DefaultWeightedEdge e :grafo.outgoingEdgesOf(p)) {
			    	sommaU += grafo.getEdgeWeight(e);
			    }
			    for(DefaultWeightedEdge e :grafo.incomingEdgesOf(p)) {
			    	sommaE += grafo.getEdgeWeight(e);
			    }
			    sommaTitolarità += sommaU - sommaE;
			}
			if(sommaTitolarità > maxTitolarità) {
				this.dreamTeam = new ArrayList(parziale);
				this.maxTitolarità = sommaTitolarità;
			}
			
			return;
		}
		
		for(Player p : grafo.vertexSet()) {
			if(!parziale.contains(p)) {
				parziale.add(p);
				cerca(k, parziale, l+1);
				parziale.remove(parziale.size()-1);
			}
		}
	}
	
	public List<Player> getDreamTeam(){
		return dreamTeam;
	}
	
	public int getmaxTitolarità() {
		return maxTitolarità;
	}
}
