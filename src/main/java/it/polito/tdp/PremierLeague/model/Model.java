package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
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
						&& this.grafo.containsVertex(t2.getTeam()) ) {
					
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
		
		Collections.sort(listaSquadre);
			
	}
	
	public List<TeamAllaFine> getSquadreMiglioriDi(Team t){
		List<TeamAllaFine> m = new ArrayList<>();
		
		TeamAllaFine taf = null;
		for(TeamAllaFine ta: this.listaSquadre) {
			if(ta.getTeam().equals(t)) {
				taf = ta;
			}
		}
		
		if(taf != null) {
			for(TeamAllaFine ta: this.listaSquadre) {
				if(ta.getPunteggio() > taf.getPunteggio())
					m.add(ta);
			}
		
			Collections.reverse(m);
			return m;
		}
		
		return null;
	}
	
	public List<TeamAllaFine> getSquadrePeggioriDi(Team t){
		List<TeamAllaFine> m = new ArrayList<>();
		
		TeamAllaFine taf = null;
		for(TeamAllaFine ta: this.listaSquadre) {
			if(ta.getTeam().equals(t)) {
				taf = ta;
			}
		}
		
		if(taf != null) {
			for(TeamAllaFine ta: this.listaSquadre) {
				if(ta.getPunteggio() < taf.getPunteggio())
					m.add(ta);
			}
			
			Collections.reverse(m);
			return m;
		}
		
		return null;
	}
	
	public int getPunteggio(Team t) {
		
		for(TeamAllaFine taf: this.listaSquadre) {
			if(taf.getTeam().equals(t))
				return taf.getPunteggio();
		}
		
		
		return 0;
	}
	
	List<Match> partite = new ArrayList<>(this.dao.listAllMatches());
	private int N;
	private int X;
	private Map<Team, Integer> reporterPerSquadra;
	
	private int reporterPerPartita = 0;
	private int numPartiteCritiche = 0;
	
	public void init(int N, int X) {
		this.reporterPerSquadra = new HashMap<>();
		this.N = N;
		this.X = X;
		
		for(Team t: this.grafo.vertexSet()) {
			this.reporterPerSquadra.put(t, this.N);
		}
	}
	
	public void run() {
		for(Match m: this.partite) {
			if(m.getReaultOfTeamHome() > 0) {
				if(Math.random() > 0.5) {
					Team home = this.getTeam(m.getTeamHomeID());
					
					this.reporterPerSquadra.put(home, this.reporterPerSquadra.get(home) - 1);
					
					Team migliore = this.getMiglioreDi(home);
					
					this.reporterPerSquadra.put(migliore, this.reporterPerSquadra.get(migliore) + 1);
					
				}
				
				if(Math.random() > 0.8) {
					Team away = this.getTeam(m.getTeamAwayID());
					int reporterPerdenti = (int)Math.random()*this.N;
					
					this.reporterPerSquadra.put(away, this.reporterPerSquadra.get(away) - reporterPerdenti);
					
					Team peggiore = this.getPeggioreDi(away);
					
					this.reporterPerSquadra.put(peggiore, this.reporterPerSquadra.get(peggiore) + reporterPerdenti);
				}
			}else if(m.getReaultOfTeamHome() < 0) {
				//MANCA LA PARTE IN CUI PERDA IL TEAM DI CASA E
				//MANCA LA GESTIONE DEI RISULTATI DA STAMPARE
			}
		}
	}
	
	private Team getPeggioreDi(Team away) {
		int punti = 0;
		for(TeamAllaFine taf: this.listaSquadre)
			if(taf.getTeam().equals(away))
				punti = taf.getPunteggio();
		
		for(TeamAllaFine taf: this.listaSquadre)
			if(taf.getPunteggio() < punti)
				return taf.getTeam();
		
		return this.listaSquadre.get(this.listaSquadre.size()-1).getTeam();
	}

	public Team getTeam(int id) {
		for(Team t: this.grafo.vertexSet())
			if(t.getTeamID().equals(id))
				return t;
		
		return null;
	}
	
	public Team getMiglioreDi(Team t) {
		int punti = 0;
		for(TeamAllaFine taf: this.listaSquadre)
			if(taf.getTeam().equals(t))
				punti = taf.getPunteggio();
		
		for(TeamAllaFine taf: this.listaSquadre)
			if(taf.getPunteggio() > punti)
				return taf.getTeam();
		
		return this.listaSquadre.get(0).getTeam();
	}
}
