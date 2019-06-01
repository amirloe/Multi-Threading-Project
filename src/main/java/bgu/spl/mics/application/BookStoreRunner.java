package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    private static AtomicInteger numOfInitializedServices = new AtomicInteger(0);

    /**
     * A Global Counter who counts how many services finished initiallize
     */
    public static void initialService() {
        numOfInitializedServices.incrementAndGet();
    }

    public static void main(String[] args) {


        //0 Loading the json file to a jsonObject called obj
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(args[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson g = new Gson();
        JsonObject obj = g.fromJson(bf, JsonObject.class);

        //1 PARSE THE BOOK INVENTORY
        JsonArray inventory = obj.getAsJsonArray("initialInventory");
        parseAndLoadInventory(inventory);

        //2 PARSE THE VHEICELS

        JsonArray resourceHolder = obj.getAsJsonArray("initialResources");
        parseAndLoadVehicals(resourceHolder);

        //3 PARSE THE SERVICES
        JsonObject services = obj.getAsJsonObject("services");
        Vector<MicroService> Services = InitialAndBuildServices(services);

        //3.1 PARSE THE TIME SERVICE

        JsonObject timeService = services.getAsJsonObject("time");
        TimeService timer = new TimeService(timeService);

        //3.3 PARSE THE API SERVICE

        JsonArray theCust = services.getAsJsonArray("customers");
        Vector<Customer> customers = new Vector<>();

        for (int i = 0; i < theCust.size(); i++) {
            customers.add(new Customer(theCust.get(i).getAsJsonObject()));
            List<Pair<String, Integer>> orderSchedule = getTheSchedule(theCust.get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray());

            Services.add(new APIService(customers.get(i), orderSchedule));
        }


        //START THE THREADS
        //Array size= num of services + timer service
        Thread[] threads = new Thread[Services.size() + 1];
        int currThreadIndex = 0;

        for (MicroService m : Services) {
            threads[currThreadIndex] = new Thread(m);
            threads[currThreadIndex].start();
            currThreadIndex++;
        }

        //BUSY WAIT
        while (numOfInitializedServices.get() != Services.size()) {
        }

        threads[currThreadIndex] = new Thread(timer);
        threads[currThreadIndex].start();
        try {
            for (Thread t : threads)
                t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //  write files
        //Creates HashMap<id,Cust>

        HashMap<Integer, Customer> custHashMap = new HashMap<>();
        for (Customer c : customers) {
            custHashMap.put(c.getId(), c);

        }

        serializeTheCust(args[1], custHashMap);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        serializeTheRegister(args[4]);


    }

    /**
     * construct all the {@link MicroService}
     * @param services a Json object that holds intel about the amount
     * of required services
     * @return a Vector holding all the {@link MicroService}
     */
    private static Vector<MicroService> InitialAndBuildServices(JsonObject services) {

        //3.2 PARSE THE SIMPLE SERVICES
        Vector<MicroService> Services = new Vector<>();
        int numOfSellingServices = services.get("selling").getAsInt();
        int numOfInventoryServices = services.get("inventoryService").getAsInt();
        int numOfLogisticsServices = services.get("logistics").getAsInt();
        int numOfResourceServices = services.get("resourcesService").getAsInt();
        InitialSellingServices(Services, numOfSellingServices);
        InitialInventoryServices(Services, numOfInventoryServices);
        InitialLogisticsServices(Services, numOfLogisticsServices);
        InitialResourceServices(Services, numOfResourceServices);

        return Services;
    }

    /**
     * constructing an array of {@link DeliveryVehicle} using the data in
     * given Json Object , and calls the load methods of the {@link ResourcesHolder}
     * @param resourceHolder a Json Object representing the {@link DeliveryVehicle}
     */
    private static void parseAndLoadVehicals(JsonArray resourceHolder) {

        JsonArray vehicles = resourceHolder.get(0).getAsJsonObject().getAsJsonArray("vehicles");
        DeliveryVehicle[] arrayOfVehicles = new DeliveryVehicle[vehicles.size()];
        //2.1 BUILD EACH DELIVERYVEHICLE
        for (int i = 0; i < vehicles.size(); i++) {
            arrayOfVehicles[i] = new DeliveryVehicle(vehicles.get(i).getAsJsonObject());
        }
        ResourcesHolder.getInstance().load(arrayOfVehicles);

    }

    /**
     * constructing an array of {@link BookInventoryInfo} using the data in
     *  given Json Object , and calls the load methods of the {@link Inventory}
     * @param inventory a Json Object representing the {@link Inventory}
     */
    private static void parseAndLoadInventory(JsonArray inventory) {
        BookInventoryInfo[] booksArray = new BookInventoryInfo[inventory.size()];

        //1.1 BUILD EACH BOOK INVENTORY INFO
        for (int i = 0; i < inventory.size(); i++) {
            booksArray[i] = new BookInventoryInfo(inventory.get(i).getAsJsonObject());
        }
        Inventory.getInstance().load(booksArray);

    }

    /**
     * serialize the {@link MoneyRegister } object
     * @param fileName the file path in which the serialized object will be saved in.
     */
    private static void serializeTheRegister(String fileName) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            outputStream.writeObject(MoneyRegister.getInstance());
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * serialized the given {@link HashMap}
     * @param fileName the file path in which the serialized object will be saved in.
     * @param toOutput an {@link HashMap} holding a Customer's Id as a key and
     *                  {@link Customer} as a value
     */
    private static void serializeTheCust(String fileName, HashMap<Integer, Customer> toOutput) {
        try {

            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            outputStream.writeObject(toOutput);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param scheduleAsJson an array of the ordering schedule
     * @return a list of pairs of the title book and the price
     */
    private static List<Pair<String, Integer>> getTheSchedule(JsonArray scheduleAsJson) {
        List<Pair<String, Integer>> output = new Vector<>();

        for (int i = 0; i < scheduleAsJson.size(); i++) {
            JsonObject toPair = scheduleAsJson.get(i).getAsJsonObject();
            int tick = toPair.get("tick").getAsInt();
            String bookName = toPair.get("bookTitle").getAsString();

            Pair<String, Integer> temp = new Pair<>(bookName, tick);
            output.add(temp);
        }
        return output;
    }

    /**
     * constructing the {@link ResourceService} and add the to the services's Vector
     * @param services Vector holding all the {@link MicroService } int the program
     * @param numOfResourceServices number of required {@link ResourceService}
     */
    private static void InitialResourceServices(Vector<MicroService> services, int numOfResourceServices) {
        for (int i = 0; i < numOfResourceServices; i++) {
            services.add(new ResourceService(i));
        }
    }

    /**
     * constructing the {@link LogisticsService} and add the to the services's Vector
     * @param services Vector holding all the {@link MicroService } int the program
     * @param numOfLogisticsServices number of required {@link LogisticsService}
     */
    private static void InitialLogisticsServices(Vector<MicroService> services, int numOfLogisticsServices) {
        for (int i = 0; i < numOfLogisticsServices; i++) {
            services.add(new LogisticsService(i));
        }
    }

    /**
     * constructing the {@link InventoryService} and add the to the services's Vector
     * @param services Vector holding all the {@link MicroService } int the program
     * @param numOfInventoryServices number of required {@link InventoryService}
     */
    private static void InitialInventoryServices(Vector<MicroService> services, int numOfInventoryServices) {
        for (int i = 0; i < numOfInventoryServices; i++) {
            services.add(new InventoryService(i));
        }
    }

    /**
     * constructing the {@link SellingService} and add the to the services's Vector
     * @param services Vector holding all the {@link MicroService } int the program
     * @param numOfSellingServices number of required {@link SellingService}
     */
    private static void InitialSellingServices(Vector<MicroService> services, int numOfSellingServices) {
        for (int i = 0; i < numOfSellingServices; i++) {
            services.add(new SellingService(i));
        }
    }



}
