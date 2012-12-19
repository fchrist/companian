package de.fct.companian.model.binding.bdl;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import de.fct.fdmm.bindingdl.MetaEntry;
import de.fct.fdmm.bindingdl.Task;

public class DefaultMetaEntry implements MetaEntry {

    private String name;
    private String description;
    private List<Task> tasks;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(type=DefaultTask.class)
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> task) {
        this.tasks = task;
    }

}
