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
package de.fct.companian.use;

import java.util.ArrayList;
import java.util.List;

import de.fct.fdmm.core.HotSpotGroup;
import de.fct.fdmm.hotspot.HotSpot;
import de.fct.fdmm.hotspot.HotSpotUnit;

public class UseResult {

    private HotSpotGroup hotSpotGroup;
    private HotSpot hotSpot;
    private HotSpotUnit hotSpotUnit;
    private List<ClassUseConfidence> confidenceList;

    public UseResult() {};
    
    public UseResult(HotSpotGroup hotSpotGroup, HotSpot hotSpot, HotSpotUnit hotSpotUnit, List<ClassUseConfidence> confidenceList) {
        this.hotSpotGroup = hotSpotGroup;
        this.hotSpot = hotSpot;
        this.hotSpotUnit = hotSpotUnit;
        this.confidenceList = confidenceList;
    }
    
    public UseResult(HotSpotGroup hotSpotGroup, HotSpot hotSpot, HotSpotUnit hotSpotUnit, ClassUseConfidence confidence) {
        this.hotSpotGroup = hotSpotGroup;
        this.hotSpot = hotSpot;
        this.hotSpotUnit = hotSpotUnit;
        this.confidenceList = new ArrayList<ClassUseConfidence>();
        this.confidenceList.add(confidence);
    }

    public HotSpotGroup getHotSpotGroup() {
        return hotSpotGroup;
    }

    public void setHotSpotGroup(HotSpotGroup hotSpotGroup) {
        this.hotSpotGroup = hotSpotGroup;
    }

    public HotSpot getHotSpot() {
        return hotSpot;
    }

    public void setHotSpot(HotSpot hotSpot) {
        this.hotSpot = hotSpot;
    }

    public HotSpotUnit getHotSpotUnit() {
        return hotSpotUnit;
    }

    public void setHotSpotUnit(HotSpotUnit hotSpotUnit) {
        this.hotSpotUnit = hotSpotUnit;
    }

    public List<ClassUseConfidence> getConfidenceList() {
        return confidenceList;
    }

    public void setConfidenceList(List<ClassUseConfidence> confidenceList) {
        this.confidenceList = confidenceList;
    }

}
