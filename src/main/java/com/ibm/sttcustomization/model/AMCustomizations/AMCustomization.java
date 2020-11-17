package com.ibm.sttcustomization.model.AMCustomizations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AMCustomization {
    private String owner = "";
    private String base_model_name = "";
    private String customization_id = "";
    private ArrayList<String> versions = new ArrayList<>();
    private Date created;
    private String name = "";
    private String description = "";
    private int progress;
    private String language = "";
    private String status = "";

    public AMCustomization() {
    }

    public AMCustomization(AMCustomization amCustomization) {
        this.owner = amCustomization.owner;
        this.base_model_name = amCustomization.base_model_name;
        this.customization_id = amCustomization.customization_id;
        this.versions = new ArrayList<>(amCustomization.versions);
        this.created = amCustomization.created;
        this.name = amCustomization.name;
        this.description = amCustomization.description;
        this.progress = amCustomization.progress;
        this.language = amCustomization.language;
        this.status = amCustomization.status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBase_model_name() {
        return base_model_name;
    }

    public void setBase_model_name(String base_model_name) {
        this.base_model_name = base_model_name;
    }

    public String getCustomization_id() {
        return customization_id;
    }

    public void setCustomization_id(String customization_id) {
        this.customization_id = customization_id;
    }

    public ArrayList<String> getVersions() {
        return versions;
    }

    public void setVersions(ArrayList<String> versions) {
        this.versions = versions;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public LocalDateTime getCreatedLocalDate() {
        if (created != null)
            return created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        else
            return null;
    }

    public void setCreatedLocalDate(LocalDate localDate) {
        this.created = java.sql.Date.valueOf(localDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersionsAsString() {
        String listString = String.join(", ", versions);
        return  listString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AMCustomization that = (AMCustomization) o;
        return progress == that.progress &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(base_model_name, that.base_model_name) &&
                Objects.equals(customization_id, that.customization_id) &&
                Objects.equals(created, that.created) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(language, that.language) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, base_model_name, customization_id, created, name, description, progress, language, status);
    }
}
