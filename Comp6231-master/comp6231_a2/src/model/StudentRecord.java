package model;



public class StudentRecord extends Record{

	public StudentRecord(String recordID, String firstName, String lastName, 
			String courseRegesitered, String status, String statusDate) {
		super(recordID, firstName, lastName);
		this.Status = status;
		this.CourseRegesitered = courseRegesitered;
		this.StatusDate = statusDate;

	}
	public String CourseRegesitered;
	public String Status;
	public String StatusDate;
	
	

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		StudentRecord record = (StudentRecord)obj;

		if(record.LastName.equals(this.LastName)&&record.FirstName.equals(this.FirstName)
				&&record.Status.equals(this.Status)&&record.StatusDate.equals(this.StatusDate)
				&&record.CourseRegesitered.equals(this.CourseRegesitered)){
			return true;
		}
		return false;
	}

	@Override
	public void editField(String fieldName, String newValue) {
		// TODO Auto-generated method stub
		
		if(fieldName.toLowerCase().equals("coursesregistered")){
			this.CourseRegesitered = newValue;
		}
		else if(fieldName.toLowerCase().equals("status")){
			this.Status = newValue;
		}
		else if(fieldName.toLowerCase().equals("statusdate")){
			this.StatusDate = newValue;
		}
		else if(fieldName.toLowerCase().equals("firstname")){
			this.FirstName = newValue;
		}
		else if(fieldName.toLowerCase().equals("lastname")){
			this.LastName = newValue;
		}
	}
	public String getCourseRegesitered() {
		return CourseRegesitered;
	}
	public void setCourseRegesitered(String courseRegesitered) {
		CourseRegesitered = courseRegesitered;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getStatusDate() {
		return StatusDate;
	}
	public void setStatusDate(String statusDate) {
		StatusDate = statusDate;
	}
	@Override
	public String toString() {
		return "StudentRecord [RecordID=" + RecordID + ", FirstName=" + FirstName + ", LastName=" + LastName
				+ ", CourseRegistered=" + CourseRegesitered + ", Status=" + Status + ", StatusDate=" + StatusDate
				+ "]";
	}

	public String format() {
		return "StudentRecord" + "," + RecordID + "," + FirstName + "," + LastName + "," + CourseRegesitered + "," + Status +  "," + StatusDate;
	}

	
	
	

}
