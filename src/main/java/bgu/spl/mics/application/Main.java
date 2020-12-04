package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		OperationData operationData = new OperationData();
		String path = args[0];
		Gson gson = new Gson();
		try {
			Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(path)));
			operationData = gson.fromJson(reader,OperationData.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MicroService[] microServices = new MicroService[5];

		microServices[0] = new LeiaMicroservice(operationData.getAttacks());
		microServices[1] = new HanSoloMicroservice();
		microServices[2] = new C3POMicroservice();
		microServices[3] = new R2D2Microservice(operationData.getR2D2());
		microServices[4] = new LandoMicroservice(operationData.getLando());

		int ewok = operationData.getEwoks();
		Ewok[] ewoksArray = new Ewok[ewok+1];
		for (int i=1;i<=ewok;i++){
			ewoksArray[i] = new Ewok(i);
		}
		Ewoks.getInstance().setEwokArray(ewoksArray);
		Thread[] threads = new Thread[microServices.length];
		for(int i =0 ; i<threads.length;i++){
			threads[i] = new Thread(microServices[i]);
			System.out.println(microServices[i].getName()+" is "+threads[i].getName());
		}
		for (int i = 0 ; i < threads.length; i++){
			threads[i].start();
		}
		for (int i = 0 ; i < threads.length; i++){
			try {
				threads[i].join();
			}catch (InterruptedException ignored){}
		}

		try (FileWriter writer = new FileWriter(args[1])) {
			gson.toJson(Diary.getInstance(), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(gson.toJson(Diary.getInstance()));
		MessageBusImpl.getInstance().clean();
	}
}
