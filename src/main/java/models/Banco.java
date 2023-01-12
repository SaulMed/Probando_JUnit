package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banco {
	
	private List<Cuenta> cuentas;

	private String nombre;
	
	

	public Banco() {
		cuentas = new ArrayList<>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}

	public void agregarCuenta(Cuenta cuenta) {	//Agregar cuenta de BANCO a CUENTA
		cuentas.add(cuenta);
		cuenta.setBanco(this);	//Se pasa la misma referencia del banco a cada cuenta cada que se crea una
	}
	
	public void transferir(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto) {
		cuentaOrigen.debito(monto);	//Restar en cuenta Origen
		cuentaDestino.credito(monto);	//Suman en cuenta Destino
	}
	
}
