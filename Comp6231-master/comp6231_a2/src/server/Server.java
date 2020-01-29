package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import model.MyLogFormatter;
import model.Record;
import model.StudentRecord;
import model.TeacherRecord;
import recordManager.RecordManager;
import recordManager.RecordManagerHelper;
import recordManager.RecordManagerImpl;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Server extends recordManager.RecordManagerPOA {
    HashMap<Character, List<Record>> RecordMap;
	HashMap<String, Integer> ManagerServerMap;
	/**
	 * The TeacherRecord_ID starts with TR10001 
	 * And the StuentRecord_ID starts with SR10001
	 */
	String TeacherRecord_ID = "TR10000";
	String StudentRecord_ID = "SR10000";
	Logger logger;
	FileHandler fileHandler;
	String name;

	int LocalPortNum;


	
	public Server(String name) throws SecurityException, IOException{
		super();
		this.name = name;
		RecordMap = new HashMap<>();
		logger = Logger.getLogger(name+"ServerLogger");
		fileHandler = new FileHandler("./log/server/" + name+ ".log");
		fileHandler.setFormatter(new model.MyLogFormatter());
		logger.addHandler(fileHandler);
		InitHashMap();
		ManagerServerMap = new HashMap<>();
		ManagerServerMap.put("MTL", 7777);
		ManagerServerMap.put("LVL", 8888);
		ManagerServerMap.put("DDO", 9999);
		LocalPortNum = ManagerServerMap.get(name);
	
	}
	
	/**
	 * Insert the record to the HashMap
	 * If there is a related key in the HashMap, then update the value.
	 * Otherwise create the list and put the list to the HashMap.
	 * @param record The record need to be inserted.
	 */
	
	public void InsertHashMap(Record record){
		// TODO Auto-generated method stub
		synchronized (RecordMap) {
		Character key = new Character(record.LastName.charAt(0));
		if(RecordMap.containsKey(key)){
			List<Record> temp = RecordMap.get(key);
			temp.add(record);
			RecordMap.put(key, temp);

			//logger.log(Level.INFO, "The record:" + record.RecordID + " has been updated on the server.");
		}
		else{
			List<Record> newList = new ArrayList<>();
			newList.add(record);
			RecordMap.put(key, newList);
			//logger.log(Level.INFO, "The record:" + record.RecordID + " has been updated to the server.");
			
		}
		}
	}
	
	/**
	 * Allocate a new StudentRecord_ID.
	 * Based on the StuentRecord_ID and +1.
	 * @return The ID allocated for the new StudentRecord.
	 */
	public String GetCurrentStudentRecordID(){
		// TODO Auto-generated method stub
		int flag = Integer.parseInt(StudentRecord_ID.substring(2, 7));
		flag++;
		StudentRecord_ID = StudentRecord_ID.substring(0,2) + flag;
		return StudentRecord_ID;
	}
	/**
	 * Allocate a new TeacherRecord_ID.
	 * Based on the TeacherRecord_ID and +1.
	 * @return The ID allocated for the new TeacherRecord.
	 */
	public String GetCurrentTeacherRecordID(){
		// TODO Auto-generated method stub
		int flag = Integer.parseInt(TeacherRecord_ID.substring(2, 7));
		flag++;
		TeacherRecord_ID = TeacherRecord_ID.substring(0,2) + flag;
		return TeacherRecord_ID;
	}
	/**
	 * Initial the HashMap with some test records at the beginning of program running.
	 */
	public void InitHashMap(){
		List<Record> recordList = new ArrayList<>();
		StudentRecord stuRecord1 = new StudentRecord(GetCurrentStudentRecordID(),"Luguang", "Liu", "maths|french", "active", "2017/01/07");
		StudentRecord stuRecord2 = new StudentRecord(GetCurrentStudentRecordID(),"Zhongxu", "Huang", "maths", "active", "2017/02/07");
		TeacherRecord teacherRecord1 = new TeacherRecord(GetCurrentTeacherRecordID(),"Tom", "Laura", "Apt9", (long)911, "french", "MTL");
		TeacherRecord teacherRecord2 = new TeacherRecord(GetCurrentTeacherRecordID(),"John", "Jerry", "Apt3", (long)311, "math", "LVL");
		recordList.add(stuRecord1);
		recordList.add(stuRecord2);
		recordList.add(teacherRecord1);
		recordList.add(teacherRecord2);
		for(Record record : recordList){
			InsertHashMap(record);
		}		
		logger.log(Level.INFO,"The HashMap has been initialized on the server.");
		logger.log(Level.INFO, IterateHashMap());
	}
	/**
	 * Iterate the RecordMap and print all the records.
	 */
	public String IterateHashMap(){
		StringBuilder sb = new StringBuilder();
		sb.append("The records on the server are listed below.\n");
		// TODO Auto-generated method stub
		Iterator iter = RecordMap.entrySet().iterator(); 
		sb.append("---------------------------\n");
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    Character key = (Character) entry.getKey(); 
		    List<Record> val = (List<Record>) entry.getValue(); 
		    sb.append("Key: " + key +"\n");   
		    for(Record record : val){
		    	sb.append(record.toString()+"\n");
		    }
		}
		sb.append("---------------------------");
		return sb.toString();
	}
	/**
	 * 
	 * @param record1
	 * @return If exist, return false

	 */
	public boolean CheckRecordIfExist2(Record record1){
		// TODO Auto-generated method stub
		Iterator iter = RecordMap.entrySet().iterator(); 
		if(record1 instanceof StudentRecord){
			StudentRecord temp = (StudentRecord) record1;
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    Character key = (Character) entry.getKey(); 
			    List<Record> val = (List<Record>) entry.getValue(); 
			    for(Record record : val){
			    	if(record instanceof StudentRecord){
			    		record = (StudentRecord)record;
				    	if(record.equals(temp)){
				    		return false;
				    	}
			    	}
			    }
			}
		}
		else if(record1 instanceof TeacherRecord){
			TeacherRecord temp = (TeacherRecord) record1;
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    Character key = (Character) entry.getKey(); 
			    List<Record> val = (List<Record>) entry.getValue(); 
			    for(Record record : val){
			    	
			    	if(record instanceof TeacherRecord){
			    		record = (TeacherRecord)record;
				    	if(record.equals(temp)){
				    		return false;
				    	}
			    	}
			    }
			}
		}
		return true;
	}
	@Override
	public String createTRecord(String managerID, String firstName, String lastName, String address, String phone,
			String specialization, String Location) {

		synchronized (RecordMap) {
			logger.log(Level.INFO,"Receive a request for creating teacher record from: " + managerID);
			//-------------
			TeacherRecord temp = new TeacherRecord("TR00000", 
					firstName, lastName, address, Long.parseLong(phone), specialization, Location);
			//-------------
			if(!CheckRecordIfExist2(temp)){
				logger.log(Level.INFO,"The request record is already exist on the server, the request from "+ managerID+" has been rejected.");
				logger.log(Level.INFO, IterateHashMap());
				return "There is already a record for " + lastName +", " + firstName;
			}
			//-------------
			TeacherRecord teacherRecord = new TeacherRecord(GetCurrentTeacherRecordID(), 
					firstName, lastName, address, Long.parseLong(phone), specialization, Location);
			InsertHashMap(teacherRecord);
			logger.log(Level.INFO,"The request for creating teacher record from "+ managerID+" has already handled.");
			logger.log(Level.INFO, IterateHashMap());
			IterateHashMap();

			return "The record has already created." +"The recordID is " +teacherRecord.RecordID +"\n";
		
		}
	}

	@Override
	public String createSRecord(String managerID, String firstName, String lastName, String courseRegesitered,
			String status, String statusDate) {

		synchronized (RecordMap) {
		logger.log(Level.INFO,"Receive a request for creating student record from: " + managerID);
		//-------------
		StudentRecord temp = new StudentRecord("SR00000", 
				firstName, lastName, courseRegesitered, status, statusDate);
		//-------------
		if(!CheckRecordIfExist2(temp)){
			logger.log(Level.INFO,"The request record is already exist on the server, the request from "+ managerID+" has been rejected.");
			logger.log(Level.INFO, IterateHashMap());
			return "There is already a record for " + lastName +", " + firstName;
		}
		//-------------
		StudentRecord studentRecord = new StudentRecord(GetCurrentStudentRecordID(), 
				firstName, lastName, courseRegesitered, status, statusDate);
		
		InsertHashMap(studentRecord);
		logger.log(Level.INFO,"The request for creating student record from "+ managerID+" has already handled.");
		IterateHashMap();
		logger.log(Level.INFO, IterateHashMap());
		
		return "The record has already created." +"The recordID is " +studentRecord.RecordID +"\n";
		}
	}

	@Override
	public String getCount(String managerID) {
		logger.log(Level.INFO,"Receive a request for getting the count of records from: " + managerID);
		int count[] = {0,0,0};
		int[] array = {7777,8888,9999};
		int i = 0;
		for(int p :array){
		    logger.info(p + "");
			if(p == this.LocalPortNum){
				Iterator iter = RecordMap.entrySet().iterator(); 
				while (iter.hasNext()) { 
				    Map.Entry entry = (Map.Entry) iter.next(); 
				    Character key = (Character) entry.getKey(); 
				    List<Record> val = (List<Record>) entry.getValue(); 
				    count[i] = count[i] + val.size();

				}
				logger.log(Level.INFO,"The count of records on local is " + count[i]);
				i++;
			}
			else{
					CompletableFuture<Integer> getCount = new CompletableFuture<>();
					new Thread( () -> {

						InetAddress address;
						try {
							address = InetAddress.getByName("localhost");
						
						byte data[] = String.valueOf(p).getBytes();
						DatagramPacket packet = new DatagramPacket(data, data.length, address, p);
						DatagramSocket socket = new DatagramSocket();
						socket.send(packet);
						logger.log(Level.INFO,"Sending a request for count of records on remote server " + address +"/" + p);
						
				        byte data2[] = new byte[1024];
				        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
				        socket.receive(packet2);
				        String reply = new String(data2, 0, packet2.getLength()); 
				        getCount.complete(Integer.parseInt(reply));
				        socket.close();
						}catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}).start();
					try {
						
						count[i] = getCount.get().intValue();
						logger.log(Level.INFO,"Receive a request for count of records on remote server "+ p+" : " + count[i]);
						i++;
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
			}
		}
		
		return count[0] +","+count[1] +"," +count[2];
	}

	@Override
	public int serverGetCount() {
		// TODO Auto-generated method stub
		int count = 0;
		Iterator iter = RecordMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    Character key = (Character) entry.getKey(); 
		    List<Record> val = (List<Record>) entry.getValue(); 
		    count = count + val.size();
		}
		
		return count;
	}
	public List<Record> GetRecordList(){
		// TODO Auto-generated method stub
		List<Record> list = new ArrayList<>();
		Iterator iter = RecordMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    Character key = (Character) entry.getKey(); 
		    List<Record> val = (List<Record>) entry.getValue(); 
//		    System.out.println("There are the record(s) in a same list. (Last name begin with " +key +")" );
		    for(Record record : val){
		    	list.add(record);
		    }
		}
		return list;
	}
	@Override
	public String editRecord(String managerID, String recordID, String fieldName, String newValue) {
		synchronized (RecordMap) {

			Record record =FindRecord(recordID);
			logger.log(Level.INFO,"Receive a request for modifying record from: " + managerID);
			if(record==null){
				logger.log(Level.INFO,"The request record is not exist on the server, the request from "+ managerID+" has been rejected.");
				logger.log(Level.INFO, IterateHashMap());
				return "The record is not in this server.";
			
			}
			else{

				if(!FindRecordWithNewValue(record, fieldName, newValue)){
					logger.log(Level.INFO,"There is already a record with the same information\n "
							+ "The edit request has been rejected.");
					return "There is already a record with the same information\n "
							+ "The edit request has been rejected.";
				}
				record.editField(fieldName, newValue);
		
				IterateHashMap();
				StringBuilder sb = new StringBuilder();

				sb.append("The request for modifying record from "+ managerID+" has already handled.");
				String temp = "The recordID is "+recordID+". The field name is " +fieldName +" , the new value is " +newValue +"\n";
			
				sb.append("The recordID is "+recordID+". The field name is " +fieldName +" , the new value is " +newValue +".");
				logger.log(Level.INFO,sb.toString());
				logger.log(Level.INFO, IterateHashMap());
				return "The record has been edited.\n" +temp;

			}
			}
	}
	public Record FindRecord(String recordID){
		Iterator iter = RecordMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    Character key = (Character) entry.getKey(); 
		    List<Record> val = (List<Record>) entry.getValue(); 
		    
		    for(Record record : val){
		    	if(record.RecordID.equals(recordID)){
		    		return record;
		    	}
		    }
		}
		return null;

	}
	
	public boolean FindRecordWithNewValue(Record record, String fieldName, String newValue){
		if(record.RecordID.startsWith("SR")){
			StudentRecord temp = new StudentRecord(record.RecordID, record.getFirstName(), record.getLastName(), 
					((StudentRecord) record).getCourseRegesitered(), ((StudentRecord) record).getStatus(), ((StudentRecord) record).StatusDate);
			if(fieldName.toLowerCase().equals("coursesregistered")){
				
				temp.CourseRegesitered = newValue;
			}
			else if(fieldName.toLowerCase().equals("status")){
				temp.Status = newValue;
			}
			else if(fieldName.toLowerCase().equals("statusdate")){
				temp.StatusDate = newValue;
			}
			else if(fieldName.toLowerCase().equals("firstname")){
				temp.FirstName = newValue;
			}
			else if(fieldName.toLowerCase().equals("lastname")){
				temp.LastName = newValue;
			}
			return CheckRecordIfExist2(temp);

		}
		if(record.RecordID.startsWith("TR")){
			TeacherRecord temp = new TeacherRecord(record.RecordID, record.FirstName, record.LastName, 
					((TeacherRecord) record).Address, ((TeacherRecord) record).Phone,((TeacherRecord) record).Specialization,
					((TeacherRecord) record).Location);
			if(fieldName.toLowerCase().equals("address")){
				temp.Address = newValue;
			}
			else if(fieldName.toLowerCase().equals("phone")){
				temp.Phone = Long.parseLong(newValue);
			}
			else if(fieldName.toLowerCase().equals("specialization")){
				temp.Specialization = newValue;
			}
			else if(fieldName.toLowerCase().equals("location")){
				temp.Location = newValue;
			}
			else if(fieldName.toLowerCase().equals("firstname")){
				temp.FirstName = newValue;
			}
			else if(fieldName.toLowerCase().equals("lastname")){
				temp.LastName = newValue;
			}
			return CheckRecordIfExist2(temp);
		}
		return true;

	}

	private String getRecordContent(String recordID) {
        ArrayList<Record> list = new ArrayList<>();
        for (List<Record> records : RecordMap.values()) {
            list.addAll(records);
        }
        return list.stream()
                .filter(record -> record.RecordID.equals(recordID))
                .map(record -> {
                    if (record.RecordID.substring(0, 2).equals("SR")){
                        StudentRecord studentRecord = (StudentRecord) record;
                        return studentRecord.format();
                    } else {
                        TeacherRecord teacherRecord = (TeacherRecord) record;
                        return teacherRecord.format();
                    }
                }).findFirst().orElse("content");
    }

	@Override
	public String transferRecord(String managerID, String recordID, String remoteServerName) {
	    String receiveData = "";
		if (!checkRecord(recordID)){
            logger.info(managerID + " can't transfer the record because the server doesn't have the record");
            return managerID + " can't transfer the record because the server doesn't have the record";
        }

		logger.info(managerID + " requests to send transferRecord to " + remoteServerName);
		if (this.name.equals(remoteServerName)){
            logger.info("you don't have to transfer the record to " + remoteServerName);
            return "you don't have to transfer the record to " + remoteServerName;
        }
        logger.info("Sending transfer record whose recordID is " + recordID + " to " + remoteServerName);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            String sendInfo = managerID + "," + this.getRecordContent(recordID);
            byte[] recordInfo = sendInfo.getBytes();
            logger.info(sendInfo);
            InetAddress aHost = InetAddress.getByName("localhost");
            int serverPort = ManagerServerMap.get(remoteServerName);
            DatagramPacket request = new DatagramPacket(recordInfo, sendInfo.length(), aHost, serverPort);
            socket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            receiveData = new String(buffer, 0, reply.getLength());
            if (checkIsTransferSuccess(receiveData)) {
                deleteRecord(recordID);
            }
            logger.info(receiveData);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }


        // TODO Auto-generated method stub
		return receiveData;
	}

    private void deleteRecord(String recordID) {
	    synchronized (RecordMap){
            boolean flag = false;
            for (List<Record> records : RecordMap.values()) {
                for (Record record : records) {
                    if (record.RecordID.equals(recordID)) {
                        records.remove(record);
                        flag = true;
                        break;
                    }
                    if (flag) break;
                }
            }
        }
    }

    private boolean checkIsTransferSuccess(String source) {
        String regExp = "created";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(source);
        return matcher.find();
    }

	private boolean checkRecord(String recordID) {
	    synchronized (RecordMap) {
            ArrayList<Record> list = new ArrayList<>();
            for (List<Record> records : RecordMap.values()) {
                list.addAll(records);
            }
            return  list
                    .stream()
                    .map(record -> record.RecordID)
                    .anyMatch(id -> id.equals(recordID));
        }
	}

	
	public static void main(String[] args) throws IOException {
		
	
		String[] serverName ={"MTL","LVL","DDO"};
		
		for(String name : serverName){
			Server server = new Server(name);
			server.begin();
		}

	}

	private void begin() {
		new Thread( () -> {
			try {
				int port = 1050;
				 try {
                     ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", "-ORBInitialPort", "1050"}, null);
                     POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
                     rootpoa.the_POAManager().activate();
                     org.omg.CORBA.Object ref = rootpoa.servant_to_reference(this);
                     RecordManager ss = RecordManagerHelper.narrow(ref);
                     org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                     NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
                     NameComponent nc = new NameComponent(this.name, "");
                     NameComponent path[] = {nc};
                     ncRef.rebind(path, ss);
                     System.out.println(name + " Server is running . . . ");
                     logger.log(Level.INFO, "The CORBA server has already started, port number:" + port);
                     orb.run();
				 } catch (Exception e) {
				 System.out.println ("Exception: " + e.getMessage());
				 }

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}).start();
		
		 
		new Thread( () -> {
			int port = ManagerServerMap.get(name);

			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(port);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte data[] = new byte[2048];//create packet data byte[]
	        DatagramPacket packet = new DatagramPacket(data, data.length);
			logger.log(Level.INFO, "UDP Server has been started, port number: " + port);

	        while(true){
	            try {
                    socket.receive(packet);
                    String receiveData = new String(data, 0, packet.getLength());
                    InetAddress address = packet.getAddress();
                    int source_port = packet.getPort();
                    if (isPort(receiveData)) {
                        byte res[] = String.valueOf(this.serverGetCount()).getBytes();
                        DatagramPacket packet2 = new DatagramPacket(res, res.length, address, source_port);
                        logger.log(Level.INFO, "Receive a request for GetCount." + this.serverGetCount());
                        socket.send(packet2);
                        logger.log(Level.INFO, "The count has already sent.");
                    } else if (isRecordID(receiveData)) {
                        logger.log(Level.INFO, "Receive a request for transferRecord.");
                        String result = "";
                        String[] strings = receiveData.split(",");
                        if (strings[1].equals("StudentRecord")) {
                            result = createSRecord(strings[0], strings[3], strings[4], strings[5], strings[6], strings[7]);
                        }
                        if (strings[1].equals("TeacherRecord"))
                            result = createTRecord(strings[0], strings[3], strings[4], strings[5], strings[6], strings[7], strings[8]);
                        byte res[] = result.getBytes();
                        DatagramPacket packet2 = new DatagramPacket(res, res.length, address, source_port);
                        socket.send(packet2);
                        logger.info("transferRecord result has been sent");
                    }

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//listening 

	            //

	        }
		}).start();
		
	}

    private boolean isPort(String source) {
	    String regExp = "\\d{4}";
	    return source.matches(regExp);
    }

    private boolean isRecordID(String source) {
        String regExp = "[A-Z]{2}\\d\\d\\d\\d";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(source);
	    return matcher.find();
    }

}
