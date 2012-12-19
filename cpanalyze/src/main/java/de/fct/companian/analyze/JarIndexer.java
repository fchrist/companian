package de.fct.companian.analyze;

import java.io.PrintStream;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.dependency.RegularExpressionSelectionCriteria;
import com.jeantessier.dependencyfinder.cli.Command;

import de.fct.companian.analyze.mvn.PomInfo;

public class JarIndexer extends Command {

    private static Logger logger = Logger.getLogger(JarIndexer.class);

    private final DataSource dataSource;
    private final PomInfo pomInfo;

    public JarIndexer(DataSource dataSource, PomInfo pomInfo) throws CommandLineException {
        super("JarIndexer");
        this.pomInfo = pomInfo;
        this.dataSource = dataSource;

        logger.debug("<init> finished");
    }

    @Override
    protected void doProcessing() throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("doProcessing() starting to index " + this.pomInfo.getJarName());
        }

        long startProcessing = System.currentTimeMillis();

        RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
        filterCriteria.setGlobalIncludes("/.*/");
        filterCriteria.setGlobalExcludes("/^java\\./");

        ClassIndexer classIndexer = new ClassIndexer(this.dataSource, this.pomInfo);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(classIndexer));
        loader.addLoadListener(getVerboseListener());
        loader.load(getCommandLine().getParameters());

        if (logger.isInfoEnabled()) {
            long now = System.currentTimeMillis();
            long diffSeconds = (now - startProcessing) / 1000;
            logger.info("doProcessing() of " + this.pomInfo.getJarName() + " finished in " + diffSeconds + " seconds");
            logger.info("with an average class processing time of " + classIndexer.getAverageClassTime()
                        + "ms per class");
        }
    }

    @Override
    protected void showSpecificUsage(PrintStream out) {
    // TODO Auto-generated method stub
    }
}
