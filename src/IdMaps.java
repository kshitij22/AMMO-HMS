package ammo;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;

import java.sql.ResultSet;

public class IdMaps implements Serializable {

    HashMap<Long,String> idMaps;

    public IdMaps(HashMap<Long,String> idMaps) {
	this.idMaps = idMaps;
    }

    public IdMaps(String objectname) {
	this.idMaps = restoreIdMap(objectname);
    }

    public IdMaps() {

    }

    public String getName(Long node) {
	String name = idMaps.get(node);
	if (name == null)
	    return "";
	else
	    return name;
    }

    public void writeIdMap(String filename,HashMap<Long,String> idMap) {
	try {
	    FileOutputStream fos = new FileOutputStream(filename);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(idMap);
	    oos.close();
	} catch (Exception e) {
	    System.out.println("Exception : " + e.getMessage());
	}
    }

    public HashMap<Long,String> restoreIdMap(String filename) {
	try {
	    FileInputStream fis = new FileInputStream(filename);
	    ObjectInputStream ois = new ObjectInputStream(fis);
	    HashMap<Long,String> idMaps = (HashMap<Long,String>) ois.readObject();
	    ois.close();
	    return idMaps;
	} catch (Exception e) {
	    System.out.println("Exception : " + e.getMessage());
            return null;
	}

    }




    public static void main(String [] args) throws Exception {

	IdMaps idMap = new IdMaps(args[0]);
	System.out.println(idMap.getName(new Long(5951525)));
	/*   HashMap<Long,String> idMaps = new HashMap<Long,String> ();
	
	     Resource resource = new Resource("ncbo-obsdb1.sunet" , "obs_stage", "ammo", "ammo");

	     ResultSet rs = resource.statement.executeQuery("SELECT termName,conceptID from OBS_TT ");
	     rs.beforeFirst();
	     while(rs.next()) {
	     String name = rs.getString("termName");
	     Long id = rs.getLong("conceptID");
	     idMaps.put(id,name);
	     }
	     rs.close();

	     idMap.writeIdMap("idMaps.obj",idMaps);

	*/
    }
	    

} 