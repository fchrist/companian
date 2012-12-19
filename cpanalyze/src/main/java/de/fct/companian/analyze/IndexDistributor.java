package de.fct.companian.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class IndexDistributor {

	private static Logger logger = Logger.getLogger(IndexDistributor.class);

	private final DataSource dataSource;

	private static final int MAX_WORKER = 3;

	private final List<IndexWorker> workerList = new ArrayList<IndexWorker>();
	private final ExecutorService executorService;

	public IndexDistributor(DataSource dataSource) {
		this.dataSource = dataSource;

		this.executorService = Executors.newFixedThreadPool(MAX_WORKER);
	}

	public synchronized void indexJar(File jarFile, File pomFile) {
		IndexWorker worker = null;
		synchronized (workerList) {
			while (!isWorkerFree()) {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					logger.warn("indexJar() sleep interrupted", e);
				}
			}

			if (logger.isInfoEnabled()) {
				logger.info("indexJar() enqueuing " + jarFile.getName());
			}
			worker = new IndexWorker(this.dataSource);
			worker.setJarFile(jarFile);
			worker.setPomFile(pomFile);
			workerList.add(worker);
		}

		try {
			if (worker != null) {
				this.executorService.execute(worker);
			}
		}
		catch (RuntimeException re) {
			logger.error("indexJar() error during execution of IndexWorker", re);
		}
	}

	private boolean isWorkerFree() {
		boolean free = false;
		if (this.workerList.size() <= MAX_WORKER) {
			free = true;
		}
		else {
			IndexWorker finishedWorker = null;
			for (IndexWorker worker : this.workerList) {
				if (worker.hasRun()) {
					finishedWorker = worker;
					break;
				}
			}
			if (finishedWorker != null) {
				this.workerList.remove(finishedWorker);
				free = true;
			}
		}

		return free;
	}

	public void waitForFinish() {
		this.executorService.shutdown();
		while(!this.executorService.isTerminated()) {
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				logger.warn("waitForFinish() sleep was interrupted");
			}
		}
	}
}
