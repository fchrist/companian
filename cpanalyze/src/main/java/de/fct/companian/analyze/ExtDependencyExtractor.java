package de.fct.companian.analyze;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.dependency.CodeDependencyCollector;
import com.jeantessier.dependency.NodeFactory;
import com.jeantessier.dependency.RegularExpressionSelectionCriteria;
import com.jeantessier.dependencyfinder.cli.Command;

import de.fct.companian.analyze.helper.DbHelper;
import de.fct.companian.analyze.helper.FileHelper;
import de.fct.companian.analyze.model.JarEpDependency;
import de.fct.companian.analyze.mvn.PomInfo;
import de.fct.companian.analyze.mvn.helper.PomHelper;

public class ExtDependencyExtractor extends Command {

    private static Logger logger = Logger.getLogger(ExtDependencyExtractor.class);

    private final PomInfo pomInfo;
    private final DataSource dataSource;

    public ExtDependencyExtractor(DataSource dataSource, PomInfo pomInfo) throws CommandLineException {
        super("EpDependencyExtractor");

        this.dataSource = dataSource;
        this.pomInfo = pomInfo;
    }

    @Override
    protected void showSpecificUsage(PrintStream out) {}

    @Override
    protected void doProcessing() throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("doProcessing() starting to extract external dependencies for "
                        + this.pomInfo.getJarName());
        }
        long startProcessing = System.currentTimeMillis();

        List<String> parameters = getCommandLine().getParameters();
        if (parameters.size() == 0) {
            parameters.add(".");
        }

        RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
        filterCriteria.setGlobalIncludes("/.*/");
        filterCriteria.setGlobalExcludes("/^java\\./");

        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory, filterCriteria);

        ClassToJarMapper classToJarMapper = new ClassToJarMapper();

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(collector));
        loader.addLoadListener(new LoadListenerVisitorAdapter(classToJarMapper));
        loader.addLoadListener(getVerboseListener());
        loader.load(parameters);

        ExtDependencyAnalyzer depAnalyzer = new ExtDependencyAnalyzer(this.dataSource, this.pomInfo);
        depAnalyzer.setClassfileCollector(classToJarMapper);
        depAnalyzer.traverseNodes(factory.getPackages().values());

        if (logger.isInfoEnabled()) {
            long now = System.currentTimeMillis();
            long diffSeconds = (now - startProcessing) / 1000;
            logger.info("doProcessing() finished in " + diffSeconds + " seconds");
        }

        if (logger.isDebugEnabled()) {
            debugDependencies(depAnalyzer.getJarEpDependencies());
        }
    }

    private void debugDependencies(Map<String,JarEpDependency> depMap) {
        StringBuffer output = formatDependencies(depMap);

        logger.debug("debugDependencies()\n" + output.toString());
    }

    private StringBuffer formatDependencies(Map<String,JarEpDependency> depMap) {
        StringBuffer output = new StringBuffer();
        for (String jarName : depMap.keySet()) {
            output.append(jarName).append("\n");
            JarEpDependency jarDep = depMap.get(jarName);
            HashMap<String,List<String>> dependencyByClass = jarDep.getDependencyByClass();
            for (String className : dependencyByClass.keySet()) {
                output.append("\t").append(className).append("\n");
                List<String> descriptions = dependencyByClass.get(className);

                Collections.sort(descriptions, new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        return (o1.compareToIgnoreCase(o2) * -1);
                    }
                });

                for (String description : descriptions) {
                    output.append("\t - ").append(description).append("\n");
                }
                output.append("\n");
            }
        }
        return output;
    }

    public static void main(String[] args) throws Exception {
        if (args != null && args.length == 1) {

            // TODO load db settings from properties file
            DataSource dataSource = DbHelper.createDataSource(null);

            if (dataSource != null) {
                File jarFile = new File(args[0]);
                if (jarFile.exists() && jarFile.canRead()) {
                    String ending = FileHelper.getFileEnding(jarFile);
                    if (ending.equalsIgnoreCase("jar")) {
                        try {
                            File pomFile = new File(jarFile.getCanonicalPath().replace(ending, "pom"));
                            if (pomFile.exists() && pomFile.canRead()) {
                                PomHelper pomHelper = new PomHelper(pomFile);
                                PomInfo pomInfo = pomHelper.extractPomInfo();
                                ExtDependencyExtractor epExtractor = new ExtDependencyExtractor(dataSource,
                                        pomInfo);
                                epExtractor.run(new String[] {jarFile.getCanonicalPath()});
                            } else {
                                logger.error("main() no POM found or POM is not readable - file "
                                             + pomFile.getName());
                                System.err.println("No POM found or POM is not readable - file "
                                                   + pomFile.getName());
                            }
                        } catch (IOException e) {
                            logger.error("main() error handling JAR file " + jarFile.getName());
                            System.err.println("Error handling JAR file " + jarFile.getName());
                        }
                    } else {
                        logger.error("main() no JAR file " + jarFile.getName());
                        System.err.println("No JAR file " + jarFile.getName());
                    }
                } else {
                    logger.error("main() JAR file does not exist or is not readable " + args[0]);
                    System.err.println("JAR file does not exist or is not readable " + args[0]);
                }
            } else {
                logger.error("main() data source creation failed");
                System.err.println("Error creating data source.");
            }
        } else {
            logger.error("main() jar file given");
            System.err.println("No JAR file given!");
        }
    }

}
