package util.Huffman;

public class CelulaHuffman extends ArvoreHuffman{

	private ArvoreHuffman esquerda;
	private ArvoreHuffman direita;
	
	public ArvoreHuffman getEsquerda() {
		return esquerda;
	}
	public void setEsquerda(ArvoreHuffman esquerda) {
		this.esquerda = esquerda;
	}
	public ArvoreHuffman getDireita() {
		return direita;
	}
	public void setDireita(ArvoreHuffman direita) {
		this.direita = direita;
	}
	
}