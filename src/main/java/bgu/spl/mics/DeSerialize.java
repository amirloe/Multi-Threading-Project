package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DeSerialize {
    public static void main(String[] args) {
      HashMap<Integer,Customer> h =  deser (args[0]);
      HashMap<String,Integer> I = deser2(args[1]);
      ArrayList<OrderReceipt> A =  deser3(args[2]);
      MoneyRegister M = deser4(args[3]);
        writeTo(args[4], h.toString());
        writeTo(args[5], I.toString());
        writeTo(args[6], A.toString());
        writeTo(args[7], M.toString());
    }

    private static  void writeTo(String path , String write){

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            String content = write;

            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(content);

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    private static MoneyRegister deser4(String arg) {
       MoneyRegister  o1=null;

        try
        {
            FileInputStream file = new FileInputStream(arg);
            ObjectInputStream in = new ObjectInputStream(file);
            o1 = (MoneyRegister) in.readObject();

            in.close();
            file.close();

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught" + ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
        return o1;

    }

    private static ArrayList<OrderReceipt> deser3(String arg) {
        ArrayList<OrderReceipt>  o1=null;

        try
        {
            FileInputStream file = new FileInputStream(arg);
            ObjectInputStream in = new ObjectInputStream(file);
            o1 = (ArrayList<OrderReceipt> )in.readObject();

            in.close();
            file.close();

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught" + ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
        return o1;
    }


    private static HashMap<String, Integer> deser2(String arg) {
        HashMap<String,Integer>  o1=null;

        try
        {
            FileInputStream file = new FileInputStream(arg);
            ObjectInputStream in = new ObjectInputStream(file);
            o1 = (HashMap<String,Integer>)in.readObject();

            in.close();
            file.close();

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught" + ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
        return o1;
    }

    public static   HashMap<Integer,Customer>  deser(String path){
            HashMap<Integer,Customer>  o1=null;

            try
            {
                FileInputStream file = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(file);
                 o1 = (HashMap<Integer,Customer>)in.readObject();

                in.close();
                file.close();

            }

            catch(IOException ex)
            {
               System.out.println("IOException is caught" + ex);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //return null;
                return o1;
        }

}
