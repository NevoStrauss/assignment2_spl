package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		Map<String,String> map = gson.fromJson(args.toString(),new TypeToken<HashMap<String,String>>(){}.getType());
		Map<String,List<Map>> map1 = gson.fromJson(gson.toJson(map.get("attacks")),new TypeToken<HashMap<String,List<Map>>>(){}.getType());

		int ewok = 5;
		Ewoks ewoks= Ewoks.getSingle_instance();
		for (int i=1;i<=ewok;i++){
			ewoks.add(new Ewok(i));
		}

		Attack[] attacks = new Attack[5];
		LeiaMicroservice leia = new LeiaMicroservice(attacks,2);
		HanSoloMicroservice han = new HanSoloMicroservice();
		C3POMicroservice c3po = new C3POMicroservice();
		Thread leiaThread = new Thread(leia);
		leiaThread.start();
		Thread hanThread = new Thread(han);
		Thread c3poThread = new Thread(c3po);
		hanThread.start();
		c3poThread.start();



	}
}
