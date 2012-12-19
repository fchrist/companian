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
