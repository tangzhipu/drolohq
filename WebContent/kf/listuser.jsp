<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../js/jquery-1.2.6.min.js"></script>
	<script type="text/javascript" src="../js/yh.js"></script>
	<script  type="text/javascript"src="../js/PageUtils.js" ></script>
<title>用户信息</title>

<style type="text/css">
	/* common styling */
	/* set up the overall width of the menu div, the font and the margins */
	.menu {
	font-family: arial, sans-serif; 
	 /*width:420px;*/ 
	margin:0; 
	margin:10px 0;
	}
	/* remove the bullets and set the margin and padding to zero for the unordered list */
	.menu ul {
	padding:0; 
	margin:0;
	list-style-type: none;
	}
	/* float the list so that the items are in a line and their position relative so that the drop down list will appear in the right place underneath each list item */
	.menu ul li {
	float:left; 
	position:relative;
	}
	/* style the links to be 104px wide by 30px high with a top and right border 1px solid white. Set the background color and the font size. */
	.menu ul li a, .menu ul li a:visited {
	display:block; 
	text-align:center; 
	text-decoration:none; 
	width:104px; 
	height:30px; 
	color:#000; 
	border:1px solid #fff;
	border-width:1px 1px 0 0;
	
	line-height:30px; 
	font-size:11px;
	}
	/* make the dropdown ul invisible */
	.menu ul li ul {
	display: none;
	}
	/* specific to non IE browsers */
	/* set the background and foreground color of the main menu li on hover */
	.menu ul li:hover a {
	color:#fff; 
	background:#b3ab79;
	}
	/* make the sub menu ul visible and position it beneath the main menu list item */
	.menu ul li:hover ul {
	display:block; 
	position:absolute; 
	top:31px; 
	left:0; 
	width:105px;
	}
	/* style the background and foreground color of the submenu links */
	.menu ul li:hover ul li a {
	display:block; 
	background:#faeec7; 
	color:#000;
	}
	/* style the background and forground colors of the links on hover */
	.menu ul li:hover ul li a:hover {
	background:#dfc184; 
	color:#000;
	}
</style>


<!--  表格 行 颜色   -->
<style type="text/css">
	.alt{
		background-color: #D1EEEE;
	}

</style>


</head>
<body>
	
<div class="header" id="header"   width: 1000px; height: 100px;>
<jsp:include page="../includes/managerheader.jsp" flush="true">
		<jsp:param name="navno" value="1" />
  </jsp:include>
</div>

<div id="left_top" class="left_top" style="border:1px solid #dddddd;float: left;width: 18%; height:150px;">
	<form  method="post" name="qusetorderForm" id="qusetorderForm">
	     	<table  class="table1" >
			<tr>
			<td ><input type="button" value="查询所有"
				onclick="javascript:listuser(1);" class="buttom">
			</td>
			</table>
			<table  class="table2" >
			</tr>
			<tr >
				<td> <select id="leibie" name="leibie">
						<!-- <option selected="" value="1">姓名</option> -->
						<option value="2">电话</option>
				</select>
				</td>
				<td align="center">
					<input type="context" size="8" id="questcontext" name="questcontext" />
				</td>
				<td ><input type="button" value="查询"onclick="javascript:listkffl(1);" 
						class="buttom">
				</td>
			</tr>
			
		</table>
	</form>
</div>

<!--  用户   信息列表  -->	  
<div align="center" id="jieguo" class="jieguo" style="border:1px solid #dddddd;float: right; width:81%; height:800px;">
	<div id="order" style="text-align:center;">
	  <form  method="post" name="orderForm" id="orderForm" >
	   <table width="99%" border="0" align="center" id="order_Id2"> 
	    <tr bgcolor="#ECFAF9" height="44">
	      <th width="61">编号</th>
	      <th width="70">电话</th>
		  <th width="120">注册时间</th>
		  <th width="60">用户姓名</th>
		  <th width="150">订单号</th>
		  <th width="300">地址</th>
	    </tr>
	    <tbody id='listuserContent'></tbody>
	  </table>
	  <div id="listuserController" class="page" style="text-align:center;">
	  </div> 
	  </form>
	</div>
</div>

</body>
</html>