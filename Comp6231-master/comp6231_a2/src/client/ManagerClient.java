package client;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


import model.MyLogFormatter;
import recordManager.RecordManager;
import recordManager.RecordManagerHelper;

public class ManagerClient extends Thread{

	static String managerID;


	String[] args2;
	static String[] args;
	String[] requestArgs;



	public ManagerClient(String[] CORBAargs, String[] args2) {
		super();
		this.args = CORBAargs;
		this.args2 = args2;
		this.requestArgs = args2;
	}

	public ManagerClient(String managerID, String[] CORBAargs, String[] args2) {
		super();
		this.args = CORBAargs;
		this.managerID = managerID;
		this.args2 = args2;
		this.requestArgs = args2;
	}

	static private void printMenu(){
		StringBuilder sb = new StringBuilder();
		sb.append("1) Create Student Record\n");
		sb.append("2) Create Teacher Record\n");
		sb.append("3) Edit Record\n");
		sb.append("4) Get Count\n");
        sb.append("5) TransferRecord\n");
		sb.append("6) Test multiple clients request\n");
		sb.append("7) Exit\n");
		sb.append("Please input a number to continue\n");
		System.out.println(sb.toString());

	}



	
	/**
	 * Check if the string is number or not.
	 * @param str the input string
	 * @return If the string is made with numbers, then return true, otherwise false.
	 */
	public static boolean isNumeric(String str){
		  for (int i = str.length();--i>=0;){
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);

		System.out.println("Please enter your managerID. (e.g. MTL1234)");
		while(true){
			managerID = scan.nextLine().toUpperCase();
			/**
			 * First check the length of the managerID
			 * Then check the 3 digit prefix
			 * Finally check the 4 digit number
			 * If passed, break the loop and print menu.
			 */
			if(managerID.length()!=7){
				System.out.println("The length of managerID is incorrect. Please enter a valid managerID");
			}
			else if(!managerID.startsWith("MTL")
					&&!managerID.startsWith("LVL")
					&&!managerID.startsWith("DDO")){
				System.out.println("The managerID should be started with MTL, LVL or DDO.");
				System.out.println("Please enter a valid managerID");
			}
			else if(!isNumeric(managerID.substring(3, managerID.length()))){
				System.out.println("The managerID should end with 4 digit number.");
				System.out.println("Please enter a valid managerID");
			}
			else{

				break;
			}
		}


		while(true){
			printMenu();
			String option = scan.nextLine();
			switch (option) {
			case "1":
				String[] request1 = InputStudentRecord(scan);

				ManagerClient client1 = new ManagerClient(args, request1);
				client1.run();
				break;

			case"2":
				String[] request2 = InputTeacherRecord(scan);
				ManagerClient client2 = new ManagerClient(managerID, args, request2);
				client2.run();
				break;

			case"3":
				String[] request3 = inputEditRecord(scan);
				ManagerClient client3 = new ManagerClient(managerID, args, request3);
				client3.run();
				break;
			case"4":
				String[] request4 = inputGetCount(scan);
				ManagerClient client4 = new ManagerClient(managerID, args, request4);
				client4.run();
				break;
				case "5":
				    String[] request7 = InputTransferRecord(scan);
                    ManagerClient client7 = new ManagerClient(managerID, args, request7);
                    client7.run();
                    break;
                case"6":
				System.out.println("Now there will be multiple request sending from " +managerID
						+ ", including create student record, create teacher record and edit student record.");
				String[] transferRecord = {"SR10003","LVL"};
				String[] createStudentRecord = {"Jingye","Hou","maths","active","123"};
				String[] editRecord = {"SR10003", "FirstName", "Thomas"};
				new ManagerClient(managerID, args, transferRecord).start();
				new ManagerClient(managerID, args, createStudentRecord).start();
				new ManagerClient(managerID, args, editRecord).start();


				break;
			case"7":
				System.out.println("GoodBye.");
				return;


			default:
				System.out.println("Input invalid. Please input a valid number (1~6).");
				break;
			}


		}

	}
	private static String[] inputGetCount(Scanner scan) {
		// TODO Auto-generated method stub
		String[] request = new String[1];
		return request;
	}

	private static String[] InputTransferRecord(Scanner scanner) {
	    String[] array = new String[2];
	    array[0] = checkInputRecordID(scanner);
        array[1] = checkInputLocation(scanner, "remoteServerName");
        return array;
    }

	private static String[] InputTeacherRecord( Scanner scan) {
		String[] array = new String[6];
		System.out.println("Please input a teacher record.");
        array[0] = checkInputName(scan, "first name");
		array[1] = checkInputName(scan, "last name");
		System.out.println("Please input the address.");
		while(true){
			String address = scan.nextLine();
			if(address.isEmpty()){
				System.out.println("Address can not be empty.");
				System.out.println("Please input a valid address.");
			}
			else{
				array[2] = address;
				break;
			}

		}

		System.out.println("Please input the Phone number.");
		while(true){
			String phone = scan.nextLine();
			if(phone.isEmpty()){
                checkInputEmpty("phone number");
			}
			else if(!isNumeric(phone)){
				System.out.println("The phone number should only contain numbers.");
				System.out.println("Please input a valid phone number.");

			}
			else{
				try{
					Long.parseLong(phone);
					array[3] = phone;
					break;
				}
				catch(NumberFormatException e){
					System.out.println("Please input a valid phone number.");
				}


			}
		}
		System.out.println("Please input the specialization.");
		while(true){
			String specialization = scan.nextLine();
			if(specialization.isEmpty()){
				checkInputEmpty("specialization");
			}
			else{
				array[4] = specialization;
				break;
			}

		}
        array[5] = checkInputLocation(scan, "location");

		return array;
	}

    private static String checkInputLocation(Scanner scan, String locationStr) {
        System.out.println("Please input the " + locationStr + ". (MTL,LVL,DDO)");
        while(true){
            String location = scan.nextLine().toUpperCase();
            if(!location.equals("MTL")
                    &&!location.equals("LVL")
                    &&!location.equals("DDO"))
            {
                System.out.println("The " + locationStr + " should be one of the range of MTL, LVL, DDO.");
                System.out.println("Please input a valid " + locationStr + ".");

            }
            else{
               return location;
            }
        }
    }

    private static void checkInputEmpty(String name) {
        System.out.println(name + " can not be empty.");
        System.out.println("Please input a valid "+ name +".");
    }

    private static String checkInputName(Scanner scan, String firstOrLastName) {
        System.out.println("Please input the " + firstOrLastName + ".");
        while(true){
            String name = scan.nextLine();
            if(name.isEmpty()){
                System.out.println(firstOrLastName + " can not be empty.");
                System.out.println("Please input a valid "+ firstOrLastName + ".");
            }
            else if(name.matches(".*\\d+.*")){
                System.out.println("The " + firstOrLastName +" should not contain any numbers.");
                System.out.println("Please input a valid " + firstOrLastName +".");
            }
            else{
                return name;
            }
        }
    }

    private static boolean CheckStudentFieldName(String input){

		if(input.toLowerCase().equals("firstname")){
			return true;
		}
		else if(input.toLowerCase().equals("lastname")){
			return true;
		}
		else if(input.toLowerCase().equals("courseregistered")){
			return true;
		}
		else if(input.toLowerCase().equals("status")){
			return true;
		}
		else if(input.toLowerCase().equals("statusdate")){
			return true;
		}
		return false;

	}

	private static boolean CheckTeacherFieldName(String input){

		if(input.toLowerCase().equals("firstname")){
			return true;
		}
		else if(input.toLowerCase().equals("lastname")){
			return true;
		}
		else if(input.toLowerCase().equals("address")){
			return true;
		}
		else if(input.toLowerCase().equals("phone")){
			return true;
		}
		else if(input.toLowerCase().equals("specialization")){
			return true;
		}
		else if(input.toLowerCase().equals("location")){
			return true;
		}

		return false;

	}

	private static String[] inputEditRecord(Scanner scan){
		String[] array = new String[3];
        array[0] = checkInputRecordID(scan);
		System.out.println("Please input a fieldname.");
		System.out.println("(firstname, lastname, address, phone, "
				+ "specialization, location, courseregistered, status or statusdate");
		while(true){
			String fieldName = scan.nextLine().toLowerCase();
			if(array[0].startsWith("SR")&&!CheckStudentFieldName(fieldName)){
				System.out.println("Please input a valid fieldname.");
				System.out.println("(firstname, lastname, courseregistered, "
						+ "status or statusdate");
			}
			else if(array[0].startsWith("TR")&&!CheckTeacherFieldName(fieldName)){
				System.out.println("Please input a valid fieldname.");
				System.out.println("(firstname, lastname, address, phone, "
						+ "specialization, location");
			}
			else{
				array[1] = fieldName;
				break;
			}
		}
		System.out.println("Please input the new value for " + array[1]);
		while(true){
			String newValue = scan.nextLine();
			switch(array[1]){
			case "phone":
				if(!isNumeric(newValue)){
					System.out.println("The phone number should only contain numbers.");
					System.out.println("Please input a valid phone number.");
					break;
				}
				else{
					try{
						Long.parseLong(newValue);
						array[2] = newValue;
						return array;
					}
					catch(NumberFormatException e){
						System.out.println("Please input a valid phone number.");
						break;
					}

				}
			case "location":
				if(!newValue.toUpperCase().equals("MTL")
						&&!newValue.toUpperCase().equals("LVL")
						&&!newValue.toUpperCase().equals("DDO"))
				{
					System.out.println("The locaiton should be one of the range of MTL, LVL, DDO.");
					System.out.println("Please input a valid location.");
					break;

				}
				else{
					array[2] = newValue.toUpperCase();
					return array;
				}
			case "courseregistered":
				if(!newValue.toLowerCase().contains("maths")
						&&!newValue.toLowerCase().contains("science")
						&&!newValue.toLowerCase().contains("french")){
					System.out.println("The registered courses should contains at least one of maths, science and french.");
					System.out.println("Please input the valid registered courses.");

				}
				else{
					array[2] = newValue.toLowerCase();
					return array;
				}
			case "status":
				newValue = newValue.toLowerCase();
				if(!newValue.equals("active")&&!newValue.equals("inactive")){
					System.out.println("The status should be one of active or inactive.");
					System.out.println("Please input a valid status.");

				}
				else{
					array[2] = newValue;
					return array;
				}
			default:
				if(newValue.isEmpty()){
					System.out.println("THe new value can not be empty.");
					System.out.println("Please input a valid new value.");
				}
				else{
					array[2] = newValue;
					return array;
				}



			}
		}

	}

    private static String checkInputRecordID(Scanner scan) {
        System.out.println("Please input a recordID.");
        while(true){
            String recordID = scan.nextLine().toUpperCase();
            if(recordID.length()!=7){
                System.out.println("RecordID length is incorrect. Please input a valid recordID. "
                        + "(e.g. SR10001 or TR10001)");
            }
            else if(!recordID.startsWith("TR")&&!recordID.startsWith("SR")){
                System.out.println("RecordID prefix failed. Please input a valid recordID. "
                        + "(e.g. SR10001 or TR10001)");

            }
            else if(!isNumeric(recordID.substring(2, recordID.length()))){
                System.out.println("RecordID invalid. Please input a valid recordID. "
                        + "(e.g. SR10001 or TR10001)");
            }
            else{
                return recordID;
            }
        }
    }

    private static String[] InputStudentRecord( Scanner scan) {
		String[] array = new String[5];
		System.out.println("Please input a student record.");
		System.out.println("Please input the firstname.");

		while(true){
			String firstname = scan.nextLine();
			if(firstname.isEmpty()){
				System.out.println("First name can not be empty.");
				System.out.println("Please input a valid first name.");
			}
			else if(firstname.matches(".*\\d+.*")){
				System.out.println("The first name should not contain any numbers.");
				System.out.println("Please input a valid first name.");
			}
			else{
				array[0] = firstname;
				break;
			}
		}
		System.out.println("Please input the lastname.");
		while(true){
			String lastname = scan.nextLine();
			if(lastname.isEmpty()){
				System.out.println("Last name can not be empty.");
				System.out.println("Please input a valid last name.");
			}
			else if(lastname.matches(".*\\d+.*")){
				System.out.println("The last name should not contain any numbers.");
				System.out.println("Please input a valid last name.");
			}
			else{
				array[1] = lastname;
				break;
			}
		}
		System.out.println("Please input the registered courses. (e.g. maths/french/science)");
		System.out.println("If you have multiple courses, please use / to split each course.");
		while(true){
			String course = scan.nextLine().toLowerCase();
			if(course.isEmpty()){
				System.out.println("Registered course can not be empty.");
				System.out.println("Please input the valid registered course.");
			}
			else if(!course.equals("maths")
					&&!course.equals("science")
					&&!course.equals("french")
					&&!course.contains("/")){
					System.out.println("Please input the valid registered courses. (e.g. maths/french/science)");
					System.out.println("If you have multiple courses, please use / to split each course.");
			}
			else if(course.equals("maths")
					||course.equals("science")
					||course.equals("french")){
				array[2] = course;
				break;

			}
			else {
				boolean check = false;
				String[] courseArray = course.split("/");
				if(courseArray.length > 3){
					System.out.println("The maximum number of courses is 3.");
					System.out.println("Please input the valid registered courses. (e.g. maths/french/science)");
					System.out.println("If you have multiple courses, please use / to split each course.");
				}
				else if(courseArray.length == 3){
					int[] checkArray = {0,0,0};//maths/french/science
					for(String str:courseArray){
						if(str.equals("maths")){
							checkArray[0] = 1;
						}
						else if(str.equals("french")){
							checkArray[1] = 1;
						}
						else if(str.equals("science")){
							checkArray[2] = 1;
						}

					}
					if(checkArray[0]==1&&checkArray[1]==1&&checkArray[2]==1){
						array[2] = course;
						break;
					}
					else{
						System.out.println("The 3 courses must be in the range of maths, french and science. And each course can only appear once.");
						System.out.println("Please input the valid registered courses. (e.g. maths/french/science)");
						System.out.println("If you have multiple courses, please use / to split each course.");
					}

				}
				else if(courseArray.length ==2){
					if(courseArray[0].equals("maths")&&courseArray[1].equals("science")){
						array[2] = course;
						break;
					}
					else if(courseArray[1].equals("maths")&&courseArray[0].equals("science")){
						array[2] = course;
						break;
					}
					else if(courseArray[0].equals("maths")&&courseArray[1].equals("french")){
						array[2] = course;
						break;
					}
					else if(courseArray[1].equals("maths")&&courseArray[0].equals("french")){
						array[2] = course;
						break;
					}
					else if(courseArray[0].equals("science")&&courseArray[1].equals("french")){
						array[2] = course;
						break;
					}
					else if(courseArray[1].equals("science")&&courseArray[0].equals("french")){
						array[2] = course;
						break;
					}
					else{
						System.out.println("The 2 courses must be in the range of maths, french and science. And each course can only appear once.");
						System.out.println("Please input the valid registered courses. (e.g. maths/french/science)");
						System.out.println("If you have multiple courses, please use / to split each course.");
					}

				}

			}

		}
		System.out.println("Please input the status.");
		while(true){
			String status = scan.nextLine().toLowerCase();
			if(!status.equals("active")&&!status.equals("inactive")){
				System.out.println("The status should be one of active or inactive.");
				System.out.println("Please input a valid status.");

			}
			else{
				array[3] = status;
				break;
			}
		}
		System.out.println("Please input the status date.");
		while(true){
			String statusdate = scan.nextLine();
			if(statusdate.isEmpty()){
				System.out.println("Status date can not be empty.");
				System.out.println("Please input a valid status date.");
			}
			else{
				array[4] = statusdate;
				break;
			}
		}

		return array;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		String Location = managerID.substring(0, 3);
		//--------------------------CORBA part
		RecordManager remote = null;
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			NameComponent nc = new NameComponent(Location, "");
			NameComponent path[] = {nc};
			remote = RecordManagerHelper.narrow(ncRef.resolve(path));

//			remote = RecordManagerHelper.narrow(ncRef.resolve_str(Location));
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}


		//RMI part------------------------

		/**
		 * Define client logger.
		 */
		Logger logger = Logger.getLogger("ClientLogger" + managerID);
		FileHandler fileHandler = null;

		try {
			fileHandler = new FileHandler("./log/client/ClientLogger" + managerID + ".log");
			fileHandler.setFormatter(new MyLogFormatter());
		} catch (SecurityException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger.addHandler(fileHandler);
		logger.log(Level.INFO, "The client has been login, the managerID is " +managerID);

		logger.log(Level.INFO, "RMI has already connected to the " + Location +" server.");

		String result = "";
		if(args2.length ==6){

			//Create teacher record.
			StringBuilder sb = new StringBuilder();
			sb.append("Receive a request for creating teacher record from user input " + managerID +" .\n");
			sb.append("The first name is " + args2[0]+" .\n");
			sb.append("The last name is " + args2[1]+" .\n");
			sb.append("The address is " + args2[2]+" .\n");
			sb.append("The phone is " + args2[3]+" .\n");
			sb.append("The specialization is " + args2[4]+" .\n");
			sb.append("The location is " + args2[5]+" .");
			sb.append("Sending... ");
			logger.log(Level.INFO,sb.toString());

			result = remote.createTRecord(managerID, args2[0], args2[1], args2[2], args2[3], args2[4], args2[5]);

			logger.log(Level.INFO, "Receive a response from server: " + result);
		}
		else if(args2.length == 5){
			//Create student record.
			StringBuilder sb = new StringBuilder();
			sb.append("Receive a request for creating student record from user input " + managerID +" .\n");
			sb.append("The first name is " + args2[0]+" .\n");
			sb.append("The last name is " + args2[1]+" .\n");
			sb.append("The courseRegistered is " + args2[2]+" .\n");
			sb.append("The status is " + args2[3]+" .\n");
			sb.append("The statusDate is " + args2[4]+" .");
			sb.append("Sending... ");
			logger.log(Level.INFO,sb.toString());

			result = remote.createSRecord(managerID, args2[0], args2[1], args2[2], args2[3], args2[4]);

			logger.log(Level.INFO, "Receive a response from server: " + result);
		}
		else if(args2.length == 3){
			//Create student record.
			StringBuilder sb = new StringBuilder();
			sb.append("Receive a request to edit record from user input " + managerID +" .");
			sb.append("The recordID is " + args2[0]+" .");
			sb.append("The field name is " + args2[1]+" .");
			sb.append("The new value is " + args2[2]+" .");
			sb.append("Sending... ");
			logger.log(Level.INFO,sb.toString());

			result = remote.editRecord(managerID, args2[0], args2[1], args2[2]);


			logger.log(Level.INFO, "Receive a response from server: " + result);

		} else if (args2.length == 2) {
		    logger.info("Receive a request to transfer record from user input " + managerID +" .");
            StringBuilder sb = new StringBuilder();
            sb.append("The recordID is " + args2[0] + " .");
            sb.append("The remoteServer is " + args2[1] + " .");
            sb.append("Sending...");

            result = remote.transferRecord(managerID, args2[0], args2[1]);
            logger.info("Receive a response from server: " + result);
        }
		else if(args2.length == 1){
			logger.log(Level.INFO,"Receive a request to "
					+ "get count of records from user input " + managerID +" .");
			logger.log(Level.INFO,"Sending... ");
			int array[] = {0,0,0};

			String temp = remote.getCount(managerID);
			array[0] = Integer.parseInt(temp.split(",")[0]);
			array[1] = Integer.parseInt(temp.split(",")[1]);
			array[2] = Integer.parseInt(temp.split(",")[2]);


			String resultGCT = "MTL " + array[0]
					+ " LVL " + array[1] + " DDO " + array[2];
			logger.log(Level.INFO, "Receive a response from server: " + resultGCT);

		}
		super.run();
	}



		// TODO Auto-generated method stub

}
