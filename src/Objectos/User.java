package Objectos;

import java.sql.Date;
import java.text.SimpleDateFormat;

import mySqlUtils.DBRow;

public class User {

	private int contaID;
	private String email;
	private String password;
	private boolean suspenso;
	private int tipoConta;
	private Number avaliacaoMedia;

	private int pessoaID;
	private String nome;
	private String dataNascimento;
	private int pessoaContaID;
	private String contacto;
	private int concelhoID;
	private String concelho;
	private String fotoPerfil;
	private String aboutMe;
	private int modoConta;

	public User(DBRow user) {

		this.contaID = (int) user.get("conta_id");
		this.email = (String) user.get("email");
		this.password = (String) user.get("pword");
		this.suspenso = (boolean) user.get("suspenso");
		this.tipoConta = (int) user.get("tipo_de_conta");
		this.avaliacaoMedia = ((Number) user.get("avaliacao_media_conta"));
		this.fotoPerfil = (String) user.get("foto_perfil");

		this.pessoaID = (int) user.get("pessoa_id");
		this.nome = (String) user.get("pessoa_nome");

		// this.dataNascimento = (Date) user.get("data_nascimento");
		this.dataNascimento = new SimpleDateFormat("yyyy-MM-dd").format((Date) user.get("data_nascimento"));
		this.pessoaContaID = (int) user.get("pessoa_conta_id");
		this.contacto = (String) user.get("telefone");
		this.concelhoID = (int) user.get("pessoa_concelho_id");
		this.aboutMe = (String) user.get("about_me");

		this.modoConta=1;
	}

	public String toString() {

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

		return builder.toString();
	}

	/**
	 * @return the contaID
	 */
	public int getContaID() {
		return contaID;
	}

	/**
	 * @param contaID the contaID to set
	 */
	public void setContaID(int contaID) {
		this.contaID = contaID;
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
	public Number getAvaliacaoMedia() {
		return avaliacaoMedia;
	}

	/**
	 * @param avaliacaoMedia the avaliacaoMedia to set
	 */
	public void setAvaliacaoMedia(Number avaliacaoMedia) {
		this.avaliacaoMedia = avaliacaoMedia;
	}

	/**
	 * @return the pessoaID
	 */
	public int getPessoaID() {
		return pessoaID;
	}

	/**
	 * @param pessoaID the pessoaID to set
	 */
	public void setPessoaID(int pessoaID) {
		this.pessoaID = pessoaID;
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
	public String getDataNascimento() {
		return dataNascimento;
	}

	/**
	 * @param dataNascimento the dataNascimento to set
	 */
	public void setDataNascimento(String dataNascimento) {
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

	/**
	 * @return the fotoPerfil
	 */
	public String getFotoPerfil() {
		return fotoPerfil;
	}

	/**
	 * @param fotoPerfil the fotoPerfil to set
	 */
	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	/**
	 *
	 * @return aboutMe
	 */
	public String getAboutMe() {
		return aboutMe;
	}

	/**
	 *
	 * @return aboutMe
	 */
	public int getModoConta() {
		return modoConta;
	}

	/**
	 *
	 * @param modoConta
	 */
	public void setModoConta(int modoConta) {
		this.modoConta = modoConta;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
}
