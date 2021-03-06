package teste;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import controller.ControllerArquivo;

public class LerArquivoTest {

	private ControllerArquivo controller = null;
	@Before
	public void setUp() throws Exception {
		ControllerArquivo.zerarSingleton();
		controller = ControllerArquivo.getInstance();

	}

	@Test
	public void LerArquivoSucesso() {

		File file = new File("arquivo.txt");
		FileWriter fileWrite = null;
		BufferedWriter buffer = null;
		try {
			file.createNewFile();
			fileWrite = new FileWriter(file);
			buffer = new BufferedWriter(fileWrite);
			
			buffer.write("ola mundo");
			
			file.delete();
			buffer.close();
			fileWrite.close();
			
		} catch (IOException e) {
			fail();
		}
		
		String string = controller.lerArquivo("arquivo.txt");
		String frase = "ola mundo";
		assertEquals(frase, string);

	}

}
