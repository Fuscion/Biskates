package Objectos;

import java.sql.Date;
import java.util.ArrayList;

import spark.Request;

public class NovaConta {

	private String email;
	private String password;
	private boolean suspenso;
	private int tipoConta;
	private float avaliacaoMedia;
	private String fotoPerfil;

	private String nome;
	private Date dataNascimento;
	private int pessoaContaID;
	private String contacto;
	private int concelhoID;
	private String concelho;

	public NovaConta(Request novaConta, int concelhoID) {

		this.email = novaConta.queryParams("email");
		this.password = novaConta.queryParams("password");
		this.suspenso = false;
		this.tipoConta = 1;
		this.avaliacaoMedia = 0F;
		if ((Integer.parseInt(novaConta.queryParams("avatar"))) == 1)
			this.fotoPerfil = "/images/male.png";
		else
			this.fotoPerfil = "/images/female.png";

		this.nome = novaConta.queryParams("firstName") + " " + novaConta.queryParams("lastName");
		this.dataNascimento = Date.valueOf(novaConta.queryParams("birthday"));
		this.pessoaContaID = -1;
		this.contacto = novaConta.queryParams("contact");
		this.concelhoID = concelhoID;
		this.concelho = novaConta.queryParams("myConcelho");

	}

	public String contaToString() {
		StringBuilder builder = new StringBuilder();
		builder.append("");
		builder.append(email);
		builder.append(", ");
		builder.append(password);
		builder.append(", ");
		builder.append(suspenso ? 1 : 0);
		builder.append(", ");
		builder.append(tipoConta);
		builder.append(", ");
		builder.append(avaliacaoMedia);
		builder.append(", ");
		builder.append(fotoPerfil);

		return builder.toString();
	}

	public ArrayList<String> toCollection() {
		ArrayList<String> collection = new ArrayList<String>();
		String str[] = this.contaToString().split(", ");

		for (String s : str)
			collection.add(s);

		return collection;
	}

	public String pessoaToString() {
		StringBuilder builder = new StringBuilder();
		builder.append("");
		builder.append(nome);
		builder.append(", ");
		builder.append(dataNascimento.toString());
		builder.append(", ");
		builder.append(pessoaContaID);
		builder.append(", ");
		builder.append(contacto);
		builder.append(", ");
		builder.append(concelhoID);
		return builder.toString();

	}

	public ArrayList<String> pessoaToCollection() {
		ArrayList<String> collection = new ArrayList<String>();
		String str[] = this.pessoaToString().split(", ");

		for (String s : str)
			collection.add(s);

		return collection;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the suspenso
	 */
	public boolean isSuspenso() {
		return suspenso;
	}

	/**
	 * @param suspenso the suspenso to set
	 */
	public void setSuspenso(boolean suspenso) {
		this.suspenso = suspenso;
	}

	/**
	 * @return the tipoConta
	 */
	public int getTipoConta() {
		return tipoConta;
	}

	/**
	 * @param tipoConta the tipoConta to set
	 */
	public void setTipoConta(int tipoConta) {
		this.tipoConta = tipoConta;
	}

	/**
	 * @return the avaliacaoMedia
	 */
	public float getAvaliacaoMedia() {
		return avaliacaoMedia;
	}

	/**
	 * @param avaliacaoMedia the avaliacaoMedia to set
	 */
	public void setAvaliacaoMedia(float avaliacaoMedia) {
		this.avaliacaoMedia = avaliacaoMedia;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the dataNascimento
	 */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	/**
	 * @param dataNascimento the dataNascimento to set
	 */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * @return the pessoaContaID
	 */
	public int getPessoaContaID() {
		return pessoaContaID;
	}

	/**
	 * @param pessoaContaID the pessoaContaID to set
	 */
	public void setPessoaContaID(int pessoaContaID) {
		this.pessoaContaID = pessoaContaID;
	}

	/**
	 * @return the contacto
	 */
	public String getContacto() {
		return contacto;
	}

	/**
	 * @param contacto the contacto to set
	 */
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	/**
	 * @return the concelhoID
	 */
	public int getConcelhoID() {
		return concelhoID;
	}

	/**
	 * @param concelhoID the concelhoID to set
	 */
	public void setConcelhoID(int concelhoID) {
		this.concelhoID = concelhoID;
	}

	/**
	 * @return the concelho
	 */
	public String getConcelho() {
		return concelho;
	}

	/**
	 * @param concelho the concelho to set
	 */
	public void setConcelho(String concelho) {
		this.concelho = concelho;
	}

}
