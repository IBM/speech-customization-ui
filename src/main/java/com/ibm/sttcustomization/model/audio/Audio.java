package com.ibm.sttcustomization.model.audio;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class Audio {
    private String name = "";
    private double duration;
    private String status = "";
    Map<String, String> details = new HashMap<>();
//    private Long count = Long.valueOf(0);

	public Audio() {
	}

    public Audio(Audio audio) {
        this.duration = audio.duration;
//        this.details = new ArrayList<>(audio.details);
        this.name = audio.name;
        this.status = audio.status;
//        this.count = audio.count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getDetailsAsString() {
        String sRet = details.toString();
        sRet = sRet.replace("{", "");
        sRet = sRet.replace("}", "");
	    return sRet;
    }

    public  boolean IsArchive() {
        String strDetails  = getDetailsAsString();
        if (strDetails.contains("compression="))
            return true;
        return false;
    }

//
//    public Long getCount() {
//        return count;
//    }
//    public void setCount(Long count) {
//        this.count = count;
//    }
}
