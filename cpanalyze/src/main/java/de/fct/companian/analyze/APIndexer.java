package de.fct.companian.analyze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.dependencyfinder.cli.Command;

import de.fct.companian.analyze.db.dao.JarDao;
import de.fct.companian.analyze.db.model.Jar;
import de.fct.companian.analyze.mvn.PomInfo;

public class APIndexer extends Command {

    private static Logger logger = Logger.getLogger(APIndexer.class);

    private final DataSource dataSource;
    private final JarDao jarDao;
    private final PomInfo pomInfo;
    private final File jarFile;

    public APIndexer(DataSource dataSource, PomInfo pomInfo, File jarFile) throws CommandLineException {
        super("APIndexer");
        this.dataSource = dataSource;
        this.jarDao = new JarDao(this.dataSource);

        this.pomInfo = pomInfo;
        this.jarFile = jarFile;

        logger.debug("<init> finished");
    }

    @Override
    protected void doProcessing() throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("doProcessing() starting JavaDoc API index process for " + this.pomInfo.getJarName());
        }

        File sourcesFile = this.determineSourcesFile();
        if (sourcesFile != null) {
            long startProcessing = System.currentTimeMillis();

            File extractDir = this.createTmpDir(this.pomInfo.getJarName());
            if (extractSourcesJar(sourcesFile, extractDir)) {

                String subpackages = determineSubpackages(extractDir);
                copyJelDoclectJar(extractDir);
                generateJelXML(extractDir, subpackages);

                if (saveApidoc(extractDir)) {
                    if (logger.isInfoEnabled()) {
                        long now = System.currentTimeMillis();
                        long diffSeconds = (now - startProcessing) / 1000;
                        logger.info("doProcessing() of " + this.pomInfo.getJarName() + " finished in "
                                    + diffSeconds + " seconds");
                    }
                } else {
                    logger.info("doProcessing() problems while saving API doc in DB - cancel");
                }
            } else if (logger.isInfoEnabled()) {
                logger.info("doProcessing() failure during extraction of sources - cancel");
            }
        } else {
            logger.info("doProcessing() no sources JAR - skipping");
        }
    }

    private File determineSourcesFile() {
        String sourcesFileName = this.jarFile.getAbsolutePath();
        sourcesFileName = sourcesFileName.replace(".jar", "-sources.jar");

        File sourcesFile = new File(sourcesFileName);
        if (sourcesFile.exists()) {
            if (sourcesFile.isFile() && sourcesFile.canRead()) {
                return sourcesFile;
            } else {
                logger.warn("determineSourcesFile() can read from file " + sourcesFileName);
            }
        } else {
            logger.info("determineSourcesFile() source file " + sourcesFileName + " does not exist");
        }

        return null;
    }

    private String determineSubpackages(File extractDir) {
        StringBuffer subpackages = new StringBuffer();
        boolean first = true;
        for (File subFile : extractDir.listFiles()) {
            if (subFile.canRead()) {
                if (subFile.isDirectory()) {
                    if (subFile.getName().equals("META-INF")) {
                        continue;
                    } else {
                        if (!first) {
                            subpackages.append(":");
                        }
                        first = false;
                        subpackages.append(subFile.getName());
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("determineSubpackages() from " + extractDir.getAbsolutePath() + " result "
                         + subpackages.toString());
        }

        return subpackages.toString();
    }

    private File createTmpDir(String suffix) {
        File tmpDir = null;
        try {
            tmpDir = File.createTempFile("cpsrc-", suffix);
            tmpDir.delete();
            tmpDir.mkdir();
            tmpDir.deleteOnExit();

            if (logger.isDebugEnabled()) {
                logger.debug("createTmpDir() created tmp dir " + tmpDir.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("createTmpDir() error while creating a temp dir", e);
        }

        return tmpDir;
    }

    private boolean extractSourcesJar(File sourcesFile, File extractDir) throws IOException,
                                                                        InterruptedException {
        if (logger.isDebugEnabled()) {
            logger.debug("extractSourcesJar() starting external JAR extract process");
        }
        List<String> command = new ArrayList<String>();
        command.add("jar");
        command.add("-xf");
        // command.add("-f");
        command.add(sourcesFile.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(extractDir);
        return executeProcess(builder);
    }

    private void copyJelDoclectJar(File extractDir) throws FileNotFoundException, Exception, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("copyJelDoclectJar() copying jeldoclet.jar to tmp dir " + extractDir.getName());
        }
        
        String jeldocletResource = "/jeldoclet.jar";
        InputStream is = this.getClass().getResourceAsStream(jeldocletResource);
        if (is != null) {
            File outJeldoclet = new File(extractDir, "jeldoclet.jar");
            FileOutputStream os = new FileOutputStream(outJeldoclet);
            try {
				byte[] buf = new byte[1024];
				int i = 0;
				while ((i = is.read(buf)) != -1) {
					os.write(buf, 0, i);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("copyJelDocletJar() copy successful");
				}
			} catch (Exception e) {
				logger.error("copyJelDocletJar() error during copy", e);
				throw e;
			} finally {
				if (is != null) is.close();
				if (os != null) os.close();
			}
        }
        else {
        	logger.error("copyJelDocletJar() Could not load resource to copy from classpath: " + jeldocletResource);
        	throw new FileNotFoundException("Could not load resource to copy from classpath: " + jeldocletResource);
        }
    }

    private boolean generateJelXML(File extractDir, String subpackages) throws IOException,
                                                                       InterruptedException {
        if (logger.isDebugEnabled()) {
            logger.debug("generateJelXML() starting external JavaDoc process");
        }
        List<String> command = new ArrayList<String>();
        command.add("javadoc");
        command.add("-docletpath");
        command.add("jeldoclet.jar");
        command.add("-doclet");
        command.add("com.jeldoclet.JELDoclet");
        command.add("-quiet");
        command.add("-private");
        command.add("-subpackages");
        command.add(subpackages);

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(extractDir);
        return executeProcess(builder);
    }

    private boolean executeProcess(ProcessBuilder builder) throws IOException, InterruptedException {
        if (logger.isDebugEnabled()) {
            logger.debug("executeProcess() executing " + builder.command().toString());
        }
        final Process process = builder.start();

        final StringBuffer out = new StringBuffer();
        final StringBuffer err = new StringBuffer();

        // Final versions of the the params, to be used within the threads
        final BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // Thread that reads std out and feeds the writer given in input
        Thread stdoutThread = new Thread() {
            @Override
            public void run() {
                String line;
                try {
                    while ((line = stdOut.readLine()) != null) {
                        out.append(line).append("\n");
                    }
                    if (logger.isDebugEnabled()) {
                    	logger.debug("executeProcess() reading from std out finished");
                    }
                } catch (IOException e) {
                    logger.error("executeProcess() error while reading std out of process", e);
                }
            }
        };
        stdoutThread.start(); // Starts now

        // Thread that reads std err and feeds the writer given in input
        Thread stderrThread = new Thread() {
        	@Override
            public void run() {
                String line;
                try {
                    while ((line = stdErr.readLine()) != null) {
                        err.append(line).append("\n");
                    }
                    if (logger.isDebugEnabled()) {
                    	logger.debug("executeProcess() reading from std err finished");
                    }
                } catch (IOException e) {
                    logger.error("executeProcess() error while reading std err of process", e);
                }
            }
        };
        stderrThread.start(); // Starts now

        int exitValue = process.waitFor();
        stderrThread.stop();
        stdoutThread.stop();
        if (logger.isDebugEnabled()) {
            logger.debug("executeProcess() process returned " + exitValue + ".\nStdOut:\n" + out.toString()
                         + "\nStdErr:\n" + err.toString());
        }
        if (process.exitValue() != 0) {
            logger.warn("executeProcess() process returned error code " + process.exitValue());
            return false;
        }

        return true;
    }

    private boolean saveApidoc(File extractDir) {
        if (logger.isDebugEnabled()) {
            logger.debug("saveApidoc() in database");
        }
        boolean success = true;
        String jel = readJelXml(extractDir);
        if (jel != null) {
            Jar jar = jarDao.loadJar(pomInfo.getArtifactId(), pomInfo.getVersion(), pomInfo.getGroupId());
            try {
                jarDao.updateApidoc(jar.getJarId(), jel);
            } catch (Exception e) {
                logger.error("saveApidoc() error saving API doc in DB", e);
                success = false;
            }
        }

        return success;
    }

    private String readJelXml(File extractDir) {
        String jel = null;
        File jelXmlFile = new File(extractDir, "jel.xml");
        if (jelXmlFile.exists() && jelXmlFile.canRead()) {
            StringBuffer jelBuf = new StringBuffer();
            try {
                BufferedReader input = new BufferedReader(new FileReader(jelXmlFile));
                try {
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        jelBuf.append(line);
                        jelBuf.append(System.getProperty("line.separator"));
                    }
                    jel = jelBuf.toString();
                } catch (IOException e) {
                    logger.error("readJelXml() error while reading from " + jelXmlFile.getAbsolutePath(), e);
                } finally {
                    try {
                        input.close();
                    } catch (IOException e) { /* Who cares? */}
                }
            } catch (FileNotFoundException e) {
                logger.error("readJelXml() could not find " + jelXmlFile.getAbsolutePath(), e);
            }

        } else {
            logger.warn("readJelXml() could not read jel.xml file");
        }

        return jel;
    }

    @Override
    protected void showSpecificUsage(PrintStream arg0) {
    // TODO Auto-generated method stub

    }

}
