package guru.bonacci.oogway.registration;


import guru.bonacci.oogway.registration.services.RegistrationServer;

/**
 * Allow the servers to be invoked from the command-line. The jar is built with
 * this as the <code>Main-Class</code> in the jar's <code>MANIFEST.MF</code>.
 * 
 * @author Thank you Paul Chapman
 */
public class Main {

	public static void main(String[] args) {

		switch (args.length) {
		case 1:
			// Optionally set the HTTP port to listen on, overrides
			// value in the <server-name>-server.yml file
			System.setProperty("server.port", args[0]);
			// Fall through into ..
			break;

		default:
			usage();
			return;
		}

		RegistrationServer.main(args);
	}

	protected static void usage() {
		System.out.println("Usage: java -jar ... [server-port]");
		System.out.println("     where server-port > 1024");
	}
}
