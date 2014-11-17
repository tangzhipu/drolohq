package ouc.drolo.action.orperator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ouc.drolo.dao.OrderDao;

import wph.iframework.Action;
import wph.iframework.dao.db.Database;

public class deleteInfo extends Action {
	private int sid;

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Database dbDatabase = getDatabase();
		OrderDao dao = new OrderDao(dbDatabase);
		int flag = dao.deleteInfo(sid);

		return flag + "";
	}
}
