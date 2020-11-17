package com.ibm.sttcustomization.model.grammars;

import java.sql.Timestamp;
import java.util.ArrayList;

public class QueryGrammarsReply {
    private String customization_id;
    private String owner;
    private Timestamp created;
    private Timestamp last_modified;
    private String name;
    private String language;
    private String description;

    ArrayList<Grammar> grammars;
    public QueryGrammarsReply() {
    }

    public ArrayList<Grammar> getGrammars() { return grammars; }
    public void setGrammars(ArrayList<Grammar> grammars) { this.grammars = grammars; }
    public String getCustomization_id() { return customization_id; }
    public void setCustomization_id(String customization_id) { this.customization_id = customization_id; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public Timestamp getCreated() { return created; }
    public void setCreated(Timestamp created) { this.created = created; }
    public Timestamp getLast_modified() { return last_modified; }
    public void setLast_modified(Timestamp last_modified) { this.last_modified = last_modified; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
