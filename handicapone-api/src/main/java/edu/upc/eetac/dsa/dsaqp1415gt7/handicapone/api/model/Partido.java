package edu.upc.eetac.dsa.dsaqp1415gt7.handicapone.api.model;

public class Partido {
	
	private int idpartido;
	private String local;
	private String visitante;
	private long fechacierre;
	private long fechapartido;
	
	
	public int getIdpartido() {
		return idpartido;
	}
	public void setIdpartido(int idpartido) {
		this.idpartido = idpartido;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getVisitante() {
		return visitante;
	}
	public void setVisitante(String visitante) {
		this.visitante = visitante;
	}
	public long getFechacierre() {
		return fechacierre;
	}
	public void setFechacierre(long fechacierre) {
		this.fechacierre = fechacierre;
	}
	public long getFechapartido() {
		return fechapartido;
	}
	public void setFechapartido(long fechapartido) {
		this.fechapartido = fechapartido;
	}


}
