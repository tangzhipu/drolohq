package ouc.drolo.action.orperator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import ouc.drolo.action.diaodu.Dispatcher;

import wph.iframework.Action;

public class online extends Action {
	private String id;
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Dispatcher dispatcher = Dispatcher.getInstance();
        
        int s;
        if (null == id)
        {
            // 错误处理
            s = -1;
        }
        else
        {
            s = dispatcher.onOperatorOnline(id);
        }
        
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("s", s);
            return obj.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
    
}
