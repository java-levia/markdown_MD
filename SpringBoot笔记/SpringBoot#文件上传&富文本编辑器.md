#SpringBoot#
##文件上传&富文本编辑器##

1 文件虚拟目录和物理路径对应关系可以通过配置类实现
	
		@Configuration
		public class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
		    @Override
		    public void addResourceHandlers(ResourceHandlerRegistry registry){
		        //指向外部目录
		        registry.addResourceHandler("img//**").addResourceLocations("file:H:/img/");
		       
				//同时还需要给本地的静态文件放行
				registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		        

				 super.addResourceHandlers(registry);
		    }
			}

	获取本机ip 
            host = InetAddress.getLocalHost().getHostAddress();

2 富文本编辑器

	Kindeditor编辑器
	* 初始化编辑器以及文件上传相关配置
		* `	<script type="text/javascript">
	/* 	var editor;
		KindEditor.ready(function(K) {
			editor = K.create('textarea[name="content"]', {
				allowFileManager : true
			});
		}); */
		var editor;
		 KindEditor.ready(function(K) { 
			 editor = K.create('textarea[name="content"]', { 
				 resizeType : 1, //这里的name属性值和下面的对应，你改成你项目用的name属性值 
				 	allowPreviewEmoticons : false, 
				 	allowImageUpload : true, //打开本地上传图片功能 
				 	uploadJson : '../news/fileupload', //文件上传url
	                fileManagerJson : '../plugins/kindeditor/jsp/file_manager_json.jsp',
				 	items : [ 'source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'cut', 'copy', 'paste', 'plainpaste', 'wordpaste', '|', 
				 		'justifyleft', 'justifycenter', 'justifyright', 'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent',
				 		'subscript', 'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/', 'formatblock', 'fontname', 'fontsize',
				 		'|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'table', 'hr',
				 		'image'],//image打开本地上传图片必须写,重要的事情说三遍 
				 		afterBlur : function() { 
				 			this.sync(); //焦点问题，这里不写会出问题.同步KindEditor的值到textarea文本框     
				 			}, 
						 afterCreate : function() {
				                this.sync();//在文本框中回显数据必须配置
				            },
			 }); 
			 editor.sync();
			 });
			 
	</script>`

		文件上传java类位于同名文件夹下的UploadController.java