var swfu;
$(function(){
	//初始化地图
	var map = new BMap.Map("map");          // 创建地图实例
	var point = new BMap.Point(106.404, 32.915);  // 创建点坐标
	map.centerAndZoom(point, 5);                 // 初始化地图，设置中心点坐标和地图级别  
	map.addControl(new BMap.NavigationControl());
	map.enableScrollWheelZoom();
	addRightClickBtn(map);
	//初始化地图上的markers
	$.post(path+"Markers/list",function(markers){
		for(var i=0;i<markers.length;i++){
			addMarker(map,new BMap.Point(markers[i].lng, markers[i].lat),markers[i].title);
		}
	});
	//初始化图片上传
	initSwfUpload();
});
//右键菜单
function addRightClickBtn(map){
	var menu = new BMap.ContextMenu();
	var txtMenuItem = [
	  {
	   text:'添加相册',
	   callback:function(point){
		   //添加Marker到数据库
		   $.post(path+"Markers/add",{lng:point.lng,lat:point.lat},function(data){
			   if(data.msg){
				   alert(data.msg);
			   }else{
				   addMarker(map,point);
			   }
		   });
	   }
	  }
	 ];
	 for(var i=0; i < txtMenuItem.length; i++){
	  menu.addItem(new BMap.MenuItem(txtMenuItem[i].text,txtMenuItem[i].callback,100));
	 }
	 map.addContextMenu(menu);
}
//添加Marker
function addMarker(map,point,title){
	var marker = new BMap.Marker(point,{title:title});  	// 创建标注
	map.addOverlay(marker);              	// 将标注添加到地图中
	marker.addEventListener("click", function(){
		openMarkerEvent(point);
	});
	var title = title=="null"?"":title.substring(0,10);
	var label = new BMap.Label(title!="null"? title.substring(0,10):"null",{"offset":new BMap.Size(20,-10)});
	label.setStyle({border:"1px solid #777",fontSize:"12px"});
	if(title!=""){
		marker.setLabel(label);
	}
}
function openMarkerEvent(point){
	//打开相册modal
	$('#image-gallery').on('show', function () {
		$("#image-gallery").css({"width":"900px","margin-left":"-450px"});
	});
	$("#image-gallery").modal("show");
	//给当前marker id域赋值，并渲染相册
	$.post(path+"Markers/open",{lng:point.lng,lat:point.lat},function(data){
		$("#markerId").val(data.id);
		//加载第一张图片
		loadPhoto(data.photo);
		$("#image").attr("filename",data.photo);
		$("#title").val(data.title);
		document.onkeydown = keyEvent;
	});
}

function nextPhoto(){
	var name = $("#image").attr("filename");
	$.post(path+"Photos/next",{name:name,markerId:$("#markerId").val()},function(data){
		loadPhoto(data.name);
		$("#image").attr("fileName",data.name);
	});
}
function prevPhoto(){
	var name = $("#image").attr("filename");
	$.post(path+"Photos/prev",{name:name,markerId:$("#markerId").val()},function(data){
		loadPhoto(data.name);
		$("#image").attr("fileName",data.name);
	});
}
function deletePhoto(){
	if(!confirm("确认删除么？")){
		return;
	}
	var name = $("#image").attr("filename");
	$.post(path+"Photos/delete",{name:name,markerId:$("#markerId").val()},function(data){
		if(data.msg){
			alert(data.msg);
		}else{
			loadPhoto(data.name);
			$("#image").attr("fileName",data.name);
		}
	});
}
function loadPhoto(name){
	$("#loading").show();
	var url = path+"data/"+name;
	//动画效果
	var img = new Image();
	$(img).bind('load',function(){
        $('#loading').hide();
        $("#image").attr('src',url);
    }).attr('src',url);
}
function saveTitle(){
	var title = $("#title").val();
	var markerId = $("#markerId").val();
	$.post(path+"Markers/saveTitle",{title:title,markerId:markerId});
}
function initSwfUpload(){
	var settings = {
			flash_url : path+"public/javascripts/swfupload/swfupload.swf",
			upload_url: path+"Photos/upload",
			post_params: {},
			file_size_limit : "10 MB",
			file_types : "*.*",
			file_types_description : "All Files",
			file_upload_limit : 0,
			file_queue_limit : 0,
			custom_settings : {
				progressTarget : "fsUploadProgress",
				cancelButtonId : "btnCancel"
			},
			debug: false,

			// Button settings
			button_width: "65",
			button_height: "26",
			button_placeholder_id: "spanButtonPlaceHolder",
			button_text: '<span class="theFont btn">Select</span>',
			button_text_style: ".theFont { font-size: 20px;text-align:center; }",
			button_text_left_padding: 18,
			button_text_top_padding: 5,
			
			// The event handler functions are defined in handlers.js
			file_queued_handler : fileQueued,
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,
			upload_start_handler : function(){
				uploadStart();
				swfu.setPostParams({"markerId" : $("#markerId").val(),"userId":$("#userId").val()});
			},
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccess,
			upload_complete_handler : uploadComplete,
			queue_complete_handler : queueComplete	// Queue plugin event
		};
		swfu = new SWFUpload(settings);
}
function checkUser(node){
	$.post(path+"Users/checkUser",{username:$(node).val()},function(data){
		if(data.exist){
			$("#signup-btn").attr("disabled","disabled");
			$("#username-tip").html("该账号已存在！");
		}else{
			$("#signup-btn").removeAttr("disabled");
			$("#username-tip").html("");
		}
	});
}

function checkPassword(){
	if($("#password").val() != $("#password_confirm").val() || $("#password").val().length<=5){
		$("#signup-btn").attr("disabled","disabled");
		$("#password-tip").html("密码长度不够或两次密码输入不一致！");
	}else{
		$("#signup-btn").removeAttr("disabled");
		$("#password-tip").html("");
	}
}
function keyEvent(e){
	e = window.event || e;
	var key=e.which || e.keyCode;
	switch(key){
		case 37:	 //左键 
			prevPhoto();
		    break;
		case 39:	 //右键 
			nextPhoto();
            break;
        default:
            break;
    }
}