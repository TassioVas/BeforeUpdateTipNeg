package br.org.faepu.sankhya.logProgramavel;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;

public class EventoProgramavel implements EventoProgramavelJava {

	String nomeUsu;
	String msg;
	
	Timestamp agora = new Timestamp(System.currentTimeMillis());

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		JapeSession.SessionHandle hnd = JapeSession.open();

		ModifingFields mofFields = event.getModifingFields();
		//instanciando o modifi
		
		DynamicVO newVO = (DynamicVO) event.getVo();
		DynamicVO oldVO = (DynamicVO) event.getOldVO();

		if (mofFields.isModifing("CODTIPVENDA")) {
			//aqui testa em qual campo teve modificação.
			StringBuilder sb = new StringBuilder();
			
			BigDecimal usuarioLogado = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();
			//Aqui e para capturar usuario logado
			
			String rsId = ServiceContext.getCurrent().getHttpRequest().getParameter("resourceID");
			//Aqui e para pegar qual rotina esta rodando

			ResultSet codG = nativeSql
					.executeQuery("SELECT NOMEUSU, CODGRUPO, EMAIL FROM TSIUSU WHERE CODUSU = " + usuarioLogado);
			//Esse select e para pegar o nome do usuario logado

			while (codG.next()) {
				nomeUsu = codG.getString("NOMEUSU");
			}
			
			try {

				JapeWrapper hisDAO = JapeFactory.dao("AD_LOGTIPNEG");
				DynamicVO savePar = hisDAO.create()
						.set("USUARIO", nomeUsu)
						.set("OLDNEG", oldVO.getProperty("CODTIPVENDA"))
						.set("NEWNEG", newVO.getProperty("CODTIPVENDA"))
						.set("DATAINCLUSAO", agora)
						.set("CODUSUARIO", usuarioLogado )
						.set("NUNOTA", oldVO.getProperty("NUNOTA"))
						.set("ROTINA", rsId)
						.save();

			} catch (Exception e) {
				e.printStackTrace();
				msg = "Erro na inclusao do item " + e.getMessage();
				System.out.println(msg);
			}

		}
		JapeSession.close(hnd);
	}

}
