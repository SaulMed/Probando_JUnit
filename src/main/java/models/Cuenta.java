package models;

import java.math.BigDecimal;

import exceptions.SaldoInsuficienteException;

// @Data de loombok - Hace que el metodo equals compare los objetos POR ATRIBUTOS , NO POR REFERENCIA.
// El metodo equals por defecto compara por REFERENCIA , no por atributos.

public class Cuenta {

	private String persona;
	private BigDecimal saldo;
	private Banco banco;

	public Cuenta(String persona, BigDecimal saldo) {
		this.persona = persona;
		this.saldo = saldo;
	}

	public String getPersona() {
		return this.persona;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public BigDecimal getSaldo() {
		return this.saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	// - = .substract para RESTAR monto
	public void debito(BigDecimal monto) {
		BigDecimal nuevoSaldo = this.saldo.subtract(monto);
		if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) { // Si se tiene un valor negativo lanzar exception
			throw new SaldoInsuficienteException("Saldo Insuficiente.");
		}

		this.saldo = nuevoSaldo;
	}

	public void credito(BigDecimal monto) {	// + = .add para SUMAR monto
		this.saldo = this.saldo.add(monto);
	} 

	@Override
	public boolean equals(Object obj) {
		Cuenta cuenta = (Cuenta) obj;

		if (!(obj instanceof Cuenta)) { // Verifica que la cuenta no sea null o que no sea de tipo cuenta
			return false;
		}

		if (this.persona == null || this.saldo == null) { // Verifica que sus atributos no vengan vacios
			return false;
		}

		return this.persona.equals(cuenta.getPersona()) && this.saldo.equals(cuenta.getSaldo());

		// Comportamiento por defecto de equals
		// return super.equals(obj); //Compara por Objeto / Instancia / Referencia en
		// memoria
	}

}
