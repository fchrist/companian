/*
 *  Copyright (c) 2001-2007, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.fct.companian.analyze;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.dependency.CodeDependencyCollector;
import com.jeantessier.dependency.LinkMaximizer;
import com.jeantessier.dependency.LinkMinimizer;
import com.jeantessier.dependency.NodeFactory;
import com.jeantessier.dependency.RegularExpressionSelectionCriteria;
import com.jeantessier.dependencyfinder.cli.Command;

public class JarDependencyExtractor extends Command {
	
    public JarDependencyExtractor() throws CommandLineException {
        super("JarDependencyExtractor");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("If no files are specified, it processes the current directory.");
        out.println();
        out.println("If file is a directory, it is recusively scanned for files");
        out.println("ending in \".class\".");
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        exceptions.addAll(validateCommandLineForFiltering());

        if (getCommandLine().getToggleSwitch("maximize") && getCommandLine().getToggleSwitch("minimize")) {
            exceptions.add(new CommandLineException("Only one of -maximize or -minimize is allowed"));
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
    	Date beforeTime = new Date();
    	
        List<String> parameters = getCommandLine().getParameters();
        if (parameters.size() == 0) {
            parameters.add(".");
        }
        
    	RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
    	filterCriteria.setGlobalIncludes("/.*/");
        filterCriteria.setGlobalExcludes("/^java\\./");

        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory, filterCriteria);

        ClassToJarMapper cfCollector = new ClassToJarMapper();
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(collector));
        loader.addLoadListener(new LoadListenerVisitorAdapter(cfCollector));
        loader.addLoadListener(getVerboseListener());
        loader.load(parameters);

        if (getCommandLine().getToggleSwitch("minimize")) {
            LinkMinimizer minimizer = new LinkMinimizer();
            minimizer.traverseNodes(factory.getPackages().values());
        } else if (getCommandLine().getToggleSwitch("maximize")) {
            LinkMaximizer maximizer = new LinkMaximizer();
            maximizer.traverseNodes(factory.getPackages().values());
        }

        JarDependencyAnalyzer sdepAnalyzer = new JarDependencyAnalyzer();
        sdepAnalyzer.setClassfileCollector(cfCollector);
        sdepAnalyzer.traverseNodes(factory.getPackages().values());
        sdepAnalyzer.printOutboundDependencyList();
        sdepAnalyzer.printInboundDependencyList();
        
        String fileName = "jardep";
        DotExporter exporter = new DotExporter(sdepAnalyzer.getDependencyList(), fileName);
        exporter.export();
        
        Date afterTime = new Date();
        
        long diffTime = (afterTime.getTime() - beforeTime.getTime()) / 1000;
        System.out.println();
        System.out.println("Dependencies calculated in " + diffTime + " seconds.");
        
        System.out.println();
        System.out.println("Generating image...");
		Runtime run = Runtime.getRuntime();
		try {
			run.exec("dot -Tpng -o" + fileName + ".png " + fileName + ".dot");
		} catch (IOException e) {
			System.out.println("Dot is not in path. Image will not be generated.");
		}	
    }

    public static void main(String[] args) throws Exception {
        new JarDependencyExtractor().run(args);
    }
}
