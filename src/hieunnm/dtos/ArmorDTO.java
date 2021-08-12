/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hieunnm.dtos;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author PC
 */
public class ArmorDTO implements Serializable {

    String id;
    String classification;
    String description;
    String status;
    Date timeOfCreate;
    int defense;

    public ArmorDTO() {
    }

    public ArmorDTO(String id, String classification, String description, String status, Date timeOfCreate, int defense) {
        this.id = id;
        this.classification = classification;
        this.description = description;
        this.status = status;
        this.timeOfCreate = timeOfCreate;
        this.defense = defense;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeOfCreate() {
        return timeOfCreate;
    }

    public void setTimeOfCreate(Date timeOfCreate) {
        this.timeOfCreate = timeOfCreate;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    @Override
    public String toString() {
        return "ArmorDTO{" + "id=" + id + ", classification=" + classification + ", description=" + description + ", status=" + status + ", timeOfCreate=" + timeOfCreate + ", defense=" + defense + '}';
    }

}
