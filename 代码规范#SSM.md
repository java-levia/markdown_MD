# 代码规范#SSM

1. 关于静态类

   1. 多数用于表示各种状态的属性被作为常量定义在该静态类中，一方面可以集中进行管理，另一方面在调用时可以很方便地得知该属性的业务意义。

2. 对一些固定了选择范围的情况下考虑使用枚举类

3. 注意错误处理，尽量考虑周全，对程序中可能出现的异常进行处理（这里的意义包括不符合业务逻辑的情况，举例如下）

   `public String handleQtyReturn(Integer pdetailid,Integer qty,Integer type,String ext){

   ​		//

   ​		int i=procDetailsMapper.updateQtyById( new UpdateQtyVo(pdetailid, type,qty));
   		if(i<1) {
   			return PropertiesUtil.getVal("shop_qty_error", ext);
   		}
   		return "";
   	}`

   在以上代码中，释放库存意味着操作数据库纪录条数>=1，所以如果返回值<1则说明在操作数据库的过程中出错了。

4. 

