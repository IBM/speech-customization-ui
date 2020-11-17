package com.ibm.sttcustomization.bak.customizations;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.words.QueryWordsReply;
import com.ibm.sttcustomization.model.words.Word;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class Customization {
	// left side is not place editable
	private String customization_id= "";    // read only, visible on the left side
    private String owner = "";               //---------- on the right side, read only
	private Timestamp created;          // read only, visible on the left side
	private Timestamp last_modified;    //---------- on the right side, read only
	private String name = "";                // editable, visible on the left side
	private String language = "";            // read only, visible on the left side
	private String description = "";         //----------  on the right side, editable

    private boolean markedForDelection;
    private boolean edited;

    private boolean wordswereloaded;

    private List<Word> words = new ArrayList<>();

    public  Customization() {
	}

	public Customization(String name, String customization_language) {
		this.name = name;
		this.language = language;
        //addtestWords();
	}

    public  List<Word>  findByWordContains(String filterText) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getWord().contains(filterText))
                arrRet.add(word);
        }
        return arrRet;
    }

    public void loadWords(RestClient restClient) throws GenericException {
        QueryWordsReply wordsReply = restClient.queryWords(customization_id);
        words = wordsReply.getWords();
        wordswereloaded = true;
    }


    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public Timestamp getLast_modified() { return last_modified; }
    public void setLast_modified(Timestamp last_modified) { this.last_modified = last_modified; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Word> getWordsList() { return words; }
    public void setWords(List<Word> words) { this.words = words; }
    public boolean isMarkedForDelection() { return markedForDelection; }
    public void setMarkedForDelection(boolean markedForDelection) { this.markedForDelection = markedForDelection; }
    public void setEdited(boolean edited) { this.edited = edited; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public String getCustomization_id() { return customization_id; }
    public void setCustomization_id(String customization_id) { this.customization_id = customization_id; }
    public Timestamp getCreated() { return created; }
    public void setCreated(Timestamp created) { this.created = created; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isWordswereloaded() { return wordswereloaded; }
    public void setWordswereloaded(boolean wordswereloaded) { this.wordswereloaded = wordswereloaded; }

    @Override
    public String toString() {
        return "Customization{" +
                "customization_id='" + customization_id + '\'' +
                ", owner='" + owner + '\'' +
                ", created=" + created +
                ", last_modified=" + last_modified +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                '}';
    }

}
