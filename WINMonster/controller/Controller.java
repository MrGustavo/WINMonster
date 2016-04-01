package controller;




import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;

import util.FilaPrioridade;
import util.Huffman.ArvoreHuffman;
import util.Huffman.FolhaHuffman;


/**
 * Classe controller, respons�vel pelo gerenciamento do programa.
 * @author Gustavo Henrique.
 * @author Leonardo Melo.
 * @since 10 de mar�o de 2016.
 *
 */
public class Controller {
	/*---------------------------------------------------------------------------------*/
	private static final int NUM = 256;
	private static Controller instance = new Controller();

	private Controller(){}

	public static Controller getInstance(){
		if(instance == null)
			instance = new Controller();

		return instance;
	}

	public static void zerarSingleton(){
		instance = new Controller();
	}
	/*---------------------------------------------------------------------------------*/
	

	public static int[] calcularFrequencia(String texto){
		int[] frequencias = new int[NUM];
		char[] caracteres = texto.toCharArray();

		for (int i = 0; i < caracteres.length; i++)
			frequencias[caracteres[i]]++;

		return frequencias;
	}
	/*---------------------------------------------------------------------------------*/

	public static FilaPrioridade criarFilaComFrequencias(int[] frequencias){
		FilaPrioridade fila = new FilaPrioridade();

		for(int i = 0; i < frequencias.length; i++){
			if(frequencias[i] > 0){
				FolhaHuffman folha = new FolhaHuffman();
				folha.setFrequencia(frequencias[i]);
				folha.setInfo((char)i);

				fila.inserir(frequencias[i], folha);
			}
		}
		return fila;
	}
	/*---------------------------------------------------------------------------------*/

	public static String[] gerarCodigoHuffman(ArvoreHuffman arvore){
		String[] dicionario = new String[NUM];

		ArvoreHuffman.construirCodigo(dicionario, arvore, "");
		
		/*-----*/
		int i = 0;
		for(String c : dicionario){
			if(c != null)
				System.out.println(c +" "+ (char)i);
			i++;
		}
		/*-----*/

		return dicionario;
	}
	/*---------------------------------------------------------------------------------*/

	public static byte[] escreverCodigo(String[] dicionario, String texto){
		String txtCompact = "";
		String dicioCompact = "";
		long[] aux;
		
		for(int i =0; i < texto.length(); i++){
			txtCompact += dicionario[texto.charAt(i)];
		}
		
		for(int i =0; i < dicionario.length ; i++){
			if(dicionario[i] != null){
				
				dicioCompact += (char)i;
				aux = transformarEmBits(dicionario[i]).toLongArray();
				dicioCompact += (char)aux[0];
				System.out.println(dicioCompact);
				
			}
		}
		dicioCompact += ")))";
		
		byte[] txtBits = transformarEmBits(txtCompact).toByteArray();
		byte[] dicioBits = dicioCompact.getBytes();
		
		byte[] txtTotal = new byte[txtBits.length + dicioBits.length];
		
		for(int i = 0; i< dicioBits.length; i++){
			txtTotal[i] = dicioBits[i];
		}
		for(int i = dicioBits.length; i< dicioBits.length + txtBits.length; i++){
			txtTotal[i] = txtBits[i - dicioBits.length];
		}
		
		return txtTotal;		

	}
	
	/*---------------------------------------------------------------------------------*/
	private static BitSet transformarEmBits(String txt) {
		
		BitSet sequenciaBits = new BitSet(txt.length()+1);
		sequenciaBits.clear();
		
		for(int i = 0; i < txt.length(); i++){
			
			if(txt.charAt(i) == '1'){
				sequenciaBits.set(txt.length() - i - 1);
			}else if(txt.charAt(i) == '0'){
				sequenciaBits.set(txt.length() - i - 1, false);
			}
		}
		
		sequenciaBits.set(txt.length());
		
		return sequenciaBits;
	}

	/*---------------------------------------------------------------------------------*/
	private static int funcaoHash(String codigo){
		
		int numero = 0;
		for(int i = codigo.length(); i> 0; i--){
			if(codigo.charAt(codigo.length() - i) == '1'){
				numero += Math.pow(2,i);
			}
		}
		return numero;
	}
/*
	private static char[] criarTabelaHash(String[] dicionario) {
		int i =0;
		char[] tabelaHash = new char[NUM];
		for(String codigo :dicionario){
			if(codigo != null){
				tabelaHash[funcaoHash(codigo)] = (char) i;
			}
			i++;
		}
		return tabelaHash;
	}
	/*---------------------------------------------------------------------------------*/
	/**
	 * M�todo respons�vel pela leitura do arquivo e retorna um array dos dados lidos.
	 * @param arquivo - Nome do arquivo a ser lido.
	 * @return dados - Array dos caracteres lidos no arquivo.
	 */

	public String lerArquivo(String arquivo){

		StringBuffer dados = new StringBuffer();// String onde ser� armazenada as informa��es lidas.
		FileReader arq = null;// Inst�ncia do arquivo.
		BufferedReader buffer = null;// Inst�ncia do leitor do arquivo.


		try {// Ver como ser� tratado esse erro.
			arq = new FileReader(arquivo);
			buffer = new BufferedReader(arq);


			while (buffer.ready()){//Ir� ser valido at� encontrar o fim do arquivo.
				dados.append(buffer.readLine());// L� linha por linha no arquivo e concatena no final da string dados.
			}
			buffer.close();// Finalizo o leitor do arquivo.
			arq.close();// Finalizo o arquivo.
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dados.toString();
	}
	/*---------------------------------------------------------------------------------*/
	/**
	 * M�todo respons�vel pela escrita dos arquivos seguindo o algoritmo de Huffman.
	 * @param arrayCaractere - Array com os c�digos dos caracteres.
	 * @param nomeArquivo - Nome do arquivo a ser gravado.
	 * @param caminhoArquivo - Caminho ao qual o arquivo ser� armazenado.
	 */
	public static void escreverArquivo(byte[] texto,  String caminhoArquivo){

		FileOutputStream writeStream = null;
		DataOutputStream writeDataStream = null;
		// Aqui � especificado o caminho e o nome do arquivo.

		String nomeCaminho = caminhoArquivo.substring(0, caminhoArquivo.indexOf('.'));
		nomeCaminho += ".monster";
		File arquivo = new File(nomeCaminho);//Inst�ncia do arquivo.

		try {// Ver como vai ser tratado esse tipo de erro.
			arquivo.createNewFile();//Crio o arquivo no diret�rio escolhido.
			writeStream = new FileOutputStream(arquivo);
			writeDataStream = new DataOutputStream(writeStream);

			writeDataStream.write(texto);
			
			writeDataStream.close();
			writeStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*---------------------------------------------------------------------------------*/
	/**
	 * M�todo respons�vel pela compress�o do arquivo escolhido.
	 * @param caminhoArquivo - Localiza��o do arquivo.
	 */

	public void comprimirArquivo(String caminhoArquivo){

		String dadosArquivo = lerArquivo(caminhoArquivo);
		int[] frequenciaCaractere = calcularFrequencia(dadosArquivo);
		FilaPrioridade filaFrequencia = criarFilaComFrequencias(frequenciaCaractere);
		ArvoreHuffman arvoreHuffman = filaFrequencia.gerarArvoreHuffman();
		String[] dicionario = gerarCodigoHuffman(arvoreHuffman);

		/*
		 * Abaixo � criado a String que armazenar� o dicion�rio e o arquivo codificado para escrita.
		 * O tamanho � definido como a soma do tamanho da array de dados + o tamanho do dicionario + dois
		 * caracteres que servir�o para definir o inicio e o fim do dicion�rio.
		 */
		escreverArquivo(escreverCodigo(dicionario, dadosArquivo), caminhoArquivo);

	}
	/*---------------------------------------------------------------------------------*/
	
	public void descomprimirArquivo(String caminhoArquivo){
		String dadosArquivo = lerArquivo(caminhoArquivo);
		String traducao = traduzirCodigo(dadosArquivo);
		escreverArquivo(traducao.getBytes(), caminhoArquivo);
	}

	/**
	 * M�todo que recebe os dados lidos do arquivo e o dicion�rio e cria o array com os caracteres codificados.
	 * @param dicionario - Dicion�rio com os c�digos.
	 * @param dadosArquivo - Os dados lidos do arquivo.
	 * @return dadosArquivoCodificado - String com a substitui��o dos caracteres.
	 */
	/*public byte[] substituirCaractere(String[] dicionario, char[] dadosArquivo){

		StringBuffer dadosArquivoCodificado = new StringBuffer();
		for(int i = 0; i < dadosArquivo.length; i++){
			if(dadosArquivo[i] != '\0')
				dadosArquivoCodificado.append(dicionario[dadosArquivo[i]]);
		}
		int tam = dadosArquivoCodificado.length() / 8;
		StringBuffer dados = new StringBuffer();
		byte[] dadosByte; 
		if(dadosArquivoCodificado.length() % 8 == 0){
			dadosByte = new byte[tam];
		}
		else{
			dadosByte = new byte[++tam];
		}
		StringBuffer string;
		int pos = 0;
		if(dadosArquivoCodificado.length() >= 8){
			for(int i = 0; i != tam; i++){
				string = new StringBuffer();
				for(int j = 0; j < 8; j++){
					string.append(dadosArquivoCodificado.toString().charAt(j));
					pos = j;
				}
				dadosByte[i] = (byte) Integer.parseInt(string.toString(), 2);
			}
		}
		if(dadosArquivoCodificado.length() % 8 != 0){

			dadosByte[tam - 1] = (byte) Integer.parseInt(dadosArquivoCodificado.substring(pos), 2);

		}

		return dadosByte;
	}*/


	private String traduzirCodigo(String dadosArquivo) {
		char[] dicionario = recuperarDicionario(dadosArquivo);
		
		String texto = recuperarTexto(dadosArquivo);
		BitSet textoBits = BitSet.valueOf(texto.getBytes());
		String textoCodigo = transformarBitsEmString(textoBits);
		String traducao = tarduzirTexto(dicionario, textoCodigo);
		
		return traducao;
	}

	private String recuperarTexto(String dadosArquivo) {
		StringBuffer buff = new StringBuffer(dadosArquivo);
		
		buff.replace(0 ,buff.indexOf(")))")+3, "");
		
		System.out.println(buff.toString());
		return buff.toString();
	}

	private String tarduzirTexto(char[] dicionario, String textoCodigo) {
		String traducao = "";
		StringBuffer buff = new StringBuffer(textoCodigo);
		String aux;
		boolean flag;
		System.out.println(buff.toString());
		//System.out.println(buff.reverse().toString());
		buff = buff.reverse();
		
		while(buff.length() > 0){
			aux = "";
			flag = true;
			
			while(flag){
				aux += buff.charAt(0);
				System.out.println(aux);
				buff.deleteCharAt(0);
				if(funcaoHash(aux) > dicionario.length)break;
				if(dicionario[funcaoHash(aux)] != 0){
					traducao += dicionario[funcaoHash(aux)];
					flag = false;
				}
				if(buff.length() == 0)
					flag = false;
			}
		}
		System.out.println("Tradu��o: " + traducao);
		return traducao;
	}

	private char[] recuperarDicionario(String dadosArquivo) {
		StringBuffer buff = new StringBuffer(dadosArquivo);
		
		System.out.println(buff.toString());
		buff.replace(buff.indexOf(")))"), buff.length(), "");
		System.out.println(buff.toString());
		
		byte[] dicionarioTxt = (buff.toString()).getBytes();
		char[] dicionario = new char[NUM*NUM];
		String codigo = new String();
		long[] codigoUnit = new long[1];
		
		for(int i =0; i+1 < dicionarioTxt.length; i += 2){
			codigoUnit[0] = dicionarioTxt[i+1];
			//codigoUnit[1] = dicionarioTxt[i+2];
			codigo = transformarBitsEmString(BitSet.valueOf(codigoUnit));
			dicionario [funcaoHash(codigo)] = (char)(dicionarioTxt[i]) ;
			System.out.println(dicionario [funcaoHash(codigo)] +" "+ codigo);
		}
		
		return dicionario;
		
	}

	private String transformarBitsEmString(BitSet bits) {
		String stringCod = "";
		for(int i = 0; i < bits.length(); i++){
			if(bits.get(bits.length() - i -1)){
				
				stringCod += '1';
			}else{
				stringCod += '0';
			}
			
		}
		StringBuffer buff = new StringBuffer(stringCod);
		return buff.reverse().toString();
	}
}
