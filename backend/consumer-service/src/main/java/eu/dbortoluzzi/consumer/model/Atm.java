package eu.dbortoluzzi.consumer.model;

import org.springframework.data.annotation.Id;

public class Atm {
	@Id
	protected String id;
	protected Integer distance;
	protected String type;
	protected Address address;

	public Atm() { }

	public Atm(Integer distance, String type, Address address) {
		this.distance = distance;
		this.type = type;
		this.address = address;
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
