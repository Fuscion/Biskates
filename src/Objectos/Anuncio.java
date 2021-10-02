package Objectos;

import java.time.LocalDateTime;
import java.util.ArrayList;

import spark.Request;

/* anuncio_id INT AUTO_INCREMENT,
    anuncio_pessoa_id INT NOT NULL,
    anuncio_categoria_id INT NOT NULL,
    titulo VARCHAR(30) NOT NULL,
    descricao VARCHAR(50) NOT NULL,
    imagem VARCHAR(200) DEFAULT NULL,
    estado BOOL DEFAULT TRUE,
    valor DECIMAL(8,2) DEFAULT NULL,
    data_hora DATETIME NOT NULL,
    anuncio_concelho_id INT NOT NULL,
    PRIMARY KEY (anuncio_id)*/

public class Anuncio {

	private int anuncioPessoaID;
	private int anuncioCategoriaID;
	private String titulo;
	private String descricao;
	private String imagem;
	private boolean estado;
	private float valor;
	private LocalDateTime dataHora;
	private int anuncioConcelhoID;
	private int anuncioID;

	/**
	 * @param anuncioCategoriaID
	 * @param object
	 * @param anuncioPessoaID
	 * @param anuncioCategoriaID
	 * @param titulo
	 * @param descricao
	 * @param imagem
	 * @param estado
	 * @param valor
	 * @param dataHora
	 * @param anuncioConcelhoID
	 * @param i
	 */
	public Anuncio(Request anuncio, User user, int anuncioConcelhoID, int anuncioCategoriaID) {

		this.anuncioPessoaID = user.getPessoaID();
		this.anuncioCategoriaID = anuncioCategoriaID;
		this.titulo = anuncio.queryParams("titulo");
		this.descricao = anuncio.queryParams("descricao");
		this.estado = true;
		this.valor = Float.parseFloat(anuncio.queryParams("valor"));
		this.dataHora = LocalDateTime.now();
		this.anuncioConcelhoID = anuncioConcelhoID;
		if(!anuncio.queryParams("imagem").isEmpty())
			this.imagem ="/images/"+anuncio.queryParams("imagem");
		else 
			this.imagem= "/images/default_anuncio.png";

	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(anuncioPessoaID);
		builder.append("#%&§ ");
		builder.append(anuncioCategoriaID);
		builder.append("#%&§ ");
		builder.append(titulo);
		builder.append("#%&§ ");
		builder.append(descricao);
		builder.append("#%&§ ");
		builder.append(imagem);
		builder.append("#%&§ ");
		builder.append(estado ? 1 : 0);
		builder.append("#%&§ ");
		builder.append(valor);
		builder.append("#%&§ ");
		builder.append(dataHora);
		builder.append("#%&§ ");
		builder.append(anuncioConcelhoID);

		return builder.toString();
	}

	public ArrayList<String> toCollection() {
		ArrayList<String> collection = new ArrayList<String>();
		String[] str = this.toString().split("#%&§ ");

		for (String s : str)
			collection.add(s);

		return collection;
	}

	/**
	 * @return the anuncioPessoaID
	 */
	public int getAnuncioPessoaID() {
		return anuncioPessoaID;
	}

	/**
	 * @param anuncioPessoaID the anuncioPessoaID to set
	 */
	public void setAnuncioPessoaID(int anuncioPessoaID) {
		this.anuncioPessoaID = anuncioPessoaID;
	}

	/**
	 * @return the anuncioCategoriaID
	 */
	public int getAnuncioCategoriaID() {
		return anuncioCategoriaID;
	}

	/**
	 * @param anuncioCategoriaID the anuncioCategoriaID to set
	 */
	public void setAnuncioCategoriaID(int anuncioCategoriaID) {
		this.anuncioCategoriaID = anuncioCategoriaID;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the imagem
	 */
	public String getImagem() {
		return imagem;
	}

	/**
	 * @param imagem the imagem to set
	 */
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	/**
	 * @return the estado
	 */
	public boolean isEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	/**
	 * @return the valor
	 */
	public float getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(float valor) {
		this.valor = valor;
	}

	/**
	 * @return the dataHora
	 */
	public LocalDateTime getDataHora() {
		return dataHora;
	}

	/**
	 * @param dataHora the dataHora to set
	 */
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	/**
	 * @return the anuncioConcelhoID
	 */
	public int getAnuncioConcelhoID() {
		return anuncioConcelhoID;
	}

	/**
	 * @param anuncioConcelhoID the anuncioConcelhoID to set
	 */
	public void setAnuncioConcelhoID(int anuncioConcelhoID) {
		this.anuncioConcelhoID = anuncioConcelhoID;
	}

}
