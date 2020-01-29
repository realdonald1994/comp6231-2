package model;

public class Record {
	public String FirstName;
	public String LastName;
	public String RecordID;
	
	public Record(String recordID, String firstName, String lastName) {
		FirstName = firstName;
		LastName = lastName;
		RecordID = recordID;
	}
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public void editField(String fieldName, String newValue){
		if(fieldName.toLowerCase().equals("firstname")){
			this.FirstName = newValue;
		}
		else if(fieldName.toLowerCase().equals("lastname")){
			this.LastName = newValue;
		}
	}

	public String format() {
		return  "record";
	}
	
	@Override
	public boolean equals(Object obj) {
		Record record = (Record)obj;
		if(record.LastName.equals(this.LastName)&&record.FirstName.equals(this.FirstName)){
			return true;
		}
		return false;
	}

}
