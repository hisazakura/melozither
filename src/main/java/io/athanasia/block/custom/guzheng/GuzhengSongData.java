package io.athanasia.block.custom.guzheng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

public class GuzhengSongData {
	private HashMap<Integer, List<GuzhengNote>> data;
	private int length;

	public GuzhengSongData() {
		this.data = new HashMap<Integer, List<GuzhengNote>>();
	}

	public GuzhengSongData(HashMap<Integer, List<GuzhengNote>> data) {
		this.data = data;
	}

	public void addNote(int time, GuzhengNote note) {
		data.computeIfAbsent(time, k -> new ArrayList<GuzhengNote>()).add(note);
	}

	public List<GuzhengNote> getNotesAtTime(int time) {
		return data.getOrDefault(time, new ArrayList<GuzhengNote>());
	}

	public Integer getLength() {
		if (this.length <= 0) refresh();
		return this.length;
	}

	public void refresh() {
		this.length =  Collections.max(new ArrayList<Integer>(data.keySet()));
	}

	public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

	public static GuzhengSongData fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, GuzhengSongData.class);
    }
}
