var assignMap;
var oldstaffid;
var oldareaName;
var areaName;
var key = 1;    //开关
var newpoint;   //一个经纬度
var points = [];    //数组，放经纬度信息
var polyline = new BMap.Polyline(); //折线覆盖物


$(document).ready(function() {
	initAssignMap();
});

function initeLonandlan(){
	 document.getElementById("p1lon").value="";
	 document.getElementById("p1lan").value="";
	 document.getElementById("p2lon").value="";
	 document.getElementById("p2lan").value="";
	 document.getElementById("p3lon").value="";
	 document.getElementById("p3lan").value="";
	 document.getElementById("p4lon").value="";
	 document.getElementById("p4lan").value="";
	 document.getElementById("p5lon").value="";
	 document.getElementById("p5lan").value="";
	 document.getElementById("p6lon").value="";
	 document.getElementById("p6lan").value="";
	 document.getElementById("p7lon").value="";
	 document.getElementById("p7lan").value="";
	 document.getElementById("p8lon").value="";
	 document.getElementById("areaName").value="";
	 document.getElementById("staffId").value="";
	 document.getElementById("areaNum").value="";

}

function startTool(){   //开关函数

	    // document.getElementById("startBtn").style.background = "green";
     	/// document.getElementById("startBtn").style.color = "white";
        document.getElementById("startBtn").value = "取点中";
	        key=0;
	        var i=0;
	        assignMap.addEventListener("click",function(e){   //单击地图，形成折线覆盖物
	            newpoint = new BMap.Point(e.point.lng,e.point.lat);
	            if(key==0){
	            	i++;
	            	if(i==1){
	            		 document.getElementById("p1lon").value = e.point.lng;
	            		 document.getElementById("p1lan").value = e.point.lat;
	            	}
	            	if(i==2){
	            		 document.getElementById("p2lon").value = e.point.lng;
	            		 document.getElementById("p2lan").value = e.point.lat;
	            	}
	            	if(i==3){
	            		 document.getElementById("p3lon").value = e.point.lng;
	            		 document.getElementById("p3lan").value = e.point.lat;
	            	}
	            	if(i==4){
	            		 document.getElementById("p4lon").value = e.point.lng;
	            		 document.getElementById("p4lan").value = e.point.lat;
	            	}
	            	if(i==5){
	            		 document.getElementById("p5lon").value = e.point.lng;
	            		 document.getElementById("p5lan").value = e.point.lat;
	            	}
	            	if(i==6){
	            		 document.getElementById("p6lon").value = e.point.lng;
	            		 document.getElementById("p6lan").value = e.point.lat;
	            	}
	            	if(i==7){
	            		 document.getElementById("p7lon").value = e.point.lng;
	            		 document.getElementById("p7lan").value = e.point.lat;
	            	}
	            	if(i==8){
	            		 document.getElementById("p8lon").value = e.point.lng;
	            		 document.getElementById("p8lan").value = e.point.lat;
	            	}
	            //    if(points[points.length].lng==points[points.length-1].lng){alert(111);}
	                points.push(newpoint);  //将新增的点放到数组中
	                polyline.setPath(points);   //设置折线的点数组
	                assignMap.addOverlay(polyline);   //将折线添加到地图上
	               
	            }
	        });
	   
	}

function endTool(){
	assignMap.clearOverlays();
    //document.getElementById("startBtn").style.background = "white";
	//document.getElementById("startBtn").style.color = "white";
	document.getElementById("startBtn").value = "开始取点";
    key=0;
}

function deleteAereInfo(areaid){
	$.ajax({
		type : "POST",
		url : "../deleteAereInfo.o?areaid="+areaid,
		success : function(msg) {
			if(msg>0){
				checkAll(1);
				alert("删除成功");
			}else{
				alert("删除失败");
			}
		}
	});
}

function serachByquest(pageNumber){
	var data = $("#qusetorderForm").serialize();
	page('../areaByquest.o', data, pageNumber, 15,
			'checkAll', function() {
				//$("#order").attr("disabled", true);// 页面内容不可用
			}, function() {
				//registerCancelSelectAllListener();
			}, function() {
				//$("#order").attr("disabled", false);// 页面内容可用
			});
}

function updataChangeArea(){
	var areaId=document.getElementById("areaid").value;
	//alert(areaId);
	var num=8;
	if(!document.getElementById("p3lon").value)
	{
		alert("至少需输入三个点");
		//num=2;
	}else{
		//alert(document.getElementById("p3lon").value);
	if(!document.getElementById("p4lon").value)num=3;
	else
		if(!document.getElementById("p5lon").value)num=4;
		else
			if(!document.getElementById("p6lon").value)num=5;
			else
				if(!document.getElementById("p7lon").value)num=6;
				else 
					if(!document.getElementById("p8lon").value)num=7;
	$.ajax({
		type : "POST",
		url : "../editAddArea.o?num="+num+"&areaId="+areaId,
		data : $("#edit_addone").serialize(),
		success : function(msg) {
			//alert(msg);
			if (msg ==2) {			
					alert("该员工已分配区域");			
			} 
			else if(msg==3)
				{
				alert("该区域名已被占用");
				}
			else if(msg==1)
				{
				alert("修改区域成功");
				}
				else{
				alert("修改区域失败");
			}
		}
	});
	}
	
}

/**
 * 显示区域详情信息
 */

function checkDatai(areaName,areaId,staffid,lon1,lon2,lon3,lon4,lon5,lon6,lon7,
		lon8,lan1,lan2,lan3,lan4,lan5,lan6,lan7,lan8,lblon,lblan,rtlon,rtlan){

	oldstaffid=staffid;
	document.getElementById("oldareaName").value=areaName;
    document.getElementById("oldstaffid").value=staffid;
	var div=document.getElementById("left_top");
	div.style.display = "none";
	var div=document.getElementById("jieguo");
	div.style.display = "none";
	document.getElementById("areaid").value=areaId;	
	//alert(document.getElementById("areaid").value);
	document.getElementById("edit_left_top").style.display = "block";
	document.getElementById("context").style.display = "block";
	
	document.getElementById("p1lon").value=lon1;
	document.getElementById("p2lon").value=lon2;
	document.getElementById("p3lon").value=lon3;
	document.getElementById("p4lon").value=lon4;
	document.getElementById("p5lon").value=lon5;
	document.getElementById("p6lon").value=lon6;
	document.getElementById("p7lon").value=lon7;
	document.getElementById("p8lon").value=lon8;
	
	document.getElementById("p1lan").value=lan1;
	document.getElementById("p2lan").value=lan2;
	document.getElementById("p3lan").value=lan3;
	document.getElementById("p4lan").value=lan4;
	document.getElementById("p5lan").value=lan5;
	document.getElementById("p6lan").value=lan6;
	document.getElementById("p7lan").value=lan7;
	document.getElementById("p8lan").value=lan8;
	document.getElementById("staffId").value=staffid;
	document.getElementById("areaName").value=areaName;
	
	document.getElementById("maxlon").value=rtlon;
	document.getElementById("maxlan").value=rtlan;
	document.getElementById("minlon").value=lblon;
	document.getElementById("minlan").value=lblan;
}

/*
 * 显示详情里的地图区域
 */
function areaAddShowDatail(){
	var lon1=document.getElementById("p1lon").value;
	 var lon2=document.getElementById("p2lon").value;
	 var lon3=document.getElementById("p3lon").value;
	 var lon4=document.getElementById("p4lon").value;
	 var lon5=document.getElementById("p5lon").value;
	 var lon6=document.getElementById("p6lon").value;
	 var lon7=document.getElementById("p7lon").value;
	 var lon8=document.getElementById("p8lon").value;
	 var lan1=document.getElementById("p1lan").value;
	 var lan2=document.getElementById("p2lan").value;
	   var lan3=document.getElementById("p3lan").value;
	   var lan4=document.getElementById("p4lan").value;
	   var lan5=document.getElementById("p5lan").value;
	   var lan6=document.getElementById("p6lan").value;
	   var lan7=document.getElementById("p7lan").value;
	   var lan8=document.getElementById("p8lan").value;
	var maidao = [ 
	             
	new BMap.Point(lon1,lan1),
	new BMap.Point(lon2,lan2),
	new BMap.Point(lon3,lan3),
	new BMap.Point(lon4,lan4),
	new BMap.Point(lon5,lan5),
	new BMap.Point(lon6,lan6),
	new BMap.Point(lon7,lan7),
	new BMap.Point(lon8,lan8),    
	               ];
	var point = new BMap.Point(lon1, lan1);
	// 初始化地图，设置中心点坐标和地图级别
	assignMap.centerAndZoom(point, 13);
	// 创建多边形
	var maidaoPolygon = new BMap.Polygon(maidao, {
		strokeColor:"#FF0000",
	//	strokeOpacity : 0.8,
		strokeWeight:2,
		//fillColor : "#FF0000",
		fillOpacity : 0.1
	});
	// 添加多边形到地图上
	assignMap.addOverlay(maidaoPolygon);
	var ltlon=document.getElementById("minlon").value;
	var ltlan=document.getElementById("maxlan").value;
	var lblon=document.getElementById("minlon").value;
	var lblan=document.getElementById("minlan").value;
	var rtlon=document.getElementById("maxlon").value;
	var rtlan=document.getElementById("maxlan").value;
	var rblon=document.getElementById("maxlon").value;
	var rblan=document.getElementById("minlan").value;
var waikuang=[
       
       new BMap.Point(ltlon,ltlan),
       new BMap.Point(rtlon,rtlan),
       new BMap.Point(rblon,rblan),
       new BMap.Point(lblon,lblan),
       
             
          ];
//创建多边形
var maidaoPolygondatai = new BMap.Polygon(waikuang, {
	strokeColor:"#009966",
	strokeOpacity : 0.8,
	strokeWeight:2,
	//fillColor : "#FF0000",
	fillOpacity : 0.1
});
// 添加多边形到地图上
assignMap.addOverlay(maidaoPolygondatai);
}

/**
 * 初始化地图
 */
function initAssignMap() {

	assignMap = new BMap.Map("assignMap");

	// 创建点坐标
	var point = new BMap.Point(117.01507, 36.648036);
	// 初始化地图，设置中心点坐标和地图级别
	assignMap.centerAndZoom(point, 15);
	// 向地图添加控件
	assignMap.addControl(new BMap.NavigationControl()); // 地图平移缩放控件，默认左上角
	assignMap.addControl(new BMap.OverviewMapControl({
		isOpen : 1
	})); // 缩略地图控件，默认右下角
	assignMap.enableScrollWheelZoom(true); // 启用鼠标滚动缩放地图
	assignMap.enableKeyboard();
	// 启用键盘上下左右键移动地图
	opts = {
		width : 100,
		height : 50
	};
}
/**
 * 查询所有
 * @param pageNumber
 */
function checkAll(pageNumber){
	var data = $("#qusetorderForm").serialize();
	page('../areaselect.o', data, pageNumber, 15,
			'checkAll', function() {
				//$("#order").attr("disabled", true);// 页面内容不可用
			}, function() {
				
			}, function() {
				
			});
	var div=document.getElementById("left_top");
	div.style.display = "block";
	var div=document.getElementById("jieguo");
	div.style.display = "block";
	
}
/**
 * 新郑区域地图显示
 */

function areaAddShow(){
	 var lon1=document.getElementById("p1lon").value;
	 var lon2=document.getElementById("p2lon").value;
	 var lon3=document.getElementById("p3lon").value;
	 var lon4=document.getElementById("p4lon").value;
	 var lon5=document.getElementById("p5lon").value;
	 var lon6=document.getElementById("p6lon").value;
	 var lon7=document.getElementById("p7lon").value;
	 var lon8=document.getElementById("p8lon").value;
	 var lan1=document.getElementById("p1lan").value;
     var lan2=document.getElementById("p2lan").value;
    var lan3=document.getElementById("p3lan").value;
    var lan4=document.getElementById("p4lan").value;
    var lan5=document.getElementById("p5lan").value;
    var lan6=document.getElementById("p6lan").value;
    var lan7=document.getElementById("p7lan").value;
    var lan8=document.getElementById("p8lan").value;
    
	var maidao = [ 
	             
	new BMap.Point(lon1,lan1),
	new BMap.Point(lon2,lan2),
	new BMap.Point(lon3,lan3),
	new BMap.Point(lon4,lan4),
	new BMap.Point(lon5,lan5),
	new BMap.Point(lon6,lan6),
	new BMap.Point(lon7,lan7),
	new BMap.Point(lon8,lan8),    
	               ];
	var point1 = new BMap.Point(lon1, lan1);
	// 初始化地图，设置中心点坐标和地图级别
	assignMap.centerAndZoom(point1, 15);
	// 创建多边形
	var maidaoPolygon = new BMap.Polygon(maidao, {
		strokeColor:"#FF0000",
		strokeOpacity : 0.3,
		strokeWeight:1,
		//fillColor : "#FF0000",
		fillOpacity : 0.1
	});
	// 添加多边形到地图上
	assignMap.addOverlay(maidaoPolygon);
		 var lon1=document.getElementById("p1lon").value;
		 var lon2=document.getElementById("p2lon").value;
		 var lon3=document.getElementById("p3lon").value;
		 var lon4=document.getElementById("p4lon").value;
		 var lon5=document.getElementById("p5lon").value;
		 var lon6=document.getElementById("p6lon").value;
		 var lon7=document.getElementById("p7lon").value;
		 var lon8=document.getElementById("p8lon").value;
		 var lan1=document.getElementById("p1lan").value;
         var lan2=document.getElementById("p2lan").value;
        var lan3=document.getElementById("p3lan").value;
        var lan4=document.getElementById("p4lan").value;
        var lan5=document.getElementById("p5lan").value;
        var lan6=document.getElementById("p6lan").value;
        var lan7=document.getElementById("p7lan").value;
        var lan8=document.getElementById("p8lan").value;
        
		var maidao = [ 
		             
		new BMap.Point(lon1,lan1),
		new BMap.Point(lon2,lan2),
		new BMap.Point(lon3,lan3),
		new BMap.Point(lon4,lan4),
		new BMap.Point(lon5,lan5),
		new BMap.Point(lon6,lan6),
		new BMap.Point(lon7,lan7),
		new BMap.Point(lon8,lan8),    
		               ];
		// 创建多边形
		var maidaoPolygon = new BMap.Polygon(maidao, {
			strokeColor:"#FF0000",
			strokeOpacity : 0.3,
			strokeWeight:1,
			//fillColor : "#FF0000",
			fillOpacity : 0.1
		});
		// 添加多边形到地图上
		assignMap.addOverlay(maidaoPolygon);
}
/**
 * 新增地图添加提交
 */
function submitArea(){ 
	var num=8;
	if(!document.getElementById("p3lon").value){
		alert("至少需输入三个点");
		//num=2;
	}else{
		//alert(document.getElementById("p3lon").value);
	if(!document.getElementById("p4lon").value)
		num=3;
	else
		if(!document.getElementById("p5lon").value)num=4;
		else
			if(!document.getElementById("p6lon").value)num=5;
			else
				if(!document.getElementById("p7lon").value)num=6;
				else 
					if(!document.getElementById("p8lon").value)num=7;
	$.ajax({
		type : "POST",
		url : "../addArea.o?num="+num,
		data : $("#addone").serialize(),
		success : function(msg) {
			//alert(msg);
			if (msg ==2) {			
					alert("该员工已分配区域");			

			} else if(msg==3)
			
			{
				alert("该区域号已存在");
			}
			else if(msg==4)
			{
				alert("该区域名已存在");
			}
			else if(msg==1)
				{
				alert("添加区域成功");
				
				initeLonandlan();
				}
			else{
				alert("添加区域失败");
			}
		}
	});
	}
}