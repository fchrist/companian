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
package de.fct.companian.web.beans;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fct.companian.analyze.AnalyzeJob;
import de.fct.companian.analyze.AnalyzeJob.STATUS;

public class AnalyzeBean {
    
    private static Logger logger = LoggerFactory.getLogger(AnalyzeBean.class);

    Map<String,WeakReference<AnalyzeJob>> jobMap = new HashMap<String,WeakReference<AnalyzeJob>>();

    public String startNewJob(String dir) {
        WeakReference<AnalyzeJob> job = new WeakReference<AnalyzeJob>(new AnalyzeJob(dir));
        String id = String.valueOf(job.hashCode());

        jobMap.put(id, job);
        Thread t = new Thread(job.get());
        t.start();

        if (logger.isInfoEnabled()) {
            logger.info("startNewJob() started new analyze job with ID " + id);
        }
        return id;
    }
    
    public List<AnalyzeJob> getJobs() {
        List<AnalyzeJob> jobs = new ArrayList<AnalyzeJob>();
        
        for (WeakReference<AnalyzeJob> job : this.jobMap.values()) {
            if (job.get() != null) {
                jobs.add(job.get());
            }
        }
        
        Collections.sort(jobs, new Comparator<AnalyzeJob>() {

            public int compare(AnalyzeJob o1, AnalyzeJob o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        return jobs;
    }
    
    public STATUS getJobStatus(String id) {
        WeakReference<AnalyzeJob> job = this.jobMap.get(id);
        if (job != null) {
            if (job.get() != null) {
                return job.get().getStatus();
            }
            else {
                this.jobMap.remove(id);
            }            
        }
        return null;
    }
}
