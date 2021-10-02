package app;

import static spark.Spark.get;
import static spark.Spark.post;

import Objectos.Anuncio;
import Objectos.NovaConta;
import Objectos.User;
import exceptions.EmptyParamException;
import exceptions.InteresseJaRegistadoException;
import exceptions.ParamAlreadyExistsException;
import mySqlUtils.DBRow;
import mySqlUtils.DBRowList;
import mySqlUtils.MySQLConn;
import utils.freemarker.FreemarkerContext;
import utils.freemarker.FreemarkerEngine;

import java.util.ArrayList;

public class SparkRest {

	private final MySQLConn conn;
	private final FreemarkerEngine engine;
	private final QueryExecuter qExec;

	public SparkRest(MySQLConn conn) {
		this.conn = conn;
		qExec = new QueryExecuter(conn);
		this.engine = new FreemarkerEngine("src/resources/templates");
		this.routes();
	}

	public void routes() {

		get("/", (request, response) -> {
			response.redirect("/login/");
			return null;
		});

		get("/login/", (request, response) -> {

			request.session().attribute("distritos", qExec.getDistritos());
			request.session().attribute("concelhos", qExec.getConcelhos());
			request.session().attribute("categorias", qExec.getCategorias());

			FreemarkerContext context = new FreemarkerContext();
			context.put("concelhos", request.session().attribute("concelhos"));
			context.put("userAlreadyExists", request.session().attribute("userAlreadyExists"));

			request.session().removeAttribute("userAlreadyExists");
			return engine.render(context, "login.html");
		});

		post("/login/", (request, response) -> {

			// interregar a BD sobre par username - password
			DBRowList result = qExec.loginTotal(request.queryParams("username"), request.queryParams("password"));
			DBRow row = result.first();

			// se nao existe o par username - password, redireciona para a pagina de login
			if (row == null) {
				FreemarkerContext context = new FreemarkerContext();
				context.put("login_error", true);
				context.put("concelhos", request.session().attribute("concelhos"));
				return engine.render(context, "login.html");
			}

			User user = new User(row);
			request.session().attribute("user", user);// colocar a conta no atributo de sessao
			response.redirect("/perfil_cliente/");

			return "";

		});

		post("/signUp/", (request, response) -> {
			try {
				NovaConta novaConta = new NovaConta(request, qExec.getConcelhoID(request.queryParams("concelho")));
				// se foi selecionada a opcao de profissional entao redireciona-se o utilizador
				// para a pagina de completar perfil pro
				if (request.queryParams("pro") != null) {
					novaConta.setTipoConta(2);
					qExec.insertConta(novaConta);
					request.session().attribute("novaConta", novaConta);
					response.redirect("/signUp/completeProfile/");
					return null;
				}
				qExec.insertConta(novaConta);

			} catch (ParamAlreadyExistsException e) {
				request.session().attribute("userAlreadyExists", e.getMessage());
				System.out.println(e.getMessage());
			} catch (EmptyParamException e) {
				System.out.println(e.getMessage());
			} catch (NullPointerException e) {
				request.session().attribute("userAlreadyExists", "selecione um concelho valido");
			}

			response.redirect("/login/");
			return null;
		});

		get("/signUp/completeProfile/", (request, response) -> {
			if (request.session().attribute("novaConta") == null) {
				response.redirect("/login/");
				return "";
			}

			FreemarkerContext context = new FreemarkerContext();
			context.put("categorias", request.session().attribute("categorias"));
			context.put("concelhos", request.session().attribute("concelhos"));
			context.put("error", request.session().attribute("error"));
			request.session().removeAttribute("error");

			return engine.render(context, "completeSignUp.html");
		});

		post("/signUp/completeProProfile/", (request, response) -> {

			String[] categorias = request.queryParamsValues("categorias");
			String[] concelhos = request.queryParamsValues("concelhos");
			try {
				qExec.insertProfissional(categorias, concelhos, request.session().attribute("novaConta"));

			} catch (EmptyParamException e) {
				request.session().attribute("error", true);
				System.out.println(e.getMessage());
				response.redirect("/signUp/completeProfile/");
				return null;

			}
			response.redirect("/login/");
			request.session().removeAttribute("novaConta");
			return null;
		});

		get("/logout/", (request, response) -> {
			request.session().removeAttribute("user");
			response.redirect("/login/");
			return "logout";
		});

		get("/perfil_detalhado/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// ID's
			int contaID = ((User) request.session().attribute("user")).getContaID();

			// query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			// query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			// query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios, notificaMensagens, notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);
			context.put("concelhosPro",
					qExec.getConcelhosPro(((User) request.session().attribute("user")).getContaID()));
			context.put("concelhos", qExec.getConcelhos());
			context.put("categorias", qExec.getCategorias());
			context.put("categoriasPro",
					qExec.getCategoriasProfissional(((User) request.session().attribute("user")).getContaID()));
			context.put("categoriasFalta",
					qExec.getCategoriasProFalta(((User) request.session().attribute("user")).getContaID()));
			context.put("concelhoByID", qExec.getConcelhoById(request.session().attribute("user")));

			context.put("ativar",request.session().attribute("ativar"));
			context.put("ERRO", request.session().attribute("ERRO"));
			context.put("erroConcelho", request.session().attribute("erroConcelho"));
			context.put("erroConcelhoResi", request.session().attribute("erroConcelhoResi"));
			context.put("erroCategoria", request.session().attribute("erroCategoria"));
			request.session().removeAttribute("ERRO");
			request.session().removeAttribute("erroConcelho");
			request.session().removeAttribute("erroConcelhoResi");
			request.session().removeAttribute("erroCategoria");
			request.session().removeAttribute("ativar");

			return engine.render(context, "perfil_detalhado.html");
		});

		post("/perfil_detalhado/alterarPassword/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}
			String pw1 = request.queryParams("pwd1");
			String pw2 = request.queryParams("pwd2");
			String pw3 = request.queryParams("pwd3");

			if (pw2.equals(pw3)) {

				if (pw1.equals(((User) request.session().attribute("user")).getPassword())) {

					if (!(pw1.equals(pw2))) {
						qExec.changePassword(pw2, request.session().attribute("user"));
						response.redirect("/perfil_detalhado/");
					} else
						request.session().attribute("ERRO", "Não pode introduzir a password atual");

				} else
					request.session().attribute("ERRO", "A password não está correta!");

			} else
				request.session().attribute("ERRO", "As passwords não são iguais");

			response.redirect("/perfil_detalhado/");
			return null;
		});

		post("/perfil_detalhado/update/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			User user = request.session().attribute("user");

			if (request.queryParams("alterar") != null) {

				user.setAboutMe(request.queryParams("aboutMe"));
				qExec.updateSobreMim(user);
				response.redirect("/perfil_detalhado/");
				return "ok";
			}
			ArrayList<String> concelhos = new ArrayList<>();

			if (request.queryParams("c1") != null && !request.queryParams("c1").equals("")) {
				concelhos.add(request.queryParams("c1"));
			}
			if (request.queryParams("c2") != null && !request.queryParams("c2").equals("")) {
				concelhos.add(request.queryParams("c2"));
			}
			if (request.queryParams("c3") != null && !request.queryParams("c3").equals("")) {
				concelhos.add(request.queryParams("c3"));
			}
			if (request.queryParams("c4") != null && !request.queryParams("c4").equals("")) {
				concelhos.add(request.queryParams("c4"));
			}
			if (request.queryParams("c5") != null && !request.queryParams("c5").equals("")) {
				concelhos.add(request.queryParams("c5"));
			}

			if (concelhos.isEmpty()) {
				request.session().attribute("erroConcelho", "Tem que introduzir pelo menos 1 concelho.");
				response.redirect("/perfil_detalhado/");
				return null;
			}
			if (request.queryParamsValues("categorias") == null) {
				request.session().attribute("erroCategoria", "Tem que escolher pelo menos 1 categoria.");
				response.redirect("/perfil_detalhado/");
				return null;
			}

			qExec.updateCategoriasPro(request.queryParamsValues("categorias"), user.getContaID());

			try {

				qExec.updateConcelhoPro(concelhos, user.getContaID());
			} catch (NullPointerException e) {
				request.session().attribute("erroConcelho", "Selecione um concelho válido");
			}

			try {
				qExec.updateConta(request.queryParams("nomeCompleto"), request.queryParams("phone"),
						qExec.getConcelhoID(request.queryParams("concelhoResi")), user);
			} catch (NullPointerException e) {
				request.session().attribute("erroConcelhoResi", "Selecione um concelho válido");
			}

			response.redirect("/perfil_detalhado/");
			return null;

		});

		post("/perfil_detalhado/ativar_profissional/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return null;
			}

			if (request.queryParams("ativar") != null) {
				request.session().attribute("ativar","modoPro");
				response.redirect("/perfil_detalhado/");
				return null;
			}

			if (request.queryParams("upgrade") != null) {
				((User) request.session().attribute("user")).setTipoConta(2);

				qExec.upgradeProfissional(request.queryParamsValues("categorias"),request.queryParamsValues("concelhos"),request.session().attribute("user"));
				response.redirect("/perfil_detalhado/");
				return null;
			}

			response.redirect("/perfil_detalhado/");
			return null;
		});

		get("/perfil_cliente/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//modo de conta
			((User) request.session().attribute("user")).setModoConta(1);

			// ID's
			int contaID = ((User) request.session().attribute("user")).getContaID();
			int pessoaID = ((User) request.session().attribute("user")).getPessoaID();

			// query dos anuncios
			DBRowList anunciosAtivos = qExec.getAnunciosAtivosCliente(pessoaID);
			DBRowList anunciosDecorrer = qExec.getAnunciosDecorrerCliente(pessoaID);
			DBRowList anunciosTerminados = qExec.getAnunciosTerminadosCliente(pessoaID);

			// query dos comentários
			DBRowList comentarios = qExec.getComentariosCliente(pessoaID);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("anunciosAtivos", anunciosAtivos);
			context.put("anunciosDecorrer", anunciosDecorrer);
			context.put("anunciosTerminados", anunciosTerminados);
			context.put("comentarios", comentarios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);

			return engine.render(context, "perfil_cliente.html");
		});

		get("/perfil_profissional/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			if (((User)request.session().attribute("user")).getTipoConta()!=2) {
				response.redirect("/perfil_cliente/");
				return "";
			}

			//modo de conta
			((User) request.session().attribute("user")).setModoConta(2);

			// ID's
			int contaID = ((User) request.session().attribute("user")).getContaID();

			//query da avaliação
			DBRow avaliacaoPro = qExec.getAvaliacaoPro(contaID);

			//query das categorias
			DBRowList categorias = qExec.getCategoriasProfissional(contaID);

			// query dos historico de serviços
			DBRowList servicos = qExec.getServicosProfissional(contaID);

			// query dos comentários
			DBRowList comentarios = qExec.getComentariosProfissional(contaID);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("avaliacaoPro", avaliacaoPro);
			context.put("categorias", categorias);
			context.put("servicos", servicos);
			context.put("comentarios", comentarios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);

			return engine.render(context, "perfil_profissional.html");
		});

		get("/perfil_profissional/visitar/:id/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// ID's
			int contaID = ((User) request.session().attribute("user")).getContaID();
			int proID = Integer.parseInt(request.params(":id"));

			if (((User)request.session().attribute("user")).getContaID()==proID) {
				response.redirect("/perfil_profissional/");
				return "";
			}


			//query do profissional
			DBRow infoPro = qExec.getProfissionalCompleto(proID).first();

			//query da avaliação
			DBRow avaliacaoPro = qExec.getAvaliacaoPro(proID);

			//query das categorias
			DBRowList categorias = qExec.getCategoriasProfissional(proID);

			// query dos historico de serviços
			DBRowList servicos = qExec.getServicosProfissional(proID);

			// query dos comentários
			DBRowList comentarios = qExec.getComentariosProfissional(proID);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("infoPro", infoPro);
			context.put("avaliacaoPro", avaliacaoPro);
			context.put("categorias", categorias);
			context.put("servicos", servicos);
			context.put("comentarios", comentarios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);

			return engine.render(context, "perfil_profissional_visita.html");
		});

		get("/anuncio/:id/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();
			int anuncioID = Integer.parseInt(request.params(":id"));

			//query de anuncio
			DBRow anuncio = qExec.getAnuncioCompleto(anuncioID);

			if ((int) anuncio.get("anuncio_pessoa_id") == ((User) request.session().attribute("user")).getPessoaID()) {
				((User) request.session().attribute("user")).setModoConta(1);
			}
			else if (((User) request.session().attribute("user")).getTipoConta() == 2
					&& (int) anuncio.get("anuncio_pessoa_id") == ((User) request.session().attribute("user")).getPessoaID()) {
				((User) request.session().attribute("user")).setModoConta(2);
			}

			// definir notificações de novo anúncio atual como lidas
			qExec.lerNotificacoesAnuncios(contaID,anuncioID);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			// definir notificações de respostas do anúncio atual como lidas
			qExec.lerNotificacoesRespostas(contaID,anuncioID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			DBRow servico = qExec.getServico(anuncioID);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("anuncio", anuncio);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);
			context.put("respostas", qExec.getRespostas(anuncioID));
			context.put("servico", servico);

			if (servico!=null) {
				if (qExec.checkAvaliacao((int) servico.get("servico_id"), 1))

					context.put("avaliacaoProfissional", "existe");

				if (qExec.checkAvaliacao((int) servico.get("servico_id"), 2))
					context.put("avaliacaoCliente", "existe");
			}

			try {
				qExec.checkInsertResposta(request.session().attribute("user"), request.params(":id"));
			} catch (InteresseJaRegistadoException e) {

				context.put("jaRegistado", e.getMessage());
			}

			return engine.render(context, "anuncio.html");
		});

		post("/anuncio/:id/confirmar/", (request, response) -> {

			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}
			if (request.queryParams("aceitar") != null) {
				try {
					qExec.insertResposta(request.session().attribute("user"), request.params(":id"));

				} catch (InteresseJaRegistadoException e) {
					e.getMessage();
				}
			} else if (request.queryParams("cancelar") != null) {
				qExec.removeResposta(request.session().attribute("user"), request.params(":id"));
			} else {
				qExec.insertServico(request.queryParams("confirmar"), request.params(":id"));
			}
			response.redirect("/anuncio/" + request.params(":id") + "/");
			return null;
		});

		get("/anuncio/:id/editar/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}
			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			// query das notificaÃ§Ãµes de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			// query das notificaÃ§Ãµes de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			// query das notificaÃ§Ãµes de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			DBRow anuncio = qExec.getAnuncioCompleto(Integer.parseInt(request.params(":id")));

			// somar o nÃºmero de notificaÃƒÂ§ÃƒÂµes
			int countNotificacoes = somaNotificacoes(notificaAnuncios, notificaMensagens, notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);
			context.put("categorias", qExec.getCategorias());
			context.put("concelhos", qExec.getConcelhos());
			context.put("anuncio", anuncio);

			if (request.session().attribute("concelhoInvalido") != null) {
				context.put("concelhoInvalido", request.session().attribute("concelhoInvalido"));
				request.session().removeAttribute("concelhoInvalido");
			}

			return engine.render(context, "editar_anuncio.html");
		});

		post("/anuncio/:id/editar/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			qExec.updateAnuncio(Integer.parseInt(request.params(":id")), request.queryParams("titulo"),
					request.queryParams("valor"), request.queryParams("descricao"), request.queryParams("categoria"),
					request.queryParams("concelho"),request.queryParams("img"));

			response.redirect("/anuncio/"+request.params(":id")+"/");
			return null;
		});

		post("/anuncio/:id/cancelar/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			qExec.removeAnuncio(request.params(":id"));
			response.redirect("/perfil_cliente/");
			return null;
		});

		get("/perfil_cliente/novo_anuncio/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			// query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			// query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			// query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificaÃ§Ãµes
			int countNotificacoes = somaNotificacoes(notificaAnuncios, notificaMensagens, notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);
			context.put("categorias", qExec.getCategorias());
			context.put("concelhos", qExec.getConcelhos());

			if(request.session().attribute("concelhoInvalido") != null) {
				context.put("concelhoInvalido", request.session().attribute("concelhoInvalido"));
				request.session().removeAttribute("concelhoInvalido");
			}
			return engine.render(context, "criar_anuncio.html");
		});

		post("/perfil_cliente/novo_anuncio/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}
			try {
				// NÃO TE ESQUEÇAS DA IMAGEM!!!!!!
				Anuncio anuncio = new Anuncio(request, request.session().attribute("user"),
						qExec.getConcelhoID(request.queryParams("concelho")),
						qExec.categorias(request.queryParams("categoria")));

				int anuncioID = qExec.insertAnuncio(anuncio);
				response.redirect("/anuncio/" + anuncioID + "/");
			} catch (NullPointerException e) {
				response.redirect("/perfil_cliente/novo_anuncio/");
				request.session().attribute("concelhoInvalido", "Introduza um concelho válido");
			}

			return "";
		});

		get("/consulta_anuncios/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = request.session().attribute("categorias");

			//query de distritos
			DBRowList distritos = request.session().attribute("distritos");

			//query dos concelhos
			DBRowList concelhos = request.session().attribute("concelhos");

			//query de anuncios
			DBRowList anuncios = qExec.getAnunciosCompletos();

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos", concelhos);
			context.put("anuncios", anuncios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);

			return engine.render(context, "consulta_anuncios.html");

		});

		get("/consulta_anuncios/categoria/:categoria/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome da categoria a filtrar
			String filtroCategoria = request.params(":categoria");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = request.session().attribute("categorias");

			//query de distritos
			DBRowList distritos = request.session().attribute("distritos");

			//query de anuncios
			DBRowList anuncios = qExec.getAnunciosCompletoByCategoria(filtroCategoria);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroCategoria", filtroCategoria);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("anuncios", anuncios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_anuncios.html");
		});

		get("/consulta_anuncios/distrito/:distrito/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList anuncios = qExec.getAnunciosCompletoByDistrito(filtroDistrito);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito",filtroDistrito);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("anuncios", anuncios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_anuncios.html");
		});


		get("/consulta_anuncios/distrito/:distrito/concelho/:concelho/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");

			// nome do concelho a filtrar
			String filtroConcelho = request.params(":concelho");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList anuncios = qExec.getAnunciosCompletoByConcelho(filtroConcelho);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito", filtroDistrito);
			context.put("filtroConcelho", filtroConcelho);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("anuncios", anuncios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_anuncios.html");
		});

		get("/consulta_anuncios/distrito/:distrito/concelho/:concelho/categoria/:categoria/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome da categoria a filtrar
			String filtroCategoria = request.params(":categoria");

			//nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");

			// nome do concelho a filtrar
			String filtroConcelho = request.params(":concelho");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList anuncios = qExec.getAnunciosCompletoByConcelhoAndCategoria(filtroConcelho,filtroCategoria);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito", filtroDistrito);
			context.put("filtroConcelho", filtroConcelho);
			context.put("filtroCategoria", filtroCategoria);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("anuncios", anuncios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_anuncios.html");
		});

		get("/consulta_anuncios/distrito/:distrito/categoria/:categoria/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome da categoria a filtrar
			String filtroCategoria = request.params(":categoria");

			//nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");


			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList anuncios = qExec.getAnunciosCompletoByDistritoAndCategoria(filtroDistrito,filtroCategoria);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito", filtroDistrito);
			context.put("filtroCategoria", filtroCategoria);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("anuncios", anuncios);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_anuncios.html");
		});

		get("/consulta_profissionais/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// ID's
			int contaID = ((User) request.session().attribute("user")).getContaID();

			//query de categorias
			DBRowList categorias = request.session().attribute("categorias");

			//query de distritos
			DBRowList distritos = request.session().attribute("distritos");

			//query dos concelhos
			DBRowList concelhos = request.session().attribute("concelhos");

			//query de profissionais
			DBRowList pros = qExec.getProfissionaisCompletos();

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos", concelhos);
			context.put("pros", pros);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);

			return engine.render(context, "consulta_profissionais.html");

		});

		get("/consulta_profissionais/categoria/:categoria/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome da categoria a filtrar
			String filtroCategoria = request.params(":categoria");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getContaID();

			//query de categorias
			DBRowList categorias = request.session().attribute("categorias");

			//query de distritos
			DBRowList distritos = request.session().attribute("distritos");

			//query de profissionais
			DBRowList pros = qExec.getProfissionaisCompletosByCategoria(filtroCategoria);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroCategoria", filtroCategoria);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("pros", pros);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_profissionais.html");
		});

		get("/consulta_profissionais/distrito/:distrito/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			// nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos
			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de profissionais
			DBRowList pros = qExec.getProfissionaisCompletosByDistrito(filtroDistrito);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito",filtroDistrito);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("pros", pros);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_profissionais.html");
		});


		get("/consulta_profissionais/distrito/:distrito/concelho/:concelho/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");

			// nome do concelho a filtrar
			String filtroConcelho = request.params(":concelho");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList pros = qExec.getProfissionaisCompletosByConcelho(filtroConcelho);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito", filtroDistrito);
			context.put("filtroConcelho", filtroConcelho);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("pros", pros);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_profissionais.html");
		});

		get("/consulta_profissionais/distrito/:distrito/concelho/:concelho/categoria/:categoria/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome da categoria a filtrar
			String filtroCategoria = request.params(":categoria");

			//nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");

			// nome do concelho a filtrar
			String filtroConcelho = request.params(":concelho");

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList pros = qExec.getProfissionaisCompletosByConcelhoAndCategoria(filtroConcelho,filtroCategoria);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito", filtroDistrito);
			context.put("filtroConcelho", filtroConcelho);
			context.put("filtroCategoria", filtroCategoria);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("pros", pros);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_profissionais.html");
		});

		get("/consulta_profissionais/distrito/:distrito/categoria/:categoria/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			//nome da categoria a filtrar
			String filtroCategoria = request.params(":categoria");

			//nome do distrito a filtrar
			String filtroDistrito = request.params(":distrito");


			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();

			//query de categorias
			DBRowList categorias = qExec.getCategorias();

			//query de distritos
			DBRowList distritos = qExec.getDistritos();

			//query dos concelhos

			DBRowList concelhos = qExec.getConcelhosByDistrito(filtroDistrito);

			//query de anuncios
			DBRowList pros = qExec.getProfissionaisCompletosByDistritoAndCategoria(filtroDistrito,filtroCategoria);

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);

			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			// adicionar ao contexto
			FreemarkerContext context = new FreemarkerContext();
			context.put("user", request.session().attribute("user"));
			context.put("filtroDistrito", filtroDistrito);
			context.put("filtroCategoria", filtroCategoria);
			context.put("categorias", categorias);
			context.put("distritos", distritos);
			context.put("concelhos",concelhos);
			context.put("pros", pros);
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);


			return engine.render(context,"consulta_profissionais.html");
		});

		get("/perfil_cliente/chat/", (request, response) -> {
			if (request.session().attribute("user") == null) {
				response.redirect("/login/");
				return "";
			}

			FreemarkerContext context = new FreemarkerContext();

			// ID's
			int contaID = ((User) request.session().attribute("user")).getPessoaContaID();
			String contactoID = request.queryParams("contacto");

			if (contactoID == null) {
				context.put("mensagens", null);
			}
			else {
				context.put("mensagens", qExec.getMensagens(Integer.parseInt(contactoID),
						((User) request.session().attribute("user")).getContaID()));
				request.session().attribute("idReceptor", contactoID);

				// alterar notificações de mensagens para lidas
				qExec.lerNotificacoesMensagens(contaID,contactoID);
			}

			//query das notificações de anuncios
			DBRowList notificaAnuncios = qExec.getNotificacoesAnuncios(contaID);

			//query das notificações de respostas
			DBRowList notificaRespostas = qExec.getNotificacoesRespostas(contaID);


			//query das notificações de mensagens
			DBRowList notificaMensagens = qExec.getNotificacoesMensagens(contaID);

			// somar o número de notificações
			int countNotificacoes = somaNotificacoes(notificaAnuncios,notificaMensagens,notificaRespostas);

			context.put("user", request.session().attribute("user"));
			context.put("notificaAnuncios", notificaAnuncios);
			context.put("notificaRespostas", notificaRespostas);
			context.put("notificaMensagens", notificaMensagens);
			context.put("countNotificacoes", countNotificacoes);
			context.put("conversas", qExec.getChatSender(request.session().attribute("user")));

			return engine.render(context, "chat.html");
		});

		post("/perfil_cliente/chat/", (request, response) -> {

			int mensagemID = qExec.insertMensagem(((User) request.session().attribute("user")).getContaID(),
					Integer.parseInt(request.session().attribute("idReceptor")), request.queryParams("mensagem"));

			int notificacaoID = qExec.novaNotificacao("mensagem",mensagemID);

			qExec.notificarUser(notificacaoID,Integer.parseInt(request.session().attribute("idReceptor")));
			response.redirect("/perfil_cliente/chat/?contacto=" + request.session().attribute("idReceptor"));
			return null;
		});

		post("/anuncio/:id/rate/", (request, response) -> {

			qExec.avaliarServico(request.queryParams("rating"),
					((User) request.session().attribute("user")).getModoConta(), request.params(":id"),
					request.queryParams("comentario"));

			if (((User) request.session().attribute("user")).getModoConta() == 1)
				response.redirect("/perfil_cliente/");
			else {
				response.redirect("/perfil_profissional/");
			}
			return null;
		});



//		before("/id:/*",(request, response) -> {
//			if (request.session().attribute("user") == null) {
//				response.redirect("/login/");
//				halt();
//			}
//		});

	}

	private static int somaNotificacoes(DBRowList anuncios, DBRowList mensagens, DBRowList respostas) {
		int countMensagens;
		try {
			countMensagens = mensagens.size();
		} catch (NullPointerException e) {
			countMensagens = 0;
		}
		int countAnuncios;
		try {
			countAnuncios = anuncios.size();
		} catch (NullPointerException e) {
			countAnuncios = 0;
		}
		int countRespostas;
		try {
			countRespostas = respostas.size();
		} catch (NullPointerException e) {
			countRespostas = 0;
		}

		return countMensagens + countAnuncios + countRespostas;
	}

}
