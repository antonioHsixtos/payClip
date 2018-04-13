
import java.io.*;
import java.util.*;
import org.json.*;
import org.apache.commons.io.FileUtils;


/*
 *
 * @author andreadesantosespinosa
 */
public class Test {

    public Test() {}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Hello, welcome to this adventure! \n");

        File transactionFile = new File("transactions.json");

        if (transactionFile.exists()) {
            System.out.println("The file transactions already exists!!");
        } else {
            try {
                if (transactionFile.createNewFile()) {
                    System.out.println("The file transactions was created!");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e);
            }
        }

        String action = args[1];

        switch (action) {
            case "add":   
                String showTransaction = addTransaction(args[0], args[2]);
                System.out.println("Response: "+showTransaction);
                break;
            case "list":
                String showList = listTransaction(args[0]);
                System.out.println("Response: "+showList);
                break;
            case "sum":
                Float showAmount = sumTransaction(args[0]);
                System.out.println("Response: $"+showAmount.toString());
                break;
            default:
                String showData = showTransaction(args[0], args[1]);
                System.out.println("Response: "+showData);
        }
    }

    public static String addTransaction(String userId, String transaction)throws Exception {
        String transactionId = getSaltString();
        JSONObject jsonObj = new JSONObject(transaction);
        jsonObj.put("transaction_id", transactionId);
        
        File transactionFile = new File("transactions.json");
        if(transactionFile.canWrite()){
            FileWriter writeFile = null;
            PrintWriter pw = null;
            try
            {   
                File file = new File("transactions.json");
                String content = FileUtils.readFileToString(file, "utf-8");
                JSONArray element   = new JSONArray();
                JSONObject rootJson = new JSONObject();
                if(!content.isEmpty()){
                    JSONObject transactionJsonObject = new JSONObject(content); 
                    JSONArray dataArrayOE = transactionJsonObject.getJSONArray("data");
                    
                    for (int i = 0; i < dataArrayOE.length(); i++) {
                        JSONObject jsonDato = new JSONObject(dataArrayOE.get(i).toString());
                        JSONObject el = new JSONObject();    
                        Float dataAmount = Float.parseFloat(jsonDato.optString("amount"));
                        
                        el.put("transaction_id", jsonDato.optString("transaction_id"));
                        el.put("amount", dataAmount);
                        el.put("description", jsonDato.getString("description"));
                        el.put("date", jsonDato.optString("date"));
                        el.put("user_id", jsonDato.getInt("user_id"));
                        //System.out.println(el.toString(4));
                        element.put(el);
                    }       
                }
                JSONObject el = new JSONObject(); 
                Float dataAmount = Float.parseFloat(jsonObj.optString("amount"));
                el.put("transaction_id", jsonObj.optString("transaction_id"));
                el.put("amount", dataAmount);
                el.put("description", jsonObj.getString("description"));
                el.put("date", jsonObj.optString("date"));
                el.put("user_id", jsonObj.getInt("user_id"));
                element.put(el);
                rootJson.put("data", element);
                writeFile = new FileWriter("transactions.json");
                pw = new PrintWriter(writeFile);
                pw.println(rootJson.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != writeFile)
                    writeFile.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } 
        }else{
            return "The file cannot write";
        }
        return jsonObj.toString();
    }

    public static String showTransaction (String userId, String transactionId) throws Exception {
        File file = new File("transactions.json");
        String content = FileUtils.readFileToString(file, "utf-8");
        JSONObject transactionJsonObject = new JSONObject(content); 
        JSONArray dataArray = transactionJsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonDato = new JSONObject(dataArray.get(i).toString());
            if(Integer.parseInt(userId)==Integer.parseInt(jsonDato.optString("user_id")) && transactionId.equals(jsonDato.getString("transaction_id"))){
                return dataArray.get(i).toString();   
            }            
        }
        return "Transaction not found"; 
    }

    public static String listTransaction(String userId) throws Exception {
        File file = new File("transactions.json");
        String content = FileUtils.readFileToString(file, "utf-8");
        JSONObject jsonObj = new JSONObject(content);
        JSONArray jsonArr = jsonObj.getJSONArray("data");
        ;
        JSONArray sortedJsonArray = new JSONArray();
        Integer cont = 0;
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonDato = new JSONObject(jsonArr.get(i).toString());
            if(Integer.parseInt(userId) == Integer.parseInt(jsonDato.optString("user_id"))){
                jsonValues.add(jsonDato);
                cont++;
            }
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "date";
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                } 
                catch (JSONException e) {
                    System.out.println("Error: "+e.getMessage());
                }
                return valA.compareTo(valB);
            }
        });   
        for (int i = 0; i < cont; i++) {
            
            sortedJsonArray.put(jsonValues.get(i));    
        }
        
        return sortedJsonArray.toString();
    }

    public static Float sumTransaction(String userId) throws Exception {
        
        File file = new File("transactions.json");
        String content = FileUtils.readFileToString(file, "utf-8");
        JSONObject transactionJsonObject = new JSONObject(content); 
        JSONArray dataArray = transactionJsonObject.getJSONArray("data");
        
        Float globalAmount = Float.parseFloat("0.00");
        
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonDato = new JSONObject(dataArray.get(i).toString());
            if(Integer.parseInt(userId)==Integer.parseInt(jsonDato.optString("user_id"))){
                    Float amountData = Float.parseFloat(jsonDato.optString("amount"));
                    globalAmount+= amountData;
            }            
        }
        return globalAmount;    
    }

    protected static String getSaltString() {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

}
