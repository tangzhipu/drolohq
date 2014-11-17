<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="wph.iframework.utils.WebsiteUtils" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>接线员窗口</title>
<%
    String basePath = WebsiteUtils.getRootPath(request);
	int oid = (Integer)request.getSession().getAttribute("oid");
%>
<script type="text/javascript" src="../js/mqttws31.js"></script>
<script type="text/javascript" src="../js/MqttUtils.js"></script>
<script type="text/javascript" src="../js/jquery-1.2.6.min.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI.js"></script> 
<script type="text/javascript" src="../js/work/work.js"></script>
<script language="javascript" type="text/javascript" src="http://fw.qq.com/ipaddress">  
</script>    
 <script type="text/javascript">
 function a()
 {
	 alert("你的IP是："+IPData[0]+"，来自："+IPData[2]);
  }  
</script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=1.4"></script>
<script type="text/javascript" src="../js/initPageutils.js"  ></script>
<script type="text/javascript" src="http://api.map.baidu.com/library/SearchInfoWindow/1.4/src/SearchInfoWindow_min.js"></script>
<link rel="stylesheet" href="http://api.map.baidu.com/library/SearchInfoWindow/1.4/src/SearchInfoWindow_min.css" />
</script>
<script type="text/javascript">

//var host = "115.28.135.178";

	var host = "115.28.243.144";
	var port = 18080;

	var oid = <%=oid%>;
	var topic = "/o/<%=oid%>";
	function bindunbeforunload(){

		   window.onbeforeunload=perforresult;
		   //window.onbeforeunload 表示.用户关闭浏览器或者是当前网页前执行的操作.或者刷新当前页面时
}
function perforresult(){
	
			return "";
		
}

</script>
  <style type="text/css">

</style>



</head>
<body >

<div class="header" id="header"   width: 1000px; height: 800px;>
<jsp:include page="../includes/header.jsp" flush="true">
		<jsp:param name="navno" value="1" />
  </jsp:include>
</div>

<div id="huifangText" style="border:1px ;width: 15%; height: 300px; text-align: center;display: none;">
			
			<form name="huitext" id="huitext" method="post"   class="editoraddone">
<table width="524" border="0">
  <tr>
    <td width="139" class="class1">用户姓名：</td>
    <td width="375" class="class2"><input type="text" name="username" id="username" "></td>
  </tr>
  <tr>
    <td width="139" class="class1">用户地址：</td>
    <td width="375" class="class2"><input type="text" name="address" id="address" "></td>
  </tr>
  <tr>
    <td class="class1">洗衣数量：</td>
    <td class="class2"><input type="text" name="num" id="num""></td>
  </tr>
  <tr>
    <td width="139" class="class1">洗衣满意度：</td>
    <td width="375" class="class2">
                           <select id="xiyimanyidu" name="xiyimanyidu">
								<option selected="" value="1">满意</option>
								<option value="2">一般</option>
								<option value="3">不满意</option>
						</select> 
</td>
  </tr>
  <tr>
    <td class="class1">送衣满意度：</td>
    <td class="class2">
     <select id="sendmanyi" name="sendmanyi">
								<option selected="" value="1">满意</option>
								<option value="2">一般</option>
								<option value="3">不满意</option>
						</select> 
    </td>
  </tr>
 
    <td colspan="2" align="center"><input type="button" name="huifangUP" id="huifangUP" value="提交 "OnClick="huifang_update();"></td>
  </tr>
</table>
<input type="hidden" value="" name="orderId" id="orderId" />
</form>
		</div>
<div id="left" class="left" style="border:1px solid #dddddd;float: left; width: 16%; height: 600px;">
  

  <div id="HaveRefuse" style=" border:1px ;width:100%; height: 300px; text-align: center;">
			
		<!--  <div id="scrollzhaung" style="overflow-y:scroll;border:1px ;width:100%; height:300px; "	>-->
	      	<form>
				<table  width="204"  height="300px" id="orderList">
					<tr  bgcolor="ECFAF9" height="30">
				        <td width="2"></td>
						<td width="30">状态</td>
						<td align="center"> 原因 </td>					
						
					</tr>
				</table>
					</form>
				</div>
		
				
		<div id="huifang" style="border:1px ;width: 100%; height: 240px; text-align: center;">
			<div id="scrollfang" style="overflow-y:scroll;border:1px ;width:100%; height:300px; "	>
			<form>
				<table  width="205" id="huif">
					<tr  bgcolor="ECFAF9" height="30">
				        <td width="2"></td>
						<td width="30">状态</td>
						<td align="center">电话</td>					
						
					</tr>
				</table>
			</form>
			</div>
		</div>


</div>


<div id="context" class="context" style="float: right; width:83.5%; height:600px;">
<table width="100%" height="100%" border="1" bordercolor="#dddddd"  >
  <tr height="400px">
    
    <td  style="width: 100%">
<div style="width: 100%; height: 600px; display: block;">
							<div style="width: 100%;  height: 100%; overflow: hidden;">
								<div id="assignMap" style="width: 100%; height: 100%; -webkit-transition: all 0.5s ease-in-out; transition: all 0.5s ease-in-out;"></div>
							</div>
						</div>
						
					
    </td>
   
  </tr>
</table>


  </div>


</body>
</html>


