package com.ibm.ttscustomization.ttsmodel.customizations;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.ttscustomization.ttsmodel.words.QueryWordsReply;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.words.Word;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class TTSCustomization {
    public static long count = -1;

	private Long runtimeid = count++;

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

    public TTSCustomization() {
	}

    public TTSCustomization(TTSCustomization t) {
        //this.runtimeid = t.runtimeid;
        this.customization_id = t.customization_id;
        this.owner = t.owner;
        this.created = t.created;
        this.last_modified = t.last_modified;
        this.name = t.name;
        this.language = t.language;
        this.description = t.description;
        this.markedForDelection = t.markedForDelection;
        this.edited = t.edited;
        this.wordswereloaded = t.wordswereloaded;
        this.words = t.words;
    }

    public TTSCustomization(String name, String customization_language) {
		this.name = name;
		this.language = language;
        addtestWords();
	}

	public void addtestWords() {
        for (int i=0; i < 20000; i++) { Word word = new Word(name + "(" + runtimeid + ")", "word " + i, "pos_" + i,
                    "orp_" + i) ;
            words.add(word);
        }
    }

    public  List<Word>  findByWordContains(String filterText) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getWord().contains(filterText))
                arrRet.add(word);
        }
        return arrRet;
    }

    public List<Word>  findByTranslationContains(String filterText) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getTranslation().contains(filterText))
                arrRet.add(word);
        }
        return arrRet;
    }

    public List<Word>  findByPartOfSpeechContains(String filterText) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getPart_of_speech().contains(filterText))
                arrRet.add(word);
        }
        return arrRet;
    }


    public boolean isEdited() {
        for (Word word : words) {
            if (word.isEdited() || word.isMarkedForDelection())
                edited = true;
        }

        return edited;
    }

    public void loadWords(RestClientTTS restClient) throws GenericException {
        QueryWordsReply wordsReply = restClient.queryWords(customization_id);
        words = wordsReply.getWords();
        wordswereloaded = true;
    }


    public Long getRuntimeid() { return runtimeid; }
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

    public LocalDateTime getlastModifiedLocalDate() {
        if (last_modified != null)
            return last_modified.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        else
            return null;
    }


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
