package com.ibm.ttscustomization.ttsmodel.words;

public class Word {

    public static long count;
	private Long runtimeid = count++;

	private String customization_id = "";
	private String word = "";
	private String part_of_speech = ""; // optional
    private String translation = "";

    private boolean markedForDelection;
    private boolean edited;

	public Word() {
	}

    public Word(String customization_id, String word, String part_of_speech, String translation) {
        this.customization_id = customization_id;
        this.word = word;
        this.part_of_speech = part_of_speech;
        this.translation = translation;
    }

    public Long getRuntimeid() { return runtimeid; }

    public String getCustomization_id() { return customization_id; }
    public String getWord() { return word; }
    public String getPart_of_speech() { return part_of_speech; }
    public boolean isMarkedForDelection() { return markedForDelection; }
    public void setMarkedForDelection(boolean markedForDelection) { this.markedForDelection = markedForDelection; }
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
    public void setCustomization_id(String customization_id) { this.customization_id = customization_id; }
    public void setWord(String word) { this.word = word; }
    public void setPart_of_speech(String part_of_speech) { this.part_of_speech = part_of_speech; }
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", part_of_speech='" + part_of_speech + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
