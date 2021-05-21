package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.javafx.geom.Edge;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private Map<Integer, Team> squadre;
	private Graph<Team, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private List<TeamAllaFine> listaSquadre;
	
	public Model() {
		this.squadre = new HashMap<>();
		this.dao = new PremierLeagueDAO();
		this.listaSquadre = new ArrayList<>();
	}
	
	public Graph<Team, DefaultWeightedEdge> creaGrafo() {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao.listAllTeams(squadre);
		
		for(Team t: this.squadre.values()) {
			this.grafo.addVertex(t);
		}
		
		this.generaPunteggio();
		
		for(TeamAllaFine t1: this.listaSquadre) {
			for(TeamAllaFine t2: this.listaSquadre) {
				if(!t1.equals(t2) && this.grafo.containsVertex(t1.getTeam())
						&& this.grafo.containsVertex(t2.getTeam()) &&
						((t1.getPunteggio() - t2.getPunteggio()) > 0 || (t1.getPunteggio() - t2.getPunteggio())<0)) {
					
					int peso = t1.getPunteggio() - t2.getPunteggio();
					
					if(peso > 0 && !this.grafo.containsEdge(t1.getTeam(), t2.getTeam())) {
						Graphs.addEdge(this.grafo, t1.getTeam(), t2.getTeam(), peso);
					}else if(peso < 0 && !this.grafo.containsEdge(t2.getTeam(), t1.getTeam())) {
						Graphs.addEdge(this.grafo, t2.getTeam(), t1.getTeam(), -peso);
					}
				}
			}
				
		}
		return this.grafo;
	}
	
	public void generaPunteggio() {
		for(Team t: this.grafo.vertexSet()) {
			
			TeamAllaFine taf = new TeamAllaFine(t, 0);
			List<Match> listaMatch = dao.listAllMatchesPerSquadra(t);
			
			for(Match m: listaMatch) {
				if(m.getTeamHomeID() == t.getTeamID() && m.getReaultOfTeamHome() > 0) {
					taf.setPunteggio(taf.getPunteggio() + 3);
				}else if(m.getTeamHomeID() == t.getTeamID() && m.getReaultOfTeamHome() == 0) {
					taf.setPunteggio(taf.getPunteggio() + 1);
				}else if(m.getTeamAwayID() == t.getTeamID() && m.getReaultOfTeamHome() < 0) {
					taf.setPunteggio(taf.getPunteggio() + 3);
				}else if(m.getTeamAwayID() == t.getTeamID() && m.getReaultOfTeamHome() == 0) {
					taf.setPunteggio(taf.getPunteggio() + 1);
				}
			}
			
			this.listaSquadre.add(taf);
		}
			
	}
}
