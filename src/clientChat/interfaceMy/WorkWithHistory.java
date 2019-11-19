package clientChat.interfaceMy;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class WorkWithHistory {
    private String nameOfFile;
    private File file;
    private ArrayList<String> arrayHistory = new ArrayList<>();


    public static void main(String[] args) {
        try {
            WorkWithHistory ww = new WorkWithHistory("login1");
            System.out.println(ww.takeLastHistory());

//            for (int i = 0; i < 150; i++) {
//               ww.addHistory(i+" ");
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WorkWithHistory(String nameOfFile) throws IOException {
        this.file = new File("history/"+nameOfFile+".txt");
        if(!file.exists()){
            file.createNewFile();
            addHistory(new Date(System.currentTimeMillis()).toString());
        }
        else {
            addHistory(new Date(System.currentTimeMillis()).toString());
        }
    }

    public void addHistory(String message)  {
        try {
            FileWriter fileWriter = new FileWriter(this.file,true);
            fileWriter.write(message+"\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String takeLastHistory() {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(this.file),"UTF-8");
            String message;
            FileReader fr = new FileReader(this.file);
            BufferedReader bf = new BufferedReader(fr);
            while ((message = bf.readLine())!=null){
                arrayHistory.add(message);
                //System.out.println(message);
            }

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listToString(doCollection(this.arrayHistory));
    }

    private ArrayList<String> doCollection(ArrayList<String> arrayHistoryIn) {
        int size = arrayHistoryIn.size();
        if(size<100){
            return this.arrayHistory;
        } else{
            ArrayList<String> arrayOut= new ArrayList<>();
            for (int i = size-100; i < size; i++) {
                arrayOut.add(arrayHistoryIn.get(i));
            }
            return arrayOut;
        }
    }

    private String listToString(ArrayList<String> arrayHistoryIn){
        String out="";
        for (int i = 0; i < arrayHistoryIn.size(); i++) {
            out=out+arrayHistoryIn.get(i)+System.lineSeparator();
        }
        return out;
    }
}
