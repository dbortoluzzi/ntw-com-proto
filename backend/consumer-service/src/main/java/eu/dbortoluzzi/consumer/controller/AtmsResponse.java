package eu.dbortoluzzi.consumer.controller;

import eu.dbortoluzzi.consumer.model.AtmIndexable;

import java.util.List;

public class AtmsResponse {
	private List<AtmIndexable> atms;
	private Long count;

	public AtmsResponse(List<AtmIndexable> atms, Long count) {
		this.atms = atms;
		this.count = count;
	}

	public List<AtmIndexable> getAtms() {
		return atms;
	}

	public void setAtms(List<AtmIndexable> atms) {
		this.atms = atms;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
