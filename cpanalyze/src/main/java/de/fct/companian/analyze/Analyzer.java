package de.fct.companian.analyze;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.fct.companian.analyze.helper.DbHelper;
import de.fct.companian.analyze.helper.FileHelper;

public class Analyzer {
	
	private static Logger logger = Logger.getLogger(Analyzer.class);

	private DataSource dataSource;
	private IndexDistributor indexDistributor;
	
	private boolean started = false;
	private boolean stopped = false;
	
	public void analyze(String dir, String startFrom, String stopAt) {
		long beforeStart = System.currentTimeMillis();
		logger.info("analyze() start " + dir);
		if (dir != null) {
			
			// TODO load db settings from properties file
			dataSource = DbHelper.createDataSource(null);
			indexDistributor = new IndexDistributor(dataSource);
			
			if (dataSource != null) {
				File entryDir = new File(dir);
				if (entryDir.exists() && entryDir.isDirectory() && entryDir.canRead()) {
					traverseDir(entryDir, startFrom, stopAt);
				}
				else {
				    logger.warn("analyze() entry dir '" + dir + "' does not exist. Analyze process will not be started.");
				}
			} else {
				logger.error("analyze() data source creation failed");
				System.err.println("Error creating data source.");
				System.exit(-1);
			}
		}
		else {
			logger.error("analyze() no repository directory given");
			System.err.println("No repository directory given!");
			System.exit(-1);
		}
		
		logger.info("analyze() waiting for indexDistributor to finish all running indexer");
		indexDistributor.waitForFinish();
		
		if (logger.isInfoEnabled()) {
			long now = System.currentTimeMillis();
			long secs = (now - beforeStart) / 1000;
			long min = 0;
			if (secs >= 60) {
				min = secs / 60;
				secs = secs - (min * 60);
			}
			logger.info("analyze() indexDistributor finished in " + min + ":" + secs + " min");
		}
		
	}
	
	private void traverseDir(File dir, String startFrom, String stopAt) {
		if (started && logger.isInfoEnabled()) {
			logger.info("traverseDir() start dir=" + dir.getAbsolutePath() + ",startFrom=" + startFrom + ",started=" + started + ",stopAt=" + stopAt + ",stopped=" + stopped);
		}
		if (startFrom == null) {
			started = true;
		}
		
		for (File subFile : dir.listFiles()) {
			if (subFile.canRead()) {
				if (subFile.isDirectory()) {
					if (!started) {
						if (subFile.getAbsolutePath().equals(startFrom)) {
							started = true;
							if (logger.isInfoEnabled()) {
								logger.info("traverseDir() starting from here " + subFile.getAbsolutePath());
							}
						}
						if (subFile.getAbsolutePath().equals(stopAt)) {
							stopped = true;
							if (logger.isInfoEnabled()) {
								logger.info("traverseDir() stopping here " + subFile.getAbsolutePath());
							}
							break;
						}
					}
					traverseDir(subFile, startFrom, stopAt);
				} else if (subFile.isFile()) {
					if (started) {
						String ending = FileHelper.getFileEnding(subFile);
						if (logger.isDebugEnabled()) {
							logger.debug("traverseDir() examing " + subFile.getName() + " with ending " + ending);
						}
						if (ending.equalsIgnoreCase("jar")) {
							try {
								File pomFile = new File(subFile.getCanonicalPath().replace(ending, "pom"));
								if (pomFile.exists() && pomFile.canRead()) {
									try {
										indexDistributor.indexJar(subFile, pomFile);
									} catch (Throwable t) {
										logger.error("traverseDir() error", t);
									}
								}
							} catch (IOException e) {
								logger.error("traverseDir() error", e);
							}
						}
					}
				}
			}
		}
	}

}
