### 前端Tips

1. 富文本标签无法通过xssFilter问题

   1. 将富文本字符串中的标签转码后提交到后端，然后在回显的时候再将被转码的字符串进行解码

   2. 转/解码方法

      ```javascript
      /*1.用正则表达式实现html转码*/
      		htmlEncodeByRegExp:function (str){
      			var s = "";
      			if(str.length == 0) return "";
      			s = str.replace(/&/g,"&amp;");
      			s = s.replace(/</g,"&lt;");
      			s = s.replace(/>/g,"&gt;");
      			s = s.replace(/ /g,"&nbsp;");
      			s = s.replace(/\'/g,"&#39;");
      			s = s.replace(/\"/g,"&quot;");
      			return s;
      			},
      /*2.用正则表达式实现html解码*/
      	   htmlDecodeByRegExp:function (str){
      	        var s = "";
      	        if(str.length == 0) return "";
      	        s = str.replace(/&amp;/g,"&");
      	        s = s.replace(/&lt;/g,"<");
      	        s = s.replace(/&gt;/g,">");
      	        s = s.replace(/&nbsp;/g," ");
      	        s = s.replace(/&#39;/g,"\'");
      	        s = s.replace(/&quot;/g,"\"");
      	        return s;
      	    }
      ```

      

2. simditor富文本编辑器

   1. 

      1. ·vue.js引入Simditor

      ```javascript
      createEditor:function () {
      			 return editor = new Simditor({
      				 toolbar: [
      					 'title', 'bold', 'italic', 'underline', 'strikethrough', 'fontScale',
      					 'color', '|', 'ol', 'ul', 'blockquote', 'code', 'table', '|', 'link',
      					 'image', 'hr', '|', 'alignment'
      				 ],
      				 textarea: '#editor',
      				 placeholder: '写点什么...',
      				 defaultImage: '/static/home/images/logo.png',
      				 imageButton: ['upload'],
      				 upload: {
      					 url: baseIMG + 'image/upload',
      					 params: null,
      					 fileKey: 'request',
      					 leaveConfirm: '正在上传文件..',
      					 connectionCount: 3
      				 }
      			 })
      
      ```

      2. 在Vue对象的data中创建editor对象  然后在页面初始化的时候调用createEditor方法，同时将文本内容赋值给editor

         ```javascript
         //向页面赋值的方法
         getInfo: function(id){
         			$.get(baseURL + "news/sysnews/info/"+id, function(r){
                         vm.sysNews = r.sysNews;
                         console.info(vm.editor);
                         //调用editor的初始化方法，并将结果赋值给data中的editor对象
         				vm.editor = vm.createEditor();
                         //赋值回显
                         vm.editor.setValue(vm.htmlDecodeByRegExp(vm.sysNews.content));
         				vm.queryType();
                     });
         		},
         ```

      3. 获取editor中的值

         ```javascript
         saveOrUpdate: function (event) {
         			var url = vm.sysNews.id == null ? "news/sysnews/save" : "news/sysnews/update";
         
         			console.info(vm.sysNews.content);
             		//通过vm.editor.getValue()获取editor对象的值
         			vm.sysNews.content=vm.htmlEncodeByRegExp(vm.editor.getValue());
         			console.info(vm.sysNews.content);
         			$.ajax({
         				type: "POST",
         			    url: baseURL + url,
                         contentType: "application/json",
         			    data: JSON.stringify(vm.sysNews),
         			    success: function(r){
         			    	if(r.code === 0){
         						alert('操作成功', function(index){
         							vm.reload();
         						});
         					}else{
         						alert(r.msg);
         					}
         				}
         			});
         		},
         ```

         

2. 富文本中调用封装的图片上传方法返回Base64或者无法返回图片路径的问题

   1. 原因可能是封装的图片上传方法的返回值和si'mditor的回调方法中的值不对应，从而无法将上传成功后回调的值赋值给editor，可以通过更改源码（在simditor.js中搜索uploadsuccess）更改其中的回调方法，使之与封装的图片上传方法中的返回值对应

      ```javascript
      //
      this.editor.uploader.on('uploadsuccess', (function(_this) {
            return function(e, file, result) {
              var $img, img_path, msg;
              if (!file.inline) {
                return;
              }
              $img = file.img;
              if (!($img.hasClass('uploading') && $img.parent().length > 0)) {
                return;
              }
              if (typeof result !== 'object') {
                try {
                  result = $.parseJSON(result);
                } catch (_error) {
                  e = _error;
                  result = {
                    success: false
                  };
                }
              }
                //被注释的为simditor源码
              if (result.status === false) {
                alert("图片上传失败,请重新上传");
                /*msg = result.msg || _this._t('uploadFailed');
                alert(msg);
                img_path = _this.defaultImage;*/
              } /*else {
                img_path = result.file_path;
              }*/
              if (result.status ) {
                alert("上传成功");
               // $img.attr('src', baseIMG+result.fileName);//重新给img标签的src属性赋值图片路径
                img_path=baseIMG+result.fileName;
              }
      ```

      