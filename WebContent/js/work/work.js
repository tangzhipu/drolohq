var OrdrId;
$(document).ready(function() {
	
	doConnect();

	initAssignMap();
	initOrderlist();
	initHuifang();
});
/**
 * 送衣后回访初始化
 */
function initHuifang(){
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "../initHuifang.o",
		data : "oid=" + oid,
		success : function(msg) {
			var length = msg.length;
			for ( var i = 0; i < length; i++) {
				var _table = document.getElementById("huif");
				// alert("a");
				var _tr = _table.insertRow(1);
				// alert("b");
				var _td = new Array(3);
				// alert("c");
				for ( var j = 0; j< _td.length; j++) {// alert("d");
					_td[j] = _tr.insertCell(-1);
				}
				_td[0].innerHTML = "<input type='hidden' id='h" + msg[i].orderId + "0' />";
				_td[1].innerHTML = "<input type='button' id='h" +  msg[i].orderId
						+ "1' size='3'  value='已送衣' onclick=\"huifangText('" + msg[i].username + "','"
						+ msg[i].orderId + "','" + msg[i].add + "','" + msg[i].amount + "')\"/>";
				_td[2].innerHTML = "<input type='text'  id='h" + msg[i].orderId
						+ "2'  style='width:90px;'  value='" + msg[i].tel + "')\"/ >";
			}
		}
	});
}

/**
 * 初始化订单列表
 */
function initOrderlist() {
	// rescue_order(1);
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "../initOrderlist.o",
		data : "oid=" + oid,
		success : function(msg) {

			var length = msg.length;
			for ( var i = 0; i < length; i++) {
				// if(i++%2==0)
				// alert(msg[i].pid+"0");
				var _table = document.getElementById("orderList");
				var _tr = _table.insertRow(1);
				// _tr.height=30px;
				var _td = new Array(3);
				var _wei1 = 0;
				var _wei2 = 0;
				var _wei3 = 0;
				for ( var j = 0; j < _td.length; j++) {
					_td[j] = _tr.insertCell(-1);

				}
				_td[0].innerHTML = "<input type='hidden' id='" + msg[i].orderid
						+ "0' />";
				_td[1].innerHTML = "<input type='button' id='" + msg[i].orderid
						+ "1' size='3' value='" + msg[i].zhuangtai
						+ "' onclick=\"refenordr('" + msg[i].sid + "','"
						+ msg[i].orderid + "','" + msg[i].lon + "','"
						+ msg[i].lan + "')\"/>";
				if (msg[i].leibie == 1) {
					_td[2].innerHTML = "<input type='text'  id='"
							+ msg[i].orderid
							+ "2'  style='width:90px;'  value='"
							+ msg[i].reason + "')\"/ >";
				} else {
					_td[2].innerHTML = "<input type='button'  id='"
							+ msg[i].orderid
							+ "2'  style='width:90px;'  value='" + msg[i].tel
							+ "' onclick=\"xiaoshi('" + msg[i].orderid
							+ "')\"/>";
				}
			}
			;
		}
	});
}
function chuli(orderid, sid, lon, lan, leibie) {
	alert("fff");
	if (leibie == 1)
		refenordr(sid, orderid, lon, lan);
	else

		xiaoshi(orderid);
}

/**
 * 上线
 */
function online() {
	$.ajax({
		type : "POST",
		url : "../online.o",
		data : "id=" + oid,
		dataType : "json",
		async : false,
		success : function(msg) {
			if (msg.s == 1) {
				alert("您已经上线啦！");
			} else if (msg.s == 2) {
				alert("上线成功！");

			} else {
				alert("上线失败！");
			}

		}
	});
}
/**
 * 下线时调用
 */
function offline() {
	$.ajax({
		type : "POST",
		url : "../offline.o",
		data : "id=" + oid,
		dataType : "json",
		async : false,
		success : function(msg) {
			if (msg.s == 1) {
				alert("已经离线！");
			} else if (msg.s == 2) {
				alert("离线成功！");
			} else {

				alert("离线失败！");
			}
		}
	});
}

/**
 * 连接mqtt
 */
function doConnect() {
	client = new Messaging.Client(host, port, getRandomClientId());
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.connect({
		keepAliveInterval : 30,
		onSuccess : onConnect,
		onFailure : function() {
			alert("连接MQTT服务器失败");
		}
	});
}
function onConnect() {
	console.log("onConnect()");
	client.subscribe(topic, {
		qos : 1
	});
}
function onConnectionLost(responseObject) {
	console.log("给接线员推送连接断开onConnectionLost():" + responseObject.errorMessage);
	if (responseObject.errorCode !== 0) {
		doConnect();
	}
}
/**
 * 推送给oprator调用
 * 
 * @param message
 */
function onMessageArrived(message) {
	console.log("给接线员推送信息:" + message.payloadString);
	var json = message.payloadString;
	var o = eval("(" + json + ")");
	var clazz = o.class;
	if (clazz == 1) {// 用户救援任务推送
		var obj = o.contents;
		uid = obj.uid;
		orderId = obj.orderId;
		lon = obj.lon;// 用户经度
		lat = obj.lat;// 用户维度

		phone = obj.phone;
		alert("lan:" + lat);
		weiZhi = obj.weiZhi;
		steps = obj.steps;
		/*
		 * $("#uid").val(uid); $("#orderId").val(orderId);
		 * $("#weizhi").val(weiZhi); $("#phone").val(phone);
		 */
		// alert("p"+phone);
		// openPopWin("areYouAccept");
		var divV = document.getElementById("areYouAccept");
		divV.style.display = "block";
	} else if (clazz == 2) {// 救援车据单推送
		// alert("接到拒绝推送");
		var obj = o.contents;
		var orderid = obj.ordrId;
		var reso = obj.reason;
		var lon = obj.lon;
		var lan = obj.lan;
		var stafid = obj.staffid;
		// alert("judan hao :"+stafid);
		var oid = obj.oid;

		addaction(orderid, reso, lon, lan, stafid);
	} else if (clazz == 3) {// 长时间不响应
		var obj = o.contents;
		var ordrId = obj.ordrId;
		var ph = obj.phone;
		var lon = obj.lon;
		var lan = obj.lan;
		var staffid = obj.stafid;
		addnoanser(ordrId, ph, lon, lan, staffid);
	} else if (clazz == 4) {// 用户评论既救援结束推送
		var obj = o.contents;
		var id = obj.pid;
		var oldP = id + "0";
		var oldJ = id + "1";
		var oldD = id + "2";
		var oldZ = id + "3";
		document.getElementById(oldP).remove();
		document.getElementById(oldJ).remove();
		document.getElementById(oldD).remove();
		document.getElementById(oldZ).remove();
	} else if (clazz == 5) {// 送衣回访

		// alert("class5");
		var obj = o.contents;
		var ordrId = obj.ordrId;
		var ph = obj.tel;
		var name = obj.name;
		var add = obj.address;
		var num = obj.num;
		document.getElementById("orderId").value = ordrId;
		addsongyihuifang(ordrId, ph, name, add, num);
	} else {
		alert("其他推送:" + clazz);
	}
}

function addsongyihuifang(orderid, ph, name, add, num) {

	var _table = document.getElementById("huif");
	// alert("a");
	var _tr = _table.insertRow(1);
	// alert("b");
	var _td = new Array(3);
	// alert("c");
	for ( var i = 0; i < _td.length; i++) {// alert("d");
		_td[i] = _tr.insertCell(-1);
	}
	_td[0].innerHTML = "<input type='hidden' id='h" + orderid + "0' />";
	_td[1].innerHTML = "<input type='button' id='h" + orderid
			+ "1' size='3'  value='已送衣' onclick=\"huifangText('" + name + "','"
			+ orderid + "','" + add + "','" + num + "')\"/>";
	_td[2].innerHTML = "<input type='text'  id='h" + orderid
			+ "2'  style='width:90px;'  value='" + ph + "')\"/ >";

}
function huifang_update() {
	$.ajax({
		type : "POST",
		url : "../huifangupdate.o",
		data : $("#huitext").serialize(),
		success : function(msg) {
			if (msg > 0) {
				// alert("ddd");
				var OrdrId = document.getElementById("orderId").value;

				var oldP = "h" + OrdrId + "0";
				var oldJ = "h" + OrdrId + "1";
				var oldD = "h" + OrdrId + "2";

				document.getElementById(oldP).remove();
				document.getElementById(oldJ).remove();
				document.getElementById(oldD).remove();
				// initleft();
				// alert("p"+oldP);
				// alert("J"+oldJ);
				// alert("D"+oldD);
				alert("添加回访成功");
				var div = document.getElementById("left");
				div.style.display = "block";
				var div = document.getElementById("context");
				div.style.display = "block";
				var div = document.getElementById("huifangText");
				div.style.display = "none";
			} else {

				alert("添加回访失败");
			}
		}
	});
}
function huifangText(name, orderid, add, num) {
	var div = document.getElementById("left");
	div.style.display = "none";
	var div = document.getElementById("context");
	div.style.display = "none";
	var div = document.getElementById("huifangText");
	div.style.display = "block";
	document.getElementById("orderId").value=orderid;
	document.getElementById("username").value = name;
	document.getElementById("address").value = add;
	document.getElementById("num").value = num;

}
function addnoanser(ordrId, ph, lon, lan, staffid) {
	var _table = document.getElementById("orderList");
	var _tr = _table.insertRow(1);
	var _td = new Array(3);
	for ( var i = 0; i < _td.length; i++) {
		_td[i] = _tr.insertCell(-1);
	}
	_td[0].innerHTML = "<input type='hidden' id='" + ordrId + "0' />";
	_td[1].innerHTML = "<input type='button' id='" + ordrId
			+ "1' size='3'  value='未响应再派' onclick=\"refenordr('" + staffid
			+ "','" + ordrId + "','" + lon + "','" + lan + "')\"/>";
	_td[2].innerHTML = "<input type='button'  id='" + ordrId
			+ "2'  style='width:90px;'  value='" + ph + "' onclick=\"xiaoshi('"
			+ ordrId + "')\"/>";

}
function xiaoshi(id) {
	var oldP = id + "0";
	var oldJ = id + "1";
	var oldD = id + "2";

	document.getElementById(oldP).remove();
	document.getElementById(oldJ).remove();
	document.getElementById(oldD).remove();
}
/**
 * 添加一行订单信息
 */
function addaction(orderid, reson, lon, lan, stafid) {

	var _table = document.getElementById("orderList");
	var _tr = _table.insertRow(1);
	var _td = new Array(3);
	for ( var i = 0; i < _td.length; i++) {
		_td[i] = _tr.insertCell(-1);
	}
	_td[0].innerHTML = "<input type='hidden' id='" + orderid + "0' />";
	_td[1].innerHTML = "<input type='button' id='" + orderid
			+ "1' size='3'  value='拒单' onclick=\"refenordr('" + stafid + "','"
			+ orderid + "','" + lon + "','" + lan + "')\"/>";
	_td[2].innerHTML = "<input type='text'  id='" + orderid
			+ "2'  style='width:90px;'  value='" + reson + "')\"/ >";

}
/**
 * 重新分配订单
 */
function refenordr(stafid, orderid, lon, lan) {
	initAssignMap();

	getMyRescue(lon, lan, stafid, orderid);

}
/**
 * 获取指定地点的若干物流人员
 * 
 * @param lon
 * @param lat
 */
function getMyRescue(lon, lan, stafid, orderid) {
	// alert("getMyRescue+"+orderid);

	$.ajax({
		type : "POST",
		url : "../getMyStaff.o",
		data : "lon=" + lon + "&lat=" + lan,
		success : function(msg) {
			var obj = eval("(" + msg + ")");
			assignMap.clearOverlays();
			// console.log("用户的经度lon:" + lon);
			// console.log("lat:" + lat);
			showMyRescue(stafid, lon, lan, obj, orderid);
		}
	});
}
/**
 * 在分配地图显示最近物流人员
 * 
 * @param obj
 */
function showMyRescue(oldstafid, lon, lat, obj, orderid) {
	OrdrId = orderid;
	// alert("showMyRescue得得得+"+orderid);
	var a = orderid.slice(0, 3);
	var b = orderid.slice(4, 7);
	var c = orderid.slice(8, 10);
	var d = orderid.slice(11, 13);
	var e = orderid.slice(14);
	var point = new BMap.Point(lon, lat);
	// 初始化地图，设置中心点坐标和地图级别
	assignMap.centerAndZoom(point, 13);

	var uicon = new BMap.Icon('../js/p/ygz.png', new BMap.Size(40, 40), {
		anchor : new BMap.Size(20, 30)
	});
	var center = new BMap.Marker(new BMap.Point(lon, lat), {
		icon : uicon
	});
	assignMap.addOverlay(center);
	alert("订单号： " + orderid +    "当前物流工号：" + oldstafid);
	var username = "", phone = "", lon = "", lat = "";
	for ( var i = 0; i < obj.length; i++) {
		var icon;
		var content;
		var staffid = obj[i].staffid;
		var name = obj[i].name;
		var phone = obj[i].phone;
		var lon = obj[i].lon;
		var lat = obj[i].lat;
		
		if (staffid == oldstafid) {
			icon = new BMap.Icon('../js/j/judan.png', new BMap.Size(40, 40), {
				anchor : new BMap.Size(20, 30)
			});
		} else {
			
			icon = new BMap.Icon('../js/j/wqq.png', new BMap.Size(40, 40), {
				anchor : new BMap.Size(20, 30)
			});
		}

		sm = "物流人员:<br/>";
		content = '<div style="margin:0;line-height:20px;padding:2px;font-size: 17px">'
				+ '姓名：'
				+ name
				+ '<br/>电话：'
				+ phone
				+ '<br/><input type="button" value="选择该人员" onclick="javascript:addJiu('
				+ staffid
				+ ','
				+ lon
				+ ','
				+ oldstafid
				+ ','
				+ lat
				+ ','
				+ orderid
				+ ','
				+ phone
				+ ');">' + '</div>';
		var createMark = function(longitude, latitude, str) {
			var searchInfoWindow = null;
			searchInfoWindow = new BMapLib.SearchInfoWindow(assignMap, content,
					{
						title : sm, // 标题
						width : 290, // 宽度
						height : 120, // 高度
						panel : "panel", // 检索结果面板
						enableAutoPan : true, // 自动平移
						searchTypes : []
					});
			var _marker = new BMap.Marker(new BMap.Point(longitude, latitude),
					{
						icon : icon
					});
			_marker.addEventListener("click", function(e) {

				searchInfoWindow.open(_marker);
			});
			return _marker;
		};

		// 创建标注 ,并将标注添加到地图中
		var marker = createMark(lon, lat, null);
		assignMap.addOverlay(marker);

	}
}

function addJiu(staffid, lon, oldstafid,lan, orderid, stel) {
	// var f=a+b+c+d+e;
	// alert("a发"+OrdrId);

	$.ajax({
		type : "POST",
		url : "../updteStaff.o",
		data : "staffid=" + staffid + "&oldstaffid=" + oldstafid+"&orderId=" + OrdrId + "&lon=" + lon
				+ "&lan=" + lan + "&stel=" + stel,
		success : function(msg) {
			if (msg > 0) {
				
				var oldP = OrdrId + "0";
				var oldJ = OrdrId + "1";
				var oldD = OrdrId + "2";

				document.getElementById(oldP).remove();
				document.getElementById(oldJ).remove();
				document.getElementById(oldD).remove();
				initAssignMap();
				// alert("p"+oldP);
				// alert("J"+oldJ);
				// alert("D"+oldD);
				//if(msg==2){
					//alert("已重派物流人员");
			//	}
				//if(msg==1){
					alert("重新分配物流人员成功");
				//	}

				/*
				 * var divV = document.getElementById("context");
				 * divV.style.display = "none";
				 */

			} else {
				alert("重新分配物流人员失败");
			}
		}
	});
}
function ping() {
	var message = new Messaging.Message("Hello");
	message.destinationName = "/World";
	client.send(message);

}
function initAssignMap() {

	assignMap = new BMap.Map("assignMap");

	// 创建点坐标
	var point = new BMap.Point(117.01507, 36.648036);
	// 初始化地图，设置中心点坐标和地图级别
	assignMap.centerAndZoom(point, 13);
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