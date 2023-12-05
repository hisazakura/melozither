package io.athanasia.block.custom.guzheng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GuzhengSongData {
	private String script;
	private HashMap<Integer, List<GuzhengNote>> data;
	private String title;
	private String author;
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
		if (this.length <= 0)
			refresh();
		return this.length;
	}

	public String geScript() {
		return this.script;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void refresh() {
		this.length = Collections.max(new ArrayList<Integer>(data.keySet()));
	}
}
