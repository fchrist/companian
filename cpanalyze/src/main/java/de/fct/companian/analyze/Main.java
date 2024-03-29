/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.fct.companian.analyze;

import org.apache.log4j.Logger;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class);
	
	/**
	 * Startet den Analyseprozess für ein bestimmtes Verzeichnis [0],
	 * beginnt Analyse im Unterverzeichnis [1] und stoppt die Analyse
	 * im Verzeichnis [2].
	 * 
	 * @param args Analyseverzeichnis, Startverzeichnis, Stopverzeichnis
	 * @throws Exception
	 * @author Fabian Christ
	 */
	public static void main(String[] args) throws Exception {
		logger.info("depanalysis main() start");
		if (args != null && args.length > 0) {
			switch (args.length) {
			case 1:
				new Analyzer().analyze(args[0], null, null);		
				break;
			case 2:
				new Analyzer().analyze(args[0], args[1], null);		
				break;
			case 3:
				new Analyzer().analyze(args[0], args[1], args[2]);		
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
