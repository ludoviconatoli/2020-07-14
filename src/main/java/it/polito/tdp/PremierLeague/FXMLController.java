/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Team;
import it.polito.tdp.PremierLeague.model.TeamAllaFine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnClassifica"
    private Button btnClassifica; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="cmbSquadra"
    private ComboBox<Team> cmbSquadra; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doClassifica(ActionEvent event) {
    	this.txtResult.clear();
    	Team squadra = this.cmbSquadra.getValue();
    	int punteggio = this.model.getPunteggio(squadra);
    	
    	if(squadra != null && punteggio != 0) {
    		this.txtResult.appendText("Hai scelto la squadra: " + squadra.toString() + ", punteggio: " + punteggio + "\n\n");
    		
    		List<TeamAllaFine> migliori = this.model.getSquadreMiglioriDi(squadra);
    		this.txtResult.appendText("Squadre vincenti: \n");
    		for(TeamAllaFine taf: migliori) {
    			this.txtResult.appendText(taf.toString() + "\n");
    		}
    		
    		
    		List<TeamAllaFine> peggiori = this.model.getSquadrePeggioriDi(squadra);
    		this.txtResult.appendText("\nSquadre battute: \n");
    		for(TeamAllaFine taf: peggiori) {
    			this.txtResult.appendText(taf.toString() + "\n");
    		}
    	}else {
    		this.txtResult.appendText("Selezionare una squadra!");
    		return;
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	Graph<Team, DefaultWeightedEdge> grafo = this.model.creaGrafo();
    	
    	for(Team t: grafo.vertexSet()) {
    		this.txtResult.appendText(t.toString() + "\n");
    	}
    	this.txtResult.appendText("\n#vertici: " + grafo.vertexSet().size() +"\n");
    	this.txtResult.appendText("#archi: "+ grafo.edgeSet().size());
    	
    	this.cmbSquadra.getItems().addAll(grafo.vertexSet());
    }

    @FXML
    void doSimula(ActionEvent event) {
    	this.txtResult.clear();
    	int n;
    	int x;
    	
    	try {
    		x = Integer.parseInt(this.txtX.getText());
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("La soglia deve essere un numero");
    		return;
    	}
    	

    	try {
    		n = Integer.parseInt(this.txtN.getText());
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("N deve essere un numero");
    		return;
    	}
    	
    	this.model.init(n, x);
    	this.model.run();
    	
    	this.txtResult.appendText("Simulazione: \n");
    	this.txtResult.appendText("#partite critiche: " + this.model.getNumeroPartiteCritiche() +"\n");
    	this.txtResult.appendText("#reporter per partita: " +this.model.getNumeroReporterPerPartita() );
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnClassifica != null : "fx:id=\"btnClassifica\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbSquadra != null : "fx:id=\"cmbSquadra\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.txtResult.setStyle("-fx-font-family: monospace");
    }
}
