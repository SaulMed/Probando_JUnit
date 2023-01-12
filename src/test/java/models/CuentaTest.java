package models;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import exceptions.SaldoInsuficienteException;

//@TestInstance(Lifecycle.PER_CLASS)	//Estableciendo un ciclo de vida POR CLASE o se puede agregar 'static' a @BeforeAll & @AfterAll
public class CuentaTest {

	Cuenta cuenta;
	
	private TestInfo testInfo;
	private TestReporter testReporter;

	@BeforeAll
	static void beforeAllTest() {
		System.out.println("Comienzo del Test GENERAL");
	}

	@AfterAll
	static void afterAllTest() {
		System.out.println("Termino del Test GENERAL");
	}

	@BeforeEach // Por cada metodo se ejecuta ANTES
	void beforeEachTest(TestInfo testInfo, TestReporter testReporter) {	//TestInfo  y TestReporter no funcionan en metodos static (beforeAllTest,afterAllTest)
		this.cuenta = new Cuenta("Saul", new BigDecimal("1000.123"));
		this.testInfo = testInfo;
		this.testReporter = testReporter;
		System.out.println("Iniciando el metodo");
		// testReporter.publishEntry => utiliza la salida del sitema de Log de JUnit, agrega timestamp y su formato propio
		testReporter.publishEntry("Ejecutando: '"+testInfo.getDisplayName()+"' , del metodo "+testInfo.getTestMethod().get().getName() + ", cuento con "+testInfo.getTags().size()+" etiquetas: "+testInfo.getTags());

		if(testInfo.getTags().contains("banco")) {	//se ejecuta si el metodo cuenta cierta etiqueta
			System.out.println("     ----->     Me ejecuto solo si el metodo cuenta con etiqueta banco");
		}
	}

	@AfterEach // Por cada metodo se ejecuta DESPUES
	void afterEachTest() {
		System.out.println("Terminando el metodo");
	}

	@Tag("SO")
	@Nested // Esta anotacion funciona para PRUEBAS ANIDADAS, categorizar las pruebas 
	@DisplayName("Sistema Operativo")
	class SistemaOperativoTest {

		@Test
		@EnabledOnOs(OS.WINDOWS) // Solo se ejecuta en ambiente Windows
		@DisplayName("- Solo windows")
		void testOnlyWindows() {
			System.out.println("Estas en un ambiente Windows");
		}

		@Test
		@EnabledOnOs({ OS.LINUX, OS.MAC }) // Solo se ejecuta en ambientes Mac o Linux
		@DisplayName("- Solo Mac/Linux")
		void testOnlyMacOrLinux() {
			System.out.println("Estas en un ambiente Linux.");
		}

		@Test
		@DisabledOnOs(OS.WINDOWS) // No me ejecutare si estas en Windows
		@DisplayName("- No trabajo en Windows")
		void testNoWindows() {
			System.out.println("No me ejecutare en windows");
		}

		@Test
		@EnabledIfSystemProperty(named = "os.version", matches = ".*10.*")
		@DisplayName("- Sistema de Version 10")
		void testVersionSistema() {
			System.out.println("La version de tu sistema es la v10.0");
		}

		@Test
		@DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
		@DisplayName("- Arquitectura de sistema 64bits.")
		void testNoArch32() {
			System.out.println("Solo correre en un sistema de 64 bits.");
		}

	}

	@Tag("Jdk")
	@Nested
	@DisplayName("Version de Java")
	class JavaVersionTest {

		@Test
		@EnabledOnJre(JRE.JAVA_17)
		@DisplayName("- Trabajar con Java 17")
		void testJDK17() {
			System.out.println("Si ves este mensaje , trabajas con Java 17");
		}

		@Test
		@DisabledOnJre(JRE.JAVA_17)
		@DisplayName("- No trabaja con Java 17")
		void testNoJDK17() {
			System.out.println("No me ejecutare si trabajas con Java 17");
		}

	}

	@Nested
	@DisplayName("Propiedades de Sistema")
	class PropiedadesDeSistemaTest {

		// Para establecer esta Propiedad de Sistema se necesita ir a 'Run' -> 'Run
		// Configuration' -> 'Arguments'
		// Y establecer la siguiente sentencia: -DENV=dev
		@Test
		@EnabledIfSystemProperty(named = "ENV", matches = "dev")
		@DisplayName("- Ambiente de Development")
		void testEnvDev() {
			System.out.println("Te encuentras en un ambiente de DEVELOPMENT");
		}

		@Test
		@Disabled
		@DisplayName("- Muestra Propiedades del Sistema")
		void mostrarPropiedadesDelSistema() { // Visualizacion de propiedades del sistema
			Properties properties = System.getProperties();
			properties.forEach((k, v) -> System.out.println(k + " : " + v));
		}

	}

	@Nested
	@DisplayName("Variables de Ambiente")
	class VariablesDeAmbienteTest {

		@Test
		@Disabled
		@DisplayName("- Muestra Variables de Ambiente")
		void mostrarVariablesDeAmbienteSO() {
			Map<String, String> envSystem = System.getenv();
			envSystem.forEach((k, v) -> System.out.println(k + " = " + v));
		}

		// Para establecer esta Variable de Entorno se necesita ir a 'Run' -> 'Run
		// Configuration' -> 'Environment'
		// Y agregar la siguiente variable: ENVIRONMENT , DEV / PROD
		@Test
		@EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
		@DisplayName("- Se encuentra la variable Environment en 'dev'")
		void testEnv() {
			System.out.println("Te encuentras en un ambiente de DESARROLLO");
		}

		@Test
		@DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
		@DisplayName("- Se encuentra la variable Environment en 'PROD'")
		void testEnvProd() {
			System.out.println("Me activo si NO estas en un ambiente de producción.");
		}

		@Test
		@EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk1.8.0.*")
		@DisplayName("- Se encuentra la variable JAVA_HOME con algun 'jdk.1.8'")
		void testJavaHome() {
			System.out.println("Cuentas con una variable de entorno JAVA_HOME = jdk1.8.0");
		}

		@Test
		@EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
		@DisplayName("- El equipo cuenta con 8 nucleos")
		void testNumeroDeNucleos() {
			System.out.println("Cuentas con 4 nucleos activados en tu sistema operativo.");
		}
	}

	@Tag("cuenta")
	@Nested
	@DisplayName("Cuenta")
	class CuentaSaldoPersonaTest {
		@Test
		@DisplayName("Verifica Nombre de Persona")
		void testNombrePersona() {
			
			String esperado = "Saul";
			String real = cuenta.getPersona();

			assertNotNull(real);
			assertEquals(esperado, real);
			assertNotEquals("Saul", cuenta.getPersona().toUpperCase());
			assertTrue(cuenta.getPersona().equals("Saul"));
			assertFalse(cuenta.getPersona().length() > 5);
		}

		@Test
		@DisplayName("Verifica Saldo de Cuenta")
		void testSaldoCuenta() {

			assertNotNull(cuenta.getSaldo());
			assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
			assertEquals(1000.123, cuenta.getSaldo().doubleValue());
		}

		@Test
		@DisplayName("Asegurar ambas instancias de cuenta por Referencia")
		void testReferenciaCuenta() {
			Cuenta cuenta1 = new Cuenta("Felipe", new BigDecimal("7580.9535"));
			Cuenta cuenta2 = new Cuenta("Felipe", new BigDecimal("7580.9535"));
			// assert( valor esperado, valor actual/real)
			assertEquals(cuenta2, cuenta1);
		}
	}

	@Nested
	@DisplayName("Cuenta Operaciones")
	class CuentaOperacionesTest{
	
		@Tag("cuenta")
		@Test
		@DisplayName("Verifica una operación Debito(-) con éxito")
		void testDebitoCuenta() {

			cuenta.debito(new BigDecimal("200"));

			assertNotNull(cuenta.getSaldo());
			assertEquals(800, cuenta.getSaldo().intValue());
			assertEquals("800.123", cuenta.getSaldo().toPlainString());

		}

		@Tag("cuenta")
		@Test
		@DisplayName("Verifica una operación Credito(+) con éxito.")
		void testCreditoCuenta() {

			cuenta.credito(new BigDecimal("200"));

			assertNotNull(cuenta.getSaldo());
			assertEquals(1200, cuenta.getSaldo().intValue());
			assertEquals("1200.123", cuenta.getSaldo().toPlainString());
		}

		@Tag("cuenta")
		@Tag("banco")
		@Test
		@DisplayName("Verifica operación de Transferencia de una cuenta a otra.") // Asignar nombre personalizado al Test
		// @Disabled //Saltar una prueba
		void testTransferirDineroCuentas() {
			// fail(); //Forzar Error
			
			Cuenta c1 = new Cuenta("Orlando", new BigDecimal("2500"));
			Cuenta c2 = new Cuenta("Johan", new BigDecimal("200"));
			Banco banco = new Banco();

			banco.setNombre("Banco Estatal");
			banco.transferir(c1, c2, new BigDecimal("500"));

			assertEquals("2000", c1.getSaldo().toPlainString());
			assertEquals("700", c2.getSaldo().toPlainString());

		}
	}
	
	@Tag("cuenta")
	@Tag("error")
	@Test
	@DisplayName("Genera Excepción de Saldo Insuficiente.")
	void testSaldoInsuficienteException() {

		Exception exception = assertThrows(SaldoInsuficienteException.class, () -> {
			cuenta.debito(new BigDecimal("1001.222"));
		});

		String esperado = "Saldo Insuficiente.";
		String real = exception.getMessage();
		assertEquals(esperado, real);
	}

	@Tag("cuenta")
	@Tag("banco")
	@Test
	@DisplayName("Asignación de banco a cuentas y busqueda de cuentas.")
	void testRelacionBancoCuentas() {
		Cuenta c1 = new Cuenta("Orlando", new BigDecimal("2500"));
		Cuenta c2 = new Cuenta("Johan", new BigDecimal("200"));
		Banco banco = new Banco();

		banco.agregarCuenta(c1); // Carga de cuentas hacia banco
		banco.agregarCuenta(c2);
		banco.setNombre("Banco Estatal");
		banco.transferir(c1, c2, new BigDecimal("500"));

		assertAll( // Agrupa un conjunto de asserts y proporciona mayor especificidad en cada
					// posible error
				() -> assertEquals("2000", c1.getSaldo().toPlainString(),
						() -> "El valor esperado es de: 2000 pero se recibio un - " + c1.getSaldo().toPlainString()),
				() -> assertEquals("700", c2.getSaldo().toPlainString(),
						() -> "El valor esperado es de: 700 pero se recibio un - " + c2.getSaldo().toPlainString()),
				() -> assertEquals(2, banco.getCuentas().size(),
						() -> "El banco deberia contar con 2 cuentas no con " + banco.getCuentas().size()
								+ " cuentas."),
				() -> assertEquals("Banco Estatal", c1.getBanco().getNombre(),
						() -> "El nombre del banco de la Cuenta1 deberia ser 'Banco Estatal' no '"
								+ c1.getBanco().getNombre() + "'"),
				() -> assertTrue(banco.getCuentas().stream().filter(c -> c.getPersona().equals("Orlando")).findFirst()
						.isPresent(), () -> "La cuenta con el titular 'Orlando' no se encuentra en el banco."),
				() -> assertEquals("Johan",
						banco.getCuentas().stream().filter(c -> c.getPersona().equals("Johan")).findFirst().get()
								.getPersona(),
						() -> "La cuenta con el titular 'Johan' no se encuentra en el banco."),
				() -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Orlando")),
						() -> "La cuenta con el titular 'Orlando' no se encuentra en el banco."));
	}

	// Assertions = Afirma una prueba unitaria
	// Asumptions = Asumir algun valor de verdad T/F

	@Test
	@DisplayName("Verifica Saldo de Cuenta Dev")
	void testSaldoCuentaDev() {
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		assumeTrue(esDev); // assumeTrue / assumeFalse = Utilizado para validar TODO un bloque de codigo
		assertNotNull(cuenta.getSaldo());
		assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
		assertEquals(1000.123, cuenta.getSaldo().doubleValue());
	}

	@Test
	@DisplayName("Verifica Saldo de Cuenta Dev 2")
	void testSaldoCuentaDev2() {
		boolean esDev = "dev".equals(System.getProperty("ENV"));
		assumingThat(esDev, () -> { // assumingThat = Util para validar cierta parte de codigo, si la parte booleana
									// no se cumple solo se omite esa seccion y se analiza el resto
			assertNotNull(cuenta.getSaldo());
			assertEquals(1000.123, cuenta.getSaldo().doubleValue());
		});
		assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
	}
	
	@RepeatedTest(value = 5, name = "Ciclo {currentRepetition} de {totalRepetitions}")		//Permite repetir una prueba determinado por un cierto numero de veces
	@DisplayName("Prueba Repetida")
	void testRepetido(RepetitionInfo repeticion) {
		if(repeticion.getCurrentRepetition() == 3) {
			System.out.println("Me encuentro en la "+repeticion.getCurrentRepetition()+" repeticion del ciclo, soy diferente.");
		}
		System.out.println("Estoy en el la repeticion " + repeticion.getCurrentRepetition());
	}
	
	@Tag("param")	//Etiqueta de seleccion , para seleccionar que pruebas ejecutar
	@Nested
	@DisplayName("Pruebas Parametrizadas")
	class PruebasParametrizadasTest{
		@DisplayName("Prueba parametrizada, valores manuales.")
		@ParameterizedTest(name = "Repeticion {index} con valor {0} - {argumentsWithNames}")	//Permite la ejecucion de esta prueba trabajando con diferentes valores por cada prueba
		@ValueSource(strings = {"150","250","350","550","1000.123"})
		void testDebitoCuenta(String cantidad) {	//Tomar cada valor del Test por parametro

			cuenta.debito(new BigDecimal(cantidad));	//Utilizar cada valor

			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}
		
		@DisplayName("Prueba parametrizada, Csv Source.")
		@ParameterizedTest(name = "Repeticion {index} con valor {0} - {argumentsWithNames}")	//Permite la ejecucion de esta prueba trabajando con diferentes valores por cada prueba
		@CsvSource({"2,150","1,250","3,350","4,550","5,1000.123"})
		void testDebitoCuentaCsvSource(String indice, String cantidad) {	//Tomar cada valor del Test por parametro

			System.out.println(indice + " -> " + cantidad);
			cuenta.debito(new BigDecimal(cantidad));	//Utilizar cada valor

			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}
		
		@DisplayName("Prueba parametrizada, Csv Source 2.")
		@ParameterizedTest(name = "Repeticion {index} con valor {0} - {argumentsWithNames}")	//Permite la ejecucion de esta prueba trabajando con diferentes valores por cada prueba
		@CsvSource({"200,150,Jesus,Jesus","300,250,Carmen,Carm","355,350,Jose,Jose","550,550,Pepe,pepe","1000.123,1000.123,Maria,Maria"})
		void testDebitoCuentaCsvSource2(String saldo, String cantidad, String esperado, String actual) {	//Tomar cada valor del Test por parametro

			cuenta.setSaldo(new BigDecimal(saldo));
			cuenta.debito(new BigDecimal(cantidad));	//Utilizar cada valor
			cuenta.setPersona(actual);

			assertNotNull(cuenta.getSaldo());
			assertNotNull(cuenta.getPersona());
			assertEquals(esperado, actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);	//Regresa true siempre y cuando el saldo final sea mayor que 0

			System.out.println("Saldo: " + saldo + " - " + cantidad + " = "+ cuenta.getSaldo());
		}
		
		@ParameterizedTest(name = "Repeticion {index} con valor {0} - {argumentsWithNames}")	//Permite la ejecucion de esta prueba trabajando con diferentes valores por cada prueba
		@CsvFileSource(resources = "/datos.csv")
		@DisplayName("Prueba parametrizada, Csv File Source.")
		void testDebitoCuentaCsvFileSource(String cantidad) {	//Tomar cada valor del Test por parametro

			cuenta.debito(new BigDecimal(cantidad));	//Utilizar cada valor

			assertNotNull(cuenta.getSaldo());
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}
		
		@ParameterizedTest(name = "Repeticion {index} con valor {0} - {argumentsWithNames}")	//Permite la ejecucion de esta prueba trabajando con diferentes valores por cada prueba
		@CsvFileSource(resources = "/datos2.csv")
		@DisplayName("Prueba parametrizada, Csv File Source 2.")
		void testDebitoCuentaCsvFileSource2(String saldo, String cantidad, String esperado, String actual) {	//Tomar cada valor del Test por parametro
			cuenta.setSaldo(new BigDecimal(saldo));	//Utilizar cada valor
			cuenta.debito(new BigDecimal(cantidad));
			cuenta.setPersona(actual);
			
			assertNotNull(cuenta.getPersona());
			assertNotNull(cuenta.getSaldo());
			assertEquals(esperado,actual);
			assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

		}	
	}
	
	@Tag("parma")
	@ParameterizedTest(name = "Repeticion {index} con valor {0} - {argumentsWithNames}")	//Permite la ejecucion de esta prueba trabajando con diferentes valores por cada prueba
	@MethodSource("montoList")	//Metodo no puede ir en una clase @Nested debido a que utiliza un metodo statico de la clase Tests
	@DisplayName("Prueba parametrizada, Method Source.")
	void testDebitoCuentaMethodSource(String cantidad) {	//Tomar cada valor del Test por parametro

		cuenta.debito(new BigDecimal(cantidad));	//Utilizar cada valor

		assertNotNull(cuenta.getSaldo());
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

	}
	
	static List<String> montoList(){	//Este metodo se encarga de poblar a testDeitoCuentaMethodSource
		return Arrays.asList("150","250","350","550","1000.123");
	}
	
	
	
	@Tag("timeout")
	@Nested
	class TimeoutTest{
		
		@Test
		@Timeout(3)	//Se establece un limite de tiempo para el test, por defecto trabaja con segundos
		@DisplayName("Primer test Timeout")
		void testTimeout1() throws InterruptedException {	//Simular carga pesada con TimeUnit
			TimeUnit.MILLISECONDS.sleep(2000);	//Esta tarea se demorara 2k ms.
		}
		
		@Test
		@Timeout(value = 1500, unit = TimeUnit.MILLISECONDS )
		@DisplayName("Segundo test Timeout")
		void testTimeout2() throws InterruptedException{
			TimeUnit.MILLISECONDS.sleep(1500);
		}
		
		@Test
		@DisplayName("Test Timeout Assertions")
		void testTimeoutAssertions() {
			assertTimeout(Duration.ofMillis(520), ()->{
				TimeUnit.MILLISECONDS.sleep(500);
			});
		}

	}
	
	
}
