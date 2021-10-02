package app;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import Objectos.Anuncio;
import Objectos.NovaConta;
import Objectos.User;
import exceptions.EmptyParamException;
import exceptions.InteresseJaRegistadoException;
import exceptions.ParamAlreadyExistsException;
import mySqlUtils.DBRow;
import mySqlUtils.DBRowList;
import mySqlUtils.MySQLConn;

public class QueryExecuter {

	private final MySQLConn conn;

	public QueryExecuter(MySQLConn conn) {
		this.conn = conn;
	}

	public DBRowList loginTotal(String email, String password) {
		String query = "SELECT * FROM conta JOIN pessoa ON conta_id = pessoa_conta_id WHERE email = ? and pword = ? ;";
		ArrayList<Object> values = new ArrayList<>();
		values.add(email);
		values.add(password);

		return conn.executeStatement(query, values);
	};

	public DBRowList login(String email, String password) {
		String query = "SELECT * FROM conta WHERE email = ? and pword = ?";

		ArrayList<Object> values = new ArrayList<>();
		values.add(email);
		values.add(password);

		return conn.executeStatement(query, values);
	}

	public DBRowList getPessoa(int contaID) {
		String query = "SELECT * FROM pessoa WHERE pessoa_conta_id = ?";

		ArrayList<Object> values = new ArrayList<>();
		values.add(contaID);
		return conn.executeStatement(query, values);
	}

	public DBRowList getProfissionalCompleto(int contaID) {
		String query = String.format("SELECT * " +
				"FROM profissional " +
				"JOIN conta ON conta_id=profissional_id " +
				"JOIN pessoa ON conta_id=pessoa_conta_id " +
				"WHERE pessoa_conta_id = %s",contaID);
		return conn.executeQuery(query);
	}

	public void insertConta(NovaConta conta) throws EmptyParamException, ParamAlreadyExistsException {
		String queryConta = "INSERT INTO conta (email,pword,suspenso,tipo_de_conta,avaliacao_media_conta,foto_perfil) VALUES(?,?,?,?,?,?);";
		checkConta(conta);

		ArrayList<Object> values = new ArrayList<>();
		values.addAll(conta.toCollection());
		int idConta = conn.executeUpdateStatement(queryConta, values);
		conta.setPessoaContaID(idConta);
		values.clear();
		String queryPessoa = "INSERT INTO pessoa (pessoa_nome,data_nascimento,pessoa_conta_id,telefone,pessoa_concelho_id) VALUES(?,?,?,?,?)";
		checkPessoa(conta);
		values.addAll(conta.pessoaToCollection());
		conn.executeStatement(queryPessoa, values);
	}

	private boolean checkPessoa(NovaConta conta) throws ParamAlreadyExistsException, EmptyParamException {
		String query = "SELECT * FROM pessoa WHERE telefone = ? ;";
		if (conta == null)
			throw new EmptyParamException("user");

		ArrayList<Object> values = new ArrayList<>();

		values.add(conta.getContacto());
		if (!conn.executeStatement(query, values).isEmpty())
			throw new ParamAlreadyExistsException(conta.getContacto());

		return true;

	}

	private boolean checkConta(NovaConta conta) throws ParamAlreadyExistsException, EmptyParamException {

		String query = "SELECT * FROM conta WHERE email = ? ;";

		if (conta == null) // a partida o html obriga que esta condicao nunca aconteca
			throw new EmptyParamException("account");

		ArrayList<Object> values = new ArrayList<>();

		values.add(conta.getEmail());

		if (!conn.executeStatement(query, values).isEmpty()) // se retorna um valor entao e pq o email ja se encontra
																// registado
			throw new ParamAlreadyExistsException(conta.getEmail());

		return true;

	}

	public int getConcelhoID(String concelho) {
		String query = "SELECT * FROM concelho WHERE concelho_nome LIKE ? ;";
		ArrayList<Object> values = new ArrayList<>();
		values.add(concelho);
		return (int) conn.executeStatement(query, values).first().get("concelho_id");

	}

	public DBRowList getAnuncios() {
		String query = "SELECT * FROM anuncio";
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosCompletos() {
		String query = "SELECT anuncio.*, pessoa.pessoa_nome, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE estado=1";
		return conn.executeQuery(query);
	}

	public DBRow getAnuncioCompleto(int anuncioID) {
		String query = String.format("SELECT anuncio.*, pessoa.*, conta.*, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN conta ON conta_id=pessoa_conta_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE anuncio_id=%s",anuncioID);
		return conn.executeQuery(query).first();
	}

	public DBRowList getAnunciosCompletoByDistrito(String distrito) {
		String query = String.format("SELECT anuncio.*, pessoa.pessoa_nome, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE distrito_nome='%s' AND estado=1",distrito);
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosCompletoByConcelho(String concelho) {
		String query = String.format("SELECT anuncio.*, pessoa.pessoa_nome, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE concelho_nome='%s' AND estado=1",concelho);
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosCompletoByConcelhoAndCategoria(String concelho, String categoria) {
		String query = String.format("SELECT anuncio.*, pessoa.pessoa_nome, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE concelho_nome='%s' AND categoria_nome='%s' AND estado=1",concelho,categoria);
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosCompletoByDistritoAndCategoria(String distrito, String categoria) {
		String query = String.format("SELECT anuncio.*, pessoa.pessoa_nome, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE distrito_nome='%s' AND categoria_nome='%s' AND estado=1",distrito,categoria);
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosCompletoByCategoria(String categoria) {
		String query = String.format("SELECT anuncio.*, pessoa.pessoa_nome, distrito.distrito_nome, concelho.concelho_nome, categoria.categoria_nome " +
				"FROM anuncio " +
				"JOIN pessoa ON pessoa_id=anuncio_pessoa_id " +
				"JOIN categoria ON categoria_id=anuncio_categoria_id " +
				"JOIN concelho ON concelho_id=anuncio_concelho_id " +
				"JOIN distrito ON distrito_id=concelho_distrito_id " +
				"WHERE categoria_nome='%s' AND estado=1",categoria);
		return conn.executeQuery(query);
	}

	public DBRowList getProfissionaisCompletos() {
		String query = "SELECT * " +
				"FROM profissional " +
				"JOIN conta ON conta.conta_id=profissional.profissional_id " +
				"JOIN pessoa ON conta.conta_id=pessoa.pessoa_conta_id " +
				"JOIN categoriaspro ON profissional.profissional_id=categoriaspro.profissional_id " +
				"JOIN concelhosPro ON profissional.profissional_id=concelhosPro.profissional_id " +
				"GROUP BY conta.conta_id";
		return conn.executeQuery(query);
	}

	public DBRowList getProfissionaisCompletosByDistrito(String distrito) {
		String query = "SELECT *" +
				"FROM profissional " +
				"JOIN conta ON conta.conta_id=profissional.profissional_id " +
				"JOIN pessoa ON conta.conta_id=pessoa.pessoa_conta_id " +
				"JOIN categoriaspro ON profissional.profissional_id=categoriaspro.profissional_id " +
				"JOIN concelhosPro ON profissional.profissional_id=concelhosPro.profissional_id " +
				"WHERE distritos_pro LIKE '%" + distrito + "%' " +
				"GROUP BY conta.conta_id";
		return conn.executeQuery(query);
	}

	public DBRowList getProfissionaisCompletosByConcelho(String concelho) {
		String query = "SELECT *" +
				"FROM profissional " +
				"JOIN conta ON conta.conta_id=profissional.profissional_id " +
				"JOIN pessoa ON conta.conta_id=pessoa.pessoa_conta_id " +
				"JOIN categoriaspro ON profissional.profissional_id=categoriaspro.profissional_id " +
				"JOIN concelhosPro ON profissional.profissional_id=concelhosPro.profissional_id " +
				"WHERE concelhos_pro LIKE '%" + concelho + "%' " +
				"GROUP BY conta.conta_id";
		return conn.executeQuery(query);
	}

	public DBRowList getProfissionaisCompletosByConcelhoAndCategoria(String concelho, String categoria) {
		String query = "SELECT *" +
				"FROM profissional " +
				"JOIN conta ON conta.conta_id=profissional.profissional_id " +
				"JOIN pessoa ON conta.conta_id=pessoa.pessoa_conta_id " +
				"JOIN categoriaspro ON profissional.profissional_id=categoriaspro.profissional_id " +
				"JOIN concelhosPro ON profissional.profissional_id=concelhosPro.profissional_id " +
				"WHERE concelhos_pro LIKE '%" + concelho + "%' AND categorias_pro LIKE '%" + categoria + "%' " +
				"GROUP BY conta.conta_id";
		return conn.executeQuery(query);
	}

	public DBRowList getProfissionaisCompletosByDistritoAndCategoria(String distrito, String categoria) {
		String query = "SELECT *" +
				"FROM profissional " +
				"JOIN conta ON conta.conta_id=profissional.profissional_id " +
				"JOIN pessoa ON conta.conta_id=pessoa.pessoa_conta_id " +
				"JOIN categoriaspro ON profissional.profissional_id=categoriaspro.profissional_id " +
				"JOIN concelhosPro ON profissional.profissional_id=concelhosPro.profissional_id " +
				"WHERE distritos_pro LIKE '%" + distrito + "%' AND categorias_pro LIKE '%" + categoria + "%' " +
				"GROUP BY conta.conta_id";
		return conn.executeQuery(query);
	}

	public DBRowList getProfissionaisCompletosByCategoria(String categoria) {
		String query = "SELECT *" +
				"FROM profissional " +
				"JOIN conta ON conta.conta_id=profissional.profissional_id " +
				"JOIN pessoa ON conta.conta_id=pessoa.pessoa_conta_id " +
				"JOIN categoriaspro ON profissional.profissional_id=categoriaspro.profissional_id " +
				"JOIN concelhosPro ON profissional.profissional_id=concelhosPro.profissional_id " +
				"WHERE categorias_pro LIKE '%" + categoria + "%' " +
				"GROUP BY conta.conta_id";
		return conn.executeQuery(query);
	}

	public DBRowList getCategorias() {
		String query = "SELECT * FROM categoria;";
		return conn.executeQuery(query);

	}

	public DBRowList getDistritos() {
		String query = "SELECT * FROM distrito;";
		return conn.executeQuery(query);
	}

	public int categorias(String categoria) {
		String query = "SELECT * FROM categoria where categoria_nome LIKE ?;";
		ArrayList<Object> values = new ArrayList<>();
		values.add(categoria);
		return (int) conn.executeStatement(query, values).first().get("categoria_id");

	}

	public DBRowList getConcelhos() {
		String query = "SELECT * FROM concelho; ";
		return conn.executeQuery(query);

	}

	public void insertProfissional(String[] categorias, String[] concelhos, NovaConta conta)
			throws EmptyParamException {
		if (categorias == null)
			throw new EmptyParamException("categorias");
		if (concelhos == null)
			throw new EmptyParamException("concelhos");

		String query = "INSERT INTO profissional (profissional_id,avaliacao_media_profissional) VALUES(?,?);";
		ArrayList<Object> values = new ArrayList<>();
		values.add(conta.getPessoaContaID());
		values.add(0.0);
		conn.executeStatement(query, values);
		putCategorias(conta.getPessoaContaID(), categorias);
		putConcelhos(conta.getPessoaContaID(), concelhos);

	}

	public void upgradeProfissional(String[] categorias, String[] concelhos, User conta)
			throws EmptyParamException {
		if (categorias == null)
			throw new EmptyParamException("categorias");
		if (concelhos == null)
			throw new EmptyParamException("concelhos");

		changeTipoConta(conta.getContaID());

		String query = "INSERT INTO profissional (profissional_id,avaliacao_media_profissional) VALUES(?,?);";
		ArrayList<Object> values = new ArrayList<>();
		values.add(conta.getContaID());
		values.add(0.0);
		conn.executeStatement(query, values);
		putCategorias(conta.getContaID(), categorias);
		putConcelhos(conta.getContaID(), concelhos);
	}

	public void changeTipoConta(int contaID) {
		String query = "UPDATE conta SET tipo_de_conta=2 WHERE conta_id = ?";
		ArrayList<Object> values = new ArrayList<>();
		values.add(contaID);
		conn.executeUpdateStatement(query, values);
	}

	public void putCategorias(int contaID, String[] categorias) {
		ArrayList<Object> values;

		String query = "INSERT INTO exerce (exerce_profissional_id,exerce_categoria_id) VALUES(?,?);";
		for (int i = 0; i < categorias.length; i++) {
			values = new ArrayList<>();
			values.add(contaID);
			values.add(categorias(categorias[i]));
			conn.executeStatement(query, values);
		}

	}

	private void putConcelhos(int idProfissional, String[] concelhos) {
		String query = "INSERT INTO atua (atua_profissional_id, atua_concelho_id) VALUES(?,?);";
		ArrayList<Object> values = new ArrayList<>();

		for (int i = 0; i < concelhos.length; i++) {
			values = new ArrayList<>();
			values.add(idProfissional);
			values.add(concelhos(concelhos[i]));
			conn.executeStatement(query, values);
		}

	}

	private int concelhos(String concelho) {
		String query = "SELECT * FROM concelho where concelho_nome = ?;";
		ArrayList<Object> values = new ArrayList<>();
		values.add(concelho);
		return (int) conn.executeStatement(query, values).first().get("concelho_id");
	}

	public DBRowList getAnunciosAtivosCliente(int pessoaID) {
		String query = String.format("SELECT * FROM anuncio WHERE anuncio_pessoa_id=%s AND estado=1", pessoaID);
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosDecorrerCliente(int pessoaID) {
		String query = String.format("SELECT * FROM anuncio WHERE anuncio_pessoa_id=%s AND estado=2", pessoaID);
		return conn.executeQuery(query);
	}

	public DBRowList getAnunciosTerminadosCliente(int pessoaID) {
		String query = String.format("SELECT * FROM anuncio WHERE anuncio_pessoa_id=%s AND estado=3", pessoaID);
		return conn.executeQuery(query);
	}



	public DBRowList getServicosProfissional(int proID) {
		String query = String.format("SELECT * " +
				"FROM servico " +
				"JOIN anuncio ON anuncio_id=servico_anuncio_id " +
				"LEFT JOIN avaliacao ON avaliacao_servico_id=servico_id " +
				"WHERE servico_profissional_id = %s AND tipo BETWEEN 1 AND 3 " +
				"GROUP BY servico_id", proID);
		return conn.executeQuery(query);
	}


	public DBRowList getComentariosCliente(int pessoaID) {
		String query = String.format("SELECT avaliacao.*, servico.*, pessoa.pessoa_nome, conta.foto_perfil " + "FROM avaliacao "
				+ "JOIN servico ON avaliacao_servico_id=servico_id "
				+ "JOIN anuncio ON servico_anuncio_id=anuncio_id "
				+ "JOIN profissional ON profissional_id=servico_profissional_id "
				+ "JOIN conta ON profissional_id=conta_id "
				+ "JOIN pessoa ON conta_id=pessoa_conta_id "
				+ "WHERE anuncio_pessoa_id=%s AND tipo=2", pessoaID);
		return conn.executeQuery(query);
	}

	public DBRowList getComentariosProfissional(int contaID) {
		String query = String.format("SELECT * " +
				"FROM servico " +
				"JOIN avaliacao ON servico_id=avaliacao_servico_id " +
				"JOIN anuncio ON servico_anuncio_id=anuncio_id " +
				"JOIN pessoa ON anuncio_pessoa_id = pessoa_id " +
				"JOIN conta ON pessoa_conta_id=conta_id " +
				"WHERE servico_profissional_id = %s AND tipo=1", contaID);
		return conn.executeQuery(query);
	}

	public DBRowList getNotificacoesAnuncios(int contaID) {
		String query = String.format("SELECT * " +
				"FROM notifica " +
				"JOIN notificacao ON notifica_notificacao_id = notificacao_id " +
				"JOIN anuncio ON referencia = anuncio_id " +
				"WHERE notifica_conta_id = %s AND tipo = 'anuncio' AND notifica.lida=0", contaID);
		return conn.executeQuery(query);
	}

	public DBRowList getNotificacoesRespostas(int contaID) {
		String query = String.format("SELECT * " +
				"FROM notifica " +
				"JOIN notificacao ON notifica_notificacao_id = notificacao_id " +
				"JOIN responde ON referencia=responde_id " +
				"WHERE notifica_conta_id = %s AND tipo = 'responde' AND notifica.lida=FALSE", contaID);
		return conn.executeQuery(query);
	}

	public DBRowList getNotificacoesMensagens(int contaID) {
		String query = String.format("SELECT * " +
				"FROM " + "notifica " +
				"JOIN notificacao ON notifica_notificacao_id = notificacao_id " +
				"JOIN mensagem ON referencia = mensagem_id " +
				"WHERE notifica_conta_id = %s AND tipo = 'mensagem' AND notifica.lida=0 " +
				"GROUP BY enviada_por", contaID);
		return conn.executeQuery(query);
	}

	public DBRowList getConcelhosPro(int proID){
		   String query = String.format("SELECT * FROM atua JOIN concelho ON concelho_id=atua_concelho_id where atua_profissional_id = %s;", proID);
		   return conn.executeQuery(query);
		}

	public DBRow getConcelhoById(User user){
		String query = String.format("SELECT * FROM concelho WHERE concelho_id = %s", user.getConcelhoID());
		return conn.executeQuery(query).first();
	}

	public DBRowList getCategoriasProfissional(int profissionalID) {
		String query = String.format("SELECT * " +
				"FROM exerce " +
				"JOIN categoria ON categoria_id=exerce_categoria_id " +
				"WHERE exerce_profissional_id = %s", profissionalID);
		return conn.executeQuery(query);
	}

	public DBRowList getConcelhosByDistrito(String distrito) {
		String query = String.format("SELECT concelho.* " +
				"FROM distrito " +
				"JOIN concelho ON concelho_distrito_id=distrito_id " +
				"WHERE distrito.distrito_nome = '%s'", distrito);
		return conn.executeQuery(query);
	}

	public DBRowList getChatSender(User user) {
		String query = String.format(
				"SELECT * FROM (SELECT DISTINCT recebida_por as conversas FROM mensagem WHERE enviada_por = %s UNION SELECT DISTINCT enviada_por FROM mensagem WHERE recebida_por = %s) AS K JOIN pessoa where conversas = pessoa_conta_id",
				user.getContaID(), user.getContaID());

		return conn.executeQuery(query);
	}

	public DBRowList getMensagens(int idEnviado, int idRecebido) {

		String query = "SELECT * FROM mensagem WHERE (enviada_por = ? AND recebida_por = ?) OR (enviada_por = ? AND recebida_por = ?) ORDER BY data_hora";

		ArrayList<Object> values = new ArrayList<>();
		values.add(idEnviado);
		values.add(idRecebido);

		values.add(idRecebido);
		values.add(idEnviado);
		return conn.executeStatement(query, values);

	}

	public int insertMensagem(int emissor, int receptor, String mensagem) {
		String query = "INSERT INTO mensagem (conteudo,enviada_por,recebida_por,data_hora,lida,mensagem_resposta_a) VALUES(?,?,?,?,?,?)";
		ArrayList<Object> values = new ArrayList<>();
		values.add(mensagem);
		values.add(emissor);
		values.add(receptor);
		values.add(LocalDateTime.now());
		values.add(0);
		values.add(null);
		return conn.executeUpdateStatement(query, values);
	}

	public int insertAnuncio(Anuncio anuncio) {

		String query = "INSERT INTO anuncio (anuncio_pessoa_id,anuncio_categoria_id,titulo,descricao,imagem,estado,valor,data_hora,anuncio_concelho_id) VALUES (?,?,?,?,?,?,?,?,?)";

		ArrayList<Object> values = new ArrayList<>(anuncio.toCollection());

		int anuncioID= conn.executeUpdateStatement(query, values);

		int notificacaoID = novaNotificacao("anuncio",anuncioID);

		DBRowList prosNotifica = getProfissionaisByCategoria(anuncio.getAnuncioCategoriaID());
		for (DBRow pro : prosNotifica ) {
			notificarUser(notificacaoID,(int)pro.get("exerce_profissional_id"));
		}

		return anuncioID;
	}

	public DBRow getAvaliacaoPro(int proID) {
		String query = String.format(
				"SELECT * FROM profissional WHERE profissional_id=%s",proID);
		return conn.executeQuery(query).first();
	}

	public void lerNotificacoesRespostas(int contaID, int anuncioID) {
		String query = String.format(
				"UPDATE notifica,notificacao,responde " +
						"SET lida=TRUE " +
						"WHERE notifica_notificacao_id=notificacao_id " +
						"AND responde_id=referencia " +
						"AND notifica_conta_id = %s " +
						"AND tipo = 'responde' " +
						"AND responde_anuncio_id=%s",contaID,anuncioID);
		conn.executeUpdate(query);
	}

	public void lerNotificacoesMensagens(int contaID, String contactoID) {
		String query = String.format(
				"UPDATE notifica,notificacao,mensagem " +
						"SET notifica.lida=TRUE " +
						"WHERE notifica_notificacao_id=notificacao_id " +
						"AND mensagem_id=referencia " +
						"AND notifica_conta_id = %s " +
						"AND tipo = 'mensagem' " +
						"AND enviada_por=%s",contaID,Integer.parseInt(contactoID));
		conn.executeUpdate(query);
	}

	public void lerNotificacoesAnuncios(int contaID, int anuncioID) {
		String query = String.format(
				"UPDATE notifica,notificacao,anuncio " +
						"SET notifica.lida=TRUE " +
						"WHERE notifica_notificacao_id=notificacao_id " +
						"AND anuncio_id=referencia " +
						"AND notifica_conta_id = %s " +
						"AND tipo = 'anuncio' " +
						"AND anuncio_id=%s",contaID,anuncioID);
		conn.executeUpdate(query);
	}

	public DBRowList getRespostas(int idAnuncio) {
		ArrayList<Object> values = new ArrayList<>();
		String query = "SELECT * FROM responde JOIN profissional ON responde_profissional_id = profissional_id JOIN conta ON conta_id = profissional_id JOIN  pessoa ON conta_id = pessoa_conta_id WHERE responde_anuncio_id = ?";
		values.add(idAnuncio);

		return conn.executeStatement(query, values);

	}

	public DBRow getServico(int idAnuncio) {
		String query = "SELECT * FROM servico JOIN profissional ON profissional_id = servico_profissional_id JOIN pessoa ON pessoa_conta_id  = servico_profissional_id JOIN conta ON pessoa_conta_id = conta_id LEFT JOIN avaliacao ON servico_id=avaliacao_servico_id WHERE servico_anuncio_id = ? ";
		ArrayList<Object> values = new ArrayList<>();
		values.add(idAnuncio);

		return conn.executeStatement(query, values).first();
	}

	public boolean checkInsertResposta(User user, String params) throws InteresseJaRegistadoException {
		ArrayList<Object> values = new ArrayList<>();
		String query = "SELECT * FROM responde WHERE responde_profissional_id = ? AND responde_anuncio_id = ?";
		values.add(user.getContaID());
		values.add(Integer.parseInt(params));

		if (conn.executeStatement(query, values).first() != null)
			throw new InteresseJaRegistadoException();

		return true;
	}

	private DBRow getOwnerDoAnuncio(int anuncioID) {
		ArrayList<Object> values = new ArrayList<>();
		String queryOwner = "SELECT * FROM anuncio JOIN pessoa ON anuncio_pessoa_id = pessoa_id WHERE anuncio_id = ?";
		values.add(anuncioID);

		return conn.executeStatement(queryOwner, values).first();
	}

	public void notificarUser(int notificacaoID, int userID) {
		ArrayList<Object> values = new ArrayList<>();
		values.add(notificacaoID);
		values.add(userID);
		String query = "INSERT INTO notifica (notifica_notificacao_id, notifica_conta_id) VALUES (?,?)";
		conn.executeUpdateStatement(query,values);
	}

	public void desnotificarUser(int notificacaoID, int userID) {
		ArrayList<Object> values = new ArrayList<>();
		values.add(notificacaoID);
		values.add(userID);
		String query = "DELETE FROM notifica WHERE notifica_notificacao_id=? AND notifica_conta_id=?";
		conn.executeUpdateStatement(query,values);
	}

	public int novaNotificacao(String tipo, int referencia) {
		String query = "INSERT INTO notificacao (tipo,referencia,data_hora) VALUES(?,?,?)";
		ArrayList<Object> values = new ArrayList<>();
		values.add(tipo);
		values.add(referencia);
		values.add(LocalDateTime.now());
		return conn.executeUpdateStatement(query, values);
	}

	public int removerNotificacao(String tipo, int referencia) {
		String query = "DELETE FROM notificacao WHERE tipo=? AND referencia=?";
		ArrayList<Object> values = new ArrayList<>();
		values.add(tipo);
		values.add(referencia);
		return conn.executeUpdateStatement(query, values);
	}

	public boolean insertResposta(User user, String anuncioID) throws InteresseJaRegistadoException {
		ArrayList<Object> values = new ArrayList<>();
		String query = "INSERT INTO responde(responde_profissional_id,responde_anuncio_id,data_hora) VALUES(?,?,?)";

		if (checkInsertResposta(user, anuncioID)) {
			values.add(user.getContaID());
			values.add(Integer.parseInt(anuncioID));
			values.add(LocalDateTime.now());

			int ownerID = (int) getOwnerDoAnuncio(Integer.parseInt(anuncioID)).get("pessoa_conta_id");

			notificarUser(novaNotificacao("responde",conn.executeUpdateStatement(query, values)), ownerID);

			return true;
		}

		return false;
	}

	public void removeResposta(User user, String anuncioID) {
		ArrayList<Object> values = new ArrayList<>();

		String query = "SELECT * FROM responde WHERE responde_profissional_id = ? AND responde_anuncio_id = ? ";
		values.add(user.getContaID());
		values.add(Integer.parseInt(anuncioID));
		int respondeID = (int) conn.executeStatement(query,values).first().get("responde_id");

		values.clear();

		query = "SELECT * FROM notificacao WHERE tipo = ? AND referencia = ? ";
		values.add("responde");
		values.add("respondeID");
		int notificacaoID = (int) conn.executeStatement(query,values).first().get("notificacao_id");

		int ownerID = (int) getOwnerDoAnuncio(Integer.parseInt(anuncioID)).get("pessoa_conta_id");
		desnotificarUser(notificacaoID,ownerID);

		removerNotificacao("responde",respondeID);

		query = "DELETE FROM responde WHERE responde_profissional_id = ? AND responde_anuncio_id = ? ";
		values.add(user.getContaID());
		values.add(Integer.parseInt(anuncioID));
		conn.executeUpdateStatement(query, values);

	}

	public void insertServico(String idProfissional, String idAnuncio) {
		ArrayList<Object> values = new ArrayList<>();
		String query = "INSERT INTO servico (data_hora,servico_profissional_id,servico_anuncio_id) VALUES (?,?,?)";
		String queryAnuncio = "UPDATE anuncio SET estado = 2 WHERE anuncio_id = ?";
		values.add(LocalDateTime.now());
		values.add(idProfissional);
		values.add(idAnuncio);

		conn.executeStatement(query, values);

		values.clear();

		values.add(idAnuncio);

		conn.executeUpdateStatement(queryAnuncio, values);
	}

	public void avaliarServico(String avaliacao, int tipo, String idAnuncio, String comentario) {
		String query = "INSERT INTO avaliacao(valor_avaliacao,tipo,avaliacao_servico_id,comentario) VALUES(?,?,?,?)";
		String queryCount = "SELECT COUNT(*) FROM AVALIACAO JOIN SERVICO on avaliacao_servico_id = servico_id WHERE servico_anuncio_id = ?";
		String queryAnuncio = "UPDATE anuncio SET estado = 3 WHERE anuncio_id = ?";
		ArrayList<Object> values = new ArrayList<>();
		values.add(Integer.parseInt(avaliacao));
		values.add(tipo);
		values.add(getServico(Integer.parseInt(idAnuncio)).get("servico_id"));
		values.add(comentario);

		conn.executeStatement(query, values);

		values.clear();
		values.add(idAnuncio);
		DBRowList res = conn.executeStatement(queryCount, values);

		if ((long) res.first().get("COUNT(*)") == 2) {
			values.clear();
			values.add(idAnuncio);
			conn.executeStatement(queryAnuncio, values);
		}

		if (tipo==1) {
			query = "SELECT * FROM servico JOIN profissional ON servico_profissional_id=profissional_id WHERE servico_anuncio_id=?";
			int proID = (int) conn.executeStatement(query, values).first().get("profissional_id");

			query = "SELECT * FROM servico JOIN avaliacao ON avaliacao.avaliacao_servico_id = servico.servico_id WHERE servico_profissional_id = ? AND tipo=1;";
			values.clear();
			values.add(proID);

			DBRowList avaliacoes = conn.executeStatement(query, values);
			if (!avaliacoes.isEmpty()) {
				int numAvals = 0;
				float somaAvals = 0;
				for (DBRow aval :
						avaliacoes) {
					numAvals++;
					somaAvals += (int) aval.get("valor_avaliacao");
				}

				float avrg = somaAvals / numAvals;

			values.clear();
			values.add(avrg);
			values.add(proID);
			query = "UPDATE profissional SET avaliacao_media_profissional=? WHERE profissional_id=?;";
			conn.executeUpdateStatement(query, values);
			}
		}

		if (tipo==2){
			query = "SELECT * FROM anuncio JOIN pessoa ON anuncio_pessoa_id=pessoa_id JOIN conta ON pessoa_conta_id=conta_id WHERE anuncio_id=?";
			int clienteID = (int) conn.executeStatement(query, values).first().get("conta_id");

			query = "SELECT * FROM servico JOIN avaliacao ON avaliacao.avaliacao_servico_id = servico.servico_id JOIN anuncio ON servico_anuncio_id=anuncio_id JOIN pessoa ON pessoa_id=anuncio_pessoa_id WHERE pessoa_conta_id = ? AND tipo=2;";
			values.clear();
			values.add(clienteID);
			DBRowList avaliacoes = conn.executeStatement(query, values);

			if (!avaliacoes.isEmpty()) {
				int numAvals = 0;
				float somaAvals = 0;
				for (DBRow aval :
						avaliacoes) {
					numAvals++;
					somaAvals += (int) aval.get("valor_avaliacao");
				}

				float avrg = somaAvals / numAvals;

				values.clear();
				values.add(avrg);
				values.add(clienteID);
				query = "UPDATE conta SET avaliacao_media_conta=? WHERE conta_id=?;";
				conn.executeUpdateStatement(query, values);
			}
		}

	}

	public boolean checkAvaliacao(int idServico, int tipo) {
		String query = "SELECT * FROM avaliacao where avaliacao_servico_id = ? AND tipo = ?";
		ArrayList<Object> values = new ArrayList<>();
		values.add(idServico);
		values.add(tipo);

		return conn.executeStatement(query, values).first() != null;
	}

	public DBRowList getProfissionaisByCategoria(int categoriaID) {
		String query = String.format( "SELECT * FROM exerce WHERE exerce_categoria_id=%s",categoriaID);

		return conn.executeQuery(query);
	}

	public void changePassword(String pw, User user){
		String query = String.format("UPDATE conta SET pword='%s' WHERE conta_id=%s", pw, user.getContaID());
		user.setPassword(pw);
		conn.executeUpdate(query);
	}

	public void updateConta(String nomeCompleto, String phone, int concelhoResi,  User user)
			throws SQLIntegrityConstraintViolationException {
		user.setNome(nomeCompleto);
		user.setContacto(phone);
		user.setConcelhoID(concelhoResi);


		String query = String.format(
				"UPDATE pessoa SET pessoa_nome='%s', telefone=%s, pessoa_concelho_id=%s WHERE pessoa_conta_id=%s",
				nomeCompleto, phone, concelhoResi, user.getContaID());
		conn.executeUpdate(query);
	}

	public void updateConcelhoPro(ArrayList<String> concelhos, int user) {
		String query = String.format("DELETE FROM atua WHERE atua_profissional_id=%s", user);
		conn.executeUpdate(query);

		for (String concelho: concelhos) {
			query = String.format("SELECT * FROM concelho WHERE concelho_nome like '%s'",concelho);
			int concelhoID = (int)conn.executeQuery(query).first().get("concelho_id");
			query = String.format("INSERT INTO atua (atua_concelho_id, atua_profissional_id) VALUES(%s,%s)", concelhoID, user);
			conn.executeUpdate(query);
		}
	}

	public DBRowList getCategoriasProFalta(int idPro) {
		String query = "SELECT * from categoria WHERE categoria_id NOT IN (select exerce_categoria_id from exerce  WHERE exerce_profissional_id = ?)";
		ArrayList<Object> values = new ArrayList<>();
		values.add(idPro);
		return conn.executeStatement(query, values);
	}

	public void updateCategoriasPro(String[] categorias, int idPro) {
		String query = String.format("DELETE FROM exerce WHERE exerce_profissional_id=%s", idPro);
		conn.executeUpdate(query);

		for (String categoria: categorias) {
			query = String.format("SELECT * FROM categoria WHERE categoria_nome like '%s'", categoria);
			int categoriaID = (int) conn.executeQuery(query).first().get("categoria_id");
			query = String.format("INSERT INTO exerce (exerce_profissional_id,exerce_categoria_id) VALUES(%s,%s)",idPro, categoriaID);
			conn.executeUpdate(query);
		}
	}

	public void removeAnuncio(String idAnuncio) {
		int anuncioID = Integer.parseInt(idAnuncio);
		ArrayList<Object> values = new ArrayList<>();
		values.add(anuncioID);

		String query = "SELECT * FROM anuncio WHERE anuncio_id= ? ";
		int anuncioCategoria = (int) conn.executeStatement(query,values).first().get("anuncio_categoria_id");

		query = "SELECT * FROM notificacao WHERE tipo='anuncio' AND referencia= ? ";
		int notificacaoID = (int) conn.executeStatement(query,values).first().get("notificacao_id");

		DBRowList prosNotifica = getProfissionaisByCategoria(anuncioCategoria);
		for (DBRow pro : prosNotifica ) {
			desnotificarUser(notificacaoID,(int)pro.get("exerce_profissional_id"));
		}

		removerNotificacao("anuncio",notificacaoID);

		query = "DELETE FROM anuncio  where anuncio_id = ? ";
		conn.executeUpdateStatement(query, values);
	}

	public void updateAnuncio(int idAnuncio, String titulo, String valor, String descricao, String categoria, String concelho, String img) {
		String query = " UPDATE anuncio SET titulo = ?, valor = ? , descricao = ?, imagem= ?, anuncio_categoria_id = ? , anuncio_concelho_id = ? WHERE anuncio_id = ? ";
		ArrayList<Object> values = new ArrayList<>();
		values.add(titulo);
		if (valor.isEmpty())
			values.add(0);
		else
			values.add(Integer.parseInt(valor));
		values.add(descricao);
		if (img.isEmpty())
			values.add("/images/default_anuncio.png");
		else
			values.add(img);
		values.add(this.categorias(categoria));
		values.add(this.getConcelhoID(concelho));
		values.add(idAnuncio);

		conn.executeUpdateStatement(query, values);
	}

	public void updateSobreMim(User user) {
		String query = " UPDATE pessoa set about_me =? WHERE pessoa_id =?";
		ArrayList<Object> values = new ArrayList<>();
		values.add(user.getAboutMe());
		values.add(user.getContaID());
		conn.executeUpdateStatement(query, values);
	}

}