package de.fct.companian.analyze.prover;

import org.apache.log4j.Logger;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class);
	
	/**
	 * Startet den Dependency Proof Prozess fŸr eine bestimmte POM.
	 * 
	 * @param args pom.xml
	 * @throws Exception
	 * @author Fabian Christ
	 */
	public static void main(String[] args) throws Exception {
		logger.info("main() start");
		if (args != null && args.length > 0) {
			switch (args.length) {
			case 1:
				new DepProver().prove(args[0]);		
				break;
			default:
				break;
			}
			System.exit(0);
		}
		else {
			System.err.println("No args given!");
			System.exit(-1);			
		}
		logger.info("main() finished");
	}
	

}
