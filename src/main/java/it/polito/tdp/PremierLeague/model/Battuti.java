package it.polito.tdp.PremierLeague.model;

public class Battuti {
	
	private Player p;
	private int peso;
	public Battuti(Player p, int peso) {
		super();
		this.p = p;
		this.peso = peso;
	}
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public int getPeso() {
		return peso;
	}
	public void setPeso(int peso) {
		this.peso = peso;
	}
	@Override
	public String toString() {
		return p.getPlayerID() + " - " + p.getName() + " | " + Math.abs(peso);
	}
	
	

}
