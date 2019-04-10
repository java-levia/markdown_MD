## #识别浏览器类型（IOS、微信或其他）

```html
<div class="JdownApp_box">
    			<!--后端传过来的安卓apk下载路径-->
				<a href="${version.androidurl }" id="JdownApp" class="download">
					<font color="#FF0000"> 下载易财通古玩app了解更多古玩信息>>></font></a>
			</div>
```

```js
<!--区别设备下载-->
<script src="/js/jquery.min.js"></script>
<%@include file="alert.jsp" %>
<script type="text/javascript">
	//判断微信浏览器
	function weixinTip(ele) {
		var u = navigator.userAgent, app = navigator.appVersion;
		var isWeixin = !!/MicroMessenger/i.test(u);
		//Android
		var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1;
		//IOS
		var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
		if (isWeixin) {
			ele.onclick = function(e) {
				window.event ? window.event.returnValue = false : e
						.preventDefault();
				document.getElementById('JweixinTip').style.display = 'block';
			};
			document.getElementById('JweixinTip').onclick = function() {
				this.style.display = 'none';
			}
		}
		if (isAndroid) {
			document.getElementById('JdownApp').setAttribute("href",
					"${version.androidurl }");
		}
		if (isiOS) {
			document.getElementById('JdownApp').setAttribute("href",
					"https://itunes.apple.com/cn/app/id${version.appid }?mt=8");
		}
	}
	var btn = document.getElementById('JdownApp');
	weixinTip(btn);
</script>
```

