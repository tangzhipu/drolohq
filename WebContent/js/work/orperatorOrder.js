$(document).ready(
		 function(){
			 ordercheckAll(1);
          }
		);
function ordercheckAll(pageNumber){
	var data = $("#orderForm").serialize();
	order('../dingdanorderPage.o', data, pageNumber, 15,
			'ordercheckAll', function() {
				//$("#order").attr("disabled", true);// 页面内容不可用
			}, function() {
				//registerCancelSelectAllListener();
			}, function() {
				//$("#order").attr("disabled", false);// 页面内容可用
			});
}
/**
 * 重新派单提交
 * @param sid
 */

function orderedit_update(){
	var oid=document.getElementById("ordernum").innerHTML;
	var sid=document.getElementById("neworderStaffnum").value;
	//alert(oid);
	//alert(sid);
	$.ajax({
		type : "POST",
		url : "../repaidan.o?staffid="+sid+"&orderId="+oid,// 更新订单，包括救援id" +			
		success : function(msg) {
			
			if (msg == 1) {
				ordercheckAll(1);
			
				var div=document.getElementById("orderjieguo");
				div.style.display = "block";
				var div=document.getElementById("repaidan");
				div.style.display = "none";
				alert("重新派单成功");
			}else {
				alert("网络异常");
			}
		}
	});

}
/**
 * 重新派送物流人员按钮
 * @param orderid
 * @param staffid
 */
function repaidanhh(orderid,staffid){


	var div=document.getElementById("orderjieguo");
	div.style.display = "none";
	//initreorder();
	var div=document.getElementById("repaidanzi");
	div.style.display = "block";
//	alert("ffffffffffff");
	document.getElementById("ordernum").innerHTML=orderid;
	document.getElementById("orderStaffnum").innerHTML=staffid;
	document.getElementById("sid").value=staffid;
}