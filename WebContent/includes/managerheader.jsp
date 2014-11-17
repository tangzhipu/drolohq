<%@ page import="wph.iframework.utils.WebsiteUtils" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String basePath = WebsiteUtils.getRootPath(request);
	int oid = (Integer)request.getSession().getAttribute("oid");
	String name=(String)request.getSession().getAttribute("name");
%>

<link rel="stylesheet" href="<%=basePath%>css/basestyle.css" />
 <div id="HEADER" class="t_header">
<TABLE id="menu">
	<TBODY >  
		<TR>
		 	<TD align="left" class="menulist menulist_img_logo" height="40">
		 	 <IMG src="<%=basePath%>images/logo.png" height="100%"><span id="logo"></span>
		 	 </TD> 
			<TD align="right" class="menulist menulist_img" height="30">
	<div class="menu" style=" border:0px solid #dddddd;float: right; ">
<ul>
	<li><a class="hide" href="<%=basePath%>kf/liststaff.jsp">物流员管理</a>
	<!--[if lte IE 6]>
	<a href="../menu/index.html">DEMOS
	<table><tr><td>
	<![endif]-->
	<!-- <a href="javascript:search(\""+courtRoomUse+"\",\""+dsr+"\");'"  -->
	    <ul>
	    <li><a href="javascript:checkAll(1);" title="">查询</a></li>
	    <li><a href="javascript:addstaff();" title="">新增</a></li>
	   
	    </ul>
	<!--[if lte IE 6]>
	</td></tr></table>
	</a>
	<![endif]-->
	</li>

	<li><a class="hide" href="<%=basePath%>kf/listKf.jsp">客服管理</a>
	<!--[if lte IE 6]>
	<a href="../menu/index.html">DEMOS
	<table><tr><td>
	<![endif]-->
	<!-- <a href="javascript:search(\""+courtRoomUse+"\",\""+dsr+"\");'"  -->
	    <ul>
		    <li><a href="javascript:listkf(1);" title="">查询</a></li> 
		    <li><a href="javascript:addkf();" title="添加客服">新增</a></li>
	    </ul>
	<!--[if lte IE 6]>
	</td></tr></table>
	</a>
	<![endif]-->
	</li>

	<li><a class="hide" href="<%=basePath%>kf/listuser.jsp">用户信息</a>
	<!--[if lte IE 6]>
	<a href="../menu/index.html">DEMOS
	<table><tr><td>
	<![endif]-->
	<!-- <a href="javascript:search(\""+courtRoomUse+"\",\""+dsr+"\");'"  -->
	    <ul>
		    <li><a href="javascript:listuser(1);" title="">查询</a></li> 
		  <!--   <li><a href="javascript:addkf();" title="添加客服">新增</a></li> -->
	    </ul>
	<!--[if lte IE 6]>
	</td></tr></table>
	</a>
	<![endif]-->
	</li>


<li><a class="hide" href="<%=basePath%>area/area.jsp">区域管理</a>
<!--[if lte IE 6]>
<a href="index.html">MENUS
<table><tr><td>
<![endif]-->
    <ul>
		    <li><a href="<%=basePath%>area/area.jsp" title="">查询</a></li> 
		    <li><a href="<%=basePath%>area/add.jsp"title="添加客服">新增</a></li>
	    </ul>
<!--[if lte IE 6]>
</td></tr></table>
</a>
<![endif]-->
</li>

<li><a class="hide" href="<%=basePath%>orperator/manager.jsp">订单管理</a>
<!--[if lte IE 6]>
<a href="../layouts/index.html">LAYOUTS
<table><tr><td>
<![endif]-->
    <ul>
    <li><a href="javascript:ordersearch();" title="The zero dollar ads page">查询</a></li>
    </ul>
<!--[if lte IE 6]>
</td></tr></table>
</a>
<![endif]-->
</li>

<li><a class="hide" href="<%=basePath%>tj/tj.jsp">数据统计</a>
	<!--[if lte IE 6]>
	<a href="../layouts/index.html">LAYOUTS
	<table><tr><td>
	<![endif]-->
    <ul>
    
	   <!-- 
	  	  <li><a href="javascript:ordersearch();" title="The zero dollar ads page">查询</a></li>
	    -->
    </ul>
	<!--[if lte IE 6]>
	</td></tr></table>
	</a>
	<![endif]-->
</li>

<li><a class="hide" href="<%=basePath%>activity/act.jsp">活动管理</a>
	<!--[if lte IE 6]>
	<a href="../layouts/index.html">LAYOUTS
	<table><tr><td>
	<![endif]-->
    <ul>
    
	   <!-- 
	  	  <li><a href="javascript:ordersearch();" title="The zero dollar ads page">查询</a></li>
	    -->
    </ul>
	<!--[if lte IE 6]>
	</td></tr></table>
	</a>
	<![endif]-->
</li>

<li><a class="hide" href="<%=basePath%>login.jsp">退出</a>
<!--[if lte IE 6]>
<a href="../boxes/index.html">BOXES
<table><tr><td>
<![endif]-->
    
<!--[if lte IE 6]>
</td></tr></table>
</a>
<![endif]-->
</li>

</div>
			
			</TD>
		</TR>
	</TBODY>
</TABLE>
</div> 
