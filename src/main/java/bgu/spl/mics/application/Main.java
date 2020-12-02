package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
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
		try {
			Gson gson = new Gson();
			Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(path)));
			operationData = gson.fromJson(reader,operationData.getClass());
		} catch (IOException e) {
			e.printStackTrace();
		}

		LeiaMicroservice leia = new LeiaMicroservice(operationData.getAttacks());
		HanSoloMicroservice han = new HanSoloMicroservice();
		C3POMicroservice c3po = new C3POMicroservice();
		R2D2Microservice r2d2 = new R2D2Microservice(operationData.getR2D2());
		LandoMicroservice lando = new LandoMicroservice(operationData.getLando());

		int ewok = operationData.getEwoks();
		Ewoks ewoks= Ewoks.getSingle_instance();
		for (int i=1;i<=ewok;i++){
			ewoks.add(new Ewok(i));
		}
	}
}
