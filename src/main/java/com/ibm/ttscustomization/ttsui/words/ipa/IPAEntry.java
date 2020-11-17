package com.ibm.ttscustomization.ttsui.words.ipa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibm.sttcustomization.utils.Utils;


@JsonIgnoreProperties(ignoreUnknown = true)
public class IPAEntry {
    private String ipa = "";
    private String ipatext = "";
    private String examples = "";
    private String group = "";

    public String getIpa() {
        return ipa;
    }
    public void setIpa(String ipa) { this.ipa = ipa; }
    public String getExamples() { return examples; }
    public void setExamples(String examples) { this.examples = examples; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public String getIpatext() { return ipatext; }
    public void setIpatext(String ipatext) { this.ipatext = ipatext; }

    public String toHTML() {
        String s = Utils.fixedLengthString(ipatext, 5)  + " "+ examples;
        return s;
    }
}
