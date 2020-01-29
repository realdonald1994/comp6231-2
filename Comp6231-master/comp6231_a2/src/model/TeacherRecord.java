package model;

public class TeacherRecord extends Record{

	public TeacherRecord(String recordID, String firstName, String lastName, 
			String address, Long phone, String specialization, String Location) {
		super(recordID, firstName, lastName);
		this.Address = address;
		this.Location = Location;
		this.Specialization = specialization;
		this.Phone = phone;

	}
	public String Address;
	public Long Phone;
	public String Specialization;
	public String Location;

	@Override
	public boolean equals(Object obj) {
		TeacherRecord record = (TeacherRecord)obj;
		if(record.LastName.equals(this.LastName)&&record.FirstName.equals(this.FirstName)
				&&record.Address.equals(this.Address)&&record.Phone.equals(this.Phone)
				&&record.Specialization.equals(this.Specialization)
				&&record.Location.equals(this.Location)){
			return true;
		}
		return false;
	}

	@Override
	public void editField(String fieldName, String newValue) {
		// TODO Auto-generated method stub

		if(fieldName.toLowerCase().equals("address")){
			this.Address = newValue;
		}
		else if(fieldName.toLowerCase().equals("phone")){
			this.Phone = Long.parseLong(newValue);
		}
		else if(fieldName.toLowerCase().equals("specialization")){
			this.Specialization = newValue;
		}
		else if(fieldName.toLowerCase().equals("location")){
			this.Location = newValue;
		}
		else if(fieldName.toLowerCase().equals("firstname")){
			this.FirstName = newValue;
		}
		else if(fieldName.toLowerCase().equals("lastname")){
			this.LastName = newValue;
		}
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public long getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = Long.parseLong(phone);
	}
	public String getSpecialization() {
		return Specialization;
	}
	public void setSpecialization(String specialization) {
		Specialization = specialization;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	@Override
	public String toString() {
		return "TeacherRecord [RecordID=" + RecordID + ", FirstName=" + FirstName + ", LastName=" + LastName
				+ ", Address=" + Address + ", Phone=" + Phone + ", Specialization=" + Specialization + ", Location="
				+ Location + "]";
	}

	public String format() {
		return "TeacherRecord" + "," + RecordID + "," + FirstName + "," + LastName + "," + Address + "," + Phone +  "," + Specialization + "," + Location;
	}


	
	
	
	
}
