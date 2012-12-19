package de.fct.companian.analyze;

import org.apache.log4j.Logger;

public class AnalyzeJob extends Thread {

    private static Logger logger = Logger.getLogger(AnalyzeJob.class);
    
    private final String dir;
    
    private STATUS status = STATUS.idle;
    
    public AnalyzeJob(String dir) {
        this.dir = dir;
        
        int pos = dir.lastIndexOf(System.getProperty("file.separator"));
        String version = dir.substring(pos + 1);
        String name = dir.substring(0, pos);
        pos = name.lastIndexOf(System.getProperty("file.separator"));
        name = name.substring(pos + 1);
        this.setName(name + "-" + version);
    }
    
    @Override
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info("run() starting new analyzer");
        }
        this.status = STATUS.running;
        
        Analyzer a = new Analyzer();
        a.analyze(this.dir, null, null);
        
        this.status = STATUS.finished;
        if (logger.isInfoEnabled()) {
            logger.info("run() analyzer has finished this job");
        }
    }
    
    public STATUS getStatus() {
        return this.status;
    }

    public enum STATUS {
        idle, running, finished; 
    }
}

